package com.cashflow.project.frontend.controller.expenses;

import com.cashflow.access.authorization.LoggedInBusinessAccount;
import com.cashflow.access.authorization.LoggedInCompanyAccount;
import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.access.authorization.uniqueid.IdContext;
import com.cashflow.access.authorization.uniqueid.UniqueIdGenerator;
import com.cashflow.accessroles.domain.accessrole.AccessRole;
import com.cashflow.accessroles.service.accessrole.AccessRoleRequestContext;
import com.cashflow.accessroles.service.accessrole.AccessRoleService;
import com.cashflow.accountreceivables.domain.customer.Customer;
import com.cashflow.accountreceivables.service.api.customer.CustomerService;
import com.cashflow.entitydomains.FileUrl;
import com.cashflow.entitydomains.facade.context.Context;
import com.cashflow.frontend.support.cacheing.requestcached.RequestCached;
import com.cashflow.frontend.support.httpparameter.HttpParameter;
import com.cashflow.project.api.expense.ExpenseReportService;
import com.cashflow.project.api.milestone.ProjectMilestoneService;
import com.cashflow.project.api.people.ProjectSubContractorService;
import com.cashflow.project.api.people.ProjectTeamMemberService;
import com.cashflow.project.api.phase.ProjectPhaseService;
import com.cashflow.project.api.project.ProjectService;
import com.cashflow.project.api.task.ProjectTaskService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.ProjectType;
import com.cashflow.project.domain.project.expense.BillableType;
import com.cashflow.project.domain.project.expense.ExpenseReport;
import com.cashflow.project.domain.project.expense.ExpenseType;
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
import com.cashflow.project.frontend.controller.model.ExpenseReportDataModel;
import com.cashflow.project.frontend.controller.model.ExpenseReportModel;
import com.cashflow.project.frontend.controller.model.ModelViewScopedController;
import com.cashflow.project.frontend.controller.model.SuccessMessageModel;
import com.cashflow.project.translation.expenses.BillableTypeTranslationService;
import com.cashflow.project.translation.expenses.ExpenseTranslationService;
import com.cashflow.project.translation.expenses.ExpenseTypeTranslationService;
import com.cashflow.salestax.api.taxrate.TaxRateRequestContext;
import com.cashflow.salestax.api.taxrate.TaxRateService;
import com.cashflow.salestax.domain.taxrate.TaxRate;
import com.cashflow.useraccount.domain.businessaccount.BusinessAccount;
import com.cashflow.useraccount.domain.businessunit.CompanyAccount;
import com.cashflow.useraccount.domain.businessunit.Department;
import com.cashflow.useraccount.service.api.businessunit.department.DepartmentService;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;

import static com.cashflow.frontend.support.errormessage.ErrorMessagePreconditions.assertNotNull;
import static com.cashflow.frontend.support.errormessage.ErrorMessagePreconditions.assertState;
import static com.cashflow.frontend.support.jsfutil.SelectItemHelper.getSelectItems;
import static com.cashflow.frontend.support.jsfutil.SuccessMessageHelper.addSuccessMessage;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_UUID;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 *
 * 
 * @since 5 Dec, 2016, 10:40:15 AM
 */
@ModelViewScopedController
public class ExpenseController implements Serializable {

    private static final long serialVersionUID = 8116839457117958176L;

    @Inject
    private Logger logger;

    @Inject
    private ExpenseTranslationService expenseTranslationService;

    @Inject
    private ExpenseTypeTranslationService expenseTypeTranslationService;

    @Inject
    private BillableTypeTranslationService billableTypeTranslationService;

    @Inject
    private ProjectService projectService;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private ProjectSubContractorService projectSubContractorService;

    @Inject
    private TaxRateService taxRateService;

    @Inject
    private CustomerService customerService;

    @Inject
    private ProjectTaskService projectTaskService;

    @Inject
    private ExpenseModelConvertor expenseModelConvertor;

    @Inject
    private MemberEntityResolver memberEntityResolver;

    @Inject
    private ProjectTeamMemberService projectTeamMemberService;

    @Inject
    private ProjectMilestoneService milestoneService;

