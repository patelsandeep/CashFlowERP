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
 * @since 21 Dec, 2016, 11:12:21 AM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface PendingTimeSheetTranslationService {

    @Nonnull
    @Default("Timesheet Pending Approval")
    @Info("The label for TimeSheet Pending Approval")
    String getTimeSheetPendingApprovalLabel();

    @Nonnull
    @Default("Non-Billable Hours")
    @Info("The label for Non-Billable Hours")
    String getNonBillableHoursLabel();

    @Nonnull
    @Default("Billable Hours")
    @Info("The label for Billable Hours")
    String getBillableHoursLabel();

    @Nonnull
    @Default("Total Hours")
    @Info("The label for Total Hours")
    String getTotalHoursLabel();

    @Nonnull
    @Default("Select")
    @Info("The label for Select")
    String getSelectLabel();

    @Nonnull
    @Default("Reject Timesheet")
    @Info("The label for Reject Timesheet")
    String getRejectTimeSheetReportLabel();

    @Nonnull
    @Default("Approve Timesheet")
    @Info("The label for Approve Timesheet Report")
    String getApproveTimeSheetReport();

    @Nonnull
    @Default("Cancel")
    @Info("The label for Cancel")
    String getCancel();

    @Nonnull
    @Default("TimeSheet Rejected Successfully")
    @Info("The label for Expense Rejected SuccessFully")
    String getRejectSuccessMessage();

    @Nonnull
    @Default("TimeSheet Approved Successfully")
    @Info("The label for Expense Approved SuccessFully")
    String getApproveSuccessMessage();

    @Nonnull
    @Default("Exception occured at the time of sending Email")
    @Info("The label for Exception occured at the time of sending Email")
    String getErrorEmailMessgae();

    @Nonnull
    @Default("Email Sent Successfully")
    @Info("The label for Email has been sent SuccessFully")
    String getEmailSentSuccessMessage();

    @Nonnull
    @Default("Total")
    @Info("The label for Total")
    String getTotalLabel();

    @Nonnull
    @Default("Project")
    @Info("The label for Project")
    String getProjectLabel();

    @Nonnull
    @Default("Phase")
    @Info("The label for Phase")
    String getPhaseLabel();

    @Nonnull
    @Default("Milestone")
    @Info("The label for Milestone")
    String getMilestoneLabel();

    @Nonnull
    @Default("Task")
    @Info("The label for Task")
    String getTaskLabel();

    @Nonnull
    @Default("Department")
    @Info("The label for Department")
    String getDepartmentLabel();

    @Nonnull
    @Default("Customer")
    @Info("The label for Customer Dropdown")
    String getCustomerLabel();

    @Nonnull
    @Default("Billable")
    @Info("The label for Billable")
    String getBillableLabel();

    @Nonnull
    @Default("Non-Billable")
    @Info("The label for Non Billable")
    String getNonBillableLabel();

    @Nonnull
    @Default("TimeSheet Details")
    @Info("The label for TimeSheet details")
    String getTimeSheetDetailsLabel();

    @Nonnull
    @Default("Sort by")
    @Info("The label for Sort by")
    String getSortByLabel();

    @Nonnull
    @Default("People")
    @Info("The label for People")
    String getPeopleLabel();

    @Nonnull
    @Default("Projects")
    @Info("The label for Projects")
    String getProjectsLabel();

    @Nonnull
    @Default("Pending Timesheet Approved Successfully")
    @Info("Message for Pending Timesheet Approved Successfully")
    String getApprovedSuccessMessageLabel();

    @Nonnull
    @Default("Pending Timesheet Rejected Successfully")
    @Info("Message for Pending Timesheet Rejected Successfully")
    String getRejectedSuccessMessageLabel();

    @Nonnull
    @Default("Timesheet Pending Approval Detail")
    @Info("Message for  Timesheet Pending Approval Detail")
    String getTimesheetPendingApprovalDetailLabel();

    @Nonnull
    @Default("Your TimeSheet")
    @Info("Message for Pending Your TimeSheet")
    String getYourTimeSheetLabel();

    @Nonnull
    @Default("has been Approved")
    @Info("Message for Pending Your TimeSheet has been Approved")
    String getYourTimeSheetApprovedLabel();

    @Nonnull
    @Default("has been Rejected")
    @Info("Message for Pending Your TimeSheet has been Rejected")
    String getYourTimeSheetRejectedLabel();

    @Nonnull
    @Default("TimeSheet")
    @Info("The label for TimeSheet")
    String getTimeSheetLabel();

    @Nonnull
    @Default("Send an Email To")
    @Info("The label for Send an Email To")
    String getSentEmailToLabel();

    @Nonnull
    @Default("E-Mail Team Member")
    @Info("The label for E-Mail Team Member")
    String getEmailTeamMemberLabel();

    @Nonnull
    @Default("Team Member")
    @Info("The label for Team Member")
    String getTeamMemberLabel();

    @Nonnull
    @Default("Team Members")
    @Info("The label for Team Members")
    String getTeamMembersLabel();

    @Nonnull
    @Default("Email Sent Successfully to Team Members")
    @Info("The label for Email Sent Successfully to Team Members")
    String getEmailSuccessLabel();

    @Nonnull
    @Default("Please Select TimeSheet")
    @Info("Message for Pending Please Select TimeSheet")
    String getSelectTimeSheetLabel();

}
