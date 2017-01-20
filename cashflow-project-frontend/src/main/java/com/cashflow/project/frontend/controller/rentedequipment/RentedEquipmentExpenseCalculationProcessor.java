package com.cashflow.project.frontend.controller.rentedequipment;

import com.cashflow.project.domain.project.personnel.MarkUpMethod;
import com.cashflow.project.frontend.controller.model.RentedEquipmentExpenseModel;
import com.cashflow.salestax.domain.taxrate.TaxRate;
import com.anosym.common.Amount;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.enterprise.context.Dependent;

/**
 *
 * 
 * @since 19 Dec, 2016, 12:07:37 PM
 */
@Dependent
public class RentedEquipmentExpenseCalculationProcessor implements Serializable {

    private static final long serialVersionUID = -1991663787757166514L;

    public void calculateSupplierInvoice(@Nonnull final RentedEquipmentExpenseModel rentedEquipmentExpenseModel,
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

    public void calculateBillableRate(@Nonnull final RentedEquipmentExpenseModel rentedEquipmentExpenseModel) {
        if (rentedEquipmentExpenseModel.getMarkUpMethod() == MarkUpMethod.ABSOLUTE) {
            rentedEquipmentExpenseModel.setBillRate(rentedEquipmentExpenseModel.getCostRate()
                    .add(rentedEquipmentExpenseModel.getMarkUpValue()).setScale(2, RoundingMode.HALF_DOWN));
        } else if (rentedEquipmentExpenseModel.getMarkUpMethod() == MarkUpMethod.PERCENTAGE) {
            rentedEquipmentExpenseModel.setBillRate(rentedEquipmentExpenseModel.getCostRate()
                    .add(rentedEquipmentExpenseModel.getCostRate()
                            .multiply(rentedEquipmentExpenseModel.getMarkUpValue())
                            .multiply(new BigDecimal(0.01))).setScale(2, RoundingMode.HALF_DOWN));
        }
    }

    public void calculateCostRate(@Nonnull final RentedEquipmentExpenseModel rentedEquipmentExpenseModel) {
        if (rentedEquipmentExpenseModel.getQty() != 0) {
            rentedEquipmentExpenseModel.setCostRate(rentedEquipmentExpenseModel.getSupplierInvoiceTotal().getValue()
                    .divide(
                            new BigDecimal(rentedEquipmentExpenseModel.getQty()), 2, RoundingMode.HALF_DOWN).setScale(2,
                                                                                                                      RoundingMode.HALF_DOWN));
        }
    }

    public void calculateBillableAmount(@Nonnull final RentedEquipmentExpenseModel rentedEquipmentExpenseModel) {
        rentedEquipmentExpenseModel.setBillableAmount(new Amount(rentedEquipmentExpenseModel.getCurrency(),
                                                                 rentedEquipmentExpenseModel.getBillRate().multiply(
                                                                         new BigDecimal(
                                                                                 rentedEquipmentExpenseModel
                                                                                         .getBillableQty()))));

        rentedEquipmentExpenseModel.setNonBillableAmount(new Amount(rentedEquipmentExpenseModel.getCurrency(),
                                                                    rentedEquipmentExpenseModel.getCostRate().multiply(
                                                                            new BigDecimal(
                                                                                    rentedEquipmentExpenseModel
                                                                                            .getNonBillableQty())))
                .scale(2));
    }

}
