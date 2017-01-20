package com.cashflow.project.frontend.controller.timesheet;

import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.access.authorization.uniqueid.IdContext;
import com.cashflow.access.authorization.uniqueid.UniqueIdGenerator;
import com.cashflow.accessroles.domain.accessrole.AccessRole;
import com.cashflow.accessroles.service.accessrole.AccessRoleRequestContext;
import com.cashflow.accessroles.service.accessrole.AccessRoleService;
import com.cashflow.accountpayables.domain.supplier.Supplier;
import com.cashflow.accountpayables.service.api.supplier.SupplierService;
import com.cashflow.accountreceivables.domain.customer.Customer;
import com.cashflow.accountreceivables.service.api.customer.CustomerService;
import com.cashflow.entitydomains.facade.context.Context;
import com.cashflow.frontend.support.cacheing.requestcached.RequestCached;
import com.cashflow.frontend.support.httpparameter.HttpParameter;
import com.cashflow.project.api.milestone.ProjectMilestoneService;
import com.cashflow.project.api.people.ProjectSubContractorService;
import com.cashflow.project.api.people.ProjectTeamMemberService;
import com.cashflow.project.api.phase.ProjectPhaseService;
import com.cashflow.project.api.project.ProjectService;
import com.cashflow.project.api.task.ProjectTaskService;
import com.cashflow.project.api.timesheet.TimeSheetService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.ProjectType;
import com.cashflow.project.domain.project.expense.BillableType;
import com.cashflow.project.domain.project.milestone.MilestoneContext;
import com.cashflow.project.domain.project.milestone.ProjectMilestone;
import com.cashflow.project.domain.project.personnel.ProjectRole;
import com.cashflow.project.domain.project.personnel.SubContractor;
import com.cashflow.project.domain.project.personnel.TeamMember;
import com.cashflow.project.domain.project.personnel.TeamMemberCategory;
import com.cashflow.project.domain.project.phase.PhaseContext;
import com.cashflow.project.domain.project.phase.ProjectPhase;
import com.cashflow.project.domain.project.task.ProjectTask;
import com.cashflow.project.domain.project.task.TaskContext;
import com.cashflow.project.domain.project.timesheet.PTOCategory;
import com.cashflow.project.domain.project.timesheet.TimeSheet;
import com.cashflow.project.domain.project.timesheet.WorkTime;
import com.cashflow.project.frontend.controller.expenses.MemberEntityModel;
import com.cashflow.project.frontend.controller.expenses.MemberEntityResolver;
import com.cashflow.project.frontend.controller.model.ModelViewScopedController;
import com.cashflow.project.frontend.controller.model.SuccessMessageModel;
import com.cashflow.project.frontend.controller.model.TimeSheetEntryModel;
import com.cashflow.project.frontend.controller.model.TimeSheetWeeklyEntryModel;
import com.cashflow.project.translation.expenses.BillableTypeTranslationService;
import com.cashflow.project.translation.timesheet.PTOCategoryTranslationService;
import com.cashflow.project.translation.timesheet.TimeEntryTranslationService;
import com.cashflow.useraccount.domain.businessunit.Department;
import com.cashflow.useraccount.service.api.businessunit.department.DepartmentService;
import com.anosym.profiler.Profile;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;

import static com.cashflow.frontend.support.errormessage.ErrorMessagePreconditions.assertNotNull;
import static com.cashflow.frontend.support.jsfutil.SelectItemHelper.getSelectItems;
import static com.cashflow.frontend.support.jsfutil.SuccessMessageHelper.addSuccessMessage;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_UUID;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 *
 * 
 * @since Dec 6, 2016, 12:00:38 PM
 */
@ModelViewScopedController
public class TimeSheetEntryController implements Serializable {

    private static final long serialVersionUID = -1474983904583947106L;

    @Inject
    @HttpParameter(SELECTED_PROJECT_UUID)
    private Instance<String> pUUID;

    @Getter
    @Setter
    private Project project;

