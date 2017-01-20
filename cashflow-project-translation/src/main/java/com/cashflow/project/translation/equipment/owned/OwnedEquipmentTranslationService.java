package com.cashflow.project.translation.equipment.owned;

import com.cashflow.core.annotations.Config;
import com.cashflow.core.annotations.ConfigProject;
import com.cashflow.core.annotations.ConfigRoot;
import com.cashflow.core.annotations.Default;
import com.cashflow.core.annotations.Info;
import javax.annotation.Nonnull;

/**
 *
 * 
 * @since 13 Dec, 2016, 6:29:58 PM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface OwnedEquipmentTranslationService {

    @Nonnull
    @Default("Owned Equipment Charge Schedule")
    @Info("Translated Header Label for Owned Equipment Charge Schedule")
    String getOwnedEquipmentHeader();

    @Nonnull
    @Default("Equipment Charge Schedule ID")
    @Info("Translated Header Label for Equipment Charge Schedule ID")
    String getEquipmentChargeScheduleId();

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
    @Default("Category")
    @Info("Translated Label for Category")
    String getCategoryLabel();

    @Nonnull
    @Default("Sub-Category")
    @Info("Translated Label for Sub-Category")
    String getSubCategoryLabel();

    @Nonnull
    @Default("Equipment Name")
    @Info("Translated Label for Equipment Name")
    String getEquipmentNameLabel();

    @Nonnull
    @Default("Equipment ID")
    @Info("Translated Label for Equipment ID")
    String getEquipmentIdLabel();

    @Nonnull
    @Default("Cost Rate")
    @Info("Translated Label for Cost Rate")
    String getCostRateLabel();

    @Nonnull
    @Default("Bill Rate")
    @Info("Translated Label for Bill Rate")
    String getBillRateLabel();

    @Nonnull
    @Default("Units")
    @Info("Translated Label for Units")
    String getUnitsLabel();

    @Nonnull
    @Default("Amount")
    @Info("Translated Label for Amount")
    String getAmountLabel();

    @Nonnull
    @Default("Billable")
    @Info("Translated Label for Billable")
    String getBillableLabel();

    @Nonnull
    @Default("Attach File")
    @Info("Translated Label for Attach File")
    String getAttachFileLabel();

    @Nonnull
    @Default("Action")
    @Info("Translated Label for Action")
    String getActionLabel();

    @Nonnull
    @Default("Cancel")
    @Info("Translated Label for Cancel")
    String getCancelLabel();

    @Nonnull
    @Default("Save")
    @Info("Translated Label for Save")
    String getSaveLabel();

    @Nonnull
    @Default("Approve")
    @Info("Translated Label for Approve")
    String getApproveLabel();

    @Nonnull
    @Default("Select Equipment Type")
    @Info("Translated Header Label for Select Equipment Type")
    String getEquipmentTypeHeader();

    @Nonnull
    @Default("Equipment Type")
    @Info("Translated Label for Equipment Type")
    String getEquipmentTypeLabel();

    @Nonnull
    @Default("Select Category")
    @Info("Translated Label for Select Category")
    String getEquipmentCategoryLabel();

    @Nonnull
    @Default("Please Select Equipment Type")
    @Info("Translated Label for Equipment Type Required")
    String getEquipmentTypeRequiredLabel();

    @Nonnull
    @Default("Next")
    @Info("Translated Label for Next")
    String getNextLabel();

    @Nonnull
    @Default("Add Line")
    @Info("Translated Label for Add Line")
    String getAddLineLabel();

    @Nonnull
    @Default("Billable Type")
    @Info("Translated Label for Billable Type")
    String getBillableTypeLabel();

    @Nonnull
    @Default("Project Must be Approved to add Equipment")
    @Info("Translated message for Project Must be Approved to add Equipment")
    String getProjectMustApprovedLabel();

    @Nonnull
    @Default("File Uploaded Successfully")
    @Info("Translated message for File Uploaded Successfully")
    String getUploadSuccessMessage();

    @Nonnull
    @Default("Attach Equipment")
    @Info("Translated message for Attach Equipment")
    String getEquipmentAttachmentsLabel();

    @Nonnull
    @Default("Owned Equipment Saved Successfully")
    @Info("Translated message for Owned Equipment Saved Successfully")
    String getOwnedEquipmentSavedSuccessMessage();

    @Nonnull
    @Default("Please Atleast add one detail of Equipemnt")
    @Info("Translated message for Equipment Detail Required")
    String getAtleastOneEquipmentRequried();

    @Nonnull
    @Default("Please Select Equipment ID")
    @Info("Translated message forPlease Select Equipment ID")
    String getEquipmentIdRequiredMessage();

    @Nonnull
    @Default("Success Message")
    @Info("Translated  Label for Success Message")
    String getSuccessMessageHeaderLabel();

    @Nonnull
    @Default("OK")
    @Info("Translated Label for OK")
    String getOkLabel();

    @Nonnull
    @Default("Validate Message")
    @Info("Translated Label for Validate Message")
    String getValidateMessageLabel();

}
