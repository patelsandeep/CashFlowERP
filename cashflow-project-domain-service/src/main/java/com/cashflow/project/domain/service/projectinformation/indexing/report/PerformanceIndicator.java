package com.cashflow.project.domain.service.projectinformation.indexing.report;

import com.anosym.common.Amount;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
 * 
 * @since Nov 16, 2016, 6:03:48 AM
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerformanceIndicator {

    private int totalProjectCounts;

    private int totalTaskCounts;

    private Amount totalProjectValues;

    private Amount totalBudgetedCosts;

    private Amount totalBudgetedGrossProfit;

    private List<ContractTypeIndicator> contractTypeIndicators;

    private Amount ltdCosts;

    private Amount committedCosts;

    private Amount ltdRevenue;

    private List<ProjectCompletionIndicator> projectCompletionIndicators;
}
