package com.cashflow.project.api.deposit;

import com.cashflow.project.domain.project.budget.Deposit;
import java.util.List;
import javax.annotation.Nonnull;

/**
 *
 * 
 */
public interface DepositService {

    void saveCustomerDeposit(@Nonnull final Deposit deposit);

    @Nonnull
    public List<Deposit> getDeposits(@Nonnull final List<String> projectLevelUUIDs);

}
