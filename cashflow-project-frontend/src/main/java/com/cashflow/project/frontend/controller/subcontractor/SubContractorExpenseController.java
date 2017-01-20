package com.cashflow.project.frontend.controller.subcontractor;

import com.cashflow.access.authorization.LoggedInBusinessAccount;
import com.cashflow.access.authorization.LoggedInCompanyAccount;
import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.access.authorization.uniqueid.IdContext;
import com.cashflow.access.authorization.uniqueid.UniqueIdGenerator;
import com.cashflow.accountpayables.domain.supplier.Supplier;
import com.cashflow.accountpayables.domain.supplier.SupplierSearchContext;
import com.cashflow.accountpayables.service.api.supplier.SupplierService;
import com.cashflow.accountreceivables.domain.customer.Customer;
import com.cashflow.accountreceivables.service.api.customer.CustomerService;
import com.cashflow.entitydomains.Address;
import com.cashflow.entitydomains.FileUrl;
import com.cashflow.frontend.support.cacheing.requestcached.RequestCached;
import com.cashflow.frontend.support.countrymodel.CountryModelService;
import com.cashflow.frontend.support.httpparameter.HttpParameter;
import com.cashflow.project.api.expense.subcontractor.SubContractorExpenseService;
import com.cashflow.project.api.milestone.ProjectMilestoneService;
import com.cashflow.project.api.phase.ProjectPhaseService;
import com.cashflow.project.api.progress.ProjectLevelProgressService;
import com.cashflow.project.api.project.ProjectService;
import com.cashflow.project.api.task.ProjectTaskService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.ProjectStatus;
import com.cashflow.project.domain.project.ProjectType;
import com.cashflow.project.domain.project.expense.subcontractor.SubContractorExpense;
import com.cashflow.project.domain.project.level.ProjectLevelProgress;
import com.cashflow.project.domain.project.milestone.MilestoneContext;
import com.cashflow.project.domain.project.milestone.ProjectMilestone;
import com.cashflow.project.domain.project.personnel.MarkUpMethod;
import com.cashflow.project.domain.project.phase.PhaseContext;
import com.cashflow.project.domain.project.phase.ProjectPhase;
import com.cashflow.project.domain.project.task.ProjectTask;
import com.cashflow.project.domain.project.task.TaskContext;
import com.cashflow.project.frontend.controller.model.ModelViewScopedController;
import com.cashflow.project.frontend.controller.model.SubContractorExpenseModel;
import com.cashflow.project.frontend.controller.model.SuccessMessageModel;
import com.cashflow.project.translation.expenses.ExpenseStatusTranslationService;
import com.cashflow.project.translation.people.MarkUpMethodTranslationService;
import com.cashflow.project.translation.subcontractor.SubContractorExpenseTranslationService;
import com.cashflow.salestax.api.taxrate.TaxRateRequestContext;
import com.cashflow.salestax.api.taxrate.TaxRateService;
import com.cashflow.salestax.domain.taxrate.TaxRate;
import com.cashflow.useraccount.domain.businessaccount.BusinessAccount;
import com.cashflow.useraccount.domain.businessunit.CompanyAccount;
import com.cashflow.useraccount.domain.businessunit.Department;
import com.cashflow.useraccount.service.api.businessunit.department.DepartmentService;
import com.anosym.profiler.Profile;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import lombok.Getter;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

import static com.cashflow.frontend.support.errormessage.ErrorMessagePreconditions.assertNotNull;
import static com.cashflow.frontend.support.jsfutil.SelectItemHelper.getSelectItems;
import static com.cashflow.frontend.support.jsfutil.SuccessMessageHelper.addSuccessMessage;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_UUID;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 *
 * 
 * @since Dec 27, 2016, 10:32:16 AM
 */
@ModelViewScopedController
public class SubContractorExpenseController implements Serializable {

    private static final long serialVersionUID = -2861616724416084896L;

    @Inject
    @HttpParameter(SELECTED_PROJECT_UUID)
    private Instance<String> pUUID;

    @Inject
    @HttpParameter("mode")
    private Instance<String> mode;

    @Inject
    @LoggedInCompanyAccount
    private Instance<CompanyAccount> companyAccount;

    @Inject
    @LoggedInBusinessAccount
    private Instance<BusinessAccount> businessAccount;

    @Inject
    private ProjectService projectService;

    @Inject
    private Logger logger;

    @Inject
    private SubContractorExpenseService subContractorExpenseService;

    @Inject
    private SubContractorExpenseModelConvertor expenseModelConvertor;

    @Inject
    private SubContractorExpenseCalculator subContractorExpenseCalculator;