    @Inject
    private ProjectService projectService;

    @Inject
    private TimeSheetService timeSheetService;

    @Inject
    private ProjectPhaseService projectPhaseService;

    @Inject
    @IdContext(forDomain = TimeSheet.class, prefix = "TS", length = 5)
    private UniqueIdGenerator uniqueIdGenerator;

    @Inject
    private TimeSheetEntryProcessor timeSheetEntryProcessor;

    @Inject
    private ProjectTeamMemberService projectTeamMemberService;

    @Inject
    private ProjectSubContractorService projectSubContractorService;

    @Inject
    private AccessRoleService accessRoleService;

    @Inject
    private ProjectMilestoneService milestoneService;

    @Inject
    private ProjectTaskService projectTaskService;

    @Inject
    private CustomerService customerService;

    @Getter
    @Setter
    private SuccessMessageModel successMessageModel;

    @Inject
    private TimeEntryTranslationService timeEntryTranslationService;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private SupplierService supplierService;

    @Inject
    private BillableTypeTranslationService billableTypeTranslationService;

    @Inject
    private PTOCategoryTranslationService pTOCategoryTranslationService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private TimeSheetHoursCalculator timeSheetHoursCalculator;

    @Inject
    private MemberEntityResolver memberEntityResolver;

    @Inject
    private TimesheetEntryModelConvertor timesheetEntryModelConvertor;

    @Getter
    @Setter
    private TimeSheetEntryModel timeSheetEntryModel;

    @PostConstruct
    void initTimesheet() {
        timeSheetEntryModel = new TimeSheetEntryModel();
        addEntry();
        final String pUUID_ = pUUID.get();
        if (!isNullOrEmpty(pUUID_)) {
            project = checkNotNull(projectService.getProject(pUUID_), "Failed to load project for uuid: %s",
                                   pUUID_);
            setProjectValues(project);
            setCurrentWeekDays();
        }
    }

    public void updateTimeSheetId() {
        timeSheetEntryModel.setTimeSheetID(uniqueIdGenerator.nextId());
        updateTask();
    }

    @Nonnull
    @RequestCached
    public List<ProjectPhase> getProjectPhases() {
        if (!isNullOrEmpty(pUUID.get())) {
            return projectPhaseService
                    .getProjectPhases(PhaseContext
                            .builder()
                            .projectUUID(pUUID.get())
                            .build());
        }
        return ImmutableList.of();
    }

    @Nonnull
    @RequestCached
    public List<ProjectMilestone> getMilestones() {
        if (!isNullOrEmpty(timeSheetEntryModel.getPhaseUUID())) {
            return milestoneService
                    .findByPhaseUUIDs(ImmutableList.of(timeSheetEntryModel.getPhaseUUID()));
        } else if (!isNullOrEmpty(pUUID.get())) {

            final MilestoneContext context = MilestoneContext.builder()
                    .parentLevelUUID(pUUID.get())
                    .build();

            return milestoneService
                    .getMilestones(context);

        }
        return ImmutableList.of();
    }

    @Nonnull
    @RequestCached
    public List<ProjectTask> getProjectTasks() {

        if (!isNullOrEmpty(timeSheetEntryModel.getMilestoneUUID())) {
            return projectTaskService
                    .findByMilestoneOrProjectUUIDs(ImmutableList.of(timeSheetEntryModel.getMilestoneUUID()));
        } else if (!isNullOrEmpty(pUUID.get())) {
            final TaskContext taskContext = TaskContext.builder()
                    .parentLevelUUID(pUUID.get())
                    .build();

            return projectTaskService
                    .getTasks(taskContext);
        }
        return ImmutableList.of();

    }

    @Nullable
    public String getTaskID() {
        if (isNullOrEmpty(timeSheetEntryModel.getTaskUUID())) {
            return null;
        }
        final ProjectTask task = projectTaskService
                .getTask(timeSheetEntryModel.getTaskUUID());
        if (null == task) {
            return null;
        }
        return task.getId();
    }

