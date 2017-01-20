package com.cashflow.project.domain.facade.deposit;

import com.cashflow.project.domain.facade.ProjectAbstractFacade;
import com.cashflow.project.domain.project.budget.Deposit;
import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.ejb.Stateless;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 */
@Stateless
public class DepositFacade extends ProjectAbstractFacade<Deposit> {

    public DepositFacade() {
        super(Deposit.class);
    }

    @Nonnull
    public List<Deposit> getDeposits(@Nonnull final List<String> projectLevelUUIDs) {
        checkNotNull(projectLevelUUIDs, "The projectLevelUUIDs must not be null");

        if (projectLevelUUIDs.isEmpty()) {
            return ImmutableList.of();
        }

        return entityManager
                .createNamedQuery("Deposit.findDeposits")
                .setParameter("projectLevelUUIDs", projectLevelUUIDs)
                .getResultList();
    }
}
