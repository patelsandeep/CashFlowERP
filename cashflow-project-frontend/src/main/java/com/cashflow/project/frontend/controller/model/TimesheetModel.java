package com.cashflow.project.frontend.controller.model;

import com.cashflow.project.frontend.controller.timesheet.TimesheetViewMode;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * 
 * @since Jan 18, 2017, 3:35:26 PM
 */
@Getter
@Setter
public class TimesheetModel implements Serializable {

    private static final long serialVersionUID = 7785976001705208286L;

    @Nonnull
    private TimesheetViewMode timesheetViewMode;

    @Nonnull
    private String timeZone;

    @Nonnull
    private Calendar currentDate;

    @Nonnull
    private Date monday;

    @Nonnull
    private Date tuesday;

    @Nonnull
    private Date wednesday;

    @Nonnull
    private Date thursday;

    @Nonnull
    private Date friday;

    @Nonnull
    private Date saturday;

    @Nonnull
    private Date sunday;

    @Nonnull
    private String totalHoursPerWeek;

}
