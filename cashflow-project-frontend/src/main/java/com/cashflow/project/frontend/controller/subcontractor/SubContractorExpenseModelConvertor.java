package com.cashflow.project.frontend.controller.subcontractor;

import com.cashflow.accountpayables.domain.supplier.Supplier;
import com.cashflow.accountreceivables.domain.customer.Customer;
import com.cashflow.accountreceivables.service.api.customer.CustomerService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.ProjectType;
import com.cashflow.project.domain.project.expense.subcontractor.Categorization;
import com.cashflow.project.domain.project.expense.subcontractor.SubContractorExpense;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.milestone.ProjectMilestone;
import com.cashflow.project.domain.project.phase.ProjectPhase;
import com.cashflow.project.domain.project.task.ProjectTask;
import com.cashflow.project.frontend.controller.model.SubContractorExpenseModel;
import com.cashflow.salestax.domain.taxrate.TaxRate;
import com.cashflow.useraccount.domain.businessunit.Department;
import com.cashflow.useraccount.service.api.businessunit.department.DepartmentService;
import com.anosym.common.Amount;
import com.anosym.common.Currency;
import com.anosym.profiler.Profile;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 *
 * 
 * @since Jan 5, 2017, 12:07:33 PM
 */
@Dependent
public class SubContractorExpenseModelConvertor implements Serializable {

    private static final long serialVersionUID = -890538729942236961L;

    @Inject
    private CustomerService customerService;

    @Inject
    private DepartmentService departmentService;

    private Map<String, ProjectLevel<?>> levels;

    public SubContractorExpenseModel toModel(@Nullable final SubContractorExpense subContractorExpense,
                                             @Nonnull final Project project,
                                             @Nonnull final List<Supplier> suppliers,
                                             @Nonnull final List<TaxRate> taxRates) {
        final SubContractorExpenseModel model = new SubContractorExpenseModel();

        if (null == subContractorExpense) {
            return model;
        }

        model.setSubContractorExpenseUUID(subContractorExpense.getUuid());

        setProjectValues(model, project);

        model.setDate(subContractorExpense.getExpenseDate());
        model.setSubContractorExpenseID(subContractorExpense.getExpenseScheduleId());

        levels = new HashMap<>();
        levels = getProjectLevels(subContractorExpense.getProjectLevel());

        levels
                .entrySet()
                .forEach(level -> {
                    if (level.getValue() instanceof ProjectPhase) {
                        model.setPhase(((ProjectPhase) level.getValue()).getUuid());
                    } else if (level.getValue() instanceof ProjectMilestone) {
                        model.setMilestone(((ProjectMilestone) level.getValue()).getUuid());
                    } else if (level.getValue() instanceof ProjectTask) {
                        model.setTask(((ProjectTask) level.getValue()).getUuid());
                        model.setTaskID(((ProjectTask) level.getValue()).getId());
                    }
                });

        final Supplier supplier = suppliers.stream()
                .filter((sup) -> sup.getUuid().equals(subContractorExpense.getSupplierUUID()))
                .findFirst()
                .orElse(null);

        if (null != supplier) {
            model.setSupplierID(supplier.getSupplierNumber());
            model.setSupplierUUID(subContractorExpense.getSupplierUUID());
        }

        model.setSubContractorService(subContractorExpense.getSubContractorService());
        model.setDescription(subContractorExpense.getDescription());

        model.setInvoiceNumber(subContractorExpense.getInvoiceNumber());
        model.setInvoiceDueDate(subContractorExpense.getInvoiceDueDate());

        model.setInvoiceAmount(subContractorExpense.getInvoiceAmount().getValue());

        model.setTaxId(subContractorExpense.getTaxId());
        model.setTaxId2(subContractorExpense.getTaxId2());

        updateInvoiceTotal(model, taxRates);

        setCostValues(subContractorExpense, model);

        model.setNotes(subContractorExpense.getNotes());
        model.setExpenseDocs(new ArrayList<>(subContractorExpense.getExpenseDocuments()));
        model.setExpenseStatus(subContractorExpense.getExpenseStatus());

        return model;
    }

    @Nonnull
    private Map<String, ProjectLevel<?>> getProjectLevels(@Nullable final ProjectLevel<?> level) {
        if (level == null) {
            return levels;
        }
        levels.put(level.getUuid(), level);
        getProjectLevels(level.getParentLevel());
        return levels;
    }

