package com.cashflow.project.domain.facade.budget;

import com.cashflow.project.domain.facade.ProjectAbstractFacade;
import com.cashflow.project.domain.project.budget.Budget;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import javax.annotation.Nonnull;
import javax.ejb.Stateless;

import com.google.common.collect.ImmutableList;

/**
 *
 * 
 * @since Nov 12, 2016, 4:05:09 PM
 */
@Stateless
public class BudgetFacade extends ProjectAbstractFacade<Budget> {

    public BudgetFacade() {
        super(Budget.class);
    }

    @Nonnull
    public List<Budget> getBudgets(@Nonnull final List<String> projectLevelUUIDs) {
        checkNotNull(projectLevelUUIDs, "The projectLevelUUIDs must not be null");

        if (projectLevelUUIDs.isEmpty()) {
            return ImmutableList.of();
        }

        return entityManager
                .createNamedQuery("Budget.findBudgets")
                .setParameter("projectLevelUUIDs", projectLevelUUIDs)
                .getResultList();
    }
}
