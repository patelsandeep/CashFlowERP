package com.cashflow.project.translation.project;

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
public interface ProjectGeneralInfoTranslationService {

    @Nonnull
    @Default("Project Summary")
    @Info("Translated Header Label for Project Summary")
    String getCreateSummaryHeader();

    @Nonnull
    @Default("Project Summary Required")
    @Info("Translated Header Label for Project Summary Required")
    String getCreateSummaryRequiredLabel();

    @Nonnull
    @Default("Attach Project Detail Files")
    @Info("Translated Label for Project Detail Files")
    String getAttachProjectDetailFilesLabel();

    @Nonnull
    @Default("Attach Project Contract/Agreement Files")
    @Info("Translated Label for Project Contract/Agreement Files")
    String getAttachProjectContractFilesLabel();

    @Nonnull
    @Default("Project Location")
    @Info("Translated Label for Project Location")
    String getProjectLocationLabel();

    @Nonnull
    @Default("Project Location Required")
    @Info("Translated Label for Project Location Required")
    String getProjectLocationRequiredLabel();

    @Nonnull
    @Default("Location Permits Required")
    @Info("Translated Label for Location Permits Required")
    String getLocationPermitRequiredLabel();

    @Nonnull
    @Default("Attach Files")
    @Info("Translated Label for Attach Files")
    String getAttachFilesLabel();

    @Nonnull
    @Default("Project Insurance Required")
    @Info("Translated Label for Project Insurance Required")
    String getProjectInsuranceRequiredLabel();

    @Nonnull
    @Default("Customer Deposit Required")
    @Info("Translated Label for Customer Deposit Required")
    String getCustomerDepositRequiredLabel();

    @Nonnull
    @Default("Amount")
    @Info("Translated Label for Amount")
    String getAmountLabel();

    @Nonnull
    @Default("Customer Deposit Amount Required")
    @Info("Translated Label for Customer Deposit Amount Required")
    String getCustomerDepositAmountRequiredLabel();

    @Nonnull
    @Default("Project Summary Structure")
    @Info("Translated Label for Project Summary Structure")
    String getProjectSummaryStructureLabel();

    @Nonnull
    @Default("In cashflow projects and structured in the following manner")
    @Info("Translated Description for In cashflow projects and structured in the following manner:")
    String getStructureDescLabel();

    @Nonnull
    @Default("Project Level")
    @Info("Translated label for Project Level")
    String getProjectLevelLabel();

    @Nonnull
    @Default("Phase Level")
    @Info("Translated label for Phase Level")
    String getPhaseLevelLabel();

    @Nonnull
    @Default("Milestone Level")
    @Info("Translated label for Milestone Level")
    String getMilestoneLevelLabel();

    @Nonnull
    @Default("Task Level")
    @Info("Translated label for Task Level")
    String getTaskLevelLabel();

    @Nonnull
    @Default("a Task is the lowest group of work activities required to accomplish a set"
            + " deliverable within a project. Project budget data, actual costs and revenue "
            + "is captured at Task Level and is then rolled up into Milestones, Phases and "
            + "finally into the overall project. ")
    @Info("Translated label for Task Level Desc")
    String getTaskLevelDescLabel();

    @Nonnull
    @Default("these are specific deliverables along the project timeline that are achieved"
            + " as an outcome of worked activities completed under Tasks ")
    @Info("Translated label for Milestone Level Desc")
    String getMilestoneLevelDescLabel();

    @Nonnull
    @Default("this is a grouping of work activities required to deliver a major part of the project"
            + ". These work activities can be grouped  into milestones and tasks to ensure good "
            + "project management.")
    @Info("Translated label for Phase Level Desc")
    String getPhaseLevelDescLabel();

    @Nonnull
    @Default("this is the overall deliverable agreed with the customer. Work activities"
            + " required to deliver the overall project deliverable can be sub divided into"
            + " phases, milestones and tasks to ensure good project management.")
    @Info("Translated label for Project Level Desc")
    String getProjectLevelDescLabel();

    @Nonnull
    @Default("For detailed information and guidance on how you can structure your project,"
            + " please visit our ")
    @Info("Translated label for Structure Description")
    String getStructureDescFooterLabel();

    @Nonnull
    @Default("Knowledge Base")
    @Info("Translated label for Knowledge Base")
    String getKnowledgeBaseLabel();

    @Nonnull
    @Default("Project Structure.")
    @Info("Translated label for Project Structure")
    String getProjectStructureLabel();

    @Nonnull
    @Default("and view ")
    @Info("Translated label for and view ")
    String getAndViewLabel();

    @Nonnull
    @Default("Include Phases")
    @Info("Translated label for Include Phases")
    String getIncludePhasesLabel();

    @Nonnull
    @Default("Include Milestones")
    @Info("Translated label for Include Milestones")
    String getIncludeMilestonesLabel();

    @Nonnull
    @Default("Include Tasks")
    @Info("Translated label for Include Tasks")
    String getIncludeTasksLabel();

    @Nonnull
    @Default("Include Sub-Contractors")
    @Info("Translated label for Include Sub-Contractors")
    String getIncludeSubContractorsLabel();

    @Nonnull
    @Default("Include Customer Deposit/Retainer at")
    @Info("Translated label for Include Customer Deposit/Retainer at")
    String getIncludeDepositOrRetainerLabel();

    @Nonnull
    @Default("Issue Deposit Invoice")
    @Info("Translated label for Issue Deposit Invoice")
    String getIssueDepositInvoiceLabel();

    @Nonnull
    @Default("Cancel")
    @Info("Translated label Label for Cancel")
    String getCancelLabel();

    @Nonnull
    @Default("Save")
    @Info("Translated label Label for Save")
    String getSaveLabel();

    @Nonnull
    @Default("Update")
    @Info("Translated label Label for Update")
    String getUpdateLabel();

    @Nonnull
    @Default("Approve")
    @Info("Translated label Label for Approve")
    String getApproveLabel();

    @Nonnull
    @Default("Close")
    @Info("Translated label Label for Close")
    String getCloseLabel();

    @Nonnull
    @Default("Go to Project Dashboard")
    @Info("Translated Label for Go to Project Dashboard")
    String getProjectDashboardLinkLabel();

    @Nonnull
    @Default("Audit Trail and Notes")
    @Info("Translated Label for Audit Trail and Notes")
    String getAuditTrailandNotesLabel();

    @Nonnull
    @Default("Customer Deposit Level required")
    @Info("Translated Label for Customer Deposit Level required")
    String getCustomerDepositLevelrequiredLabel();

}
