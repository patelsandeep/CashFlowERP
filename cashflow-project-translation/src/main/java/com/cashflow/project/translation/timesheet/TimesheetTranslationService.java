package com.cashflow.project.translation.timesheet;

import com.cashflow.core.annotations.Config;
import com.cashflow.core.annotations.ConfigProject;
import com.cashflow.core.annotations.ConfigRoot;
import com.cashflow.core.annotations.Default;
import com.cashflow.core.annotations.Info;
import javax.annotation.Nonnull;

/**
 *
 * 
 * @since Jan 17, 2017, 12:10:31 PM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface TimesheetTranslationService {

    @Nonnull
    @Default("Timesheet")
    @Info("Translation Label for New Timesheet")
    String getHeader();

    @Nonnull
    @Default("Timesheet List")
    @Info("Translation Label for New Timesheet")
    String getTimesheetList();

    @Nonnull
    @Default("Today")
    @Info("Translation Label for Today")
    String getToday();

    @Nonnull
    @Default("Day")
    @Info("Translation Label for Day")
    String getDay();

    @Nonnull
    @Default("Week")
    @Info("Translation Label for Week")
    String getWeek();

    @Nonnull
    @Default("Enter Time")
    @Info("Translation Label for Enter Time")
    String getEnterTime();

    @Nonnull
    @Default("Total")
    @Info("Translation Label for Total")
    String getTotal();

    @Nonnull
    @Default("Start")
    @Info("Translation Label for Start")
    String getStart();

    @Nonnull
    @Default("Project")
    @Info("Translation Label for Project")
    String getProject();

}