    @Nonnull
    @RequestCached
    public List<MemberEntityModel> getProjectMembers() {
        return memberEntityResolver.loadMemberValues(pUUID.get());
    }

    @Nonnull
    @RequestCached
    public SelectItem[] getBillableTypes() {
        return getSelectItems(BillableType.values(),
                              true,
                              timeEntryTranslationService.getSelectBillableType(),
                              billableTypeTranslationService::getBillableTypeLabel);
    }

    @Nonnull
    @RequestCached
    public SelectItem[] getPtoCategories() {
        return getSelectItems(PTOCategory.values(),
                              true,
                              timeEntryTranslationService.getSelectPtoCategory(),
                              pTOCategoryTranslationService::getPTOCategoryLabel);
    }

    public void calculateTotals() {
        timeSheetHoursCalculator.updateTotals(timeSheetEntryModel);
    }

    @Nonnull
    public WorkTime getDailyTotal(@Nonnull final TimeSheetWeeklyEntryModel timeSheetWeeklyEntryModel) {
        return timeSheetHoursCalculator.getDailyTotal(timeSheetWeeklyEntryModel);
    }

    public void addEntry() {
        final TimeSheetWeeklyEntryModel model = new TimeSheetWeeklyEntryModel();
        model.setTimeSheetDate(Calendar.getInstance());
        model.setBillableType(BillableType.YES);
        timeSheetEntryModel.getWeeklyEntries().add(model);
    }

    public void updateMemberValue() {
        if (null == timeSheetEntryModel.getMember()) {
            return;
        }
        final TeamMember teamMember = projectTeamMemberService
                .getTeamMember(timeSheetEntryModel.getMember());
        if (null != teamMember) {
            timeSheetEntryModel.setMemberID(teamMember.getMemberId());
            timeSheetEntryModel.setTeamMemberCategory(TeamMemberCategory.EMPLOYEE);
            final ProjectRole projectRole = getProjectRole(teamMember
                    .getProjectRoles());
            final List<AccessRole> accessRoles = getAccessRoles();
            accessRoles.stream()
                    .filter((accessRole) -> (accessRole.getUuid().equals(projectRole.getAccessScopeUUID())))
                    .forEach((accessRole) -> {
                        timeSheetEntryModel.setProjectRole(accessRole.getRoleName());
                    });

        } else {
            updateSubContractorValue();
        }

    }

    private void updateSubContractorValue() {

        final SubContractor subContractor = projectSubContractorService
                .getSubContractor(timeSheetEntryModel.getMember());
        if (null == subContractor) {
            return;
        }
        final Future<List<AccessRole>> accessRolesRequest = asynchronousService
                .execute(() -> getAccessRoles());
        final Future<Supplier> supplierRequest = asynchronousService
                .execute(() -> supplierService.findSupplier(subContractor.getSubContractorUUID()));
        timeSheetEntryModel.setMemberID(subContractor.getMemberId());
        timeSheetEntryModel.setTeamMemberCategory(TeamMemberCategory.SUB_CONTRACTOR);
        final ProjectRole projectRole = getProjectRole(subContractor
                .getProjectRoles());
        try {
            final List<AccessRole> accessRoles = accessRolesRequest.get();
            final Supplier supplier = supplierRequest.get();
            accessRoles.stream()
                    .filter((accessRole) -> (accessRole.getUuid().equals(projectRole.getAccessScopeUUID())))
                    .forEach((accessRole) -> {
                        timeSheetEntryModel.setProjectRole(accessRole.getRoleName());
                    });
            timeSheetEntryModel.setSupplierName(supplier.getName());
            timeSheetEntryModel.setSupplierType(supplier.isSubContractor() ? "Sub-Contractor" : "Consultant");
        } catch (InterruptedException | ExecutionException ex) {
            throw Throwables.propagate(ex);
        }
    }

