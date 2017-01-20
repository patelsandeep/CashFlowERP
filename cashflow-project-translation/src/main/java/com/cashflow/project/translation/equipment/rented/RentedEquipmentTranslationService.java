package com.cashflow.project.translation.equipment.rented;

import com.cashflow.core.annotations.Config;
import com.cashflow.core.annotations.ConfigProject;
import com.cashflow.core.annotations.ConfigRoot;
import com.cashflow.core.annotations.Default;
import com.cashflow.core.annotations.Info;
import javax.annotation.Nonnull;

/**
 *
 * 
 * @since 19 Dec, 2016, 10:25:37 AM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface RentedEquipmentTranslationService {

    @Nonnull
    @Default("Rented Equipment Charge Schedule")
    @Info("Translated Header Label for Rented Equipment Charge Schedule")
    String getRentedEquipmentHeader();

    @Nonnull
    @Default("Date")
    @Info("Translated Header Label for Date")
    String getDateLabel();

    @Nonnull
    @Default("Equipment Charge Schedule ID")
    @Info("Translated Header Label for Equipment Charge Schedule ID")
    String getEquipmentChargeScheduleId();

    @Nonnull
    @Default("Equipment Charge Schedule ID Required")
    @Info("Translated Header Label for Equipment Charge Schedule ID Required")
    String getEquipmentChargeScheduleIdRequired();

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
    @Default("Suppier")
    @Info("Translated Label for Suppier")
    String getSuppierLabel();

    @Nonnull
    @Default("Suppier Required")
    @Info("Translated Label for Suppier Required")
    String getSuppierRequiredLabel();

    @Nonnull
    @Default("Suppier ID")
    @Info("Translated Label for  Suppier ID")
    String getSuppierIdLabel();

    @Nonnull
    @Default("Suppier ID Required")
    @Info("Translated Label for  Suppier ID Required")
    String getSuppierIdRequiredLabel();

    @Nonnull
    @Default("Equipment Name")
    @Info("Translated Label for Equipment Name")
    String getEquipmentNameLabel();

    @Nonnull
    @Default("Equipment Name Required")
    @Info("Translated Label for Equipment Name Required")
    String getEquipmentNameRequiredLabel();

    @Nonnull
    @Default("Serial Number")
    @Info("Translated Label for Serial Number")
    String getSerialNumberLabel();

    @Nonnull
    @Default("Serial Number Required")
    @Info("Translated Label for Serial Number Required")
    String getSerialNumberRequiredLabel();

    @Nonnull
    @Default("Invoice Number")
    @Info("Translated Label for Invoice Number")
    String getInvoiceNumberLabel();

    @Nonnull
    @Default("Invoice Number Required")
    @Info("Translated Label for Invoice Number Required")
    String getInvoiceNumberRequiredLabel();

    @Nonnull
    @Default("Invoice Due Date")
    @Info("Translated Label for Invoice Due Date")
    String getInvoiceDueDateLabel();

    @Nonnull
    @Default("Supplier Invoice Amount")
    @Info("Translated Label for Supplier Invoice Amount")
    String getSupplierInvoiceAmountLabel();

    @Nonnull
    @Default("Tax")
    @Info("Translated Label for Tax")
    String getTaxLabel();

    @Nonnull
    @Default("Supplier Invoice Total")
    @Info("Translated Label for Supplier Invoice Total")
    String getSupplierInvoiceTotalLabel();

    @Nonnull
    @Default("Billing Unit")
    @Info("Translated Label for Billing Unit")
    String getBillingUnitLabel();

    @Nonnull
    @Default("QTY")
    @Info("Translated Label for QTY")
    String getQtyLabel();

    @Nonnull
    @Default("Cost Rate")
    @Info("Translated Label for Cost Rate")
    String getCostRateLabel();

    @Nonnull
    @Default("Mark-Up")
    @Info("Translated Label for Mark-Up")
    String getMarkUpLabel();

    @Nonnull
    @Default("Mark-Up Amount")
    @Info("Translated Label for Mark-Up Amount")
    String getMarkUpAmountLabel();

    @Nonnull
    @Default("Billing Rate")
    @Info("Translated Label for Billing Rate")
    String getBillingRateLabel();

    @Nonnull
    @Default("Billable QTY")
    @Info("Translated Label for Billable QTY")
    String getBillableQtyLabel();

    @Nonnull
    @Default("Billable Amount")
    @Info("Translated Label for Billable Amount")
    String getBillableAmountLabel();

    @Nonnull
    @Default("Non-Billable Amount")
    @Info("Translated Label for Non-Billable Amount")
    String getNonBillableAmountLabel();

    @Nonnull
    @Default("Non-Billable QTY")
    @Info("Translated Label for Non-Billable QTY")
    String getNonBillableQtyLabel();

    @Nonnull
    @Default("Notes")
    @Info("Translated Label for Notes")
    String getNotesLabel();

    @Nonnull
    @Default("Attach Files")
    @Info("Translated Label for Attach Files")
    String getAttachFilesLabel();

    @Nonnull
    @Default("Advance Settings")
    @Info("Translated Label for Advance Settings")
    String getAdvanceSettingsLabel();

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
    @Default("File Uploaded Successfully")
    @Info("Translated Label for File Uploaded Successfully")
    String getUploadSuccessMessage();

    @Nonnull
    @Default("Rented Equipment Attachment")
    @Info("Translated Label for Rented Equipment Attachment")
    String getRentedEquipAttachmentsLabel();

    @Nonnull
    @Default("Select Mark-Up Method")
    @Info("Translated Label for Select Mark-Up Method")
    String getSelectMarkUpMethod();

    @Nonnull
    @Default("Select Bill Unit")
    @Info("Translated Label for Select Bill Unit")
    String getSelectBillUnitMethod();

    @Nonnull
    @Default("Rented Equipment Saved Successfully")
    @Info("Translated Message for Rented Equipment Saved Successfully")
    String getRentedEquipSavedSuccessfully();

    @Nonnull
    @Default("QTY must be Greater or Equal with Billable and Non-Billable QTY")
    @Info("Translated Label for QTY must be Greater or Equal with Billable and Non-Billable QTY")
    String getQtyMustMatchLabel();

    @Nonnull
    @Default("Success Message")
    @Info("Translated  Label for Success Message")
    String getSuccessMessageHeaderLabel();

    @Nonnull
    @Default("OK")
    @Info("Translated Label for OK")
    String getOkLabel();

}
