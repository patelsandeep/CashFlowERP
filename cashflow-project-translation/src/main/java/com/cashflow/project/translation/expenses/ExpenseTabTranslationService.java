package com.cashflow.project.translation.expenses;

import com.cashflow.core.annotations.Config;
import com.cashflow.core.annotations.ConfigProject;
import com.cashflow.core.annotations.ConfigRoot;
import com.cashflow.core.annotations.Default;
import com.cashflow.core.annotations.Info;
import javax.annotation.Nonnull;

/**
 *
 * 
 * @since Dec 14, 2016, 10:37:27 AM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface ExpenseTabTranslationService {

    @Nonnull
    @Default("Timesheets & Expenses")
    @Info("Translated Header Label for Timesheets and Expenses")
    String getTimesheetsAndExpenses();

    @Nonnull
    @Default("Project Labor expenses are recorded and accumulated through Timesheets and Expense Reports. Time and expenses can be submitted directly through cashflow Timesheets and Expense Reports or submitted through an integration feed from third party applications.")
    @Info("Expense description line 1")
    String getExpenseDetails1();

    @Nonnull
    @Default(" For detailed information and guidance on how to work with Timesheets and Expense Reports on your project please visit our ")
    @Info("Expense description line 2")
    String getExpenseDetails2();

    @Nonnull
    @Default("Knowledge Base")
    @Info("Translated label for Knowledge Base")
    String getKnowledgeBaseLabel();

    @Nonnull
    @Default("Timesheets and Expense Reports.")
    @Info("Translated label for Timesheets and Expense Reports.")
    String getExpenseReportsLabel();

    @Nonnull
    @Default("Add New Timesheet or Expense Report")
    @Info("Translated label for Add New Timesheet or Expense Report")
    String getAddExpenseLabel();

    @Nonnull
    @Default("Project ID")
    @Info("Transleted Message of Project ID")
    String getProjectId();

    @Nonnull
    @Default("Phase ID")
    @Info("Transleted Message of Phase ID")
    String getPhaseId();

    @Nonnull
    @Default("Milestone ID")
    @Info("Transleted Message of Milestone ID")
    String getMilestoneId();

    @Nonnull
    @Default("Task ID")
    @Info("Transleted Message of Task ID")
    String getTaskId();

    @Nonnull
    @Default("Team Member")
    @Info("Transleted Message of Team Member")
    String getTeamMember();

    @Nonnull
    @Default("Member ID")
    @Info("Transleted Message of Member ID")
    String getMemberId();

    @Nonnull
    @Default("Platform Role")
    @Info("Transleted Message of Platform Role")
    String getPlatformRole();

    @Nonnull
    @Default("Department")
    @Info("Transleted Message of Department")
    String getDepartment();

    @Nonnull
    @Default("Project Role")
    @Info("Transleted Message of Project Role")
    String getProjectRole();

    @Nonnull
    @Default("Regular Time Hours")
    @Info("Transleted Message of Regular Time Hours")
    String getRegularTimeHours();

    @Nonnull
    @Default("PTO Hours")
    @Info("Transleted Message of PTO Hours")
    String getPtoTimeHours();

    @Nonnull
    @Default("Over Time Hours")
    @Info("Transleted Message of Over Time Hours")
    String getOverTimeHours();

    @Nonnull
    @Default("Total Hours")
    @Info("Transleted Message of Total Hours")
    String getTotalHours();

    @Nonnull
    @Default("Expense Report")
    @Info("Transleted Message of Expense Report")
    String getExpenseReport();

    @Nonnull
    @Default("Action")
    @Info("Transleted Message of Action")
    String getAction();

    @Nonnull
    @Default("Cancel")
    @Info("Transleted Message of Cancel")
    String getCancel();

    @Nonnull
    @Default("Next")
    @Info("Transleted Message of Next")
    String getNext();

    @Nonnull
    @Default("Previous")
    @Info("Transleted Message of Previous")
    String getPrevious();

    @Nonnull
    @Default("OK")
    @Info("Transleted Message of OK")
    String getOkLabel();

    @Nonnull
    @Default("To Add Time and/or Expenses Project Must be Approved First")
    @Info("Translated message for To Add Time and Expense Project Must be Approved First")
    String getMustApprovedLabel();

    @Nonnull
    @Default("Select Phase")
    @Info("Translated Label for Select Phase")
    String getSelectPhaseLabel();

    @Nonnull
    @Default("Select Milestone")
    @Info("Translated Label for Select Milestone")
    String getSelectMilestoneLabel();

    @Nonnull
    @Default("Select Task")
    @Info("Translated Label for Select Task")
    String getSelectTaskLabel();

    @Nonnull
    @Default("Select Project Role")
    @Info("Translated Label for Select Project Role")
    String getSelectProjectRole();

    @Nonnull
    @Default("Select Expense Type")
    @Info("Translated Label for Select Expense Type")
    String getSelectExpenseType();

    @Nonnull
    @Default("Filter by: (All)")
    @Info("Translated Label for filter by")
    String getFilterByLabel();

}
