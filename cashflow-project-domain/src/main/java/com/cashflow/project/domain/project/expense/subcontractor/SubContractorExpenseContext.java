package com.cashflow.project.domain.project.expense.subcontractor;

import com.cashflow.entitydomains.facade.context.Context;
import com.cashflow.project.domain.expenseinformation.FilterProperty;
import com.cashflow.project.domain.project.timesheet.ExpenseStatus;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.google.common.base.MoreObjects.firstNonNull;

/**
 *
 * 
 * @since Jan 3, 2017, 12:06:26 PM
 */
@Getter
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SubContractorExpenseContext implements Context {

    @Nullable
    @FilterProperty
    private Integer offset;

    @Nullable
    @FilterProperty
    private Integer limit;

    @Nullable
    @FilterProperty
    private String projectLevelUUID;

    @Nullable
    @FilterProperty
    private ExpenseStatus expenseStatus;

    @Nullable
    @FilterProperty
    private String supplierUUID;

    @Nullable
    @FilterProperty("freeText")
    private String name;

    @Nonnull
    @Override
    public Integer getOffset() {
        return firstNonNull(offset, 0);
    }

    @Nonnull
    @Override
    public Integer getLimit() {
        return firstNonNull(limit, 100);
    }

}
