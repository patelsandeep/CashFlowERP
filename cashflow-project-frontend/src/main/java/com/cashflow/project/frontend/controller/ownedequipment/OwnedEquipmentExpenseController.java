package com.cashflow.project.frontend.controller.ownedequipment;

import com.cashflow.access.authorization.LoggedInBusinessAccount;
import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.access.authorization.uniqueid.IdContext;
import com.cashflow.access.authorization.uniqueid.UniqueIdGenerator;
import com.cashflow.accountreceivables.domain.customer.Customer;
import com.cashflow.accountreceivables.service.api.customer.CustomerService;
import com.cashflow.entitydomains.FileUrl;
import com.cashflow.entitydomains.facade.context.Context;
import com.cashflow.fixedasset.api.FixedAssetService;
import com.cashflow.fixedasset.domain.FixedAsset;
import com.cashflow.fixedasset.domain.FixedAssetCategories;
import com.cashflow.fixedasset.domain.FixedAssetResult;
import com.cashflow.fixedasset.domain.FixedAssetSearchContext;
import com.cashflow.fixedasset.domain.FixedAssetSubCategories;
import com.cashflow.fixedasset.domain.SearchResult;
import com.cashflow.frontend.support.cacheing.requestcached.RequestCached;
import com.cashflow.frontend.support.httpparameter.HttpParameter;
import com.cashflow.project.api.milestone.ProjectMilestoneService;
import com.cashflow.project.api.ownedequipment.OwnedEquipmentExpenseService;
import com.cashflow.project.api.phase.ProjectPhaseService;
import com.cashflow.project.api.project.ProjectService;
import com.cashflow.project.api.task.ProjectTaskService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.ProjectType;
import com.cashflow.project.domain.project.expense.BillableType;
import com.cashflow.project.domain.project.expense.ExpenseReport;
import com.cashflow.project.domain.project.milestone.MilestoneContext;
import com.cashflow.project.domain.project.milestone.ProjectMilestone;
import com.cashflow.project.domain.project.ownedequipment.OwnedEquipmentExpense;
import com.cashflow.project.domain.project.phase.PhaseContext;
import com.cashflow.project.domain.project.phase.ProjectPhase;
import com.cashflow.project.domain.project.task.ProjectTask;
import com.cashflow.project.domain.project.task.TaskContext;
import com.cashflow.project.frontend.controller.model.EquipmentDetailModel;
import com.cashflow.project.frontend.controller.model.ModelViewScopedController;
import com.cashflow.project.frontend.controller.model.OwnedEquipmentExpenseModel;
import com.cashflow.project.frontend.controller.model.SuccessMessageModel;
import com.cashflow.project.translation.expenses.BillableTypeTranslationService;
import com.cashflow.project.translation.ownedequipment.OwnedEquipmentExpenseTranslationService;
import com.cashflow.useraccount.domain.businessaccount.BusinessAccount;
import com.cashflow.useraccount.domain.businessunit.Department;
import com.cashflow.useraccount.service.api.businessunit.department.DepartmentService;
import com.anosym.common.Amount;
import com.anosym.profiler.Profile;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * @since 14 Dec, 2016, 10:27:03 AM
 */
@ModelViewScopedController
public class OwnedEquipmentExpenseController implements Serializable {

    private static final long serialVersionUID = -4706175553531650268L;

    @Inject
    private Logger logger;

    @Inject
    private OwnedEquipmentExpenseTranslationService ownedEquipmentExpenseTranslationService;

    @Inject
    private BillableTypeTranslationService billableTypeTranslationService;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private OwnedEquipmentExpenseService ownedEquipmentExpenseService;

    @Inject
    private OwnedEquipmentExpenseModelConverter ownedEquipmentExpenseModelConverter;

    @Inject
    private OwnedEqupmentExpenseProcessor ownedEqupmentProcessor;

    @Inject
    private ProjectService projectService;

    @Inject
    private CustomerService customerService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private ProjectTaskService projectTaskService;

