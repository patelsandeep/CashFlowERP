package com.cashflow.project.frontend.controller.timesheet;

import com.cashflow.accountpayables.domain.supplier.ContactPerson;
import com.cashflow.accountpayables.domain.supplier.Supplier;
import com.cashflow.accountpayables.service.api.supplier.SupplierService;
import com.cashflow.accountreceivables.domain.customer.Customer;
import com.cashflow.accountreceivables.service.api.customer.CustomerService;
import com.cashflow.project.api.project.ProjectService;
import com.cashflow.project.api.timesheet.TimeSheetService;
import com.cashflow.project.config.projectrole.ProjectProfessionalTypeMappingConfigurationService;
import com.cashflow.project.domain.expenseinformation.TimesheetContext;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.ProjectType;
import com.cashflow.project.domain.project.expense.BillableType;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.milestone.ProjectMilestone;
import com.cashflow.project.domain.project.personnel.SubContractor;
import com.cashflow.project.domain.project.personnel.TeamMember;
import com.cashflow.project.domain.project.phase.ProjectPhase;
import com.cashflow.project.domain.project.task.ProjectTask;
import com.cashflow.project.domain.project.timesheet.TimeSheet;
import com.cashflow.project.domain.project.timesheet.WorkTime;
import com.cashflow.project.domain.util.menuoption.MenuOption;
import com.cashflow.project.frontend.controller.model.ModelViewScopedController;
import com.cashflow.project.frontend.controller.model.TimeSheetInfoModel;
import com.cashflow.project.frontend.controller.pagination.PaginationModel;
import com.cashflow.project.frontend.service.urlbuilder.StaticLinkUrlBuilder;
import com.cashflow.project.frontend.service.urlbuilder.UrlContext;
import com.cashflow.useraccount.domain.businessunit.Department;
import com.cashflow.useraccount.domain.businessuser.Employee;
import com.cashflow.useraccount.domain.businessuser.professional.Professional;
import com.cashflow.useraccount.service.api.EmployeeService;
import com.cashflow.useraccount.service.api.businessunit.department.DepartmentService;
import com.cashflow.useraccount.service.api.professional.ProfessionalService;
import com.anosym.profiler.Profile;
import com.anosym.urlbuilder.QueryParameter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;

import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_MENU;

/**
 *
 * 
 * @since 5 Jan, 2017, 4:07:53 PM
 */
@ModelViewScopedController
public class TimeSheetSummaryController extends PaginationModel implements Serializable {

    private static final long serialVersionUID = 406951349299825741L;

    private static final String PROJECT_TIMESHEET_SUMMARY_URL = "/timesheet/timesheet-summary.xhtml";

    @Inject
    private TimeSheetService timeSheetService;

    @Inject
    private EmployeeService employeeService;

    @Inject
    private ProfessionalService professionalService;

    @Inject
    private StaticLinkUrlBuilder staticLinkUrlBuilder;

    @Inject
    private CustomerService customerService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private SupplierService supplierService;

    @Inject
    private ProjectService projectService;

    @Inject
    private ProjectProfessionalTypeMappingConfigurationService projectProfessionalTypeMappingConfigurationService;

    @Getter
    @Setter
    private List<TimeSheet> timeSheets;

    @Getter
    @Setter
    @Nullable
    private Map<String, List<TimeSheetInfoModel>> pendingTimeSheetInfoModel;

    private final List<Calendar> dates = new ArrayList<>();

    private Map<String, ProjectLevel<?>> levels;

    @PostConstruct
    public void loadTimesheets() {
        getTimeSheets();
    }

