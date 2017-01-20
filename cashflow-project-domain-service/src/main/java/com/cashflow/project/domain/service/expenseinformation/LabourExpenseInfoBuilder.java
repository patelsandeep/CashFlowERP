package com.cashflow.project.domain.service.expenseinformation;

import com.cashflow.accessroles.domain.accessrole.AccessRole;
import com.cashflow.accountpayables.domain.supplier.ContactPerson;
import com.cashflow.accountpayables.domain.supplier.Supplier;
import com.cashflow.project.api.expenseinformation.LabourExpenseInformation;
import com.cashflow.project.api.expenseinformation.LabourExpenseInformation.LabourExpenseInformationBuilder;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.expense.ExpenseDetail;
import com.cashflow.project.domain.project.expense.ExpenseReport;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.milestone.ProjectMilestone;
import com.cashflow.project.domain.project.personnel.ProjectRole;
import com.cashflow.project.domain.project.personnel.TeamMemberCategory;
import com.cashflow.project.domain.project.phase.ProjectPhase;
import com.cashflow.project.domain.project.task.ProjectTask;
import com.cashflow.project.domain.project.timesheet.TimeSheet;
import com.cashflow.project.domain.project.timesheet.TimeSheetInfo;
import com.cashflow.project.domain.project.timesheet.WorkTime;
import com.cashflow.useraccount.domain.businessuser.EmployeeInformation;
import com.anosym.common.Amount;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.Dependent;

/**
 *
 * 
 * @since Dec 14, 2016, 3:41:52 PM
 */
@Dependent
public class LabourExpenseInfoBuilder implements Serializable {

    private static final long serialVersionUID = 3560214572041117489L;

    private Map<String, ProjectLevel<?>> levels;

    @Nonnull
    public LabourExpenseInformationBuilder preapreInfo(@Nonnull final List<ExpenseReport> reports,
                                                       @Nonnull final Map<String, EmployeeInformation> employees,
                                                       @Nonnull final Map<String, Supplier> suppliers,
                                                       @Nonnull final Map<String, AccessRole> accessRoles) {
        final LabourExpenseInformationBuilder info
                = LabourExpenseInformation.builder();
        Amount totalAmount = null;
        for (ExpenseReport report : reports) {
            info.phaseId("All");
            info.milestoneId("All");
            info.taskId("All");
            levels = new HashMap<>();
            levels = setProjectLevels(report.getProjectLevel());
            levels
                    .entrySet()
                    .forEach(level -> {
                        if (level.getValue() instanceof Project) {
                            info.projectId(((Project) level.getValue()).getId());
                        } else if (level.getValue() instanceof ProjectPhase) {
                            info.phaseId(((ProjectPhase) level.getValue()).getId());
                        } else if (level.getValue() instanceof ProjectMilestone) {
                            info.milestoneId(((ProjectMilestone) level.getValue()).getId());
                        } else if (level.getValue() instanceof ProjectTask) {
                            info.taskId(((ProjectTask) level.getValue()).getId());
                        }
                    });
            final Amount expenseAmount = report.getExpenseDetails()
                    .stream()
                    .map(ExpenseDetail::getTotalExpense)
                    .reduce((am1, am2) -> am1.add(am2)).get();
            if (null != totalAmount && null != expenseAmount) {
                totalAmount = totalAmount.add(expenseAmount);
            } else {
                totalAmount = expenseAmount;
            }
            info.expenseReportAmount(totalAmount);
            if (null != report.getTeamMember()) {
                info.memberId(report.getTeamMember().getMemberId());
                info.teamMemberCategory(TeamMemberCategory.EMPLOYEE);
                if (employees.containsKey(report.getTeamMember().getEmployeeUUID())) {
                    info.memberName(employees.get(report.getTeamMember().getEmployeeUUID()).getEmployee()
                            .getName());
                    info.systemRole(employees.get(report.getTeamMember().getEmployeeUUID())
                            .getEmployee().getFunctionalRoles().stream()
                            .findFirst().orElse(null));
                    final ProjectRole role = report.getTeamMember()
                            .getProjectRoles()
                            .stream()
                            .findFirst()
                            .orElse(null);
                    info.projectRole((null != role && null != accessRoles.get(role.getAccessScopeUUID()))
                            ? accessRoles.get(role.getAccessScopeUUID()).getRoleName() : null);
                }
            } else {
                info.teamMemberCategory(TeamMemberCategory.SUB_CONTRACTOR);
                info.memberId(report.getSubContractor().getMemberId());
                if (suppliers.containsKey(report.getSubContractor().getSubContractorUUID())) {
                    final Supplier supplier = suppliers.get(report.getSubContractor().getSubContractorUUID());
                    final List<ContactPerson> contactPersons = Stream
                            .concat(Stream.of(supplier.getContactPerson()),
                                    supplier.getOtherContactPersons().stream())
                            .collect(Collectors.toList());
                    final ProjectRole role = report.getSubContractor()
                            .getProjectRoles()
                            .stream()
                            .findFirst()
                            .orElse(null);
                    info.projectRole((null != role && null != accessRoles.get(role.getAccessScopeUUID()))
                            ? accessRoles.get(role.getAccessScopeUUID()).getRoleName() : null);
                    info.memberName(contactPersons
                            .stream()
                            .filter((cp) -> cp.getUuid().equals(report.getSubContractor().getMemberName()))
                            .findFirst().get().getName());
                }
            }
        }
        return info;
    }

