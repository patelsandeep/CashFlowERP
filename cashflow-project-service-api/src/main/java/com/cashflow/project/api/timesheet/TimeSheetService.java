package com.cashflow.project.api.timesheet;

import com.cashflow.project.domain.expenseinformation.TimesheetContext;
import com.cashflow.project.domain.project.timesheet.TimeSheet;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * 
 * @since Dec 6, 2016, 10:04:53 AM
 */
public interface TimeSheetService {

    void create(@Nonnull final TimeSheet timeSheet);

    @Nullable
    TimeSheet getTimesheet(@Nonnull final String timeSheetUUID);

    @Nonnull
    List<TimeSheet> getTimesheets(@Nonnull final TimesheetContext timesheetContext);

}
