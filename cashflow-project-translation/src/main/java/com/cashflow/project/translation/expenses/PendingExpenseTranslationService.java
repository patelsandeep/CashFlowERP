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
 * @since 4 Jan, 2017, 1:59:31 PM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface PendingExpenseTranslationService {

    @Nonnull
    @Default("Expense Pending Approval")
    @Info("The label for Expense Pending Approval")
    String getExpensePendingApprovalLabel();

    @Nonnull
    @Default("Non-Billable Expenses")
    @Info("The label for Non-Billable Expenses")
    String getNonBillableExpensesLabel();

    @Nonnull
    @Default("Billable Expenses")
    @Info("The label for Billable Expenses")
    String getBillableExpensesLabel();

    @Nonnull
    @Default("Tax Amount")
    @Info("The label for Tax Amount")
    String getTaxLabel();

    @Nonnull
    @Default("Tax")
    @Info("The label for Tax")
    String getTax();

    @Nonnull
    @Default("Discount")
    @Info("The label for Discount")
    String getDiscountLabel();

    @Nonnull
    @Default("Total Expenses")
    @Info("The label for Total Expenses")
    String getTotalExpensesLabel();

    @Nonnull
    @Default("Select")
    @Info("The label for Select")
    String getSelectLabel();

    @Nonnull
    @Default("Reject")
    @Info("The label for Expense Pending Approval")
    String getRejectExpenseReportLabel();

    @Nonnull
    @Default("Approve")
    @Info("The label for Approve Expense Report")
    String getApproveExpenseReport();

    @Nonnull
    @Default("Cancel")
    @Info("The label for Cancel")
    String getCancel();

    @Nonnull
    @Default("Expense Rejected SuccessFully")
    @Info("The label for Expense Rejected SuccessFully")
    String getRejectSuccessMessage();

    @Nonnull
    @Default("Email Sent Successfully")
    @Info("The label for Email has been sent SuccessFully")
    String getSuccessEmailMessgae();

    @Nonnull
    @Default("Exception occured at the time of sending Email")
    @Info("The label for Exception occured at the time of sending Email")
    String getErrorEmailMessgae();

    @Nonnull
    @Default("Expense Approved Successfully")
    @Info("The label for Expense Approved SuccessFully")
    String getApproveSuccessMessage();

    @Nonnull
    @Default("Expense Pending Approval Detail")
    @Info("The label for Expense Approve Detail")
    String getExpensePendingApprovalDetail();

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
    @Default("Customer")
    @Info("The label for Customer")
    String getCustomerLabel();

    @Nonnull
    @Default("Department")
    @Info("The label for Department")
    String getDepartmentLabel();

    @Nonnull
    @Default("Task")
    @Info("The label for Task")
    String getTaskLabel();

    @Nonnull
    @Default("Expense Type")
    @Info("The label for Expense Type")
    String getExpenseTypeLabel();

    @Nonnull
    @Default("Item Type")
    @Info("The label for Item Type")
    String getItemTypeLabel();

    @Nonnull
    @Default("Inventory Item")
    @Info("The label for inventory Item")
    String getInventoryItem();

    @Nonnull
    @Default("Direct Purchase")
    @Info("The label for Direct Purchase")
    String getDirectPurchase();

    @Nonnull
    @Default("Item Name")
    @Info("The label for Item Name")
    String getItemNameLabel();

    @Nonnull
    @Default("Quantity")
    @Info("The label for Quantity")
    String getQuantity();

    @Nonnull
    @Default("Billable")
    @Info("The label for Billable")
    String getBillableLabel();

    @Nonnull
    @Default("Non-Billable Amount")
    @Info("The label for Non-Billable")
    String getNonBillable();

    @Nonnull
    @Default("Billable Amount")
    @Info("The label for Billable")
    String getBillable();

    @Nonnull
    @Default("Non-Billable")
    @Info("The label for Non Billable")
    String getNonBillableLabel();

    @Nonnull
    @Default("Expense Details")
    @Info("The label for Expense details")
    String getExpenseDetailsLabel();

    @Nonnull
    @Default("Tax Amount")
    @Info("The label for Tax Amount")
    String getTaxAmountlabel();

    @Nonnull
    @Default("Member")
    @Info("The label Member")
    String getMemberLabel();

    @Nonnull
    @Default("Date")
    @Info("The label Date")
    String getDateLabel();

    @Nonnull
    @Default("Memo")
    @Info("The label Memo")
    String getMemoLabel();

    @Nonnull
    @Default("Supplier")
    @Info("The label Supplier")
    String getSupplierLabel();

    @Nonnull
    @Default("Account Number")
    @Info("The label Account Number")
    String getAccountNumberLabel();

    @Nonnull
    @Default("Material Document")
    @Info("The label  Material Document")
    String getMaterialDocument();

    @Nonnull
    @Default("Expense Document")
    @Info("The label  Expense Document")
    String getExpenseDocument();

    @Nonnull
    @Default("SubContractor Document")
    @Info("The label  SubContractor Document")
    String getSubContractorDocument();

    @Nonnull
    @Default("Contractor")
    @Info("The label Contractor")
    String getContractorLabel();

    @Nonnull
    @Default("Expense For")
    @Info("Label for Expense For")
    public String getExpenseForLabel();

    @Nonnull
    @Default("Mark-up")
    @Info("The label for markup")
    String getMarkupLabel();

    @Nonnull
    @Default("Sort By")
    @Info("The label for Sort By")
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
    @Default("Expense Report has been Approved successfully")
    @Info("Translated message for Expense Report Approved success")
    String getApprovedSuccessMessage();

    @Nonnull
    @Default("Expense Report has been Rejected successfully")
    @Info("Translated message for Expense Report Rejected success")
    String getRejectedSuccessMessage();

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
    @Default("Expense Pending Approval Detail")
    @Info("Message for Pending Expense Approval Detail")
    String getExpensePendingApprovalDetailLabel();

    @Nonnull
    @Default("Team Member")
    @Info("Translated label for Team Member")
    String getTeamMemberLabel();

    @Nonnull
    @Default("Send an Email To")
    @Info("The label for Send an Email To")
    String getSentEmailToLabel();

    @Nonnull
    @Default("E-Mail Team Member")
    @Info("The label for E-Mail Team Member")
    String getEmailTeamMemberLabel();

    @Nonnull
    @Default("Team Members")
    @Info("The label for Team Members")
    String getTeamMembersLabel();

    @Nonnull
    @Default("Expense Report")
    @Info("The label for Expense Report")
    String getExpenseReportLabel();

    @Nonnull
    @Default("E-Mail Send Successfully")
    @Info("The label for E-Mail Send Successfully")
    String getEmailSendSuccessMessage();

}