    @Inject
    private SupplierService supplierService;

    @Inject
    private ProjectPhaseService projectPhaseService;

    @Inject
    private CountryModelService countryModelService;

    @Inject
    private SubContractorProcessor subContractorProcessor;

    @Inject
    private ProjectMilestoneService projectMilestoneService;

    @Inject
    private ProjectTaskService projectTaskService;

    @Inject
    private ProjectLevelProgressService projectLevelProgressService;

    @Inject
    private MarkUpMethodTranslationService markUpMethodTranslationService;

    @Inject
    private ExpenseStatusTranslationService expenseStatusTranslationService;

    @Inject
    private SubContractorExpenseTranslationService subContractorExpenseTranslationService;

    @Inject
    private CustomerService customerService;

    @Inject
    private SubContractorExpenseProcessor subContractorExpenseProcessor;

    @Inject
    private DepartmentService departmentService;

    @Inject
    @IdContext(forDomain = SubContractorExpense.class, prefix = "SCE", length = 5)
    private UniqueIdGenerator uniqueIdGenerator;

    @Inject
    @IdContext(forDomain = Supplier.class, prefix = "S", length = 5)
    private UniqueIdGenerator uniqueIdGeneratorSupplier;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private TaxRateService taxRateService;

    @Getter
    private Project project;

    @Getter
    private List<TaxRate> taxRates;

    @Getter
    private List<ProjectPhase> projectPhases;

    @Getter
    private List<ProjectMilestone> projectMilestones;

    @Getter
    private List<ProjectTask> projectTasks;

    @Getter
    private List<Supplier> suppliers;

    @Getter
    private SubContractorExpenseModel subContractorExpenseModel;

    @Getter
    private SupplierModel supplierModel;

    @Getter
    private SuccessMessageModel successMessageModel;

    @PostConstruct
    void initTimesheet() {
        final String pUUID_ = checkNotNull(pUUID.get(), "ProjectUUID can not be null");

        subContractorExpenseModel = new SubContractorExpenseModel();
        subContractorExpenseModel.setInvoiceDueDate(Calendar.getInstance());
        subContractorExpenseModel.setDate(Calendar.getInstance());

        updateBasicDropdowns(pUUID_);
        updateTask();

    }

    private void updateTask() {

        final ProjectTask task = projectTasks
                .stream()
                .findFirst()
                .orElse(null);
        if (null == task) {
            return;
        }
        subContractorExpenseModel.setTask(task.getUuid());
        subContractorExpenseModel.setTaskID(task.getId());
        if (null != task.getParentLevel().getParentLevel()) {
            subContractorExpenseModel.setMilestone(task.getParentLevel().getUuid());
            subContractorExpenseModel.setPhase(task.getParentLevel().getParentLevel().getUuid());
        }
    }

    @Profile
    public void updateBasicDropdowns(@Nonnull final String pUUID_) {

        final TaxRateRequestContext trrc = TaxRateRequestContext
                .builder()
                .country(companyAccount.get().getAddress().getCountry())
                .build();
        final Future<Project> projectRequest = asynchronousService
                .execute(() -> checkNotNull(projectService.getProject(pUUID_), "Failed to load project for uuid: %s",
                                            pUUID_));

        final Future<List<TaxRate>> taxRatesRequest = asynchronousService
                .execute(() -> taxRateService.findTaxRates(trrc));

        final Future<List<ProjectPhase>> projectPhasesRequest = asynchronousService
                .execute(() -> projectPhaseService
                        .getProjectPhases(PhaseContext
                                .builder()
                                .projectUUID(pUUID_)
                                .build()));

        final Future<List<ProjectMilestone>> projectMilestoneRequest = asynchronousService
                .execute(() -> projectMilestoneService
                        .getMilestones(MilestoneContext.builder()
                                .parentLevelUUID(pUUID_)
                                .build()));

        final Future<List<ProjectTask>> projectTasksRequest = asynchronousService
                .execute(() -> projectTaskService
                        .getTasks(TaskContext.builder()
                                .parentLevelUUID(pUUID_)
                                .build()));

        final Future<List<Supplier>> supplierRequest = asynchronousService
                .execute(() -> supplierService.findSuppliers(SupplierSearchContext
                        .builder()
                        .build()));

        try {
            project = projectRequest.get();
            taxRates = taxRatesRequest.get();
            projectPhases = projectPhasesRequest.get();
            projectMilestones = projectMilestoneRequest.get();
            projectTasks = projectTasksRequest.get();
            suppliers = supplierRequest.get();
        } catch (InterruptedException | ExecutionException ex) {
            throw Throwables.propagate(ex);
        }

    }

