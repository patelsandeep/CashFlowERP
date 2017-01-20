package com.cashflow.project.frontend.controller.rentedequipment;

import com.cashflow.accountpayables.domain.supplier.Supplier;
import com.cashflow.accountpayables.service.api.supplier.SupplierService;
import com.cashflow.accountreceivables.domain.customer.Customer;
import com.cashflow.accountreceivables.service.api.customer.CustomerService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.ProjectType;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.milestone.ProjectMilestone;
import com.cashflow.project.domain.project.personnel.MarkUpMethod;
import com.cashflow.project.domain.project.phase.ProjectPhase;
import com.cashflow.project.domain.project.rentedequipment.RentedEquipmentExpense;
import com.cashflow.project.domain.project.task.ProjectTask;
import com.cashflow.project.frontend.controller.model.RentedEquipmentExpenseModel;
import com.cashflow.salestax.domain.taxrate.TaxRate;
import com.cashflow.useraccount.domain.businessunit.Department;
import com.cashflow.useraccount.service.api.businessunit.department.DepartmentService;
import com.anosym.common.Amount;
import com.anosym.profiler.Profile;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
 * @since Dec 24, 2016, 2:56:12 PM
 */
@Dependent
public class RentedEquipmentExpenseModelConverter implements Serializable {

    private static final long serialVersionUID = 1312148307557023830L;

    private final Map<String, ProjectLevel<?>> levels = new HashMap<>();

    @Inject
    private CustomerService customerService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private SupplierService supplierService;

