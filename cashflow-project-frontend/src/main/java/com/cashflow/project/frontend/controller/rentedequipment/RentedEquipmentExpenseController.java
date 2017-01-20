package com.cashflow.project.frontend.controller.rentedequipment;

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
import com.cashflow.datarepository.service.DataRepository;
import com.cashflow.datarepository.service.DataRepositoryService;
import com.cashflow.entitydomains.FileUrl;
import com.cashflow.entitydomains.facade.context.Context;
import com.cashflow.frontend.support.cacheing.requestcached.RequestCached;
import com.cashflow.frontend.support.httpparameter.HttpParameter;
import com.cashflow.project.api.milestone.ProjectMilestoneService;
import com.cashflow.project.api.phase.ProjectPhaseService;
import com.cashflow.project.api.project.ProjectService;
import com.cashflow.project.api.rentedequipment.RentedEquipmentExpenseService;
import com.cashflow.project.api.task.ProjectTaskService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.ProjectType;
import com.cashflow.project.domain.project.expense.ExpenseReport;
import com.cashflow.project.domain.project.milestone.MilestoneContext;
import com.cashflow.project.domain.project.milestone.ProjectMilestone;
import com.cashflow.project.domain.project.personnel.MarkUpMethod;
import com.cashflow.project.domain.project.phase.PhaseContext;
import com.cashflow.project.domain.project.phase.ProjectPhase;
import com.cashflow.project.domain.project.rentedequipment.BillUnit;
import com.cashflow.project.domain.project.rentedequipment.RentedEquipmentExpense;
import com.cashflow.project.domain.project.task.ProjectTask;
import com.cashflow.project.domain.project.task.TaskContext;
import com.cashflow.project.frontend.controller.model.ModelViewScopedController;
import com.cashflow.project.frontend.controller.model.RentedEquipmentExpenseModel;
import com.cashflow.project.frontend.controller.model.SuccessMessageModel;
import com.cashflow.project.translation.equipment.rented.BillUnitTranslationService;
import com.cashflow.project.translation.people.MarkUpMethodTranslationService;
import com.cashflow.project.translation.rentedequipment.RentedEquipmentExpenseTranslationService;
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
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
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
import static com.cashflow.project.frontend.controller.model.DataRepositoryName.REPOSITORY_NAME;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 *
 * 
 * @since 19 Dec, 2016, 11:16:44 AM
 */
@ModelViewScopedController
public class RentedEquipmentExpenseController implements Serializable {

    private static final long serialVersionUID = -8544471450641590897L;

    @Inject
    private Logger logger;

    @Inject
    private RentedEquipmentExpenseTranslationService rentedEquipmentExpenseTranslationService;

    @Inject
    private MarkUpMethodTranslationService markUpMethodTranslationService;

    @Inject
    private BillUnitTranslationService billUnitTranslationService;

    @Inject
    private ProjectService projectService;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private TaxRateService taxRateService;

    @Inject
    private CustomerService customerService;

    @Inject
    private ProjectTaskService projectTaskService;

    @Inject
    private ProjectMilestoneService milestoneService;

    @Inject
    private ProjectPhaseService projectPhaseService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private SupplierService supplierService;

    @Inject
    private RentedEquipmentExpenseCalculationProcessor calculationProcessor;

    @Inject
    private RentedEquipmentExpenseProcessor rentedEquipmentProcessor;

    @Inject
    private RentedEquipmentExpenseService rentedEquipmentExpenseService;

    @Inject
    private RentedEquipmentExpenseModelConverter rentedEquipmentExpenseModelConverter;

    @Inject
    @HttpParameter(SELECTED_PROJECT_UUID)
    private Instance<String> pUUID;

    @Inject
    @IdContext(forDomain = ExpenseReport.class, prefix = "EC", length = 6)
    private UniqueIdGenerator uniqueIdGenerator;

    @Getter
    @Setter
    private RentedEquipmentExpenseModel equipmentModel;

    @Getter
    @Setter
    private Project project;

    @Getter
    @Setter
    private SuccessMessageModel successMessageModel;

    @Getter
    private Map<String, Supplier> supplierData;

    @Getter
    @Setter
    @Nonnull
    private List<TaxRate> taxRates;

    @Inject
    @DataRepository(REPOSITORY_NAME)
    private DataRepositoryService dataRepositoryService;

