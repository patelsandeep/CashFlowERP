package com.cashflow.project.api.expense.subcontractor;

import com.cashflow.project.domain.project.expense.subcontractor.SubContractorExpense;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * 
 * @since Dec 26, 2016, 5:10:45 PM
 */
public interface SubContractorExpenseService {

    void create(@Nonnull final SubContractorExpense subContractorExpense);

    @Nullable
    SubContractorExpense get(@Nonnull final String uuid);

}