    @Nonnull
    public LabourExpenseInformationBuilder preapreOnlyTimeHours(@Nonnull final List<TimeSheet> timeSheets,
                                                                @Nonnull final LabourExpenseInformationBuilder info) {
        WorkTime totalRegular = new WorkTime();
        WorkTime totalPtoHours = new WorkTime();
        WorkTime totalOverTimeHours = new WorkTime();
        for (TimeSheet report : timeSheets) {
            final WorkTime regularhours = report.getTimeSheetInfos()
                    .stream()
                    .map(TimeSheetInfo::getRegularTime)
                    .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());
            totalRegular = totalRegular.add(regularhours);
            info.regularHours(totalRegular);
            final WorkTime ptoHours = report.getTimeSheetInfos()
                    .stream()
                    .map(TimeSheetInfo::getPtoTime)
                    .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());
            totalPtoHours = totalPtoHours.add(ptoHours);
            info.ptoHours(totalPtoHours);
            final WorkTime overTimeHours = report.getTimeSheetInfos()
                    .stream()
                    .map(TimeSheetInfo::getOverTime)
                    .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());
            totalOverTimeHours = totalOverTimeHours.add(overTimeHours);
            info.overTimeHours(totalOverTimeHours);
            info.totalHours(totalRegular.add(totalPtoHours).add(totalOverTimeHours));
        }
        return info;
    }

    @Nonnull
    public LabourExpenseInformationBuilder prepareAllTimeInfo(@Nonnull final List<TimeSheet> timeSheets,
                                                              @Nonnull final Map<String, EmployeeInformation> employees,
                                                              @Nonnull final Map<String, Supplier> suppliers,
                                                              @Nonnull final Map<String, AccessRole> accessRoles) {
        LabourExpenseInformationBuilder info = LabourExpenseInformation.builder();

        timeSheets
                .stream()
                .forEach(report -> {
                    info.phaseId("All");
                    info.milestoneId("All");
                    info.taskId("All");
                    levels = new HashMap<>();
                    levels = setProjectLevels(report.getProjectLevel());
                    levels
                            .entrySet()
                            .forEach(level -> {
                                if (level.getValue() instanceof Project) {
                                    info.projectId(((Project) level.getValue()).getId());
                                } else if (level.getValue() instanceof ProjectPhase) {
                                    info.phaseId(((ProjectPhase) level.getValue()).getId());
                                } else if (level.getValue() instanceof ProjectMilestone) {
                                    info.milestoneId(((ProjectMilestone) level.getValue()).getId());
                                } else if (level.getValue() instanceof ProjectTask) {
                                    info.taskId(((ProjectTask) level.getValue()).getId());
                                }
                            });

                    if (null != report.getTeamMember()) {
                        info.teamMemberCategory(TeamMemberCategory.EMPLOYEE);
                        info.memberId(report.getTeamMember().getMemberId());
                        if (employees.containsKey(report.getTeamMember().getEmployeeUUID())) {
                            info.memberName(employees.get(report.getTeamMember().getEmployeeUUID()).getEmployee()
                                    .getName());
                            info.systemRole(employees.get(report.getTeamMember().getEmployeeUUID())
                                    .getEmployee().getFunctionalRoles().stream()
                                    .findFirst().orElse(null));
                            info.department(null != employees.get(report.getTeamMember().getEmployeeUUID())
                                    .getDepartment() ? employees.get(report.getTeamMember().getEmployeeUUID())
                                            .getDepartment().getName() : null);
                            final ProjectRole role = report.getTeamMember()
                                    .getProjectRoles()
                                    .stream()
                                    .findFirst()
                                    .orElse(null);
                            info.projectRole((null != role && null != accessRoles.get(role.getAccessScopeUUID()))
                                    ? accessRoles.get(role.getAccessScopeUUID()).getRoleName() : null);
                        }
                    } else {
                        info.teamMemberCategory(TeamMemberCategory.SUB_CONTRACTOR);
                        info.memberId(report.getSubContractor().getMemberId());
                        if (suppliers.containsKey(report.getSubContractor().getSubContractorUUID())) {
                            final Supplier supplier = suppliers.get(report.getSubContractor().getSubContractorUUID());
                            final List<ContactPerson> contactPersons = Stream
                                    .concat(Stream.of(supplier.getContactPerson()),
                                            supplier.getOtherContactPersons().stream())
                                    .collect(Collectors.toList());
                            final ProjectRole role = report.getSubContractor()
                                    .getProjectRoles()
                                    .stream()
                                    .findFirst()
                                    .orElse(null);
                            info.projectRole((null != role && null != accessRoles.get(role.getAccessScopeUUID()))
                                    ? accessRoles.get(role.getAccessScopeUUID()).getRoleName() : null);
                            info.memberName(contactPersons
                                    .stream()
                                    .filter((cp) -> cp.getUuid().equals(report.getSubContractor().getMemberName()))
                                    .findFirst().get().getName());
                        }
                    }
                });
        preapreOnlyTimeHours(timeSheets, info);
        return info;
    }

    @Nonnull
    private Map<String, ProjectLevel<?>> setProjectLevels(@Nullable final ProjectLevel<?> level) {
        if (level == null) {
            return levels;
        }
        levels.put(level.getUuid(), level);
        setProjectLevels(level.getParentLevel());
        return levels;
    }

}