    @Inject
    private ProjectMilestoneService milestoneService;

    @Inject
    private FixedAssetService fixedAssetService;

    @Inject
    private ProjectPhaseService projectPhaseService;

    @Inject
    @HttpParameter(SELECTED_PROJECT_UUID)
    private Instance<String> pUUID;

    @Inject
    @IdContext(forDomain = ExpenseReport.class, prefix = "ER", length = 5)
    private UniqueIdGenerator uniqueIdGenerator;

    @Getter
    @Setter
    private Project project;

    @Getter
    @Setter
    private SuccessMessageModel successMessageModel;

    @Getter
    @Setter
    private OwnedEquipmentExpenseModel ownedEquipmentExpenseModel;

    @Getter
    private Map<String, EquipmentDetailModel> equipmentDetailModel;

    @Getter
    @Inject
    @LoggedInBusinessAccount
    private Instance<BusinessAccount> businessAccount;

    private static int count = 001;

    @PostConstruct
    void init_equipment() {
        final String projectUUID = checkNotNull(pUUID.get(), "Project not selected for Owned Equipment");
        ownedEquipmentExpenseModel = new OwnedEquipmentExpenseModel();

        final Future<Project> projectRequest = asynchronousService.execute(() -> checkNotNull(projectService
                .getProject(projectUUID), "Failed to load project for uuid: %s", projectUUID));

        try {
            project = projectRequest.get();
            setProjectValues(projectRequest.get());
        } catch (InterruptedException | ExecutionException ex) {
            throw Throwables.propagate(ex);
        }

        addEquipmentData();
        setCurrentWeekDays();
    }

    public void addEquipmentData() {
        final EquipmentDetailModel detailModel = new EquipmentDetailModel();
        detailModel.setUuid(UUID.randomUUID().toString());
        this.getOwnedEquipmentExpenseModel().getEquipmentDetailModels().add(detailModel);
    }

    private void setCurrentWeekDays() {
        final Calendar start = Calendar.getInstance();
        start.set(Calendar.DAY_OF_WEEK, start.getFirstDayOfWeek());
        ownedEquipmentExpenseModel.setWeekStartDate(start);
        final Calendar end = Calendar.getInstance();
        end.set(Calendar.DAY_OF_WEEK, end.getFirstDayOfWeek());
        end.add(Calendar.DAY_OF_MONTH, 6);
        ownedEquipmentExpenseModel.setWeekEndingDate(end);
    }

    private void setProjectValues(@Nonnull final Project project) {

        ownedEquipmentExpenseModel.setProject(project.getName());
        ownedEquipmentExpenseModel.setCurrency(project.getCurrency());
        if (project.getProjectType() == ProjectType.CUSTOMER_PROJECT) {
            final Customer customer = customerService.getCustomer(project.getCustomerUUID());
            ownedEquipmentExpenseModel.setCustomer(customer.getName());
        } else if (project.getProjectType() == ProjectType.INTERNAL_PROJECT) {
            final Department department = departmentService.findDepartment(project.getCustomerUUID());
            ownedEquipmentExpenseModel.setCustomer(department.getName());
        }
    }

    public void updateOwnedEquipmentId() {
        ownedEquipmentExpenseModel.setOwnedEquipmentId(uniqueIdGenerator.nextId());
        updateTaskValue();
    }

    private void updateTaskValue() {
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
        ownedEquipmentExpenseModel.setTask(task.getUuid());
        ownedEquipmentExpenseModel.setTaskId(task.getId());
        if (null != task.getParentLevel().getParentLevel()) {
            ownedEquipmentExpenseModel.setMilestone(task.getParentLevel().getUuid());
            ownedEquipmentExpenseModel.setPhase(task.getParentLevel().getParentLevel().getUuid());
        }
    }

