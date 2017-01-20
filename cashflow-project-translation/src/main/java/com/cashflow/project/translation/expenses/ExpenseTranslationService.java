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
 * @since 5 Dec, 2016, 10:23:18 AM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface ExpenseTranslationService {

    @Nonnull
    @Default("New Expense Report")
    @Info("Translated Header Label for New Expense Report")
    String getNewExpenseHeader();

    @Nonnull
    @Default("Expense Report Detail")
    @Info("Translated Header Label for Expense Report Detail")
    String getViewExpenseHeader();

    @Nonnull
    @Default("Edit")
    @Info("Translated label for Edit")
    String getEdit();

    @Nonnull
    @Default("Select Labor Expense Type")
    @Info("Translated Header Label for Select Labor Expense Type")
    String getExpenseTypeHeader();

    @Nonnull
    @Default("Select Timesheet or Expense Report")
    @Info("Translated Header Label for view Labor Expense Type")
    String getViewExpenseTypeHeader();

    @Nonnull
    @Default("Do you want to view a Timesheet or Expense Report for the selected Team Member?")
    @Info("Translated message for view which report Type")
    String getViewWhichReportMessage();

    @Nonnull
    @Default("Select")
    @Info("Translated message for select label")
    String getSelect();

    @Nonnull
    @Default("Labor Expense Type")
    @Info("Translated Label for Labor Expense Type")
    String getLaborExpenseTypeLabel();

    @Nonnull
    @Default("Labor Expense Type Required")
    @Info("Translated Required Message for Labor Expense Type")
    String getSelectLaborExpenseTypeRquiredMessage();

    @Nonnull
    @Default("Cancel")
    @Info("Translated Label for Cancel")
    String getCancelLabel();

    @Nonnull
    @Default("Download Expense Files")
    @Info("Translated Label for Download Expense Files")
    String getDownloadExpenseFilesLabel();

    @Nonnull
    @Default("Next")
    @Info("Translated Label for Next")
    String getNextLabel();

    @Nonnull
    @Default("Expense Report ID")
    @Info("Translated Label for Expense Report ID")
    String getExpenseReportIdLabel();

    @Nonnull
    @Default("Project")
    @Info("Translated label for Project")
    String getProjectLabel();

    @Nonnull
    @Default("Customer")
    @Info("Translated label for Customer")
    String getCustomerLabel();

    @Nonnull
    @Default("Phase")
    @Info("Translated Label for Phase")
    String getPhaseLabel();

    @Nonnull
    @Default("Milestone")
    @Info("Translated Label for Milestone")
    String getMilestoneLabel();

    @Nonnull
    @Default("Task")
    @Info("Translated Label for Task")
    String getTaskLabel();

    @Nonnull
    @Default("Task ID")
    @Info("Translated Label for  Task ID")
    String getTaskIdLabel();

    @Nonnull
    @Default("Name")
    @Info("Translated Label for Name")
    String getNameLabel();

    @Nonnull
    @Default("Employee ID")
    @Info("Translated Label for Employee ID")
    String getEmployeeIdLabel();

    @Nonnull
    @Default("Project Role")
    @Info("Translated Label for Project Role")
    String getProjectRoleLabel();

    @Nonnull
    @Default("Week Start")
    @Info("Translated Label for Week Start")
    String getWeekStartLabel();

    @Nonnull
    @Default("Week Ending")
    @Info("Translated Label for Week Ending")
    String getWeekEndingLabel();

    @Nonnull
    @Default("Date")
    @Info("Translated Label for Date")
    String getDateLabel();

    @Nonnull
    @Default("Expense Type")
    @Info("Translated Label for Expense Type")
    String getExpenseTypeLabel();

    @Nonnull
    @Default("Billable Type")
    @Info("Translated Label for Billable Type")
    String getBillableTypeLabel();

    @Nonnull
    @Default("Amount")
    @Info("Translated Label for Amount")
    String getAmountLabel();

    @Nonnull
    @Default("Tax")
    @Info("Translated Label for Tax")
    String getTaxLabel();

    @Nonnull
    @Default("Billable")
    @Info("Translated Label for Billable")
    String getBillableLabel();

    @Nonnull
    @Default("Totals")
    @Info("Translated Label for Totals")
    String getTotalsLabel();

    @Nonnull
    @Default("Total")
    @Info("Translated Label for Total")
    String getTotalLabel();

    @Nonnull
    @Default("Attach Receipt")
    @Info("Translated Label for Attach Receipt")
    String getAttachReceiptLabel();

    @Nonnull
    @Default("Download Receipt")
    @Info("Translated Label for Download Receipt")
    String getDownloadReceiptLabel();

    @Nonnull
    @Default("Upload Receipt")
    @Info("Translated Label for Upload Receipt")
    String getUploadReceiptLabel();

    @Nonnull
    @Default("Action")
    @Info("Translated Label for Action")
    String getActionLabel();

    @Nonnull
    @Default("Audit Trail and Notes")
    @Info("Translated Label for Audit Trail and Notes")
    String getAuditTrailAndNotesLabel();

    @Nonnull
    @Default("Save")
    @Info("Translated Label for Save")
    String getSaveLabel();

    @Nonnull
    @Default("Update")
    @Info("Translated Label for Update")
    String getUpdate();

    @Nonnull
    @Default("Submit")
    @Info("Translated Label for Submit")
    String getSubmitLabel();

    @Nonnull
    @Default("Approve")
    @Info("Translated Label for Approve")
    String getApproveLabel();

    @Nonnull
    @Default("Reject")
    @Info("Translated Label for Reject")
    String getRejectLabel();

    @Nonnull
    @Default("Download Files")
    @Info("Translated Label for Download")
    String getDownload();

    @Nonnull
    @Default("Add a line")
    @Info("Translated Label for Add a line")
    String getAddLineLabel();

    @Nonnull
    @Default("Please Select Labour Expense Type")
    @Info("Translated Label for Select Labour Expense Type Required")
    String getSelectLabourExpenseTypeRquiredMessage();

    @Nonnull
    @Default("Expense Report Saved Successfully")
    @Info("Translated Label for Expense Report Saved Successfully")
    String getExpenseReportSavedSuccessfully();

    @Nonnull
    @Default("Expense Report Updated Successfully")
    @Info("Translated Label for Expense Report Updated Successfully")
    String getExpenseReportUpdatedSuccessfully();

    @Nonnull
    @Default("Expense Report Approved Successfully")
    @Info("Translated Label for Expense Report Approved Successfully")
    String getExpenseReportApprovedSuccessfully();

    @Nonnull
    @Default("Expense Report Rejected Successfully")
    @Info("Translated Label for Expense Report Rejected Successfully")
    String getExpenseReportRejectedSuccessfully();

    @Nonnull
    @Default("Please Select TeamMember")
    @Info("Translated Label for TeamMember Required")
    String getMemberRequiredMessage();

    @Nonnull
    @Default("Billable Required")
    @Info("Translated Label for Billable Required")
    String getBillableRequiredMessage();

    @Nonnull
    @Default("Expense Type Required")
    @Info("Translated Label for Expense Type Required")
    String getExpenseTypeRequiredMessage();

    @Nonnull
    @Default("Expense Date Required")
    @Info("Translated Label for Expense Date Required")
    String getExpenseDateRequiredMessage();

    @Nonnull
    @Default("Please Add Expense")
    @Info("Translated Label for Expense Required")
    String getAtleastOneExpenseRequried();

    @Nonnull
    @Default("File Uploaded Successfully!")
    @Info("success message for file upload")
    String getUploadSuccessMessage();

    @Nonnull
    @Default("Expense Attachments")
    @Info("Translated Label for Expense Attachments")
    String getExpenseAttachmentsLabel();

    @Nonnull
    @Default("Success Message")
    @Info("Translated  Label for Success Message")
    String getSuccessMessageHeaderLabel();

    @Nonnull
    @Default("OK")
    @Info("Translated Label for OK")
    String getOkLabel();

    @Nonnull
    @Default("To Add Time and Expense Project Must be Approved First")
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
    @Default("Select Tax")
    @Info("Translated Label for Select Tax")
    String getSelectTaxLabel();

    @Nonnull
    @Default("Select Member")
    @Info("Translated Label for Select Member")
    String getSelectMemberLabel();

    @Nonnull
    @Default("Expense Report Submitted successfully")
    @Info("Translated message for Expense Report Submit success")
    String getSubmitSuccessMessage();

    @Nonnull
    @Default("Expense Report has been Approved successfully")
    @Info("Translated message for Expense Report Approved success")
    String getApprovedSuccessMessage();

    @Nonnull
    @Default("Expense Report has been Rejected successfully")
    @Info("Translated message for Expense Report Rejected success")
    String getRejectSuccessMessage();

    @Nonnull
    @Default("Your  Report")
    @Info("Message for Pending Your Expense Report")
    String getYourExpenseReportLabel();

    @Nonnull
    @Default("has been Approved")
    @Info("Message for Pending Your Expense has been Approved")
    String getYourExpenseReportApprovedLabel();

    @Nonnull
    @Default("has been Rejected")
    @Info("Message for Pending Your Expense has been Rejected")
    String getYourExpenseReportRejectedLabel();

    @Nonnull
    @Default("Expense Report")
    @Info("Translated Header Label for Expense Report")
    String getExpenseReportLabel();
}
