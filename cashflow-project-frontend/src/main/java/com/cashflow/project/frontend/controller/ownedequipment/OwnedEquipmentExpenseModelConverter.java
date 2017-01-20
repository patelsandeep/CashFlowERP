package com.cashflow.project.frontend.controller.ownedequipment;

import com.cashflow.accountreceivables.domain.customer.Customer;
import com.cashflow.accountreceivables.service.api.customer.CustomerService;
import com.cashflow.fixedasset.api.FixedAssetService;
import com.cashflow.fixedasset.domain.FixedAsset;
import com.cashflow.fixedasset.domain.FixedAssetResult;
import com.cashflow.fixedasset.domain.FixedAssetSearchContext;
import com.cashflow.fixedasset.domain.FixedAssetSubCategories;
import com.cashflow.fixedasset.domain.SearchResult;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.ProjectType;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.milestone.ProjectMilestone;
import com.cashflow.project.domain.project.ownedequipment.OwnedEquipmentDetail;
import com.cashflow.project.domain.project.ownedequipment.OwnedEquipmentExpense;
import com.cashflow.project.domain.project.phase.ProjectPhase;
import com.cashflow.project.domain.project.task.ProjectTask;
import com.cashflow.project.frontend.controller.model.EquipmentDetailModel;
import com.cashflow.project.frontend.controller.model.OwnedEquipmentExpenseModel;
import com.cashflow.useraccount.domain.businessunit.Department;
import com.cashflow.useraccount.service.api.businessunit.department.DepartmentService;
import com.anosym.profiler.Profile;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 *
 * 
 * @since Dec 24, 2016, 12:23:04 PM
 */
@Dependent
public class OwnedEquipmentExpenseModelConverter implements Serializable {

    private static final long serialVersionUID = -2921933003741663255L;

    private final Map<String, ProjectLevel<?>> levels = new HashMap<>();

    @Inject
    private CustomerService customerService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private FixedAssetService fixedAssetService;

    @Profile
    @Nonnull
    public OwnedEquipmentExpenseModel convertToModel(@Nullable final OwnedEquipmentExpense ownedEquipmentExpense) {
        final OwnedEquipmentExpenseModel model = new OwnedEquipmentExpenseModel();
        setCurrentWeekDays(model);
        if (null == ownedEquipmentExpense) {
            return model;
        }
        model.setOwnedEquipmentUUID(ownedEquipmentExpense.getUuid());
        model.setOwnedEquipmentId(ownedEquipmentExpense.getEquipmentId());
        model.setPhaseName("All");
        model.setMilestoneName("All");
        model.setTaskName("All");
        setProjectLevels(ownedEquipmentExpense.getProjectLevel());
        levels
                .entrySet()
                .forEach(level -> {
                    if (level.getValue() instanceof Project) {
                        final Project project = (Project) level.getValue();
                        model.setProject(project.getName());
                        model.setCurrency(project.getCurrency());
                        model.setCustomer(getCustomerValue(project));
                    } else if (level.getValue() instanceof ProjectPhase) {
                        model.setPhaseName(((ProjectPhase) level.getValue()).getName());
                        model.setPhase(((ProjectPhase) level.getValue()).getUuid());
                    } else if (level.getValue() instanceof ProjectMilestone) {
                        model.setMilestoneName(((ProjectMilestone) level.getValue())
                                .getName());
                        model.setMilestone(((ProjectMilestone) level.getValue())
                                .getUuid());
                    } else if (level.getValue() instanceof ProjectTask) {
                        model.setTask(((ProjectTask) level.getValue()).getUuid());
                        model.setTaskName(((ProjectTask) level.getValue()).getName());
                        model.setTaskId(((ProjectTask) level.getValue()).getId());
                    }
                });
        final List<EquipmentDetailModel> models = ownedEquipmentExpense.getOwnedEquipmentDetails()
                .stream()
                .map((expense) -> prepareModelExpense(expense))
                .collect(Collectors.toList());

        model.setEquipmentDetailModels(models);

        return model;
    }

    private void setProjectLevels(@Nullable final ProjectLevel<?> level) {
        if (level == null) {
            return;
        }
        levels.put(level.getUuid(), level);
        setProjectLevels(level.getParentLevel());
    }

    @Nullable
    private String getCustomerValue(@Nonnull final Project project) {
        if (project.getProjectType() == ProjectType.CUSTOMER_PROJECT) {
            final Customer customer = customerService
                    .getCustomer(project.getCustomerUUID());
            return customer.getName();
        }

        if (project.getProjectType() == ProjectType.INTERNAL_PROJECT) {
            final Department department = departmentService
                    .findDepartment(
                            project.getCustomerUUID());
            return department.getName();
        }
        return null;
    }

    private void setCurrentWeekDays(@Nonnull final OwnedEquipmentExpenseModel ownedEquipmentExpenseModel) {
        final Calendar start = Calendar.getInstance();
        start.set(Calendar.DAY_OF_WEEK, start.getFirstDayOfWeek());
        ownedEquipmentExpenseModel.setWeekStartDate(start);
        final Calendar end = Calendar.getInstance();
        end.set(Calendar.DAY_OF_WEEK, end.getFirstDayOfWeek());
        end.add(Calendar.DAY_OF_MONTH, 6);
        ownedEquipmentExpenseModel.setWeekEndingDate(end);
    }

    @Nonnull
    private EquipmentDetailModel prepareModelExpense(@Nonnull final OwnedEquipmentDetail ownedEquipmentDetail) {
        final EquipmentDetailModel detailModel = new EquipmentDetailModel();

        detailModel.setEquipmentDate(ownedEquipmentDetail.getEquipmentDate());

        final FixedAsset asset = fixedAssetService
                .findByUUIDOrAssetIDOrSerialNumber(ownedEquipmentDetail.getFixedAssetUUID());

        detailModel.setOwnedEquipment(asset);
        detailModel.setEquipmentId(asset.getAssetItemID());
        detailModel.setEquipmentCategory(asset.getCategory());

        setSubCategories(detailModel);

        detailModel.setEquipmentSubCategory(asset.getSubCategory());

        setEquipments(detailModel);

        detailModel.setCostRate(asset.getCostRateAmount());
        detailModel.setBillRate(ownedEquipmentDetail.getBillRate().getValue());
        detailModel.setBillable(ownedEquipmentDetail.getBillable());
        detailModel.setUnit(ownedEquipmentDetail.getUnit());
        detailModel.setEquipmentAmount(ownedEquipmentDetail.getBillRate()
                .multiply(ownedEquipmentDetail.getUnit()));
        detailModel.setEquipmentDocs(ownedEquipmentDetail.getEquipmentDocuments());

        return detailModel;
    }

    private void setSubCategories(@Nullable final EquipmentDetailModel detailModel) {
        if (null != detailModel) {
            final List<FixedAssetSubCategories> subList = FixedAssetSubCategories.getFixedAssetSubcategories(detailModel
                    .getEquipmentCategory());
            detailModel.getSubCatList().clear();
            detailModel.getSubCatList().addAll(subList);
        }

    }

    public void setEquipments(@Nullable final EquipmentDetailModel detailModel) {
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

}