    public void checkApprovedProject() {
        if (null == pUUID.get()) {
            return;
        }
        final List<String> projectUUIDs = ImmutableList.of(pUUID.get());

        final List<ProjectLevelProgress> progress = projectLevelProgressService
                .getProjectLevelProgresss(projectUUIDs);

        final Optional<ProjectLevelProgress> projectProgress = progress
                .stream()
                .findFirst();

        final boolean isActive = projectProgress.get().getProjectStatus() == ProjectStatus.APPROVED;

        if (!isActive) {
            RequestContext.getCurrentInstance().execute("PF('validation_message').show();");
        } else {
            subContractorExpenseModel.setSubContractorExpenseID(uniqueIdGenerator.nextId());
            setProjectValues();
            RequestContext.getCurrentInstance().execute("PF('add_subcontractor_expense').show();");
        }
    }

    @Profile
    private void setProjectValues() {

        subContractorExpenseModel.setProject(project.getName());
        subContractorExpenseModel.setCurrency(project.getCurrency());

        if (project.getProjectType() == ProjectType.CUSTOMER_PROJECT) {
            final Customer customer = customerService
                    .getCustomer(project.getCustomerUUID());
            subContractorExpenseModel.setCustomer(customer.getName());
        } else if (project.getProjectType() == ProjectType.INTERNAL_PROJECT) {
            final Department department = departmentService
                    .findDepartment(
                            project.getCustomerUUID());
            subContractorExpenseModel.setCustomer(department.getName());
        }
    }

    public void setMilestones() {
        if (isNullOrEmpty(subContractorExpenseModel.getPhase())) {
            projectMilestones = new ArrayList<>();
        }
        projectMilestones = projectMilestoneService
                .findByPhaseUUIDs(ImmutableList.of(subContractorExpenseModel.getPhase()));
    }

    public void setTasks() {
        if (isNullOrEmpty(subContractorExpenseModel.getMilestone())) {
            projectTasks = new ArrayList<>();
        }
        projectTasks = projectTaskService
                .findByMilestoneOrProjectUUIDs(ImmutableList.of(subContractorExpenseModel.getMilestone()));
    }

    public void updateTaskID() {
        if (isNullOrEmpty(subContractorExpenseModel.getTask())) {
            return;
        }
        final ProjectTask task = projectTaskService
                .getTask(subContractorExpenseModel.getTask());
        if (null == task) {
            return;
        }
        subContractorExpenseModel.setTaskID(task.getId());
    }

    public void updateSupplierID() {
        if (isNullOrEmpty(subContractorExpenseModel.getSupplierUUID())) {
            subContractorExpenseModel.setSupplierID(null);
            return;
        }
        if (subContractorExpenseModel.getSupplierUUID().equals("Add New Supplier")) {
            supplierModel = new SupplierModel();
            supplierModel.setAddress(new Address());
            supplierModel.setContactAddress(new Address());
            supplierModel.setCountryModel(countryModelService
                    .getCountryModel(
                            companyAccount.get().getAddress().getCountry()));
            supplierModel.getAddress().setStateOrProvince(companyAccount.get().getAddress().getStateOrProvince());
            supplierModel.setId(uniqueIdGeneratorSupplier.nextId());
            RequestContext.getCurrentInstance().execute("PF('new_subcontractor').show();");
        }
        final Supplier supplier = suppliers
                .stream()
                .filter((sup) -> sup.getUuid().equals(subContractorExpenseModel.getSupplierUUID()))
                .findFirst()
                .orElse(null);
        if (null == supplier) {
            return;
        }
        subContractorExpenseModel.setSupplierID(supplier.getSupplierNumber());
    }

    @Nonnull
    @RequestCached
    public SelectItem[] getMarkUpMethods() {
        return getSelectItems(MarkUpMethod.values(),
                              true,
                              subContractorExpenseTranslationService.getSelectMarkUpMethod(),
                              markUpMethodTranslationService::getMarkUpMethodLabel);
    }