    @Nonnull
    @Profile
    public RentedEquipmentExpenseModel convertToModel(@Nullable final RentedEquipmentExpense rentedEquipmentExpense,
                                                      @Nonnull final List<TaxRate> taxRates) {
        final RentedEquipmentExpenseModel model = new RentedEquipmentExpenseModel();

        if (null == rentedEquipmentExpense) {
            return model;
        }

        model.setRentedEquipmentUUID(rentedEquipmentExpense.getUuid());
        model.setDate(rentedEquipmentExpense.getEquipmentDate());
        model.setRentedEquipmentId(rentedEquipmentExpense.getEquipmentId());
        model.setPhaseName("All");
        model.setMilestoneName("All");
        model.setTaskName("All");
        setProjectLevels(rentedEquipmentExpense.getProjectLevel());

        levels
                .entrySet()
                .forEach(level -> {
                    if (level.getValue() instanceof Project) {
                        model.setProject(((Project) level.getValue()).getName());
                        model.setCustomer(getCustomerValue((Project) level.getValue()));
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

        final Supplier supplier = supplierService
                .findSupplier(rentedEquipmentExpense.getSupplierUUID());
        if (null != supplier) {
            model.setSupplier(supplier.getUuid());
            model.setSupplierName(supplier.getName());
            model.setSupplierId(supplier.getSupplierNumber());
        }

        model.setEquipmentName(rentedEquipmentExpense.getEquipmentName());
        model.setSerialNumber(rentedEquipmentExpense.getSerialNumber());
        model.setInvoiceNumber(rentedEquipmentExpense.getInvoiceNumber());
        model.setInvocieDueDate(rentedEquipmentExpense.getInvoiceDueDate());
        model.setSupplierInvoiceAmount(rentedEquipmentExpense.getSupplierInvoiceAmount().getValue());
        model.setCurrency(rentedEquipmentExpense.getSupplierInvoiceAmount().getCurrency());
        model.setTaxId(rentedEquipmentExpense.getTaxId());
        model.setTaxId2(rentedEquipmentExpense.getTaxId2());

        calculateSupplierInvoice(model, taxRates);

        model.setMarkUpMethod(rentedEquipmentExpense.getMarkUpMethod());
        model.setMarkUpValue(rentedEquipmentExpense.getMarkUp());
        model.setCostRate(rentedEquipmentExpense.getCostRate().getValue());
        model.setBillingUnit(rentedEquipmentExpense.getBillUnit());

        calculateBillableRate(model);

        model.setBillableQty(rentedEquipmentExpense.getBillableQty());
        model.setNonBillableQty(rentedEquipmentExpense.getNonBillableQty());
        model.setQty(rentedEquipmentExpense.getBillableQty() + rentedEquipmentExpense.getNonBillableQty());

        calculateCostRate(model);
        calculateBillableAmount(model);

        model.setNotes(rentedEquipmentExpense.getNotes());
        model.setRentedEquipmentDocs(rentedEquipmentExpense.getEquipmentDocuments());

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

    private void calculateSupplierInvoice(@Nonnull final RentedEquipmentExpenseModel rentedEquipmentExpenseModel,
                                          @Nonnull final List<TaxRate> taxRates) {
        Amount supplierInvoiceAmount = new Amount(rentedEquipmentExpenseModel.getCurrency(), BigDecimal.ZERO);
        final BigDecimal supplierPrice = rentedEquipmentExpenseModel.getSupplierInvoiceAmount();
        final TaxRate tr = Optional
                .ofNullable(rentedEquipmentExpenseModel.getTaxId())
                .map((tax) -> taxRates
                .stream()
                .filter((tr_) -> tr_.getTaxId().equals(tax))
                .findFirst()
                .orElse(null))
                .orElse(null);
        final BigDecimal taxTotal = Optional
                .ofNullable(tr)
                .map((rate) -> supplierPrice
                .multiply(new BigDecimal(0.01))
                .multiply(rate.getRate()).setScale(2, RoundingMode.HALF_DOWN))
                .orElse(BigDecimal.ZERO);

        final TaxRate tr2 = Optional
                .ofNullable(rentedEquipmentExpenseModel.getTaxId2())
                .map((tax) -> taxRates
                .stream()
                .filter((tr_) -> tr_.getTaxId().equals(tax))
                .findFirst()
                .orElse(null))
                .orElse(null);

        final BigDecimal tax2Total = Optional
                .ofNullable(tr2)
                .map((rate) -> supplierPrice
                .multiply(new BigDecimal(0.01))
                .multiply(rate.getRate()).setScale(2, RoundingMode.HALF_DOWN))
                .orElse(BigDecimal.ZERO);

        final Amount invoiceTotal = new Amount(rentedEquipmentExpenseModel.getCurrency(), supplierPrice).scale(2);
        final Amount taxAmount = new Amount(rentedEquipmentExpenseModel.getCurrency(), taxTotal).scale(2);
        final Amount tax2Amount = new Amount(rentedEquipmentExpenseModel.getCurrency(), tax2Total).scale(2);

        rentedEquipmentExpenseModel.setTaxAmount(taxAmount);
        rentedEquipmentExpenseModel.setTaxAmount2(tax2Amount);

        supplierInvoiceAmount = supplierInvoiceAmount.add(invoiceTotal.add(taxAmount).add(tax2Amount));
        rentedEquipmentExpenseModel.setSupplierInvoiceTotal(supplierInvoiceAmount);

    }

    private void calculateBillableRate(@Nonnull final RentedEquipmentExpenseModel rentedEquipmentExpenseModel) {
        if (rentedEquipmentExpenseModel.getMarkUpMethod() == MarkUpMethod.ABSOLUTE) {
            rentedEquipmentExpenseModel.setBillRate(rentedEquipmentExpenseModel.getCostRate()
                    .add(rentedEquipmentExpenseModel.getMarkUpValue()));
        } else if (rentedEquipmentExpenseModel.getMarkUpMethod() == MarkUpMethod.PERCENTAGE) {
            rentedEquipmentExpenseModel.setBillRate(rentedEquipmentExpenseModel.getCostRate()
                    .add(rentedEquipmentExpenseModel.getCostRate()
                            .multiply(rentedEquipmentExpenseModel.getMarkUpValue())
                            .multiply(new BigDecimal(0.01))).setScale(2, RoundingMode.HALF_DOWN));
        }
    }

    private void calculateCostRate(@Nonnull final RentedEquipmentExpenseModel rentedEquipmentExpenseModel) {
        if (rentedEquipmentExpenseModel.getQty() != 0) {
            rentedEquipmentExpenseModel.setCostRate(rentedEquipmentExpenseModel.getSupplierInvoiceTotal()
                    .divide(rentedEquipmentExpenseModel.getQty()).scale(2).getValue());
        }
    }

    private void calculateBillableAmount(@Nonnull final RentedEquipmentExpenseModel rentedEquipmentExpenseModel) {
        rentedEquipmentExpenseModel.setBillableAmount(new Amount(rentedEquipmentExpenseModel.getCurrency(),
                                                                 rentedEquipmentExpenseModel.getBillRate())
                .multiply(rentedEquipmentExpenseModel.getBillableQty()).scale(2));

        rentedEquipmentExpenseModel.setNonBillableAmount(new Amount(rentedEquipmentExpenseModel.getCurrency(),
                                                                    rentedEquipmentExpenseModel.getCostRate())
                .multiply(rentedEquipmentExpenseModel.getNonBillableQty()).scale(2));
    }

}