    public void updateTaskID() {
        if (isNullOrEmpty(ownedEquipmentExpenseModel.getTask())) {
            return;
        }
        final ProjectTask task = projectTaskService.getTask(ownedEquipmentExpenseModel.getTask());
        if (null == task) {
            return;
        }
        ownedEquipmentExpenseModel.setTaskId(task.getId());
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
        if (!isNullOrEmpty(ownedEquipmentExpenseModel.getPhase())) {
            return milestoneService
                    .findByPhaseUUIDs(ImmutableList.of(ownedEquipmentExpenseModel.getPhase()));
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
        if (!isNullOrEmpty(ownedEquipmentExpenseModel.getMilestone())) {
            return projectTaskService
                    .findByMilestoneOrProjectUUIDs(ImmutableList.of(ownedEquipmentExpenseModel.getMilestone()));
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
    public SelectItem[] getBillableType() {
        return getSelectItems(BillableType.values(),
                              true,
                              ownedEquipmentExpenseTranslationService.getBillableTypeLabel(),
                              billableTypeTranslationService::getBillableTypeLabel);
    }

    @Nonnull
    @RequestCached
    public List<EquipmentDetailModel> getEquipmentDetails() {
        final List<EquipmentDetailModel> equipmentDetailModels = this.getOwnedEquipmentExpenseModel()
                .getEquipmentDetailModels();
        equipmentDetailModel = new HashMap<>();
        for (EquipmentDetailModel detailModel : equipmentDetailModels) {
            equipmentDetailModel.put(detailModel.getUuid(), detailModel);
        }
        return equipmentDetailModels;
    }
// I have used this beacause if I was using translation service it was returning Category name for
// display but i want to bind category ID in value to fethch sub-category from selected category

    @Nonnull
    public FixedAssetCategories[] getAllFaParentCategories() {
        return FixedAssetCategories.values();
    }

    @Nonnull
    @RequestCached
    public void getSubCategories(@Nullable final EquipmentDetailModel detailModel) {
        if (null != detailModel) {
            final List<FixedAssetSubCategories> subList = FixedAssetSubCategories.getFixedAssetSubcategories(detailModel
                    .getEquipmentCategory());
            detailModel.getSubCatList().clear();
            detailModel.getSubCatList().addAll(subList);
        }

    }

    public void getEquipments(@Nullable final EquipmentDetailModel detailModel) {
        if (null != detailModel) {
            detailModel.getEquipmentList().clear();
            final FixedAssetResult assetsResult = fixedAssetService
                    .getFixedAssets(FixedAssetSearchContext
                            .builder()
                            .searchResult(SearchResult.RESULT)
                            .subCategoryUUID(detailModel.getEquipmentSubCategory())
                            .build());
            detailModel.setEquipmentList(assetsResult.getFixedAssets());
        }
    }

    @Nonnull
    @RequestCached
    public List<FixedAsset> getEquipmentByID(@Nonnull final String nameQuery) {

        final FixedAssetResult assetsResult = fixedAssetService
                .getFixedAssets(FixedAssetSearchContext
                        .builder()
                        .searchResult(SearchResult.RESULT)
                        .assetItemId(nameQuery.trim())
                        .build());

        return assetsResult.getFixedAssets();
    }

    @Nullable
    public void getEquipmentDetails(@Nullable final EquipmentDetailModel detailModel) {
        if (null == detailModel) {
            return;
        }
        getEquipments(detailModel);
        final FixedAsset asset = detailModel.getOwnedEquipment();
        if (null == asset) {
            return;
        }

        detailModel.setCostRate(null != asset.getCostRateAmount() ? asset.getCostRateAmount() : new Amount(
                ownedEquipmentExpenseModel.getCurrency(),
                BigDecimal.ZERO));
        detailModel.setBillRate(
                null != asset.getCostRateAmount() ? asset.getBillRateAmount().getValue() : BigDecimal.ZERO);
        detailModel.setEquipmentCategory(asset.getCategory());
        getSubCategories(detailModel);
        detailModel.setEquipmentSubCategory(asset.getSubCategory());
        detailModel.setUnit(1);
        calculateEquipmentAmount(detailModel);

    }

    public void calculateEquipmentAmount(@Nullable final EquipmentDetailModel detailModel) {
        final BigDecimal equipmentPrice = detailModel.getBillRate().multiply(new BigDecimal(detailModel.getUnit()));
        final Amount equipmentAmount = new Amount(ownedEquipmentExpenseModel.getCurrency(), equipmentPrice).scale(2);
        detailModel.setEquipmentAmount(equipmentAmount);
    }

    public void fileUpload(@Nonnull final FileUploadEvent event) {

        final String detailModelUUID = (String) event.getComponent().getAttributes().get("equipUUID");
        final EquipmentDetailModel detailModel = equipmentDetailModel.get(detailModelUUID);
        InputStream itemImageInputStream = null;
        try {
            if (null != event.getFile() && null != event.getFile().getInputstream()) {
                itemImageInputStream = event.getFile().getInputstream();

                final String itemUrl = new StringBuilder(businessAccount.get().getUuid())
                        .append("/")
                        .append("projects")
                        .append("/")
                        .append("Owned Equipment")
                        .append("/")
                        .append(ownedEquipmentExpenseModel.getOwnedEquipmentId())
                        .append("/")
                        .append(UUID.randomUUID().toString()).toString();

                detailModel.getEquipmentDocs().add(new FileUrl(itemUrl,
                                                               event.getFile().getContentType(),
                                                               event.getFile().getSize(),
                                                               event.getFile().getFileName(),
                                                               ownedEquipmentExpenseTranslationService
                                                                       .getEquipmentAttachmentsLabel() + count
                ));
                detailModel.getInputStreams().put(itemUrl, itemImageInputStream);
                count++;
                successMessageModel = new SuccessMessageModel();
                successMessageModel.setSuccessMessage(ownedEquipmentExpenseTranslationService
                        .getUploadSuccessMessage());
                addSuccessMessage(ownedEquipmentExpenseTranslationService.getUploadSuccessMessage());

            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    @Profile
    public void saveOwnedEquipment() {
        final Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap();
        final String action = params.get("action");
        validate();
        ownedEqupmentProcessor.saveOwnedEquipment(ownedEquipmentExpenseModel, project, action);
        successMessageModel = new SuccessMessageModel();
        if (action.equals("approve")) {
            successMessageModel.setSuccessMessage(ownedEquipmentExpenseTranslationService
                    .getOwnedEquipmentApprovedSuccessMessage());
            addSuccessMessage(ownedEquipmentExpenseTranslationService.getOwnedEquipmentApprovedSuccessMessage());
        } else {
            successMessageModel.setSuccessMessage(ownedEquipmentExpenseTranslationService
                    .getOwnedEquipmentSavedSuccessMessage());
            addSuccessMessage(ownedEquipmentExpenseTranslationService.getOwnedEquipmentSavedSuccessMessage());
        }

        init_equipment();
        updateOwnedEquipmentId();
    }

    private void validate() {

        assertState(ownedEquipmentExpenseModel.getEquipmentDetailModels().size() > 0,
                    () -> ownedEquipmentExpenseTranslationService
                            .getAtleastOneEquipmentRequried());

        assertNotNull(ownedEquipmentExpenseModel.getEquipmentDetailModels().get(0).getOwnedEquipment(),
                      () -> ownedEquipmentExpenseTranslationService
                              .getAtleastOneEquipmentRequried());

        assertNotNull(ownedEquipmentExpenseModel.getEquipmentDetailModels().get(0).getOwnedEquipment(),
                      () -> ownedEquipmentExpenseTranslationService
                              .getEquipmentIdRequiredMessage()
        );

    }

    @Profile
    public void editExpense(@Nonnull final String equipmentUUID) {
        if (isNullOrEmpty(equipmentUUID)) {
            return;
        }
        final OwnedEquipmentExpense expense = ownedEquipmentExpenseService
                .getExpenseReport(equipmentUUID);
        ownedEquipmentExpenseModel = ownedEquipmentExpenseModelConverter
                .convertToModel(expense);
    }
}
