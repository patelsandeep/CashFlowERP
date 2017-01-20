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
 * @since Dec 6, 2016, 10:47:43 AM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface TimeEntryTranslationService {

    @Nonnull
    @Default("New Time Entry")
    @Info("Translation Message for New Time Entry Header")
    String getHeader();

    @Nonnull
    @Default("Time Entry Detail")
    @Info("Translation Message for Time Entry detail Header")
    String getDetailHeader();

    @Nonnull
    @Default("Edit")
    @Info("Translated label for Edit")
    String getEditLabel();

    @Nonnull
    @Default("Approval Date")
    @Info("Translated label for Approval Date")
    String getApprovalDate();

    @Nonnull
    @Default("Timesheet ID")
    @Info("Translation Message for Timesheet ID")
    String getTimesheetID();

    @Nonnull
    @Default("Customer")
    @Info("Translated label for Customer")
    String getCustomerLabel();

    @Nonnull
    @Default("Project")
    @Info("Translated label for Project")
    String getProjectLabel();

    @Nonnull
    @Default("Phase")
    @Info("Translated Label for Phase")
    String getPhaseLabel();

    @Nonnull
    @Default("Select Phase")
    @Info("Translated Label for Select Phase")
    String getSelectPhaseLabel();

    @Nonnull
    @Default("Milestone")
    @Info("Translated Label for Milestone")
    String getMilestoneLabel();

    @Nonnull
    @Default("Select Milestone")
    @Info("Translated Label for Select Milestone")
    String getSelectMilestoneLabel();

    @Nonnull
    @Default("Select Billable Type")
    @Info("Translated Label for Billable Type")
    String getSelectBillableType();

    @Nonnull
    @Default("Task")
    @Info("Translated Label for Task")
    String getTaskLabel();

    @Nonnull
    @Default("Select Task")
    @Info("Translated Label for Select Task")
    String getSelectTaskLabel();

    @Nonnull
    @Default("Task ID")
    @Info("Translated Label for Task ID")
    String getTaskIdLabel();

    @Nonnull
    @Default("Name")
    @Info("Translated Label for Name")
    String getName();

    @Nonnull
    @Default("Team Member ID")
    @Info("Translated Label for Team Member ID")
    String getTeamMemberIdLabel();

    @Nonnull
    @Default("Supplier")
    @Info("Translated Label for Supplier")
    String getSupplierLabel();

    @Nonnull
    @Default("Supplier Type")
    @Info("Translated Label for Supplier Type")
    String getSupplierTypeLabel();

    @Nonnull
    @Default("Project Role")
    @Info("Translated Label for Project Role")
    String getProjectRole();

    @Nonnull
    @Default("Week Start")
    @Info("Translated Label for Week Start")
    String getWeekStart();

    @Nonnull
    @Default("Week Ending")
    @Info("Translated Label for Week Ending")
    String getWeekEnding();

    @Nonnull
    @Default("Date")
    @Info("Translated Label for Date")
    String getDate();

    @Nonnull
    @Default("Day")
    @Info("Translated Label for Day")
    String getDay();

    @Nonnull
    @Default("Regular Time")
    @Info("Translated Label for Regular Time")
    String getRegularTime();

    @Nonnull
    @Default("Over Time")
    @Info("Translated Label for Over Time")
    String getOverTime();

    @Nonnull
    @Default("PTO")
    @Info("Translated Label for PTO")
    String getPto();

    @Nonnull
    @Default("PTO Category")
    @Info("Translated Label for PTO Category")
    String getPtoCategory();

    @Nonnull
    @Default("Select Paid Time Off(PTO) Category")
    @Info("Translated Label for Select PTO Category")
    String getSelectPtoCategory();

    @Nonnull
    @Default("Paid Time Off")
    @Info("Translated message Label for PTO")
    String getPaidTimeOffMessage();

    @Nonnull
    @Default("Billable")
    @Info("Translated Label for Billable")
    String getBillable();

    @Nonnull
    @Default("Total")
    @Info("Translated Label for Total")
    String getTotal();

    @Nonnull
    @Default("Total Hours")
    @Info("Translated Label for Total Hours")
    String getTotalHours();

    @Nonnull
    @Default("Cancel")
    @Info("Translated Label for Cancel")
    String getCancel();

    @Nonnull
    @Default("Save")
    @Info("Translated Label for Save")
    String getSave();

    @Nonnull
    @Default("Update")
    @Info("Translated Label for Update")
    String getUpdate();

    @Nonnull
    @Default("Submit")
    @Info("Translated Label for Submit")
    String getSubmit();

    @Nonnull
    @Default("Reject")
    @Info("Translated Label for Reject")
    String getReject();

    @Nonnull
    @Default("Approve")
    @Info("Translated Label for Approve")
    String getApprove();

    @Nonnull
    @Default("Add a line")
    @Info("Translated Label for Add a line")
    String getAddaLine();

    @Nonnull
    @Default("Please select Task")
    @Info("Translated message for Please select Task")
    String getTaskRequiredMessage();

    @Nonnull
    @Default("Please select Team Member")
    @Info("Translated message for Please select Team Member")
    String getTeamMemberRequiredMessage();

    @Nonnull
    @Default("Please Add atleast one line")
    @Info("Translated message for add atleast one line")
    String getPleaseAddOneLine();

    @Nonnull
    @Default("Time Entry saved successfully")
    @Info("Translated message for success")
    String getSaveSuccessMessage();

    @Nonnull
    @Default("Time Entry Submitted successfully")
    @Info("Translated message for Time Entry Submit success")
    String getSubmitSuccessMessage();

    @Nonnull
    @Default("Time Entry has been Approved successfully")
    @Info("Translated message for Time Entry Approved success")
    String getApprovedSuccessMessage();

    @Nonnull
    @Default("Time Entry has been Rejected successfully")
    @Info("Translated message for Time Entry Rejected success")
    String getRejectSuccessMessage();

    @Nonnull
    @Default("Timesheet Entry updated successfully")
    @Info("Translated message for update success")
    String getUpdateSuccessMessage();

    @Nonnull
    @Default("OK")
    @Info("Translated Label for OK")
    String getOkLabel();

    @Nonnull
    @Default("Success Message")
    @Info("Translated  Label for Success Message")
    String getSuccessMessageHeaderLabel();

    @Nonnull
    @Default("Select Member")
    @Info("Translated Label for Select Member")
    String getSelectMemberLabel();

    @Nonnull
    @Default("Hrs")
    @Info("Translated Label for Hrs")
    String getHrsLabel();

    @Nonnull
    @Default("Your TimeSheet")
    @Info("Message for Pending Your TimeSheet")
    String getYourTimeSheetReportLabel();

    @Nonnull
    @Default("has been Approved")
    @Info("Message for Pending Your TimeSheet has been Approved")
    String getYourTimeSheetReportApprovedLabel();

    @Nonnull
    @Default("has been Rejected")
    @Info("Message for Pending Your TimeSheet has been Rejected")
    String getYourTimeSheetReportRejectedLabel();
}