    @Profile
    public void getTimeSheets() {

        final TimesheetContext timesheetContext = TimesheetContext
                .builder()
                .offset(getPage() * getLimit())
                .limit(getLimit())
                .build();
        timeSheets = timeSheetService.getTimesheets(timesheetContext);
        pendingTimeSheetInfoModel = new HashMap<>();
        timeSheets.forEach((timeSheet) -> {
            timeSheet.getTimeSheetInfos()
                    .forEach((timeSheetInfo) -> {
                        final TimeSheetInfoModel timeSheetInfoModel = new TimeSheetInfoModel();
                        final List<TimeSheetInfoModel> timeInfos = new ArrayList<>();
                        dates.add(timeSheetInfo.getTimeSheetDate());
                        timeSheetInfoModel.setPhaseId("All");
                        timeSheetInfoModel.setMilestoneId("All");
                        timeSheetInfoModel.setTaskId("All");
                        levels = new HashMap<>();
                        setProjectLevels(timeSheet.getProjectLevel());
                        levels
                                .entrySet()
                                .forEach(level -> {
                                    if (level.getValue() instanceof Project) {
                                        timeSheetInfoModel.setProjectUUID(((Project) level.getValue()).getUuid());
                                        timeSheetInfoModel.setProjectId(((Project) level.getValue()).getId());
                                        timeSheetInfoModel.setProjectName(((Project) level.getValue()).getName());
                                        final Project project = projectService.getProject(((Project) level.getValue())
                                                .getUuid());
                                        setCustomerName(project, timeSheetInfoModel);
                                    }
                                    if (level.getValue() instanceof ProjectPhase) {
                                        timeSheetInfoModel.setPhaseName(((ProjectPhase) level.getValue()).getName());
                                        timeSheetInfoModel.setPhaseId(((ProjectPhase) level.getValue()).getId());
                                    }
                                    if (level.getValue() instanceof ProjectMilestone) {
                                        timeSheetInfoModel.setMilestoneName(((ProjectMilestone) level.getValue())
                                                .getName());
                                        timeSheetInfoModel.setMilestoneId(((ProjectMilestone) level.getValue()).getId());
                                    }
                                    if (level.getValue() instanceof ProjectTask) {
                                        timeSheetInfoModel.setTaskName(((ProjectTask) level.getValue()).getName());
                                        timeSheetInfoModel.setTaskId(((ProjectTask) level.getValue()).getId());
                                    }
                                });
                        setMemberValue(timeSheet, timeSheetInfoModel);
                        timeSheetInfoModel.setTimesheetStatus(timeSheet.getStatus());
                        timeSheetInfoModel.setTimeSheetInfoDate(timeSheetInfo.getTimeSheetDate());
                        timeSheetInfoModel.setTimesheetDate(Collections.min(dates));
                        timeSheetInfoModel.setToTimesheetDate(Collections.max(dates));
                        timeSheetInfoModel.setTimesheetId(timeSheet.getTimeSheetID());
                        timeSheetInfoModel.setTimeSheetInfoDate(timeSheetInfo.getTimeSheetDate());
                        timeSheetInfoModel.setBillableType(timeSheetInfo.getBillableType());
                        timeSheetInfoModel.setRegularTime(timeSheetInfo.getRegularTime());
                        timeSheetInfoModel.setPtoTime(timeSheetInfo.getPtoTime());
                        timeSheetInfoModel.setOverTime(timeSheetInfo.getOverTime());
                        if (pendingTimeSheetInfoModel.containsKey(timeSheet.getUuid())) {
                            final List<TimeSheetInfoModel> timeInfoModel = pendingTimeSheetInfoModel.get(timeSheet
                                    .getUuid());
                            timeInfoModel.add(timeSheetInfoModel);
                            pendingTimeSheetInfoModel.put(timeSheet.getUuid(), timeInfoModel);
                        } else {

                            timeInfos.add(timeSheetInfoModel);
                            pendingTimeSheetInfoModel.put(timeSheet.getUuid(), timeInfos);
                        }
                    });
        });
        setTimeSheetCount();
    }

    @Nonnull
    public String redirectTimeSheetSummary() {
        final QueryParameter parameter = QueryParameter
                .<String>builder()
                .parameter(SELECTED_MENU)
                .parameterValue(MenuOption.TIMESHEETS.name())
                .build();

        final UrlContext.Builder context = UrlContext
                .builder()
                .additionalParameter(parameter)
                .path(PROJECT_TIMESHEET_SUMMARY_URL)
                .forceFacesRedirect(true);
        return staticLinkUrlBuilder.buildURL(context.build());
    }

    @Nonnull
    public WorkTime calculateBillableHours(@Nonnull final String uuid) {
        WorkTime totalBillableHours = new WorkTime();
        WorkTime totalBillableRegularHours = new WorkTime();
        WorkTime totalBillablePtoHours = new WorkTime();
        WorkTime totalBillableOverTimeHours = new WorkTime();
        final List<TimeSheetInfoModel> timeSheetInfos = pendingTimeSheetInfoModel.get(uuid);
        final WorkTime regularhours = timeSheetInfos
                .stream()
                .filter((time) -> time.getBillableType() == BillableType.YES)
                .map(TimeSheetInfoModel::getRegularTime)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());
        totalBillableRegularHours = totalBillableRegularHours.add(regularhours);

