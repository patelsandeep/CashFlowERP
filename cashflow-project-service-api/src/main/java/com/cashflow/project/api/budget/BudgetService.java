package com.cashflow.project.api.budget;

import com.cashflow.project.domain.project.budget.Budget;
import java.util.List;
import javax.annotation.Nonnull;

/**
 *
 * 
 */
public interface BudgetService {

    void saveBudget(@Nonnull final Budget budget);

    @Nonnull
    public List<Budget> getBudgets(@Nonnull final List<String> projectLevelUUIDs);

}
