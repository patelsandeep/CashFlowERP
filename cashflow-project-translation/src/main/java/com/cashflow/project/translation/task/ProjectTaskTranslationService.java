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
 * @since 23 Nov, 2016, 12:49:07 PM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface ProjectTaskTranslationService {

    @Nonnull
    @Default("Add New Task")
    @Info("Translated Header Label for New Task")
    String getTaskHeader();

    @Nonnull
    @Default("Task Details")
    @Info("Translated Header Label for Task Details")
    String getViewTaskHeader();

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
    @Default("Milestone ID")
    @Info("Translated Label for Milestone ID")
    String getMilestoneIdLabel();

    @Nonnull
    @Default("Milestone")
    @Info("Translated Label for Milestone")
    String getMilestoneLabel();

    @Nonnull
    @Default("Milestone Required")
    @Info("Translated Label for Milestone Required")
    String getMilestoneRequiredLabel();

    @Nonnull
    @Default("Task Number")
    @Info("Translated Label for Task Number")
    String getTaskNumberLabel();

    @Nonnull
    @Default("Task Number Required")
    @Info("Translated Label for  Task Number Required")
    String getTaskNumberRequiredLabel();

    @Nonnull
    @Default("Task ID")
    @Info("Translated Label for Task ID")
    String getTaskIdLabel();

    @Nonnull
    @Default("Task ID Required")
    @Info("Translated Label for Task ID Required")
    String getTaskIdRequiredLabel();

    @Nonnull
    @Default("Task Name")
    @Info("Translated Label for Task Name")
    String getTaskNameLabel();

    @Nonnull
    @Default("Task Name Required")
    @Info("Translated Label for Task Name Required")
    String getTaskNameRequiredLabel();

    @Nonnull
    @Default("Task Supervisor")
    @Info("Translated Label for Task Supervisor")
    String getTaskSupervisorLabel();

    @Nonnull
    @Default("Task Supervisor Required")
    @Info("Translated Label for Task Supervisor Required")
    String getTaskSupervisorRequiredLabel();

    @Nonnull
    @Default("Start Date")
    @Info("Translated  Label for Start date")
    String getStartDateLabel();

    @Nonnull
    @Default("End date")
    @Info("Translated  Label for End date")
    String getEndDateLabel();

    @Nonnull
    @Default("Task Summary")
    @Info("Translated Label for Task Summary")
    String getTaskSummaryLabel();

    @Nonnull
    @Default("Task Deliverable")
    @Info("Translated Label for Task Deliverable")
    String getTaskDeliverableLabel();

    @Nonnull
    @Default("Task Budget Cost")
    @Info("Translated Label for Task Budget Cost")
    String getTaskBudgetCostLabel();

    @Nonnull
    @Default("Task Budget Revenue")
    @Info("Translated Label for Task Budget Revenue")
    String getTaskBudgetRevenueLabel();

    @Nonnull
    @Default("Task Budget Gross Profit")
    @Info("Translated Label for Task Budget Gross Profit")
    String getTaskBudgetProfitLabel();

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
    @Default("Add/Edit Task Budget")
    @Info("Translated Label for Add/Edit Task Budget")
    String getAddOrEditTaskBudgetLabel();

    @Nonnull
    @Default("View Task Financials")
    @Info("Translated Label for View Task Financials")
    String getViewTaskFinancialsLabel();

    @Nonnull
    @Default("Team Member Count")
    @Info("Translated Label for Team Member Count")
    String getTeamMemberLabel();

    @Nonnull
    @Default("Sub-Contractor Count")
    @Info("Translated Label for Sub-Contractor Count")
    String getSubContractorLabel();

    @Nonnull
    @Default("Task POC")
    @Info("Translated Label for Task POC")
    String getTaskPOCLabel();

    @Nonnull
    @Default("Task PPC")
    @Info("Translated Label for Task PPC")
    String getTaskPPCLabel();

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
    @Default("Task Status")
    @Info("Translated Label for Task Status")
    String getTaskStatusLabel();

    @Nonnull
    @Default("Performance Status Required")
    @Info("Translated Label for Performance Status Required")
    String getPerformanceStatusRequiredLabel();

    @Nonnull
    @Default("Task Status Required")
    @Info("Translated Label for Task Status Required")
    String getTaskStatusRequiredLabel();

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
    @Default("No Task Supervisor Found")
    @Info("Translated Label for No Task Supervisor Found")
    String getNoTaskSupervisorFoundMessage();

    @Nonnull
    @Default("Type at least 3 characters to search Task Supervisors")
    @Info("Translated Label for Type at least 3 characters to search Task Supervisors")
    String getMessageForTaskSupervisorSearch();

    @Nonnull
    @Default("Select Safety Rating")
    @Info("Translated Label for Select Safety Rating")
    String getSelectSafetyRatingLabel();

    @Nonnull
    @Default("Select Performance Status")
    @Info("Translated Label for Select Performance Status")
    String getSelectPerformanceStatusLabel();

    @Nonnull
    @Default("Select Task Status")
    @Info("Translated Label for Select Task Status")
    String getSelectTaskStatusLabel();

    @Nonnull
    @Default("Task Attachments")
    @Info("Translated Label for Task Attachments")
    String getTaskAttachmentsLabel();

    @Nonnull
    @Default("File Uploaded Successfully!")
    @Info("success message for file upload")
    String getUploadSuccessMessage();

    @Nonnull
    @Default("Project Task Added Successfully")
    @Info("success message for Project Project Task Added Successfully")
    String getProjectTaskSuccessMessage();

    @Nonnull
    @Default("Project Task Updated Successfully")
    @Info("success message for Project Project Task Updated Successfully")
    String getProjectTaskUpdatedSuccessMessage();

    @Nonnull
    @Default("Project Task Budget Details Added Successfully")
    @Info("success message for Project Project Task Budget Details Added Successfully")
    String getProjectTaskBudgetDetailsSuccessMessage();

    @Nonnull
    @Default("Project Task Budget Details Updated Successfully")
    @Info("success message for Project Project Task Budget Details Updated Successfully")
    String getProjectTaskBudgetDetailsUpdateSuccessMessage();

    @Nonnull
    @Default("OK")
    @Info("Translated Label for OK")
    String getOkLabel();

    @Nonnull
    @Default("Update")
    @Info("Translated Label for Update")
    String getUpdateLabel();

    @Nonnull
    @Default("Success Message")
    @Info("Translated  Label for Success Message")
    String getSuccessMessageHeaderLabel();

    @Nonnull
    @Default("Direct Cost")
    @Info("Translated Label for Direct Cost")
    String getDirectCostLabel();

    @Nonnull
    @Default("Direct Revenue")
    @Info("Translated Label for Direct Revenue")
    String getDirectRevenueLabel();

    @Nonnull
    @Default("Indirect Cost")
    @Info("Translated Label for Indirect Cost")
    String getIndirectCostLabel();

    @Nonnull
    @Default("Indirect Revenue")
    @Info("Translated Label for Indirect Revenue")
    String getIndirectRevenueLabel();

    @Nonnull
    @Default("Task Total Cost")
    @Info("Translated Label for Task Total Cost")
    String getTaskTotalCostLabel();

    @Nonnull
    @Default("Total Task Revenue")
    @Info("Translated Label for Total Task Revenue")
    String getTotalTaskRevenueLabel();

    @Nonnull
    @Default("Gross Profit")
    @Info("Translated Label for Gross Profit")
    String getGrossProfitLabel();

    @Nonnull
    @Default("Budget Labour Cost")
    @Info("Translated Label for Budget Labour Cost")
    String getBudgetLabourCostLabel();

    @Nonnull
    @Default("Budget Labour Revenue")
    @Info("Translated Label for Budget Labour Revenue")
    String getBudgetLabourRevenueLabel();

    @Nonnull
    @Default("Budget Equipment Cost")
    @Info("Translated Label for Budget Equipment Cost")
    String getBudgetEquipmentCostLabel();

    @Nonnull
    @Default("Budget Equipment Revenue")
    @Info("Translated Label for Budget Equipment Revenue")
    String getBudgetEquipmentRevenueLabel();

    @Nonnull
    @Default("Budget Material Cost")
    @Info("Translated Label for Budget Material Cost")
    String getBudgetMaterialCostLabel();

    @Nonnull
    @Default("Budget Material Revenue")
    @Info("Translated Label for Budget Material Revenue")
    String getBudgetMaterialRevenueLabel();

    @Nonnull
    @Default("Budget Sub-Contractor Cost")
    @Info("Translated Label for Budget Sub-Contractor Cost")
    String getBudgetSubContractorCostLabel();

    @Nonnull
    @Default("Budget Sub-Contractor Revenue")
    @Info("Translated Label for Budget Sub-Contractor Revenue")
    String getBudgetSubContractorRevenueLabel();

    @Nonnull
    @Default("Overhead Cost Allocation")
    @Info("Translated Label for Overhead Cost Allocation")
    String getOverheadCostAllocationLabel();

    @Nonnull
    @Default("Overhead Revenue Allocation")
    @Info("Translated Label for Budget Sub-Contractor Revenue")
    String getOverheadRevenueAllocationLabel();

    @Nonnull
    @Default("Other Indirect Cost")
    @Info("Translated Label for Other Indirect Cost")
    String getOtherIndirectCostLabel();

    @Nonnull
    @Default("Other Indirect Revenue")
    @Info("Translated Label for Other Indirect Revenue")
    String getOtherIndirectRevenueLabel();

    @Nonnull
    @Default("Budgeted Revenue")
    @Info("Translated Label for Budgeted Revenue")
    String getBudgetedRevenueLabel();

    @Nonnull
    @Default("Total Budgeted Cost")
    @Info("Translated Label for Total Budgeted Cost")
    String getTotalBudgetedCostLabel();

    @Nonnull
    @Default("Total Budgeted Revenue")
    @Info("Translated Label for Total Budgeted Revenue")
    String getTotalBudgetedRevenueLabel();

    @Nonnull
    @Default("Include Budget Revenue Details")
    @Info("Translated Label for Include Budget Revenue Details")
    String getIncludeBudgetRevenueDetailsLabel();

    @Nonnull
    @Default("Budgeted Gross Profit")
    @Info("Translated Label for Budgeted Gross Profit")
    String getBudgetedGrossProfitLabel();

    @Nonnull
    @Default("Task Budgeted Revenue")
    @Info("Translated Label for Task Budgeted Revenue")
    String getTaskBudgetedRevenueLabel();

    @Nonnull
    @Default("Overhead costs allocated to the project can be recovered from the customer"
            + " by adding the costs to the LEMS mark-ups when you invoice the customer. "
            + "If you add a mark-up to your overhead costs, such mark-up must also be included"
            + " in your LEMS mark-ups when you invoice the customer")
    @Info("Tip for Overhead Cost Allocation ")
    String getOverheadCostAllocationTip();

    @Nonnull
    @Default("Entered Deposit Amount is greater than Budget Cost")
    @Info("Translated  Label for Contract Type Required")
    String getCheckDepositAmount();

    @Nonnull
    @Default("Select Phase")
    @Info("Translated Label for Select Phase")
    String getSelectPhaseLabel();

    @Nonnull
    @Default("Select Milestone")
    @Info("Translated Label for Select Milestone")
    String getSelectMilestoneLabel();
}