    @Nonnull
    private List<AccessRole> getAccessRoles() {
        final AccessRoleRequestContext requestContext = AccessRoleRequestContext
                .builder()
                .applicationCode("AZPM")
                .build();
        return accessRoleService.getAccessRoles(requestContext);
    }

    @Nullable
    private ProjectRole getProjectRole(@Nonnull final Set<ProjectRole> projectRoles) {
        return projectRoles
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Profile
    public void saveEntries() {
        final Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap();
        final String action = params.get("action");
        validate();
        timeSheetEntryProcessor
                .processEntries(timeSheetEntryModel, project, action);
        successMessageModel = new SuccessMessageModel();
        initTimesheet();
        if (action.equals("submit")) {
            successMessageModel.setSuccessMessage(timeEntryTranslationService.getSubmitSuccessMessage());
            addSuccessMessage(timeEntryTranslationService.getSubmitSuccessMessage());
        } else if (isNullOrEmpty(timeSheetEntryModel.getTimeSheetUUID())) {
            successMessageModel.setSuccessMessage(timeEntryTranslationService.getSaveSuccessMessage());
            addSuccessMessage(timeEntryTranslationService.getSaveSuccessMessage());
        } else {
            successMessageModel.setSuccessMessage(timeEntryTranslationService.getUpdateSuccessMessage());
            addSuccessMessage(timeEntryTranslationService.getUpdateSuccessMessage());
        }
    }

    private void validate() {

        assertNotNull(timeSheetEntryModel.getMember(), ()
                      -> timeEntryTranslationService.getTeamMemberRequiredMessage());

        assertNotNull(timeSheetEntryModel.getWeeklyEntries(), ()
                      -> timeEntryTranslationService.getPleaseAddOneLine());
    }

    @Profile
    private void setProjectValues(@Nonnull final Project project) {

        timeSheetEntryModel.setProject(project.getName());

        if (project.getProjectType() == ProjectType.CUSTOMER_PROJECT) {
            final Customer customer = customerService
                    .getCustomer(project.getCustomerUUID());
            timeSheetEntryModel.setCustomer(customer.getName());
        } else if (project.getProjectType() == ProjectType.INTERNAL_PROJECT) {
            final Department department = departmentService
                    .findDepartment(
                            project.getCustomerUUID());
            timeSheetEntryModel.setCustomer(department.getName());
        }
    }

    private void setCurrentWeekDays() {
        final Calendar start = Calendar.getInstance();
        start.set(Calendar.DAY_OF_WEEK, start.getFirstDayOfWeek());
        timeSheetEntryModel.setWeekStartDate(start);
        final Calendar end = Calendar.getInstance();
        end.set(Calendar.DAY_OF_WEEK, end.getFirstDayOfWeek());
        end.add(Calendar.DAY_OF_MONTH, 6);
        timeSheetEntryModel.setWeekEndDate(end);
    }

    private void updateTask() {
        final TaskContext taskContext = TaskContext.builder()
                .orderFields(ImmutableMap.of("updatedDate", Context.Order.DESC))
                .parentLevelUUID(pUUID.get())
                .build();

        final List<ProjectTask> tasks = projectTaskService
                .getTasks(taskContext);
        final ProjectTask task = tasks
                .stream()
                .findFirst()
                .orElse(null);
        if (null == task) {
            return;
        }
        timeSheetEntryModel.setTaskUUID(task.getUuid());
        timeSheetEntryModel.setTaskID(task.getId());
        if (null != task.getParentLevel().getParentLevel()) {
            timeSheetEntryModel.setMilestoneUUID(task.getParentLevel().getUuid());
            timeSheetEntryModel.setPhaseUUID(task.getParentLevel().getParentLevel().getUuid());
        }
    }

    public void edit(@Nullable final String timeUUID) {
        if (!isNullOrEmpty(timeUUID)) {
            final TimeSheet time = timeSheetService.getTimesheet(timeUUID);
            timesheetEntryModelConvertor.setToModel(time, timeSheetEntryModel);
        }
    }

}
