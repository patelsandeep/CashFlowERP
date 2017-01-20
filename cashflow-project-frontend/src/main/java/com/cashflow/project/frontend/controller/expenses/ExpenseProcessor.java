package com.cashflow.project.frontend.controller.expenses;

import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.datarepository.service.DataRepository;
import com.cashflow.datarepository.service.DataRepositoryService;
import com.cashflow.project.api.expense.ExpenseReportService;
import com.cashflow.project.api.milestone.ProjectMilestoneService;
import com.cashflow.project.api.people.ProjectSubContractorService;
import com.cashflow.project.api.people.ProjectTeamMemberService;
import com.cashflow.project.api.phase.ProjectPhaseService;
import com.cashflow.project.api.task.ProjectTaskService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.expense.ExpenseDetail;
import com.cashflow.project.domain.project.expense.ExpenseReport;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.personnel.SubContractor;
import com.cashflow.project.domain.project.personnel.TeamMember;
import com.cashflow.project.domain.project.personnel.TeamMemberCategory;
import com.cashflow.project.domain.project.timesheet.ExpenseStatus;
import com.cashflow.project.frontend.controller.model.ExpenseReportDataModel;
import com.cashflow.project.frontend.controller.model.ExpenseReportModel;
import com.anosym.common.Amount;
import com.google.common.base.Throwables;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import static com.cashflow.project.frontend.controller.model.DataRepositoryName.REPOSITORY_NAME;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 *
 * 
 * @since 7 Dec, 2016, 12:17:11 PM
 */
@Dependent
public class ExpenseProcessor implements Serializable {

    private static final long serialVersionUID = -3088648832248012233L;

    @Inject
    private ProjectTaskService projectTaskService;

    @Inject
    private ProjectMilestoneService projectMilestoneService;

    @Inject
    private ProjectPhaseService projectPhaseService;

    @Inject
    private ExpenseReportService expenseReportService;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private ProjectTeamMemberService projectTeamMemberService;

    @Inject
    private ProjectSubContractorService projectSubContractorService;

    @Inject
    @DataRepository(REPOSITORY_NAME)
    private DataRepositoryService dataRepositoryService;

    public void saveExpenses(@Nonnull final ExpenseReportModel expenseReportModel,
                             @Nonnull final Project project,
                             @Nonnull final String action) {
        ExpenseReport expenseReport;
        if (isNullOrEmpty(expenseReportModel.getExpenseReportUUID())) {
            expenseReport = new ExpenseReport();
            final ProjectLevel level = getProjectLevel(expenseReportModel, project);
            expenseReport.setProjectLevel(level);
        } else {
            final Future<ExpenseReport> expenseReportRequest = asynchronousService
                    .execute(() -> expenseReportService
                    .getExpenseReport(expenseReportModel.getExpenseReportUUID()));
            final Future<ProjectLevel> levelRequest = asynchronousService
                    .execute(() -> getProjectLevel(expenseReportModel, project));
            try {
                expenseReport = expenseReportRequest.get();
                final ProjectLevel level = levelRequest.get();
                expenseReport.setProjectLevel(level);
            } catch (InterruptedException | ExecutionException ex) {
                throw Throwables.propagate(ex);
            }
        }
        if (action.equals("submit")) {
            expenseReport.setStatus(ExpenseStatus.SUBMITTED);
        } else {
            expenseReport.setStatus(ExpenseStatus.SAVED);
        }
        expenseReport.setBusinessUnitUUID(project.getBusinessUnitUUID());
        expenseReport.setDepartmentUUID(project.getDepartmentUUID());
        expenseReport.setExpenseReportId(expenseReportModel.getExpenseReportId());
        expenseReport.setWeekStartDate(expenseReportModel.getWeekStartDate());
        expenseReport.setWeekEndDate(expenseReportModel.getWeekEndingDate());
        if (expenseReportModel.getMemberCategory() == TeamMemberCategory.EMPLOYEE) {
            final TeamMember teamMember = projectTeamMemberService.getTeamMember(expenseReportModel.getMember());
            expenseReport.setTeamMember(teamMember);
        }
        if (expenseReportModel.getMemberCategory() == TeamMemberCategory.SUB_CONTRACTOR) {
            final SubContractor subContractor = projectSubContractorService.getSubContractor(expenseReportModel
                    .getMember());
            expenseReport.setSubContractor(subContractor);
        }

        final List<ExpenseDetail> details = new ArrayList<>();
        expenseReportModel
                .getReportDataModels()
                .stream()
                .forEach(p -> details.add(toDomainExpenseDetails(p, expenseReportModel, expenseReport)));

        expenseReport.getExpenseDetails().clear();
        expenseReport.getExpenseDetails().addAll(details);

        expenseReportService.save(expenseReport);

    }

    private ProjectLevel getProjectLevel(@Nonnull final ExpenseReportModel expenseReportModel,
                                         @Nonnull final Project project) {
        if (!isNullOrEmpty(expenseReportModel.getTask())) {
            return projectTaskService.getTask(expenseReportModel.getTask());
        } else if (!isNullOrEmpty(expenseReportModel.getMilestone())) {
            return projectMilestoneService.getMilestone(expenseReportModel.getMilestone());
        } else if (!isNullOrEmpty(expenseReportModel.getPhase())) {
            return projectPhaseService.getPhase(expenseReportModel.getPhase());
        } else {
            return project;

        }

    }

    @Nonnull
    private ExpenseDetail toDomainExpenseDetails(final ExpenseReportDataModel dataModel,
                                                 @Nonnull final ExpenseReportModel expenseReportModel,
                                                 @Nullable final ExpenseReport expenseReport) {
        ExpenseDetail newExpense = null;
        if (expenseReport != null) {
            newExpense = expenseReport.getExpenseDetails()
                    .stream()
                    .filter(
                            p -> p != null && p.getUuid().equals(dataModel.getUuid()))
                    .findFirst()
                    .orElse(new ExpenseDetail(dataModel.getExpenseType(),
                                              new Amount(expenseReportModel.getExpenseCurrency(), dataModel
                                                         .getExpenseAmount()),
                                              dataModel.getExpenseDate(),
                                              dataModel.getTaxId(),
                                              dataModel.getTaxId2(),
                                              dataModel.getTotalAmount(),
                                              dataModel.getBillable(),
                                              dataModel.getProjectFileUrls())
                    );

        }
        dataModel.getProjectFileUrls()
                .stream()
                .filter((url) -> (dataModel.getInputStreams().containsKey(url.getUrl())))
                .forEach((url) -> {
                    dataRepositoryService.set(dataModel.getInputStreams().get(url.getUrl()),
                                              url.getUrl(), url
                                              .getContentType(),
                                              url.getContentSize());
                });
        return newExpense;
    }
}
