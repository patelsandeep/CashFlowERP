package com.cashflow.project.domain.service.expense;

import com.cashflow.access.authorization.LoggedInBusinessAccount;
import com.cashflow.backend.businessaccount.authorization.RequiresBusinessAccount;
import com.cashflow.project.api.expense.ExpenseReportService;
import com.cashflow.project.domain.expenseinformation.ExpenseReportContext;
import com.cashflow.project.domain.facade.expense.ExpenseReportContextFacade;
import com.cashflow.project.domain.facade.expense.ExpenseReportFacade;
import com.cashflow.project.domain.project.expense.ExpenseReport;
import com.cashflow.useraccount.domain.businessaccount.BusinessAccount;
import java.util.List;
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
 * @since 5 Dec, 2016, 2:57:32 PM
 */
@Stateless
@Dependent
@RequiresBusinessAccount
@Local(ExpenseReportService.class)
public class ExpenseReportServiceImpl implements ExpenseReportService {

    @EJB
    private ExpenseReportFacade expenseReportFacade;

    @EJB
    private ExpenseReportContextFacade expenseReportContextFacade;

    @Inject
    @LoggedInBusinessAccount
    private Instance<BusinessAccount> businessAccount;

    @Override
    public void save(@Nonnull final ExpenseReport expenseReport) {
        checkNotNull(expenseReport, "The expenseReport must not be null");

        expenseReportFacade.edit(expenseReport);
    }

    @Override
    @Nullable
    public ExpenseReport getExpenseReport(@Nonnull final String expenseReportIdOrUUID) {
        checkNotNull(expenseReportIdOrUUID, "The expenseReportIdOrUUID must not be null");

        return expenseReportFacade.findByMemberIdOrUUID(expenseReportIdOrUUID,
                                                        businessAccount.get().getAccountId());
    }

    @Override
    @Nonnull
    public List<ExpenseReport> getExpenseReports(@Nonnull final ExpenseReportContext expenseReportContext) {
        checkNotNull(expenseReportContext, "The expenseReportContext must not be null");

        return expenseReportContextFacade
                .find(businessAccount.get().getAccountId(), expenseReportContext);
    }

}