    public void fileUpload(@Nonnull
            final FileUploadEvent event) {
        InputStream itemImageInputStream = null;
        try {
            if (null != event.getFile() && null != event.getFile().getInputstream()) {
                itemImageInputStream = event.getFile().getInputstream();
                final String itemUrl = new StringBuilder(businessAccount.get().getUuid())
                        .append("/")
                        .append("projects")
                        .append("/")
                        .append("SubcontractorExpense")
                        .append("/")
                        .append(UUID.randomUUID().toString()).toString();
                subContractorExpenseModel.getExpenseDocs().add(new FileUrl(itemUrl,
                                                                           event.getFile().getContentType(),
                                                                           event.getFile().getSize(),
                                                                           event.getFile().getFileName(),
                                                                           "SubContractor Expense Doc."));
                subContractorExpenseModel.getInputStreams().put(itemUrl, itemImageInputStream);
                successMessageModel = new SuccessMessageModel();
                successMessageModel.setSuccessMessage(subContractorExpenseTranslationService.getUploadSuccessMessage());
                addSuccessMessage(subContractorExpenseTranslationService.getUploadSuccessMessage());
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void updateAmountValues() {
        subContractorExpenseCalculator.updateAmountValues(subContractorExpenseModel, taxRates);
    }

    public void deductFromLabour(@Nonnull final BigDecimal value) {
        if (value.compareTo(subContractorExpenseModel.getLabourValue()) > 0) {
            successMessageModel = new SuccessMessageModel();
            successMessageModel.setSuccessMessage(subContractorExpenseTranslationService
                    .getValidationMessageForCategorization());
            RequestContext.getCurrentInstance().execute("PF('subcontractor_success_message').show();");
        } else {
            subContractorExpenseModel.setLabourValue(subContractorExpenseModel.getLabourValue().subtract(value));
            validateCatezValues();
        }
    }

    public void validateCatezValues() {
        subContractorExpenseCalculator.updateCatezValues(subContractorExpenseModel);
    }

    public void updateBillableValues() {
        subContractorExpenseCalculator.updateBillableValues(subContractorExpenseModel);
    }

    @Profile
    public void save() {
        validate();

        subContractorExpenseModel.setAction("save");

        subContractorExpenseProcessor.process(subContractorExpenseModel, project);

        initDefaultValues();

        successMessageModel = new SuccessMessageModel();
        if (isNullOrEmpty(subContractorExpenseModel.getSubContractorExpenseUUID())) {
            successMessageModel.setSuccessMessage(subContractorExpenseTranslationService.getSuccessMessage());
            addSuccessMessage(subContractorExpenseTranslationService.getSuccessMessage());
        } else {
            successMessageModel.setSuccessMessage(subContractorExpenseTranslationService.getUpdateSuccessMessage());
            addSuccessMessage(subContractorExpenseTranslationService.getUpdateSuccessMessage());
        }
    }

    @Profile
    public void approve() {
        validate();
        subContractorExpenseModel.setAction("approve");

        subContractorExpenseProcessor.process(subContractorExpenseModel, project);

        initDefaultValues();

        successMessageModel = new SuccessMessageModel();
        successMessageModel.setSuccessMessage(subContractorExpenseTranslationService.getApprovedSuccessMessage());
        addSuccessMessage(subContractorExpenseTranslationService.getApprovedSuccessMessage());
    }

    private void initDefaultValues() {
        subContractorExpenseModel = new SubContractorExpenseModel();

        subContractorExpenseModel.setInvoiceDueDate(Calendar.getInstance());
        subContractorExpenseModel.setDate(Calendar.getInstance());
        subContractorExpenseModel.setSubContractorExpenseID(uniqueIdGenerator.nextId());

        setProjectValues();

        updateTask();
    }

    private void validate() {
        assertNotNull(subContractorExpenseModel.getSupplierUUID(),
                      subContractorExpenseTranslationService.getSupplierRequiredMessage());
        assertNotNull(subContractorExpenseModel.getSubContractorService(),
                      subContractorExpenseTranslationService.getSubContractorServiceRequiredMessage());
        assertNotNull(subContractorExpenseModel.getInvoiceNumber(),
                      subContractorExpenseTranslationService.getInvoiceNumberRequiredMessage());
    }

    public void saveSubcontractor() {
        final Supplier supplier = subContractorProcessor.createSupplier(supplierModel);

        suppliers.add(0, supplier);

        subContractorExpenseModel.setSupplierUUID(supplier.getUuid());

        successMessageModel = new SuccessMessageModel();
        successMessageModel.setSuccessMessage(subContractorExpenseTranslationService.getSubcontractorSuccessMessage());
    }

    public void viewExpense(@Nonnull final String expenseUUID) {
        final SubContractorExpense expense = subContractorExpenseService.get(expenseUUID);

        subContractorExpenseModel = expenseModelConvertor.toModel(expense, project, suppliers, taxRates);

        subContractorExpenseModel.setMode(mode.get());
    }

    public void updateDesc() {
        subContractorExpenseModel.setDescription(subContractorExpenseModel.getSubContractorService());
    }

}
