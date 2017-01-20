package com.cashflow.project.domain.service.projectinformation.indexing.report;

import com.anosym.common.Amount;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
 * 
 * @since Nov 16, 2016, 5:57:46 AM
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpenseInformation {

    private Amount totalExpenses;

    private Amount billableExpenses;

    private Amount nonBillableExpenses;

    private int nonBillableTime;
}
