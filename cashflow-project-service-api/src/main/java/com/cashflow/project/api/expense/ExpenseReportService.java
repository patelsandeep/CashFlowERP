package com.cashflow.project.api.expense;

import com.cashflow.project.domain.expenseinformation.ExpenseReportContext;
import com.cashflow.project.domain.project.expense.ExpenseReport;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * 
 * @since 5 Dec, 2016, 2:58:29 PM
 */
public interface ExpenseReportService {

    void save(@Nonnull final ExpenseReport expenseReport);

    @Nullable
    ExpenseReport getExpenseReport(@Nonnull final String expenseReportIdOrUUID);

    @Nonnull
    List<ExpenseReport> getExpenseReports(@Nonnull final ExpenseReportContext expenseReportContext);

}
