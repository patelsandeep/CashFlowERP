package com.cashflow.project.domain.project.timesheet;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.Max;
import lombok.Getter;
import lombok.Setter;

import static java.lang.String.format;

/**
 *
 * 
 * @since Dec 5, 2016, 12:46:47 PM
 */
public class WorkTime implements Serializable {

    private static final long serialVersionUID = -8268134553787958488L;

    private final long SECONDS_PER_MINUTE = 60;
    private final long MINUTES_PER_HOUR = 60;

    @Getter
    @Setter
    @Nonnull
    @Nonnegative
    private long totalHours = 0;

    @Getter
    @Setter
    @Nonnull
    @Max(99)
    @Nonnegative
    private long totalMinutes = 0;

    @Override
    public String toString() {
        return format("%s.%s", this.totalHours, totalMinutes);
    }

    @Nonnull
    public WorkTime add(@Nullable final WorkTime workTime) {

        final long thisSecodsForHours = TimeUnit.SECONDS.convert(this.getTotalHours(), TimeUnit.HOURS);
        final long thisMinutesIn60Format = (60 * this.getTotalMinutes()) / 100;
        final long thisSecondsForMinutes = TimeUnit.SECONDS.convert(thisMinutesIn60Format,
                                                                    TimeUnit.MINUTES);
        final long thisTotal = thisSecodsForHours + thisSecondsForMinutes;

        long total = 0;
        if (workTime != null) {
            final long secodsForHours = TimeUnit.SECONDS.convert(workTime.getTotalHours(), TimeUnit.HOURS);
            final long minutesIn60Format = (60 * workTime.getTotalMinutes()) / 100;
            final long secondsForMinutes = TimeUnit.SECONDS.convert(minutesIn60Format,
                                                                    TimeUnit.MINUTES);
            total = secodsForHours + secondsForMinutes;
        }

        final long totalSeconds
                = thisTotal + total;
        return convertToEntityAttribute(totalSeconds);
    }

    @Nonnull
    private WorkTime convertToEntityAttribute(@Nonnull final long totalSeconds) {
        final long totalMins = totalSeconds / SECONDS_PER_MINUTE;
        final long hours = totalMins / MINUTES_PER_HOUR;
        final long minutes = totalMins % MINUTES_PER_HOUR;
        final long minutesInPer = (minutes * 100) / MINUTES_PER_HOUR;

        WorkTime workTime = new WorkTime();
        workTime.setTotalHours(hours);
        workTime.setTotalMinutes(minutesInPer);
        return workTime;
    }

}
