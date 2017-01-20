package com.cashflow.project.domain.project.timesheet;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 *
 * 
 * @since Dec 5, 2016, 12:45:58 PM
 */
@Converter(autoApply = true)
public class WorkTimeJPAConverter implements AttributeConverter<WorkTime, BigDecimal> {

    private final long SECONDS_PER_MINUTE = 60;
    private final long MINUTES_PER_HOUR = 60;

    @Nullable
    @Override
    public BigDecimal convertToDatabaseColumn(@Nullable final WorkTime workTime) {
        if (workTime == null) {
            return null;
        } else {
            final long secodsForHours = TimeUnit.SECONDS.convert(workTime.getTotalHours(), TimeUnit.HOURS);
            final long minutesIn60Format = (SECONDS_PER_MINUTE * workTime.getTotalMinutes()) / 100;
            final long secondsForMinutes = TimeUnit.SECONDS.convert(minutesIn60Format,
                                                                    TimeUnit.MINUTES);

            return new BigDecimal(secodsForHours + secondsForMinutes);
        }
    }

    @Nullable
    @Override
    public WorkTime convertToEntityAttribute(@Nullable final BigDecimal totalSeconds) {
        if (totalSeconds == null) {
            return null;
        }

        final long totalMinutes = totalSeconds.longValue() / SECONDS_PER_MINUTE;
        final long hours = totalMinutes / MINUTES_PER_HOUR;
        final long minutes = totalMinutes % MINUTES_PER_HOUR;
        final long minutesInPer = (minutes * 100) / MINUTES_PER_HOUR;

        WorkTime workTime = new WorkTime();
        workTime.setTotalHours(hours);
        workTime.setTotalMinutes(minutesInPer);
        return workTime;
    }
}
