package com.cashflow.project.frontend.controller.subcontractor;

import com.cashflow.project.frontend.controller.model.SubContractorExpenseModel;
import com.cashflow.project.translation.subcontractor.SubContractorExpenseTranslationService;
import com.cashflow.salestax.domain.taxrate.TaxRate;
import com.anosym.common.Amount;
import com.anosym.common.Currency;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import static com.cashflow.frontend.support.errormessage.ErrorMessagePreconditions.assertState;

/**
 *
 * 
 * @since Dec 29, 2016, 12:46:55 AM
 */
@Dependent
public class SubContractorExpenseCalculator implements Serializable {

    private static final long serialVersionUID = -8185605126568953507L;

    @Inject
    private SubContractorExpenseTranslationService subContractorExpenseTranslationService;

    public void updateAmountValues(@Nonnull final SubContractorExpenseModel contractorExpenseModel,
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
        contractorExpenseModel.setLabourValue(contractorExpenseModel.getInvoiceTotal().getValue());
        updateBillableValues(contractorExpenseModel);
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

    public void updateCatezValues(@Nonnull final SubContractorExpenseModel contractorExpenseModel) {

        if (null == contractorExpenseModel.getEquipmentValue()
                || null == contractorExpenseModel.getLabourValue()
                || null == contractorExpenseModel.getMaterialValue()) {
            return;
        }

        final BigDecimal catezValue = contractorExpenseModel.getEquipmentValue()
                .add(contractorExpenseModel.getLabourValue())
                .add(contractorExpenseModel.getMaterialValue());

        if (null != contractorExpenseModel.getInvoiceTotal()) {
            assertState(catezValue.compareTo(contractorExpenseModel.getInvoiceTotal().getValue()) <= 0,
                        subContractorExpenseTranslationService.getValidationMessageForCategorization());
        }

    }

    public void updateBillableValues(@Nonnull final SubContractorExpenseModel contractorExpenseModel) {
        if (null != contractorExpenseModel.getLabourMarkUpValue()
                && null != contractorExpenseModel.getLabourValue()
                && null != contractorExpenseModel.getLaborNonBillableValue()) {
            final Amount labourAmount = toAmount(contractorExpenseModel.getCurrency(),
                                                 contractorExpenseModel.getLabourValue()
                                                 .subtract(contractorExpenseModel.getLaborNonBillableValue()));
            final Amount labourMarkUp = labourAmount
                    .multiply(0.01)
                    .multiply(contractorExpenseModel.getLabourMarkUpValue()).scale(2);
            contractorExpenseModel.setLabourMarkUpAmount(labourMarkUp);
            contractorExpenseModel.setLaborBillableAmount(labourMarkUp.add(labourAmount));
        }

        if (null != contractorExpenseModel.getMaterialMarkUpValue()
                && null != contractorExpenseModel.getMaterialValue()
                && null != contractorExpenseModel.getMaterialNonBillableValue()) {
            final Amount materialAmount = toAmount(contractorExpenseModel.getCurrency(),
                                                   contractorExpenseModel.getMaterialValue()
                                                   .subtract(contractorExpenseModel
                                                           .getMaterialNonBillableValue()));
            final Amount materialMarkUp = materialAmount
                    .multiply(0.01)
                    .multiply(contractorExpenseModel.getMaterialMarkUpValue()).scale(2);
            contractorExpenseModel.setMaterialMarkUpAmount(materialMarkUp);
            contractorExpenseModel.setMaterialBillableAmount(materialMarkUp.add(materialAmount));
        }

        if (null != contractorExpenseModel.getEquipmentMarkUpValue()
                && null != contractorExpenseModel.getEquipmentValue()
                && null != contractorExpenseModel.getEquipmentNonBillableValue()) {
            final Amount equipmentAmount = toAmount(contractorExpenseModel.getCurrency(),
                                                    contractorExpenseModel.getEquipmentValue()
                                                    .subtract(contractorExpenseModel
                                                            .getEquipmentNonBillableValue()));
            final Amount equipmentMarkUp = equipmentAmount
                    .multiply(0.01)
                    .multiply(contractorExpenseModel.getEquipmentMarkUpValue()).scale(2);
            contractorExpenseModel.setEquipmentMarkUpAmount(equipmentMarkUp);
            contractorExpenseModel.setEquipmentBillableAmount(equipmentMarkUp.add(equipmentAmount));
        }

        if (null != contractorExpenseModel.getEquipmentNonBillableValue()
                && null != contractorExpenseModel.getMaterialNonBillableValue()
                && null != contractorExpenseModel.getLaborNonBillableValue()) {
            final BigDecimal total = contractorExpenseModel.getEquipmentNonBillableValue()
                    .add(contractorExpenseModel.getMaterialNonBillableValue())
                    .add(contractorExpenseModel.getLaborNonBillableValue());
            contractorExpenseModel.setNonBillableAmount(toAmount(contractorExpenseModel.getCurrency(), total));
        }

        if (null != contractorExpenseModel.getLaborBillableAmount()
                && null != contractorExpenseModel.getMaterialBillableAmount()
                && null != contractorExpenseModel.getEquipmentBillableAmount()) {
            contractorExpenseModel.setTotalBillableAmount(contractorExpenseModel.getLaborBillableAmount()
                    .add(contractorExpenseModel.getMaterialBillableAmount())
                    .add(contractorExpenseModel.getEquipmentBillableAmount()));
        }

        if (null != contractorExpenseModel.getEquipmentMarkUpAmount()
                && null != contractorExpenseModel.getLabourMarkUpAmount()
                && null != contractorExpenseModel.getMaterialMarkUpAmount()) {
            contractorExpenseModel.setTotalMarkUpAmount(contractorExpenseModel.getEquipmentMarkUpAmount()
                    .add(contractorExpenseModel.getLabourMarkUpAmount())
                    .add(contractorExpenseModel.getMaterialMarkUpAmount()));
        }
    }
}