        final WorkTime ptoHours = timeSheetInfos
                .stream()
                .filter((time) -> time.getBillableType() == BillableType.YES)
                .map(TimeSheetInfoModel::getPtoTime)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());

        totalBillablePtoHours = totalBillablePtoHours.add(ptoHours);

        final WorkTime overTimeHours = timeSheetInfos
                .stream()
                .filter((time) -> time.getBillableType() == BillableType.YES)
                .map(TimeSheetInfoModel::getOverTime)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());
        totalBillableOverTimeHours = totalBillableOverTimeHours.add(overTimeHours);
        totalBillableHours = totalBillableOverTimeHours.add(totalBillablePtoHours).add(totalBillableRegularHours);

        return totalBillableHours;
    }

    @Nonnull
    public WorkTime calculateNonBillableHours(@Nonnull final String uuid) {
        WorkTime totalNonBillableHours = new WorkTime();
        WorkTime totalNonBillableRegularHours = new WorkTime();
        WorkTime totalNonBillablePtoHours = new WorkTime();
        WorkTime totalNonBillableOverTimeHours = new WorkTime();
        final List<TimeSheetInfoModel> timeSheetInfos = pendingTimeSheetInfoModel.get(uuid);
        final WorkTime regularhours = timeSheetInfos
                .stream()
                .filter((time) -> time.getBillableType() == BillableType.NO)
                .map(TimeSheetInfoModel::getRegularTime)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());
        totalNonBillableRegularHours = totalNonBillableRegularHours.add(regularhours);

        final WorkTime ptoHours = timeSheetInfos
                .stream()
                .filter((time) -> time.getBillableType() == BillableType.NO)
                .map(TimeSheetInfoModel::getPtoTime)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());

        totalNonBillablePtoHours = totalNonBillablePtoHours.add(ptoHours);

        final WorkTime overTimeHours = timeSheetInfos
                .stream()
                .filter((time) -> time.getBillableType() == BillableType.NO)
                .map(TimeSheetInfoModel::getOverTime)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new WorkTime());
        totalNonBillableOverTimeHours = totalNonBillableOverTimeHours.add(overTimeHours);
        totalNonBillableHours = totalNonBillableOverTimeHours.add(totalNonBillablePtoHours).add(
                totalNonBillableRegularHours);

        return totalNonBillableHours;
    }

    @Nonnull
    public WorkTime calculateTotalHours(@Nonnull final String uuid) {
        final WorkTime totalNonBillableHours = calculateNonBillableHours(uuid);
        final WorkTime totalBillableHours = calculateBillableHours(uuid);
        WorkTime totalHours = totalBillableHours.add(totalNonBillableHours);
        return totalHours;
    }

    @Override
    public void loadData() {
        loadTimesheets();
    }

    private void setTimeSheetCount() {
        final TimesheetContext timesheetContext = TimesheetContext
                .builder()
                .build();
        final List<TimeSheet> timeSheets = timeSheetService.getTimesheets(timesheetContext);
        this.setCount(timeSheets.size());
    }

    private void setCustomerName(@Nonnull final Project project,
                                 @Nonnull final TimeSheetInfoModel timeSheetInfoModel) {

        if (project.getProjectType() == ProjectType.CUSTOMER_PROJECT) {
            final Customer customer = customerService.getCustomer(project.getCustomerUUID());
            timeSheetInfoModel.setCustomerName(customer.getName());
        } else if (project.getProjectType() == ProjectType.INTERNAL_PROJECT) {
            final Department department = departmentService.findDepartment(project.getCustomerUUID());
            timeSheetInfoModel.setCustomerName(department.getName());
        }
    }

    @Nullable
    private String setSubContractor(@Nonnull final SubContractor subContractor) {
        final Supplier supplier = supplierService.findSupplier(subContractor.getSubContractorUUID());
        if (supplier.getUuid().equals(subContractor.getSubContractorUUID())) {
            final List<ContactPerson> contactPersons = supplier.getOtherContactPersons();
            contactPersons.add(supplier.getContactPerson());
            ContactPerson contact = contactPersons.stream()
                    .filter((cp) -> (cp.getUuid().equals(subContractor.getMemberName())))
                    .findFirst()
                    .orElse(null);

            return contact.getName();
        }

        return null;
    }

    @Nullable
    private String setTeamMemberName(@Nonnull final TeamMember teamMember) {
        final Employee employee = employeeService.findEmployee(teamMember.getEmployeeUUID());
        if (null != employee) {
            return employee.getName();
        } else {
            final List<Professional> professionals = professionalService.findProfessionals(
                    projectProfessionalTypeMappingConfigurationService
                            .getTypeMappingsForProfessionals());

            Professional proffessional = professionals.stream()
                    .filter((professional) -> (professional.getUuid().equals(teamMember.getEmployeeUUID())))
                    .findFirst()
                    .orElse(null);
            return proffessional.getName();
        }
    }

    private void setMemberValue(@Nonnull final TimeSheet timeSheet,
                                @Nonnull final TimeSheetInfoModel timeSheetInfoModel) {

        if (null != timeSheet.getTeamMember()) {
            timeSheetInfoModel.setMemberName(setTeamMemberName(timeSheet.getTeamMember()));
            timeSheetInfoModel.setMemberId(timeSheet.getTeamMember().getMemberId());
        } else {
            final SubContractor subContractor = timeSheet.getSubContractor();
            timeSheetInfoModel.setMemberName(setSubContractor(subContractor));
            timeSheetInfoModel.setMemberId(timeSheet
                    .getSubContractor().getMemberId());
        }
    }

    private void setProjectLevels(@Nullable final ProjectLevel<?> level) {
        if (level == null) {
            return;
        }
        levels.put(level.getUuid(), level);
        setProjectLevels(level.getParentLevel());
    }
}
