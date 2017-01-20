package com.cashflow.project.frontend.controller.timesheet;

import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.accessroles.domain.accessrole.AccessRole;
import com.cashflow.accessroles.service.accessrole.AccessRoleRequestContext;
import com.cashflow.accessroles.service.accessrole.AccessRoleService;
import com.cashflow.accountpayables.domain.supplier.ContactPerson;
import com.cashflow.accountpayables.domain.supplier.Supplier;
import com.cashflow.accountpayables.service.api.supplier.SupplierService;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.milestone.ProjectMilestone;
import com.cashflow.project.domain.project.personnel.ProjectRole;
import com.cashflow.project.domain.project.personnel.TeamMemberCategory;
import com.cashflow.project.domain.project.phase.ProjectPhase;
import com.cashflow.project.domain.project.task.ProjectTask;
import com.cashflow.project.domain.project.timesheet.TimeSheet;
import com.cashflow.project.domain.project.timesheet.TimeSheetInfo;
import com.cashflow.project.domain.project.timesheet.WorkTime;
import com.cashflow.project.frontend.controller.model.TimeSheetEntryModel;
import com.cashflow.project.frontend.controller.model.TimeSheetWeeklyEntryModel;
import com.cashflow.useraccount.domain.businessuser.Employee;
import com.cashflow.useraccount.service.api.EmployeeService;
import com.anosym.profiler.Profile;
import com.google.common.base.Throwables;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 *
 * 
 * @since Dec 15, 2016, 4:32:50 PM
 */
@Dependent
public class TimesheetEntryModelConvertor implements Serializable {

    private static final long serialVersionUID = 978996158532237323L;

    @Inject
    private EmployeeService employeeService;

    @Inject
    private SupplierService supplierService;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private AccessRoleService accessRoleService;

    private Map<String, ProjectLevel<?>> levels;

    @Profile
    public void setToModel(@Nullable final TimeSheet time,
                           @Nonnull final TimeSheetEntryModel timeSheetEntryModel) {

        if (time == null) {
            return;
        }
        timeSheetEntryModel.setTimeSheetUUID(time.getUuid());
        timeSheetEntryModel.setTimeSheetID(time.getTimeSheetID());
        timeSheetEntryModel.setTimesheetStatus(time.getStatus());
        timeSheetEntryModel.setApprovalDate(time.getApprovalDate());
        timeSheetEntryModel.setPhaseName("All");
        timeSheetEntryModel.setMileStoneName("All");
        timeSheetEntryModel.setTaskname("All");
        levels = new HashMap<>();
        setProjectLevels(time.getProjectLevel());
        levels
                .entrySet()
                .forEach(level -> {
                    if (level.getValue() instanceof ProjectPhase) {
                        timeSheetEntryModel.setPhaseName(((ProjectPhase) level.getValue()).getName());
                        timeSheetEntryModel.setPhaseUUID(((ProjectPhase) level.getValue()).getUuid());
                    } else if (level.getValue() instanceof ProjectMilestone) {
                        timeSheetEntryModel.setMileStoneName(((ProjectMilestone) level.getValue())
                                .getName());
                        timeSheetEntryModel.setMilestoneUUID(((ProjectMilestone) level.getValue())
                                .getUuid());
                    } else if (level.getValue() instanceof ProjectTask) {
                        timeSheetEntryModel.setTaskUUID(((ProjectTask) level.getValue()).getUuid());
                        timeSheetEntryModel.setTaskname(((ProjectTask) level.getValue()).getName());
                        timeSheetEntryModel.setTaskID(((ProjectTask) level.getValue()).getId());
                    }
                });
        final Future<Map<String, AccessRole>> accesRoleRequest = asynchronousService
                .execute(() -> accessRoleService
                .getAccessRoles(AccessRoleRequestContext
                        .builder()
                        .applicationCode("AZPM")
                        .build())
                .stream()
                .collect((Collectors.toMap((role_) -> role_.getUuid(), Function.identity()))));

        if (null != time.getTeamMember()) {
            final Future<Employee> employeeRequest = asynchronousService
                    .execute(() -> employeeService
                    .findEmployee(time.getTeamMember().getEmployeeUUID()));
            timeSheetEntryModel.setTeamMemberCategory(TeamMemberCategory.EMPLOYEE);
            timeSheetEntryModel.setMemberID(time.getTeamMember().getMemberId());
            final ProjectRole role = time.getTeamMember()
                    .getProjectRoles()
                    .stream()
                    .findFirst()
                    .orElse(null);
            try {
                final Map<String, AccessRole> accessRoles = accesRoleRequest.get();
                timeSheetEntryModel.setProjectRole((null != role && null != accessRoles.get(role.getAccessScopeUUID()))
                        ? accessRoles.get(role.getAccessScopeUUID()).getRoleName() : null);
                final Employee employee = employeeRequest.get();
                timeSheetEntryModel.setMemberName(null != employee ? employee
                        .getName() : null);
                timeSheetEntryModel.setMember(time.getTeamMember().getUuid());

            } catch (InterruptedException | ExecutionException ex) {
                throw Throwables.propagate(ex);
            }

        } else {
            final Future<Supplier> supplierRequest = asynchronousService
                    .execute(() -> supplierService
                    .findSupplier(time.getSubContractor().getSubContractorUUID()));
            timeSheetEntryModel.setTeamMemberCategory(TeamMemberCategory.SUB_CONTRACTOR);
            timeSheetEntryModel.setMemberID(time.getSubContractor().getMemberId());
            final ProjectRole role = time.getSubContractor()
                    .getProjectRoles()
                    .stream()
                    .findFirst()
                    .orElse(null);
            try {
                final Map<String, AccessRole> accessRoles = accesRoleRequest.get();
                timeSheetEntryModel.setProjectRole((null != role && null != accessRoles.get(role.getAccessScopeUUID()))
                        ? accessRoles.get(role.getAccessScopeUUID()).getRoleName() : null);
                final Supplier supplier = supplierRequest.get();
                final List<ContactPerson> contactPersons = Stream
                        .concat(Stream.of(supplier.getContactPerson()),
                                supplier.getOtherContactPersons().stream())
                        .collect(Collectors.toList());
                timeSheetEntryModel.setSupplierName(supplier.getName());
                timeSheetEntryModel.setSupplierType(supplier.isSubContractor() ? "Sub-Contractor" : "Consultant");
                timeSheetEntryModel.setMemberName(contactPersons
                        .stream()
                        .filter((cp) -> cp.getUuid().equals(time.getSubContractor().getMemberName()))
                        .findFirst().get().getName());
                timeSheetEntryModel.setMember(time.getSubContractor().getUuid());
            } catch (InterruptedException | ExecutionException ex) {
                throw Throwables.propagate(ex);
            }
        }
        setEntryHoursDetails(time, timeSheetEntryModel);
    }

