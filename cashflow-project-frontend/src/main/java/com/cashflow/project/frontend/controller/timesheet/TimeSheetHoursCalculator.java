package com.cashflow.project.frontend.controller.timesheet;

import com.cashflow.project.domain.project.timesheet.WorkTime;
import com.cashflow.project.frontend.controller.model.TimeSheetEntryModel;
import com.cashflow.project.frontend.controller.model.TimeSheetWeeklyEntryModel;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.Dependent;

/**
 *
 * 
 * @since Dec 6, 2016, 4:23:52 PM
 */
@Dependent
public class TimeSheetHoursCalculator implements Serializable {

    private static final long serialVersionUID = 8987615378380987170L;

    public void updateTotals(@Nonnull final TimeSheetEntryModel sheetEntryModel) {
        resetAllTotals(sheetEntryModel);
        sheetEntryModel.getWeeklyEntries()
                .stream()
                .forEach((entry) -> {
                    updateRagularTotal(entry, sheetEntryModel);
                    updateOverTimeTotal(entry, sheetEntryModel);
                    updatePtoTotal(entry, sheetEntryModel);
                });
        updateFinalTotal(sheetEntryModel);
    }

    @Nonnull
    public WorkTime getDailyTotal(@Nonnull final TimeSheetWeeklyEntryModel timeSheetWeeklyEntryModel) {
        final WorkTime regularWt = convertToHoursSec(timeSheetWeeklyEntryModel.getRegularTime());
        final WorkTime overTimeWt = convertToHoursSec(timeSheetWeeklyEntryModel.getOverTime());
        final WorkTime ptoWt = convertToHoursSec(timeSheetWeeklyEntryModel.getPtoTime());
        return regularWt.add(overTimeWt).add(ptoWt);
    }

    private void updateRagularTotal(@Nonnull final TimeSheetWeeklyEntryModel sheetWeeklyEntryModel,
                                    @Nonnull final TimeSheetEntryModel sheetEntryModel) {
        final WorkTime wt = convertToHoursSec(sheetWeeklyEntryModel.getRegularTime());
        if (null == sheetEntryModel.getTotalRegularTime()) {
            sheetEntryModel.setTotalRegularTime(wt);
        } else {
            sheetEntryModel.setTotalRegularTime(wt
                    .add(sheetEntryModel.getTotalRegularTime()));
        }
    }

    private void updateOverTimeTotal(@Nonnull final TimeSheetWeeklyEntryModel sheetWeeklyEntryModel,
                                     @Nonnull final TimeSheetEntryModel sheetEntryModel) {
        final WorkTime wt = convertToHoursSec(sheetWeeklyEntryModel.getOverTime());
        if (null == sheetEntryModel.getTotalOverTime()) {
            sheetEntryModel.setTotalOverTime(wt);
        } else {
            sheetEntryModel.setTotalOverTime(wt
                    .add(sheetEntryModel.getTotalOverTime()));
        }
    }

    private void updatePtoTotal(@Nonnull final TimeSheetWeeklyEntryModel sheetWeeklyEntryModel,
                                @Nonnull final TimeSheetEntryModel sheetEntryModel) {
        final WorkTime wt = convertToHoursSec(sheetWeeklyEntryModel.getPtoTime());
        if (null == sheetEntryModel.getTotalPtoTime()) {
            sheetEntryModel.setTotalPtoTime(wt);
        } else {
            sheetEntryModel.setTotalPtoTime(wt
                    .add(sheetEntryModel.getTotalPtoTime()));
        }
    }

    private void updateFinalTotal(@Nonnull final TimeSheetEntryModel sheetEntryModel) {
        sheetEntryModel.setFinalTotal(sheetEntryModel.getFinalTotal()
                .add(sheetEntryModel.getTotalOverTime())
                .add(sheetEntryModel.getTotalRegularTime())
                .add(sheetEntryModel.getTotalPtoTime()));
    }

    private void resetAllTotals(@Nonnull final TimeSheetEntryModel sheetEntryModel) {
        sheetEntryModel.setTotalRegularTime(new WorkTime());
        sheetEntryModel.setTotalOverTime(new WorkTime());
        sheetEntryModel.setTotalPtoTime(new WorkTime());
        sheetEntryModel.setFinalTotal(new WorkTime());
    }

    @Nonnull
    public WorkTime convertToHoursSec(@Nullable final BigDecimal value) {
        final WorkTime workTime = new WorkTime();
        String hours = "00";
        String minutes = "00";
        final String textBD = value.toPlainString();
        final String[] textBDs = textBD.split("\\.");
        if (textBDs.length > 0) {
            hours = textBDs[0];
        }
        if (textBDs.length > 1) {
            minutes = textBDs[1];
        }
        workTime.setTotalHours(Long.valueOf(hours));
        workTime.setTotalMinutes(Long.valueOf(minutes));
        return workTime;
    }

}
