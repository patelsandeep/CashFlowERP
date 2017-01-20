package com.cashflow.project.translation.task;

import com.cashflow.core.annotations.Config;
import com.cashflow.core.annotations.ConfigProject;
import com.cashflow.core.annotations.ConfigRoot;
import com.cashflow.core.annotations.Default;
import com.cashflow.core.annotations.Info;
import javax.annotation.Nonnull;

/**
 *
 * 
 * @since 5 Jan, 2017, 3:03:10 PM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface TimeSheetSummaryTranslationService {

    @Nonnull
    @Default("TimeSheet Summary Listing")
    @Info("Translation Message for TimeSheet Summary Listing Header")
    String getHeaderLabel();

    @Nonnull
    @Default("Create a New TimeSheet")
    @Info("Translation Message for Create a New TimeSheet")
    String getCreateTimeSheetLabel();

    @Nonnull
    @Default("Date")
    @Info("Translation Message for Date")
    String getDateLabel();

    @Nonnull
    @Default("Customer Name")
    @Info("Translation Message for Customer Name")
    String getCustomerNameLabel();

    @Nonnull
    @Default("Project ID")
    @Info("Translation Message for Project ID")
    String getProjectIdLabel();

    @Nonnull
    @Default("Project Name")
    @Info("Translation Message for Project Name")
    String getProjectNameLabel();

    @Nonnull
    @Default("Phase ID")
    @Info("Translation Message for Phase ID")
    String getPhaseIdLabel();

    @Nonnull
    @Default("Milestone ID")
    @Info("Translation Message for Milestone ID")
    String getMilestoneIdLabel();

    @Nonnull
    @Default("Task ID")
    @Info("Translation Message for Task ID")
    String getTaskIdLabel();

    @Nonnull
    @Default("TimeSheet ID")
    @Info("Translation Message for TimeSheet ID")
    String getTimeSheetIdLabel();

    @Nonnull
    @Default("Team Member")
    @Info("Translation Message for Team Member")
    String getTeamMemberLabel();

    @Nonnull
    @Default("Non-Billable Hours")
    @Info("Translation Message for Non-Billable Hours")
    String getNonBillableHoursLabel();

    @Nonnull
    @Default("Billable Hours")
    @Info("Translation Message for Billable Hours")
    String getBillableHoursLabel();

    @Nonnull
    @Default("Total Hours")
    @Info("Translation Message for Total Hours")
    String getTotalHoursLabel();

    @Nonnull
    @Default("Status")
    @Info("Translation Message for Status")
    String getStatusLabel();

    @Nonnull
    @Default("Action")
    @Info("Translation Message for Action")
    String getActionLabel();

    @Nonnull
    @Default("Edit This Timesheet")
    @Info("Translation Message for Edit This Timesheet")
    String getEditThisTimesheet();

    @Nonnull
    @Default("View This Timesheet")
    @Info("Translation Message for View This Timesheet")
    String getViewThisTimesheet();

}
