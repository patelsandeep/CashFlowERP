package com.cashflow.project.frontend.controller.model;

import com.cashflow.project.domain.project.expense.BillableType;
import com.cashflow.project.domain.project.timesheet.PTOCategory;
import com.cashflow.project.domain.project.timesheet.WorkTime;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * 
 * @since Dec 6, 2016, 12:03:10 PM
 */
@Getter
@Setter
public class TimeSheetWeeklyEntryModel implements Serializable {

    private static final long serialVersionUID = 8393322370711048837L;

    @Nonnull
    private Calendar timeSheetDate;

    @Nonnull
    private BigDecimal regularTime = BigDecimal.ZERO;

    @Nonnull
    private BigDecimal overTime = BigDecimal.ZERO;

    @Nonnull
    private BigDecimal ptoTime = BigDecimal.ZERO;

    @Nonnull
    private BillableType billableType;

    @Nullable
    private PTOCategory ptOCategory;

    @Nullable
    private WorkTime regularWorkTime;

    @Nullable
    private WorkTime overTimeWorkTime;

    @Nullable
    private WorkTime ptoWorkTime;

    @Nonnull
    private WorkTime total = new WorkTime();

}
