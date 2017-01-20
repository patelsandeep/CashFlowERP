package com.cashflow.project.domain.service.expense.subcontractor;

import com.cashflow.access.authorization.LoggedInBusinessAccount;
import com.cashflow.backend.businessaccount.authorization.RequiresBusinessAccount;
import com.cashflow.project.api.expense.subcontractor.SubContractorExpenseService;
import com.cashflow.project.domain.facade.expense.subcontractor.SubContractorExpenseFacade;
import com.cashflow.project.domain.project.expense.subcontractor.SubContractorExpense;
import com.cashflow.useraccount.domain.businessaccount.BusinessAccount;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since Dec 26, 2016, 5:36:15 PM
 */
@Stateless
@Dependent
@RequiresBusinessAccount
@Local(SubContractorExpenseService.class)
public class SubContractorExpenseServiceImpl implements SubContractorExpenseService {

    @EJB
    private SubContractorExpenseFacade subContractorExpenseFacade;

    @Inject
    @LoggedInBusinessAccount
    private Instance<BusinessAccount> businessAccount;

    @Override
    public void create(@Nonnull final SubContractorExpense subContractorExpense) {
        checkNotNull(subContractorExpense, "The subContractorExpense must not be null");

        subContractorExpenseFacade.edit(subContractorExpense);
    }

    @Override
    @Nullable
    public SubContractorExpense get(@Nonnull final String uuid) {
        checkNotNull(uuid, "The uuid must not be null");

        return subContractorExpenseFacade.find(uuid);
    }
}
