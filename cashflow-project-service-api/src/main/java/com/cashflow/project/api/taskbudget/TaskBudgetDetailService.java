package com.cashflow.project.api.taskbudget;

import com.cashflow.project.domain.project.costing.DirectProjectCosting;
import com.cashflow.project.domain.project.costing.IndirectProjectCosting;
import com.cashflow.project.domain.project.revenue.DirectProjectRevenue;
import com.cashflow.project.domain.project.revenue.IndirectProjectRevenue;
import java.util.List;
import javax.annotation.Nonnull;

/**
 *
 * 
 * @since 25 Nov, 2016, 12:42:08 PM
 */
public interface TaskBudgetDetailService {

    void saveTaskBudgetDetails(@Nonnull final List<DirectProjectCosting> directProjectCostings,
                               @Nonnull final List<IndirectProjectCosting> indirectProjectCostings,
                               @Nonnull final List<DirectProjectRevenue> directProjectRevenues,
                               @Nonnull final List<IndirectProjectRevenue> indirectProjectRevenues);

    @Nonnull
    public List<DirectProjectCosting> getDirectCostings(@Nonnull final List<String> projectLevelUUIDs);

    @Nonnull
    public List<IndirectProjectCosting> getIndirectCostings(@Nonnull final List<String> projectLevelUUIDs);

    @Nonnull
    public List<DirectProjectRevenue> getDirectRevenues(@Nonnull final List<String> projectLevelUUIDs);

    @Nonnull
    public List<IndirectProjectRevenue> getIndirectRevenues(@Nonnull final List<String> projectLevelUUIDs);
}
