package com.cashflow.project.translation.milestone;

import com.cashflow.core.annotations.Config;
import com.cashflow.core.annotations.ConfigProject;
import com.cashflow.core.annotations.ConfigRoot;
import com.cashflow.core.annotations.Default;
import com.cashflow.core.annotations.Info;
import javax.annotation.Nonnull;

/**
 *
 * 
 * @since 21 Nov, 2016, 7:27:06 PM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface ProjectMilestoneTranslationService {

    @Nonnull
    @Default("Add New Milestone")
    @Info("Translated Header Label for New Milestone")
    String getMilestoneHeader();

    @Nonnull
    @Default("Project Name")
    @Info("Translated label for Project Name")
    String getProjectNameLabel();

    @Nonnull
    @Default("Project ID")
    @Info("Translated label for Project ID")
    String getProjectIdLabel();

    @Nonnull
    @Default("Customer")
    @Info("Translated label for Customer")
    String getCustomerLabel();

    @Nonnull
    @Default("Customer ID")
    @Info("Translated  Label for Customer ID")
    String getCustomerIdLabel();

    @Nonnull
    @Default("Milestone Number")
    @Info("Translated Label for Milestone Number")
    String getMilestoneNumberLabel();

    @Nonnull
    @Default("Milestone Number Required")
    @Info("Translated Label for  Milestone Number Required")
    String getMilestoneNumberRequiredLabel();

    @Nonnull
    @Default("Milestone ID Required")
    @Info("Translated Label for Milestone ID Required")
    String getMilestoneIdRequiredLabel();

    @Nonnull
    @Default("Milestone ID")
    @Info("Translated Label for Milestone ID")
    String getMilestoneIdLabel();

    @Nonnull
    @Default("Phase ID")
    @Info("Translated Label for Phase ID")
    String getPhaseIdLabel();

    @Nonnull
    @Default("Phase")
    @Info("Translated Label for Phase")
    String getPhaseLabel();

    @Nonnull
    @Default("Phase Required")
    @Info("Translated Label for Phase Required")
    String getPhaseRequiredLabel();

    @Nonnull
    @Default("Milestone Name")
    @Info("Translated Label for Milestone Name")
    String getMilestoneNameLabel();

    @Nonnull
    @Default("Milestone Name Required")
    @Info("Translated Label for Milestone Name Required")
    String getMilestoneNameRequiredLabel();

    @Nonnull
    @Default("Milestone Supervisor")
    @Info("Translated Label for Milestone Supervisor")
    String getMilestoneSupervisorLabel();

    @Nonnull
    @Default("Milestone Supervisor Required")
    @Info("Translated Label for Milestone Supervisor Required")
    String getMilestoneSupervisorRequiredLabel();

    @Nonnull
    @Default("Start Date")
    @Info("Translated  Label for Start date")
    String getStartDateLabel();

    @Nonnull
    @Default("End date")
    @Info("Translated  Label for End date")
    String getEndDateLabel();

    @Nonnull
    @Default("Milestone Summary")
    @Info("Translated Label for Milestone Summary")
    String getMilestoneSummaryLabel();

    @Nonnull
    @Default("Milestone Deliverable")
    @Info("Translated Label for Milestone Deliverable")
    String getMilestoneDeliverableLabel();

    @Nonnull
    @Default("Milestone Budget Cost")
    @Info("Translated Label for Milestone Budget Cost")
    String getMilestoneBudgetCostLabel();

    @Nonnull
    @Default("Milestone Budget Revenue")
    @Info("Translated Label for Milestone Budget Revenue")
    String getMilestoneBudgetRevenueLabel();

    @Nonnull
    @Default("Milestone Budget Gross Profit")
    @Info("Translated Label for Milestone Budget Gross Profit")
    String getMilestoneBudgetProfitLabel();

    @Nonnull
    @Default("Deposit/Retainers")
    @Info("Translated Label for Deposit/Retainers")
    String getDepositOrRetainersLabel();

    @Nonnull
    @Default("Invoice Issue")
    @Info("Translated Label for Invoice Issue")
    String getInvoiceIssueLabel();

    @Nonnull
    @Default("Manage Alerts")
    @Info("Translated Label for Manage Alerts")
    String getManageAlertsLabel();

    @Nonnull
    @Default("Manage Approvals")
    @Info("Translated Label for Manage Approvals")
    String getManageApprovalsLabel();

    @Nonnull
    @Default("View Milestone Financials")
    @Info("Translated Label for View Milestone Financials")
    String getViewMilestoneFinancialsLabel();

    @Nonnull
    @Default("Milestone Count")
    @Info("Translated Label for Milestone Count")
    String getMilestoneCountLabel();

    @Nonnull
    @Default("Task Count")
    @Info("Translated Label for Task Count")
    String getTaskCountLabel();

    @Nonnull
    @Default("Team Member Count")
    @Info("Translated Label for Team Member Count")
    String getTeamMemberLabel();

    @Nonnull
    @Default("Sub-Contractor Count")
    @Info("Translated Label for Sub-Contractor Count")
    String getSubContractorLabel();

    @Nonnull
    @Default("Milestone POC")
    @Info("Translated Label for Milestone POC")
    String getMilestonePOCLabel();

    @Nonnull
    @Default("Milestone PPC")
    @Info("Translated Label for Milestone PPC")
    String getMilestonePPCLabel();

    @Nonnull
    @Default("Change Order Count")
    @Info("Translated Label for Change Order Count")
    String getChangeOrderCountLabel();

    @Nonnull
    @Default("Penalty Count")
    @Info("Translated Label for Penalty Count")
    String getPenaltyCountLabel();

    @Nonnull
    @Default("Penalties Amount")
    @Info("Translated Label for Penalties Amount")
    String getPenaltyAmountLabel();

    @Nonnull
    @Default("Change Order Amount")
    @Info("Translated Label for Change Order Amount")
    String getChangeOrderAmountLabel();

    @Nonnull
    @Default("Project Manager")
    @Info("Translated Label for Project Manager")
    String getProjectManagerLabel();

    @Nonnull
    @Default("Safety Rating")
    @Info("Translated Label for Safety Rating")
    String getSafetyRatingLabel();

    @Nonnull
    @Default("Safety Rating Required")
    @Info("Translated Label for Safety Rating Required")
    String getSafetyRatingRequiredLabel();

    @Nonnull
    @Default("Performance Status")
    @Info("Translated Label for Performance Status")
    String getPerformanceStatusLabel();

    @Nonnull
    @Default("Milestone Status")
    @Info("Translated Label for Milestone Status")
    String getMilestoneStatusLabel();

    @Nonnull
    @Default("Performance Status Required")
    @Info("Translated Label for Performance Status Required")
    String getPerformanceStatusRequiredLabel();

    @Nonnull
    @Default("Milestone Status Required")
    @Info("Translated Label for Milestone Status Required")
    String getMilestoneStatusRequiredLabel();

    @Nonnull
    @Default("Attach Files")
    @Info("Translated Label for Attach Files")
    String getAttachFilesLabel();

    @Nonnull
    @Default("Audit Trail and Notes")
    @Info("Translated Label for Audit Trail and Notes")
    String getAuditTrailandNotesLabel();

    @Nonnull
    @Default("Cancel")
    @Info("Translated label Label for Cancel")
    String getCancelLabel();

    @Nonnull
    @Default("Save")
    @Info("Translated label Label for Save")
    String getSaveLabel();

    @Nonnull
    @Default("Approve")
    @Info("Translated label Label for Approve")
    String getApproveLabel();

    @Nonnull
    @Default("Close")
    @Info("Translated label Label for Close")
    String getCloseLabel();

    @Nonnull
    @Default("No Milestone Supervisor Found")
    @Info("Translated Label for No Milestone Supervisor Found")
    String getNoMilestoneSupervisorFoundMessage();

    @Nonnull
    @Default("Type at least 3 characters to search Milestone Supervisors")
    @Info("Translated Label for Type at least 3 characters to search Milestone Supervisors")
    String getMessageForMilestoneSupervisorSearch();

    @Nonnull
    @Default("Select Safety Rating")
    @Info("Translated Label for Select Safety Rating")
    String getSelectSafetyRatingLabel();

    @Nonnull
    @Default("Select Performance Status")
    @Info("Translated Label for Select Performance Status")
    String getSelectPerformanceStatusLabel();

    @Nonnull
    @Default("Select Milestone Status")
    @Info("Translated Label for Select Milestone Status")
    String getSelectMilestoneStatusLabel();

    @Nonnull
    @Default("Milestone Attachments")
    @Info("Translated Label for Milestone Attachments")
    String getMilestoneAttachmentsLabel();

    @Nonnull
    @Default("File Uploaded Successfully!")
    @Info("success message for file upload")
    String getUploadSuccessMessage();

    @Nonnull
    @Default("Project Milestone Added Successfully")
    @Info("success message for Project Project Milestone Added Successfully")
    String getProjectMilestoneSuccessMessage();

    @Nonnull
    @Default("OK")
    @Info("Translated Label for OK")
    String getOkLabel();

    @Nonnull
    @Default("Success Message")
    @Info("Translated  Label for Success Message")
    String getSuccessMessageHeaderLabel();

    @Nonnull
    @Default("Entered Deposit Amount is greater than Budget Cost")
    @Info("Translated  Label for Contract Type Required")
    String getCheckDepositAmount();

    @Nonnull
    @Default("Select Phase")
    @Info("Translated Label for Select Phase")
    String getSelectPhaseLabel();
}
