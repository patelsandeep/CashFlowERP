package com.cashflow.project.frontend.controller.timesheet;

import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.project.api.milestone.ProjectMilestoneService;
import com.cashflow.project.api.people.ProjectSubContractorService;
import com.cashflow.project.api.people.ProjectTeamMemberService;
import com.cashflow.project.api.phase.ProjectPhaseService;
import com.cashflow.project.api.task.ProjectTaskService;
import com.cashflow.project.api.timesheet.TimeSheetService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.personnel.SubContractor;
import com.cashflow.project.domain.project.personnel.TeamMember;
import com.cashflow.project.domain.project.personnel.TeamMemberCategory;
import com.cashflow.project.domain.project.timesheet.ExpenseStatus;
import com.cashflow.project.domain.project.timesheet.TimeSheet;
import com.cashflow.project.domain.project.timesheet.TimeSheetInfo;
import com.cashflow.project.frontend.controller.model.TimeSheetEntryModel;
import com.cashflow.project.frontend.controller.model.TimeSheetWeeklyEntryModel;
import com.anosym.profiler.Profile;
import com.google.common.base.Throwables;
import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 *
 * 
 * @since Dec 10, 2016, 12:32:53 PM
 */
@Dependent
public class TimeSheetEntryProcessor implements Serializable {

    private static final long serialVersionUID = -5033428985645965899L;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private TimeSheetService timeSheetService;

    @Inject
    private ProjectTeamMemberService projectTeamMemberService;

    @Inject
    private TimeSheetHoursCalculator timeSheetHoursCalculator;

    @Inject
    private ProjectSubContractorService projectSubContractorService;

    @Inject
    private ProjectTaskService projectTaskService;

    @Inject
    private ProjectMilestoneService projectMilestoneService;

    @Inject
    private ProjectPhaseService projectPhaseService;

    @Profile
    public void processEntries(@Nonnull final TimeSheetEntryModel timeSheetEntryModel,
                               @Nonnull final Project project,
                               @Nonnull final String action) {
        TimeSheet timeSheet;
        if (isNullOrEmpty(timeSheetEntryModel.getTimeSheetUUID())) {
            timeSheet = new TimeSheet(getProjectLevel(timeSheetEntryModel, project), timeSheetEntryModel
                                      .getTimeSheetID(), ExpenseStatus.SAVED);
        } else {
            final Future<TimeSheet> timesheetRequest = asynchronousService
                    .execute(() -> timeSheetService
                    .getTimesheet(timeSheetEntryModel.getTimeSheetUUID()));
            final Future<ProjectLevel> levelRequest = asynchronousService
                    .execute(() -> getProjectLevel(timeSheetEntryModel, project));
            try {
                timeSheet = timesheetRequest.get();
                timeSheet.setProjectLevel(levelRequest.get());
                timeSheet.setTimeSheetID(timeSheetEntryModel.getTimeSheetID());
            } catch (InterruptedException | ExecutionException ex) {
                throw Throwables.propagate(ex);
            }
        }
        if (action.equals("submit")) {
            timeSheet.setStatus(ExpenseStatus.SUBMITTED);
        } else {
            timeSheet.setStatus(ExpenseStatus.SAVED);
        }
        timeSheet.setBusinessUnitUUID(project.getBusinessUnitUUID());
        timeSheet.setDepartmentUUID(project.getDepartmentUUID());

        if (timeSheetEntryModel.getTeamMemberCategory() == TeamMemberCategory.EMPLOYEE) {
            final TeamMember member = projectTeamMemberService
                    .getTeamMember(timeSheetEntryModel.getMember());
            timeSheet.setTeamMember(member);
        } else {
            final SubContractor subContractor = projectSubContractorService
                    .getSubContractor(timeSheetEntryModel.getMember());
            timeSheet.setSubContractor(subContractor);
        }
        final Set<TimeSheetInfo> infos = timeSheetEntryModel.getWeeklyEntries()
                .stream()
                .map((entry) -> prepareEntity(entry))
                .collect(Collectors.toSet());
        timeSheet.setTimeSheetInfos(infos);
        timeSheetService.create(timeSheet);
    }

    private TimeSheetInfo prepareEntity(
            @Nonnull final TimeSheetWeeklyEntryModel timeSheetWeeklyEntryModel) {
        final TimeSheetInfo info = new TimeSheetInfo(timeSheetWeeklyEntryModel.getTimeSheetDate(),
                                                     timeSheetWeeklyEntryModel.getBillableType());
        info.setPtoCategory(timeSheetWeeklyEntryModel.getPtOCategory());

        info.setOverTime(timeSheetHoursCalculator
                .convertToHoursSec(timeSheetWeeklyEntryModel.getOverTime()));

        info.setPtoTime(timeSheetHoursCalculator
                .convertToHoursSec(timeSheetWeeklyEntryModel.getPtoTime()));

        info.setRegularTime(timeSheetHoursCalculator
                .convertToHoursSec(timeSheetWeeklyEntryModel.getRegularTime()));

        return info;
    }

    private ProjectLevel getProjectLevel(@Nonnull final TimeSheetEntryModel timeSheetEntryModel,
                                         @Nonnull final Project project) {
        if (!isNullOrEmpty(timeSheetEntryModel.getTaskUUID())) {
            return projectTaskService.getTask(timeSheetEntryModel.getTaskUUID());
        } else if (!isNullOrEmpty(timeSheetEntryModel.getMilestoneUUID())) {
            return projectMilestoneService.getMilestone(timeSheetEntryModel.getMilestoneUUID());
        } else if (!isNullOrEmpty(timeSheetEntryModel.getPhaseUUID())) {
            return projectPhaseService.getPhase(timeSheetEntryModel.getPhaseUUID());
        } else {
            return project;

        }

    }
}