    @Inject
    private ProjectPhaseService projectPhaseService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private AccessRoleService accessRoleService;

    @Inject
    private ExpenseProcessor expenseProcessor;

    @Inject
    private ExpenseReportService expenseReportService;

    @Getter
    @Setter
    private SuccessMessageModel successMessageModel;

    @Inject
    @HttpParameter(SELECTED_PROJECT_UUID)
    private Instance<String> pUUID;

    @Inject
    @IdContext(forDomain = ExpenseReport.class, prefix = "ER", length = 5)
    private UniqueIdGenerator uniqueIdGenerator;

    @Getter
    @Setter
    private ExpenseReportModel expenseReportModel;

    @Getter
    @Setter
    private Project project;

    @Getter
    private Map<String, ExpenseReportDataModel> expenseDetailData;

    @Getter
    @Setter
    @Nonnull
    private List<TaxRate> taxRates;

    @Getter
    @Inject
    @LoggedInBusinessAccount
    private Instance<BusinessAccount> businessAccount;

    @Getter
    @Inject
    @LoggedInCompanyAccount
    protected Instance<CompanyAccount> companyAccount;

    private static int count = 001;

    @PostConstruct
    void init_expense() {
        final String projectUUID = checkNotNull(pUUID.get(), "Project not selected for phase");
        expenseReportModel = new ExpenseReportModel();

        final Future<Project> projectRequest = asynchronousService.execute(() -> checkNotNull(projectService
                .getProject(projectUUID), "Failed to load project for uuid: %s", projectUUID));
        final TaxRateRequestContext context = TaxRateRequestContext.builder()
                .country(companyAccount.get().getAddress().getCountry())
                .build();
        final Future<List<TaxRate>> taxRequest = asynchronousService
                .execute(() -> taxRateService.findTaxRates(context));

        try {
            project = projectRequest.get();
            taxRates = taxRequest.get();
            setProjectValues(projectRequest.get());
        } catch (InterruptedException | ExecutionException ex) {
            throw Throwables.propagate(ex);
        }

        addExpenseData();
        setCurrentWeekDays();

    }

    @Nonnull
    public SelectItem[] getExpenseType() {
        return getSelectItems(ExpenseType.values(),
                              true,
                              expenseTranslationService.getExpenseTypeLabel(),
                              expenseTypeTranslationService::getExpenseTypeLabel);
    }

    @Nonnull
    public SelectItem[] getBillableType() {
        return getSelectItems(BillableType.values(),
                              true,
                              expenseTranslationService.getBillableTypeLabel(),
                              billableTypeTranslationService::getBillableTypeLabel);
    }

    public void updateExpenseReportId() {
        expenseReportModel.setExpenseReportId(uniqueIdGenerator.nextId());
        updateTask();
    }

    private void setProjectValues(@Nonnull final Project project) {

        expenseReportModel.setProject(project.getName());
        expenseReportModel.setExpenseCurrency(project.getCurrency());
        if (project.getProjectType() == ProjectType.CUSTOMER_PROJECT) {
            final Customer customer = customerService.getCustomer(project.getCustomerUUID());
            expenseReportModel.setCustomer(customer.getName());
        } else if (project.getProjectType() == ProjectType.INTERNAL_PROJECT) {
            final Department department = departmentService.findDepartment(project.getCustomerUUID());
            expenseReportModel.setCustomer(department.getName());
        }
    }

    @Nonnull
    @RequestCached
    public List<ProjectPhase> getProjectPhases() {
        if (!isNullOrEmpty(pUUID.get())) {
            return projectPhaseService.getProjectPhases(PhaseContext
                    .builder()
                    .projectUUID(pUUID.get())
                    .build());
        }
        return ImmutableList.of();
    }

