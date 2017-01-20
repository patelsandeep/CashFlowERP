package com.cashflow.project.frontend.controller.timesheet;

import com.cashflow.project.api.timesheet.TimeSheetService;
import com.cashflow.project.domain.expenseinformation.TimesheetContext.TimesheetContextBuilder;
import com.cashflow.project.domain.project.timesheet.TimeSheet;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import javax.annotation.Nonnull;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 *
 * 
 * @since Jan 19, 2017, 10:01:48 AM
 */
@Dependent
public class TimesheetHelper implements Serializable {

    private static final long serialVersionUID = -4788968574127784623L;

    @Inject
    private TimeSheetService timeSheetService;

    @Nonnull
    public List<TimeSheet> getBetweenTimesheets(@Nonnull final TimesheetContextBuilder contextBuilder,
                                                @Nonnull final Calendar from,
                                                @Nonnull final Calendar to) {

        contextBuilder.timesheetFromDate(from);
        contextBuilder.timesheetToDate(to);

        return timeSheetService.getTimesheets(contextBuilder.build());
    }

    @Nonnull
    public List<TimeSheet> getTimesheetsByDate(@Nonnull final TimesheetContextBuilder contextBuilder,
                                               @Nonnull final Calendar date) {

        contextBuilder.timesheetDate(date);

        return timeSheetService.getTimesheets(contextBuilder.build());
    }

}
