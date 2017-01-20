package com.cashflow.project.api.expense.subcontractor;

import com.cashflow.project.domain.project.timesheet.ExpenseStatus;
import com.anosym.common.Amount;
import java.util.Calendar;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
 * 
 * @since Dec 20, 2016, 11:37:56 AM
 */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubContractorExpenseInformation {

    @Nonnull
    private String expenseUuid;

    @Nonnull
    private String projectId;

    @Nonnull
    private String subContractorName;

    @Nonnull
    private String supplierID;

    @Nonnull
    private Calendar invoiceDate;

    @Nonnull
    private String invoiceNumber;

    @Nonnull
    private Amount costAmount;

    @Nullable
    private Amount markUpAmount;

    @Nonnull
    private Amount billableAmount;

    @Nonnull
    private ExpenseStatus expenseStatus;

}
