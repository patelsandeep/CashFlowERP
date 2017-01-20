package com.cashflow.project.domain.service.deposit;

import com.cashflow.backend.businessaccount.authorization.RequiresBusinessAccount;
import com.cashflow.project.api.deposit.DepositService;
import com.cashflow.project.domain.facade.deposit.DepositFacade;
import com.cashflow.project.domain.project.budget.Deposit;
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
@Local(DepositService.class)
public class DepositServiceImpl implements DepositService {

    @EJB
    private DepositFacade depositFacde;

    @Override
    public void saveCustomerDeposit(@Nonnull final Deposit deposit) {
        checkNotNull(deposit, "The deposit must not be null");

        depositFacde.edit(deposit);
    }

    @Nonnull
    @Override
    public List<Deposit> getDeposits(@Nonnull final List<String> projectLevelUUIDs) {
        checkNotNull(projectLevelUUIDs, "The projectLevelUUIDs must not be null");
        if (projectLevelUUIDs.isEmpty()) {
            return ImmutableList.of();
        }
        return depositFacde.getDeposits(projectLevelUUIDs);
    }
}
