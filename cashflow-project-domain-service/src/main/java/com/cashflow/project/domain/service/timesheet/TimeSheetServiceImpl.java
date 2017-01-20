package com.cashflow.project.domain.service.timesheet;

import com.cashflow.access.authorization.LoggedInBusinessAccount;
import com.cashflow.backend.businessaccount.authorization.RequiresBusinessAccount;
import com.cashflow.project.api.timesheet.TimeSheetService;
import com.cashflow.project.domain.expenseinformation.TimesheetContext;
import com.cashflow.project.domain.facade.timesheet.TimeSheetFacade;
import com.cashflow.project.domain.facade.timesheet.TimesheetContextFacade;
import com.cashflow.project.domain.project.timesheet.TimeSheet;
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
 * @since Dec 6, 2016, 10:28:12 AM
 */
@Stateless
@Dependent
@RequiresBusinessAccount
@Local(TimeSheetService.class)
public class TimeSheetServiceImpl implements TimeSheetService {

    @Inject
    @LoggedInBusinessAccount
    private Instance<BusinessAccount> businessAccount;

    @EJB
    private TimeSheetFacade timeSheetFacade;

    @EJB
    private TimesheetContextFacade timesheetContextFacade;

    @Override
    public void create(@Nonnull final TimeSheet timeSheet) {
        checkNotNull(timeSheet, "The timeSheet must not be null");

        timeSheetFacade
                .edit(timeSheet);

    }

    @Override
    @Nullable
    public TimeSheet getTimesheet(@Nonnull final String timeSheetUUID) {
        checkNotNull(timeSheetUUID, "The timeSheetUUID must not be null");

        return timeSheetFacade
                .findByIdOrUUID(timeSheetUUID, businessAccount.get().getAccountId());

    }

    @Override
    @Nonnull
    public List<TimeSheet> getTimesheets(@Nonnull final TimesheetContext timesheetContext) {
        checkNotNull(timesheetContext, "The timesheetContext must not be null");

        return timesheetContextFacade
                .find(businessAccount.get().getAccountId(), timesheetContext);
    }

}