    private void setProjectLevels(@Nullable final ProjectLevel<?> level) {
        if (level == null) {
            return;
        }
        levels.put(level.getUuid(), level);
        setProjectLevels(level.getParentLevel());
    }

    private void setEntryHoursDetails(@Nonnull final TimeSheet report,
                                      @Nonnull final TimeSheetEntryModel timeSheetEntryModel) {
        final List<TimeSheetWeeklyEntryModel> weeklyEntries = report.getTimeSheetInfos()
                .stream()
                .map((model) -> prepareModel(model))
                .collect(Collectors.toList());
        timeSheetEntryModel.setWeeklyEntries(weeklyEntries);

        WorkTime totalRegular = new WorkTime();
        WorkTime totalPtoHours = new WorkTime();
        WorkTime totalOverTimeHours = new WorkTime();

        final WorkTime regularhours = report.getTimeSheetInfos()
                .stream()
                .map(TimeSheetInfo::getRegularTime)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());

        totalRegular = totalRegular.add(regularhours);

        timeSheetEntryModel.setTotalRegularTime(totalRegular);

        final WorkTime ptoHours = report.getTimeSheetInfos()
                .stream()
                .map(TimeSheetInfo::getPtoTime)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());
        totalPtoHours = totalPtoHours.add(ptoHours);

        timeSheetEntryModel.setTotalPtoTime(totalPtoHours);

        final WorkTime overTimeHours = report.getTimeSheetInfos()
                .stream()
                .map(TimeSheetInfo::getOverTime)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());
        totalOverTimeHours = totalOverTimeHours.add(overTimeHours);

        timeSheetEntryModel.setTotalOverTime(totalOverTimeHours);

        timeSheetEntryModel.setFinalTotal(totalRegular.add(totalPtoHours).add(totalOverTimeHours));
    }

    @Nonnull
    private TimeSheetWeeklyEntryModel prepareModel(@Nonnull final TimeSheetInfo timeSheetInfo) {
        final TimeSheetWeeklyEntryModel model = new TimeSheetWeeklyEntryModel();

        model.setBillableType(timeSheetInfo.getBillableType());
        model.setPtOCategory(timeSheetInfo.getPtoCategory());
        model.setTimeSheetDate(timeSheetInfo.getTimeSheetDate());

        model.setRegularTime(new BigDecimal(timeSheetInfo.getRegularTime().toString()));
        model.setOverTime(new BigDecimal(timeSheetInfo.getOverTime().toString()));
        model.setPtoTime(new BigDecimal(timeSheetInfo.getPtoTime().toString()));

        model.setRegularWorkTime(timeSheetInfo.getRegularTime());
        model.setOverTimeWorkTime(timeSheetInfo.getOverTime());
        model.setPtoWorkTime(timeSheetInfo.getPtoTime());

        model.setTotal(timeSheetInfo.getRegularTime()
                .add(timeSheetInfo.getOverTime())
                .add(timeSheetInfo.getPtoTime()));

        return model;
    }

}