    @Profile
    private void setProjectValues(@Nonnull final SubContractorExpenseModel subContractorExpenseModel,
                                  @Nonnull final Project project) {

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

    private void setCostValues(@Nonnull final SubContractorExpense expense,
                               @Nonnull final SubContractorExpenseModel subContractorExpenseModel) {

        final Categorization categorization = expense.getCategorization();

        subContractorExpenseModel.setEquipmentValue(categorization.getEquipmentExpense().getValue());
        subContractorExpenseModel.setEquipmentMarkUpValue(categorization.getEquipmentMarkUp());
        subContractorExpenseModel.setEquipmentNonBillableValue(categorization.getEquipmentNonBillable().getValue());

        final Amount equipmentAmount = categorization.getEquipmentExpense()
                .subtract(categorization.getEquipmentNonBillable());

        final Amount equipmentMarkUp = equipmentAmount
                .multiply(0.01)
                .multiply(categorization.getEquipmentMarkUp()).scale(2);

        subContractorExpenseModel.setEquipmentMarkUpAmount(equipmentMarkUp);

        final Amount equipmentBillable = equipmentMarkUp.add(equipmentAmount);

        subContractorExpenseModel.setEquipmentBillableAmount(equipmentBillable);

        subContractorExpenseModel.setMaterialValue(categorization.getMaterialExpense().getValue());
        subContractorExpenseModel.setMaterialMarkUpValue(categorization.getMaterialMarkUp());
        subContractorExpenseModel.setMaterialNonBillableValue(categorization.getMaterialNonBillable().getValue());

        final Amount materialAmount = categorization.getMaterialExpense()
                .subtract(categorization.getMaterialNonBillable());

        final Amount materialMarkUp = materialAmount
                .multiply(0.01)
                .multiply(categorization.getMaterialMarkUp()).scale(2);

        subContractorExpenseModel.setMaterialMarkUpAmount(materialMarkUp);

        final Amount materialBillable = materialMarkUp.add(materialAmount);

        subContractorExpenseModel.setMaterialBillableAmount(materialBillable);

        subContractorExpenseModel.setLabourValue(categorization.getLabourExpense().getValue());
        subContractorExpenseModel.setLabourMarkUpValue(categorization.getLabourMarkUp());
        subContractorExpenseModel.setLaborNonBillableValue(categorization.getLabourNonBillable().getValue());

        final Amount labourAmount = categorization.getLabourExpense()
                .subtract(categorization.getLabourNonBillable());

        final Amount labourMarkUp = labourAmount
                .multiply(0.01)
                .multiply(categorization.getLabourMarkUp()).scale(2);

        subContractorExpenseModel.setLabourMarkUpAmount(labourMarkUp);

        final Amount labourBillable = labourMarkUp.add(labourAmount);

        subContractorExpenseModel.setLaborBillableAmount(labourBillable);

        subContractorExpenseModel.setTotalMarkUpAmount(labourMarkUp
                .add(equipmentMarkUp)
                .add(materialMarkUp));
        subContractorExpenseModel.setTotalBillableAmount(labourBillable
                .add(materialBillable)
                .add(equipmentBillable));
        subContractorExpenseModel.setNonBillableAmount(categorization.getMaterialNonBillable()
                .add(categorization.getLabourNonBillable())
                .add(categorization.getEquipmentNonBillable()));

    }

    private void updateInvoiceTotal(@Nonnull final SubContractorExpenseModel contractorExpenseModel,
                                    @Nonnull final List<TaxRate> taxRates) {
        final Currency currency = contractorExpenseModel.getCurrency();

        final Amount invoiceAmount = toAmount(currency, contractorExpenseModel.getInvoiceAmount());

        contractorExpenseModel.setInvoiceTotal(invoiceAmount);

        final TaxRate tr = Optional
                .ofNullable(contractorExpenseModel.getTaxId())
                .map((tax) -> taxRates
                        .stream()
                        .filter((tr_) -> tr_.getTaxId().equals(tax))
                        .findFirst()
                        .orElse(null))
                .orElse(null);

        final BigDecimal taxValue1 = Optional
                .ofNullable(tr)
                .map((rate) -> invoiceAmount
                        .multiply(0.01)
                        .multiply(rate.getRate().setScale(2, RoundingMode.HALF_DOWN))
                        .scale(2)
                        .getValue())
                .orElse(BigDecimal.ZERO);
        contractorExpenseModel.setTaxValue(taxValue1);

        final TaxRate tr2 = Optional
                .ofNullable(contractorExpenseModel.getTaxId2())
                .map((tax) -> taxRates
                        .stream()
                        .filter((tr_) -> tr_.getTaxId().equals(tax))
                        .findFirst()
                        .orElse(null))
                .orElse(null);
        final BigDecimal taxValue2 = Optional
                .ofNullable(tr2)
                .map((rate) -> invoiceAmount
                        .multiply(0.01)
                        .multiply(rate.getRate().setScale(2, RoundingMode.HALF_DOWN))
                        .scale(2)
                        .getValue())
                .orElse(BigDecimal.ZERO);
        contractorExpenseModel.setTaxValue2(taxValue2);

        contractorExpenseModel.setInvoiceTotal(contractorExpenseModel.getInvoiceTotal()
                .add(toAmount(currency, taxValue1))
                .add(toAmount(currency, taxValue2)));
    }

    @Nonnull
    private Amount toAmount(@Nonnull final Currency currency, @Nullable final BigDecimal value) {
        BigDecimal zeroValue = BigDecimal.ZERO;
        if (null == value) {
            return new Amount(currency, zeroValue);
        } else {
            return new Amount(currency, value);
        }
    }

}