    @Nonnull
    @RequestCached
    public List<ProjectMilestone> getMilestones() {
        if (!isNullOrEmpty(expenseReportModel.getPhase())) {
            return milestoneService
                    .findByPhaseUUIDs(ImmutableList.of(expenseReportModel.getPhase()));
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
        if (!isNullOrEmpty(expenseReportModel.getMilestone())) {
            return projectTaskService
                    .findByMilestoneOrProjectUUIDs(ImmutableList.of(expenseReportModel.getMilestone()));
        } else if (!isNullOrEmpty(pUUID.get())) {
            final TaskContext taskContext = TaskContext.builder()
                    .parentLevelUUID(pUUID.get())
                    .build();

            return projectTaskService
                    .getTasks(taskContext);
        }
        return ImmutableList.of();
    }

    @Nonnull
    @RequestCached
    public List<MemberEntityModel> getProjectMembers() {
        return memberEntityResolver.loadMemberValues(pUUID.get());
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
        expenseReportModel.setTask(task.getUuid());
        expenseReportModel.setTaskId(task.getId());
        if (null != task.getParentLevel().getParentLevel()) {
            expenseReportModel.setMilestone(task.getParentLevel().getUuid());
            expenseReportModel.setPhase(task.getParentLevel().getParentLevel().getUuid());
        }
    }

    public void updateTaskID() {
        if (isNullOrEmpty(expenseReportModel.getTask())) {
            return;
        }
        final ProjectTask task = projectTaskService.getTask(expenseReportModel.getTask());
        if (null == task) {
            return;
        }
        expenseReportModel.setTaskId(task.getId());
    }

    public void updateMemberValue() {
        if (null == (expenseReportModel.getMember())) {
            return;
        }
        final TeamMember teamMember = projectTeamMemberService.getTeamMember(expenseReportModel.getMember());
        if (null != teamMember) {
            expenseReportModel.setMemberId(teamMember.getMemberId());
            expenseReportModel.setMemberCategory(TeamMemberCategory.EMPLOYEE);

            final ProjectRole projectRole = getProjectRole(teamMember
                    .getProjectRoles());
            final Future<List<AccessRole>> accessRoles = asynchronousService.execute(()
                    -> getAccessRoles()
            );
            try {
                for (AccessRole accessRole : accessRoles.get()) {
                    if (accessRole.getUuid().equals(projectRole.getAccessScopeUUID())) {
                        expenseReportModel.setProjectRole(accessRole.getRoleName());
                    }
                }
            } catch (InterruptedException | ExecutionException ex) {
                throw Throwables.propagate(ex);
            }

        } else {
            updateSubContractorValue();
        }

    }

    private void updateSubContractorValue() {

        final SubContractor subContractor = projectSubContractorService.getSubContractor(expenseReportModel.getMember());
        if (null != subContractor) {
            expenseReportModel.setMemberId(subContractor.getMemberId());
            expenseReportModel.setMemberCategory(TeamMemberCategory.SUB_CONTRACTOR);
            final ProjectRole projectRole = getProjectRole(subContractor
                    .getProjectRoles());
            final Future<List<AccessRole>> accessRoles = asynchronousService.execute(()
                    -> getAccessRoles());
            try {
                for (AccessRole accessRole : accessRoles.get()) {
                    if (accessRole.getUuid().equals(projectRole.getAccessScopeUUID())) {
                        expenseReportModel.setProjectRole(accessRole.getRoleName());
                    }
                }
            } catch (InterruptedException | ExecutionException ex) {
                throw Throwables.propagate(ex);
            }

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
    public ProjectRole getProjectRole(@Nonnull final Set<ProjectRole> projectRoles) {
        final ProjectRole projectRole = projectRoles
                .stream()
                .findFirst()
                .orElse(null);

        return projectRole;
    }

    @Nonnull
    @RequestCached
    public List<ExpenseReportDataModel> getExpenseData() {
        final List<ExpenseReportDataModel> expenseData = this.getExpenseReportModel().getReportDataModels();
        expenseDetailData = new HashMap<>();
        for (ExpenseReportDataModel dataModel : expenseData) {
            expenseDetailData.put(dataModel.getUuid(), dataModel);
        }
        return expenseData;
    }

    public void addExpenseData() {
        final ExpenseReportDataModel dataModel = new ExpenseReportDataModel();
        dataModel.setUuid(UUID.randomUUID().toString());
        this.getExpenseReportModel().getReportDataModels().add(dataModel);
    }

    public void calculateExpense(@Nullable final ExpenseReportDataModel reportDataModel) {
        expenseModelConvertor.calculateExpense(expenseReportModel, reportDataModel);
    }

    public void fileUpload(@Nonnull
            final FileUploadEvent event) {
        final String expDataUUID = (String) event.getComponent().getAttributes().get("expDataUUID");
        final ExpenseReportDataModel dataModel = expenseDetailData.get(expDataUUID);
        InputStream itemImageInputStream = null;
        try {
            if (null != event.getFile() && null != event.getFile().getInputstream()) {
                itemImageInputStream = event.getFile().getInputstream();
                final String itemUrl = new StringBuilder(businessAccount.get().getUuid())
                        .append("/")
                        .append("projects")
                        .append("/")
                        .append("Expense Report")
                        .append("/")
                        .append(expenseReportModel.getExpenseReportId())
                        .append("/")
                        .append(UUID.randomUUID().toString()).toString();

                dataModel.getProjectFileUrls().add(new FileUrl(itemUrl, event.getFile().getContentType(),
                                                               event.getFile().getSize(),
                                                               event.getFile().getFileName(),
                                                               expenseTranslationService
                                                                       .getExpenseAttachmentsLabel() + count));
                dataModel.getInputStreams().put(itemUrl, itemImageInputStream);
                count++;
                addSuccessMessage(expenseTranslationService.getUploadSuccessMessage());

            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

    }

    public void saveExpenseReport() {
        final Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap();
        final String action = params.get("action");
        validate();
        expenseProcessor.saveExpenses(expenseReportModel, project, action);
        successMessageModel = new SuccessMessageModel();
        if (action.equals("submit")) {
            successMessageModel.setSuccessMessage(expenseTranslationService.getSubmitSuccessMessage());
            addSuccessMessage(expenseTranslationService.getSubmitSuccessMessage());
        } else if (isNullOrEmpty(expenseReportModel.getExpenseReportUUID())) {
            successMessageModel.setSuccessMessage(expenseTranslationService.getExpenseReportSavedSuccessfully());
            addSuccessMessage(expenseTranslationService.getExpenseReportSavedSuccessfully());
        } else {
            successMessageModel.setSuccessMessage(expenseTranslationService.getExpenseReportUpdatedSuccessfully());
            addSuccessMessage(expenseTranslationService.getExpenseReportUpdatedSuccessfully());
        }
        init_expense();
        updateExpenseReportId();

    }

    private void setCurrentWeekDays() {
        final Calendar start = Calendar.getInstance();
        start.set(Calendar.DAY_OF_WEEK, start.getFirstDayOfWeek());
        expenseReportModel.setWeekStartDate(start);
        final Calendar end = Calendar.getInstance();
        end.set(Calendar.DAY_OF_WEEK, end.getFirstDayOfWeek());
        end.add(Calendar.DAY_OF_MONTH, 6);
        expenseReportModel.setWeekEndingDate(end);
    }

    private void validate() {

        assertNotNull(expenseReportModel.getMember(), () -> expenseTranslationService.getMemberRequiredMessage());

        assertState(expenseReportModel.getReportDataModels().size() > 0, () -> expenseTranslationService
                .getAtleastOneExpenseRequried());
        assertNotNull(expenseReportModel.getReportDataModels().get(0).getExpenseDate(), () -> expenseTranslationService
                .getExpenseDateRequiredMessage());

        assertNotNull(expenseReportModel.getReportDataModels().get(0).getExpenseType(), () -> expenseTranslationService
                .getExpenseTypeRequiredMessage());
        assertNotNull(expenseReportModel.getReportDataModels().get(0).getBillable(), () -> expenseTranslationService
                .getBillableRequiredMessage());
    }

    public void edit(@Nullable final String expenseId) {
        if (!isNullOrEmpty(expenseId)) {
            final ExpenseReport expense = expenseReportService.getExpenseReport(expenseId);
            expenseModelConvertor.converExpenseDetailsToModel(expense, expenseReportModel);
        }
    }
}
