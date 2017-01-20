package com.cashflow.project.domain.service.budget;

import com.cashflow.backend.businessaccount.authorization.RequiresBusinessAccount;
import com.cashflow.project.api.budget.BudgetService;
import com.cashflow.project.domain.facade.budget.BudgetFacade;
import com.cashflow.project.domain.project.budget.Budget;
import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 */
@Stateless
@Dependent
@RequiresBusinessAccount
@Local(BudgetService.class)
public class BudgetServiceImpl implements BudgetService {

    @EJB
    private BudgetFacade budgetFacade;

    @Override
    public void saveBudget(@Nonnull final Budget budget) {
        checkNotNull(budget, "The budget must not be null");

        budgetFacade.edit(budget);
    }

    @Nonnull
    @Override
    public List<Budget> getBudgets(@Nonnull final List<String> projectLevelUUIDs) {
        checkNotNull(projectLevelUUIDs, "The projectLevelUUIDs must not be null");
        if (projectLevelUUIDs.isEmpty()) {
            return ImmutableList.of();
        }
        return budgetFacade.getBudgets(projectLevelUUIDs);
    }
}