    @Getter
    @Inject
    @LoggedInCompanyAccount
    protected Instance<CompanyAccount> companyAccount;

    @Getter
    @Inject
    @LoggedInBusinessAccount
    private Instance<BusinessAccount> businessAccount;

    private static int count = 001;

    @PostConstruct
    void init_rentedEquipment() {
        final String projectUUID = checkNotNull(pUUID.get(), "Project not selected for Equipment");
        equipmentModel = new RentedEquipmentExpenseModel();
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
    }

    public void updateRentedEquipId() {
        equipmentModel.setRentedEquipmentId(uniqueIdGenerator.nextId());
        updateTask();
    }

    private void setProjectValues(@Nonnull final Project project) {

        equipmentModel.setProject(project.getName());
        equipmentModel.setCurrency(project.getCurrency());
        if (project.getProjectType() == ProjectType.CUSTOMER_PROJECT) {
            final Customer customer = customerService.getCustomer(project.getCustomerUUID());
            equipmentModel.setCustomer(customer.getName());
        } else if (project.getProjectType() == ProjectType.INTERNAL_PROJECT) {
            final Department department = departmentService.findDepartment(project.getCustomerUUID());
            equipmentModel.setCustomer(department.getName());
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
        if (!isNullOrEmpty(equipmentModel.getPhase())) {
            return milestoneService
                    .findByPhaseUUIDs(ImmutableList.of(equipmentModel.getPhase()));
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
        if (!isNullOrEmpty(equipmentModel.getMilestone())) {
            return projectTaskService
                    .findByMilestoneOrProjectUUIDs(ImmutableList.of(equipmentModel.getMilestone()));
        } else if (!isNullOrEmpty(pUUID.get())) {
            final TaskContext taskContext = TaskContext.builder()
                    .parentLevelUUID(pUUID.get())
                    .build();

            return projectTaskService
                    .getTasks(taskContext);
        }
        return ImmutableList.of();
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
        equipmentModel.setTask(task.getUuid());
        equipmentModel.setTaskId(task.getId());
        if (null != task.getParentLevel().getParentLevel()) {
            equipmentModel.setMilestone(task.getParentLevel().getUuid());
            equipmentModel.setPhase(task.getParentLevel().getParentLevel().getUuid());
        }
    }

    public void updateTaskID() {
        if (isNullOrEmpty(equipmentModel.getTask())) {
            return;
        }
        final ProjectTask task = projectTaskService.getTask(equipmentModel.getTask());
        if (null == task) {
            return;
        }
        equipmentModel.setTaskId(task.getId());
    }

    @Nonnull
    @RequestCached
    public List<Supplier> getSuppliers() {
        if (!isNullOrEmpty(pUUID.get())) {
            final SupplierSearchContext searchContext = SupplierSearchContext.builder()
                    .offset(0)
                    .limit(100)
                    .build();
            final List<Supplier> suppliers = supplierService.findSuppliers(searchContext);
            supplierData = new HashMap<>();
            suppliers.forEach((supplier) -> {
                supplierData.put(supplier.getUuid(), supplier);
            });

            return suppliers;

        }
        return ImmutableList.of();
    }

    public void updateSupplierID() {
        if (isNullOrEmpty(equipmentModel.getSupplier())) {
            return;
        }
        final Supplier supplier = supplierData.get(equipmentModel.getSupplier());
        if (null != supplier) {
            equipmentModel.setSupplierId(supplier.getSupplierNumber());
        }

    }

    public void calculateSupplierInvoice() {
        calculationProcessor.calculateSupplierInvoice(equipmentModel, taxRates);
        updateCostRate();
    }

    public void updateCostRate() {
        calculationProcessor.calculateCostRate(equipmentModel);
        updateBillRate();
    }

    public void updateBillRate() {
        calculationProcessor.calculateBillableRate(equipmentModel);
        calculateBillableAmount();
    }

    public void calculateBillableAmount() {
        calculationProcessor.calculateBillableAmount(equipmentModel);
    }

    public void calculateBillableQty() {
        assertState(equipmentModel.getNonBillableQty() < equipmentModel.getQty(),
                    () -> rentedEquipmentExpenseTranslationService
                            .getNonBillableQtyValidationMessage());
        equipmentModel.setBillableQty(equipmentModel.getQty() - equipmentModel.getNonBillableQty());
        calculateBillableAmount();
    }

    public void calculateNonBillableQty() {
        assertState(equipmentModel.getBillableQty() < equipmentModel.getQty(),
                    () -> rentedEquipmentExpenseTranslationService
                            .getBillableQtyValidationMessage());
        equipmentModel.setNonBillableQty(equipmentModel.getQty() - equipmentModel.getBillableQty());
        calculateBillableAmount();
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
                        .append("Expense Report")
                        .append("/")
                        .append(equipmentModel.getRentedEquipmentId())
                        .append("/")
                        .append(UUID.randomUUID().toString()).toString();

                equipmentModel.getRentedEquipmentDocs().add(new FileUrl(itemUrl, event.getFile().getContentType(),
                                                                        event.getFile().getSize(),
                                                                        event.getFile().getFileName(),
                                                                        rentedEquipmentExpenseTranslationService
                                                                                .getRentedEquipAttachmentsLabel() + count));
                equipmentModel.getInputStreams().put(itemUrl, itemImageInputStream);
                count++;
                addSuccessMessage(rentedEquipmentExpenseTranslationService.getUploadSuccessMessage());

            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

    }

    @Nonnull
    @RequestCached
    public SelectItem[] getMarkUpMethods() {
        return getSelectItems(MarkUpMethod.values(),
                              true,
                              rentedEquipmentExpenseTranslationService.getSelectMarkUpMethod(),
                              markUpMethodTranslationService::getMarkUpMethodLabel);
    }

    @Nonnull
    @RequestCached
    public SelectItem[] getBillingUnits() {
        return getSelectItems(BillUnit.values(),
                              true,
                              rentedEquipmentExpenseTranslationService.getSelectBillUnitMethod(),
                              billUnitTranslationService::getBillUnitLabel);
    }

    @Profile
    public void saveRentedEquipment() {
        final Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap();
        final String action = params.get("action");
        validate();
        rentedEquipmentProcessor.saveRentedEquipment(equipmentModel, project, action);
        equipmentModel.getRentedEquipmentDocs()
                .stream()
                .filter((url) -> (equipmentModel.getInputStreams().containsKey(url.getUrl())))
                .forEach((url) -> {
                    dataRepositoryService.set(equipmentModel.getInputStreams().get(url.getUrl()),
                                              url.getUrl(), url
                                              .getContentType(),
                                              url.getContentSize());
                });

        if (action.equals("approve")) {
            successMessageModel = new SuccessMessageModel();
            successMessageModel
                    .setSuccessMessage(rentedEquipmentExpenseTranslationService.getRentedEquipApprovedSuccessfully());
            addSuccessMessage(rentedEquipmentExpenseTranslationService.getRentedEquipApprovedSuccessfully());

        } else {
            successMessageModel = new SuccessMessageModel();
            successMessageModel
                    .setSuccessMessage(rentedEquipmentExpenseTranslationService.getRentedEquipSavedSuccessfully());
            addSuccessMessage(rentedEquipmentExpenseTranslationService.getRentedEquipSavedSuccessfully());

        }

        init_rentedEquipment();
        updateRentedEquipId();

    }

    private void validate() {
        assertNotNull(equipmentModel.getSupplier(), () -> rentedEquipmentExpenseTranslationService
                .getSuppierRequiredLabel());
        assertNotNull(equipmentModel.getEquipmentName(), () -> rentedEquipmentExpenseTranslationService
                .getEquipmentNameRequiredLabel());
        assertNotNull(equipmentModel.getSerialNumber(), () -> rentedEquipmentExpenseTranslationService
                .getSerialNumberRequiredLabel());
        assertNotNull(equipmentModel.getInvoiceNumber(), () -> rentedEquipmentExpenseTranslationService
                .getInvoiceNumberRequiredLabel());

    }

    public void editExpense(@Nonnull final String equipmentUUID) {
        if (isNullOrEmpty(equipmentUUID)) {
            return;
        }
        final RentedEquipmentExpense equipmentExpense = rentedEquipmentExpenseService
                .getRentedEquipmentExpense(equipmentUUID);
        equipmentModel = rentedEquipmentExpenseModelConverter
                .convertToModel(equipmentExpense, taxRates);
    }
}
