package com.cashflow.project.translation.phase;

import com.cashflow.core.annotations.Config;
import com.cashflow.core.annotations.ConfigProject;
import com.cashflow.core.annotations.ConfigRoot;
import com.cashflow.core.annotations.Default;
import com.cashflow.core.annotations.Info;
import javax.annotation.Nonnull;

/**
 *
 * 
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface ProjectPhaseTranslationService {

    @Nonnull
    @Default("Add New Phase")
    @Info("Translated Header Label for New Phase")
    String getPhaseHeader();

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
    @Default("New Phase Number")
    @Info("Translated Label for New Phase Number")
    String getNewPhaseNumberLabel();

    @Nonnull
    @Default("Phase Number Required")
    @Info("Translated Label for  Phase Number Required")
    String getPhaseNumberRequiredLabel();

    @Nonnull
    @Default("Phase ID Required")
    @Info("Translated Label for Phase ID Required")
    String getPhaseIdRequiredLabel();

    @Nonnull
    @Default("Phase ID")
    @Info("Translated Label for Phase ID")
    String getPhaseIdLabel();

    @Nonnull
    @Default("Phase Name")
    @Info("Translated Label for Phase Name")
    String getPhaseNameLabel();

    @Nonnull
    @Default("Phase Name Required")
    @Info("Translated Label for Phase Name Required")
    String getPhaseNameRequiredLabel();

    @Nonnull
    @Default("Phase Supervisor")
    @Info("Translated Label for Phase Supervisor")
    String getPhaseSupervisorLabel();

    @Nonnull
    @Default("Phase Supervisor Required")
    @Info("Translated Label for Phase Supervisor Required")
    String getPhaseSupervisorRequiredLabel();

    @Nonnull
    @Default("Start Date")
    @Info("Translated  Label for Start date")
    String getStartDateLabel();

    @Nonnull
    @Default("End date")
    @Info("Translated  Label for End date")
    String getEndDateLabel();

    @Nonnull
    @Default("Phase Summary")
    @Info("Translated Label for Phase Summary")
    String getPhaseSummaryLabel();

    @Nonnull
    @Default("Phase Deliverable")
    @Info("Translated Label for Phase Deliverable")
    String getPhaseDeliverableLabel();

    @Nonnull
    @Default("Phase Budget Cost")
    @Info("Translated Label for Phase Budget Cost")
    String getPhaseBudgetCostLabel();

    @Nonnull
    @Default("Phase Budget Revenue")
    @Info("Translated Label for Phase Budget Revenue")
    String getPhaseBudgetRevenueLabel();

    @Nonnull
    @Default("Phase Budget Gross Profit")
    @Info("Translated Label for Phase Budget Gross Profit")
    String getPhaseBudgetProfitLabel();

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
    @Default("View Phase Financials")
    @Info("Translated Label for View Phase Financials")
    String getViewPhaseFinancialsLabel();

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
    @Default("Phase POC")
    @Info("Translated Label for Phase POC")
    String getPhasePOCLabel();

    @Nonnull
    @Default("Phase PPC")
    @Info("Translated Label for Phase PPC")
    String getPhasePPCLabel();

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
    @Default("Phase Status")
    @Info("Translated Label for Phase Status")
    String getPhaseStatusLabel();

    @Nonnull
    @Default("Performance Status Required")
    @Info("Translated Label for Performance Status Required")
    String getPerformanceStatusRequiredLabel();

    @Nonnull
    @Default("Phase Status Required")
    @Info("Translated Label for Phase Status Required")
    String getPhaseStatusRequiredLabel();

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
    @Default("No Phase Supervisor Found")
    @Info("Translated Label for No Phase Supervisor Found")
    String getNoPhaseSupervisorFoundMessage();

    @Nonnull
    @Default("Type at least 3 characters to search Phase Supervisors")
    @Info("Translated Label for Type at least 3 characters to search Phase Supervisors")
    String getMessageForPhaseSupervisorSearch();

    @Nonnull
    @Default("Select Safety Rating")
    @Info("Translated Label for Select Safety Rating")
    String getSelectSafetyRatingLabel();

    @Nonnull
    @Default("Select Performance Status")
    @Info("Translated Label for Select Performance Status")
    String getSelectPerformanceStatusLabel();

    @Nonnull
    @Default("Select Phase Status")
    @Info("Translated Label for Select Phase Status")
    String getSelectPhaseStatusLabel();

    @Nonnull
    @Default("Phase Attachments")
    @Info("Translated Label for Phase Attachments")
    String getPhaseAttachmentsLabel();

    @Nonnull
    @Default("File Uploaded Successfully!")
    @Info("success message for file upload")
    String getUploadSuccessMessage();

    @Nonnull
    @Default("Project Phase Added Successfully")
    @Info("success message for Project Project Phase Added Successfully")
    String getProjectPhaseSuccessMessage();

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
}
