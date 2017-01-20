package com.cashflow.project.frontend.controller.expenses;

import com.cashflow.project.domain.project.expense.BillableType;
import com.cashflow.project.frontend.controller.model.ExpenseInfoModel;
import com.cashflow.project.frontend.controller.model.PendingExpenseModel;
import com.anosym.common.Amount;
import com.anosym.common.Currency;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.annotation.Nonnull;
import javax.enterprise.context.Dependent;

/**
 *
 * 
 * @since 10 Jan, 2017, 3:58:41 PM
 */
@Dependent
public class PendingExpenseCalculationProcessor implements Serializable {

    private static final long serialVersionUID = -8977253904727405325L;

    @Nonnull
    public Amount calculateProjectInfoBillableAmount(@Nonnull final ExpenseInfoModel infoModel,
                                                     @Nonnull final List<ExpenseInfoModel> expenseInfos) {

        final Amount billable = expenseInfos
                .stream()
                .filter((exp) -> exp.getBillableType() == BillableType.YES && exp.getProjectLevel().equals(infoModel
                .getProjectLevel()))
                .map(ExpenseInfoModel::getExpenseAmount)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new Amount(infoModel.getCurrency(), BigDecimal.ZERO));
        return billable;
    }

    @Nonnull
    public Amount calculateProjectInfoNonBillableAmount(@Nonnull final ExpenseInfoModel infoModel,
                                                        @Nonnull final List<ExpenseInfoModel> expenseInfos) {

        final Amount nonBillable = expenseInfos
                .stream()
                .filter((exp) -> exp.getBillableType() == BillableType.NO && exp.getProjectLevel().equals(infoModel
                .getProjectLevel()))
                .map(ExpenseInfoModel::getExpenseAmount)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new Amount(infoModel.getCurrency(), BigDecimal.ZERO));
        return nonBillable;
    }

    @Nonnull
    public Amount calculateMemberInfoBillableAmount(@Nonnull final ExpenseInfoModel infoModel,
                                                    @Nonnull final List<ExpenseInfoModel> expenseInfos) {

        final Amount billable = expenseInfos
                .stream()
                .filter((exp) -> exp.getBillableType() == BillableType.YES && exp.getMemberId().equals(infoModel
                .getMemberId()))
                .map(ExpenseInfoModel::getExpenseAmount)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new Amount(infoModel.getCurrency(), BigDecimal.ZERO));
        return billable;
    }

    @Nonnull
    public Amount calculateMemberInfoNonBillableAmount(@Nonnull final ExpenseInfoModel infoModel,
                                                       @Nonnull final List<ExpenseInfoModel> expenseInfos) {

        final Amount nonBillable = expenseInfos
                .stream()
                .filter((exp) -> exp.getBillableType() == BillableType.NO && exp.getMemberId().equals(infoModel
                .getMemberId()))
                .map(ExpenseInfoModel::getExpenseAmount)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new Amount(infoModel.getCurrency(), BigDecimal.ZERO));
        return nonBillable;
    }

    @Nonnull
    public Amount calculateBillableAmount(@Nonnull final List<ExpenseInfoModel> expenseInfos,
                                          @Nonnull final PendingExpenseModel pendingExpenseModel) {

        final Currency currency = expenseInfos
                .stream()
                .map((cur) -> cur.getCurrency())
                .findFirst()
                .orElse(Currency.CAD);

        final Amount billableAmount = expenseInfos
                .stream()
                .filter((exp) -> exp.getBillableType() == BillableType.YES)
                .map(ExpenseInfoModel::getExpenseAmount)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new Amount(currency, BigDecimal.ZERO));

        if (null == pendingExpenseModel.getBillableExpense()) {
            pendingExpenseModel.setBillableExpense(billableAmount);
            return billableAmount;
        } else {
            pendingExpenseModel.setBillableExpense(pendingExpenseModel.getBillableExpense().add(billableAmount));
            return billableAmount;
        }

    }

    @Nonnull
    public Amount calculateNonBillableAmount(@Nonnull final List<ExpenseInfoModel> expenseInfos,
                                             @Nonnull final PendingExpenseModel pendingExpenseModel) {

        final Currency currency = expenseInfos
                .stream()
                .map((cur) -> cur.getCurrency())
                .findFirst()
                .orElse(Currency.CAD);
        final Amount nonBillableAmount = expenseInfos
                .stream()
                .filter((exp) -> exp.getBillableType() == BillableType.NO)
                .map(ExpenseInfoModel::getExpenseAmount)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new Amount(currency, BigDecimal.ZERO));

        if (null == pendingExpenseModel.getNonBillableExpense()) {
            pendingExpenseModel.setNonBillableExpense(nonBillableAmount);
            return nonBillableAmount;
        } else {
            pendingExpenseModel
                    .setNonBillableExpense(pendingExpenseModel.getNonBillableExpense().add(nonBillableAmount));
            return nonBillableAmount;
        }

    }

    @Nonnull
    public Amount calculateTotalAmount(@Nonnull final List<ExpenseInfoModel> expenseInfos,
                                       @Nonnull final PendingExpenseModel pendingExpenseModel) {

        final Currency currency = expenseInfos
                .stream()
                .map((cur) -> cur.getCurrency())
                .findFirst()
                .orElse(Currency.CAD);

        final Amount billableAmount = expenseInfos
                .stream()
                .filter((exp) -> exp.getBillableType() == BillableType.YES)
                .map(ExpenseInfoModel::getExpenseAmount)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new Amount(currency, BigDecimal.ZERO));

        final Amount nonBillableAmount = expenseInfos
                .stream()
                .filter((exp) -> exp.getBillableType() == BillableType.NO)
                .map(ExpenseInfoModel::getExpenseAmount)
                .reduce((w1, w2) -> w1.add(w2)).orElse(new Amount(currency, BigDecimal.ZERO));

        final Amount totalAmount = billableAmount.add(nonBillableAmount);

        if (null == pendingExpenseModel.getTotalExpense()) {
            pendingExpenseModel.setTotalExpense(totalAmount);
            return totalAmount;
        } else {
            pendingExpenseModel.setTotalExpense(pendingExpenseModel.getTotalExpense().add(totalAmount));
            return totalAmount;
        }
    }

}
