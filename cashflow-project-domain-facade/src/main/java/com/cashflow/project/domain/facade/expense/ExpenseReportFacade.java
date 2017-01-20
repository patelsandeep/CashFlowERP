package com.cashflow.project.domain.facade.expense;

import com.cashflow.project.domain.facade.ProjectAbstractFacade;
import com.cashflow.project.domain.project.expense.ExpenseReport;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 *
 * 
 * @since 5 Dec, 2016, 2:50:00 PM
 */
@Stateless
public class ExpenseReportFacade extends ProjectAbstractFacade<ExpenseReport> {

    public ExpenseReportFacade() {
        super(ExpenseReport.class);
    }

    @Nullable
    public ExpenseReport findByMemberIdOrUUID(@Nonnull final String reportIdOrUUID,
                                              @Nonnull final String businessAccountId) {
        checkNotNull(reportIdOrUUID, "The reportIdOrUUID must not be null");

        final List<ExpenseReport> expenseReports = getEntityManager()
                .createNamedQuery("ExpenseReport.findByExpenseReportIdOrUUID", ExpenseReport.class)
                .setParameter("id", reportIdOrUUID)
                .setParameter("businessAccountId", businessAccountId)
                .getResultList();
        checkState(expenseReports.size() <= 1,
                   "Invalid lookup. Expense Report ID/uuid{%s} returns multiple results",
                   reportIdOrUUID);

        return expenseReports
                .stream()
                .findAny()
                .orElse(null);
    }

}
