package com.cashflow.project.translation.subcontractor;

import com.cashflow.core.annotations.Config;
import com.cashflow.core.annotations.ConfigProject;
import com.cashflow.core.annotations.ConfigRoot;
import com.cashflow.core.annotations.Default;
import com.cashflow.core.annotations.Info;
import javax.annotation.Nonnull;

/**
 *
 * 
 * @since Dec 26, 2016, 6:46:28 PM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface SubContractorExpenseTranslationService {

    @Nonnull
    @Default("Add New Sub-contractor Expenses")
    @Info("Translated Header Label for add Sub-contractor Expenses")
    String getAddSubContractorExpenses();

    @Nonnull
    @Default("Add New Sub-contractor")
    @Info("Translated Header Label for add Sub-contractor")
    String getAddSubContractor();

    @Nonnull
    @Default("Date")
    @Info("Translated label for date")
    String getDate();

    @Nonnull
    @Default("Sub-Contractor Expense Schedule ID")
    @Info("Translated label for Sub-Contractor Expense Schedule ID")
    String getSubContractorExpenseID();

    @Nonnull
    @Default("Customer")
    @Info("Translated label for Customer")
    String getCustomer();

    @Nonnull
    @Default("Project")
    @Info("Translated label for Project")
    String getProject();

    @Nonnull
    @Default("Phase")
    @Info("Translated label for Phase")
    String getPhase();

    @Nonnull
    @Default("All")
    @Info("Translated label for All")
    String getAll();

    @Nonnull
    @Default("Milestone")
    @Info("Translated label for Milestone")
    String getMilestone();

    @Nonnull
    @Default("Task")
    @Info("Translated label for Task")
    String getTask();

    @Nonnull
    @Default("Task ID")
    @Info("Translated label for Task ID")
    String getTaskID();

    @Nonnull
    @Default("Supplier")
    @Info("Translated label for Supplier")
    String getSupplier();

    @Nonnull
    @Default("Supplier ID")
    @Info("Translated label for Supplier ID")
    String getSupplierID();

    @Nonnull
    @Default("Please select Supplier")
    @Info("Translated label for Supplier required")
    String getSupplierRequiredMessage();

    @Nonnull
    @Default("Sub-Contractor Service")
    @Info("Translated label for Sub-Contractor Service")
    String getSubContractorService();

    @Nonnull
    @Default("Please enter Sub-Contractor Service")
    @Info("Translated label for Sub-Contractor Service required")
    String getSubContractorServiceRequiredMessage();

    @Nonnull
    @Default("Description")
    @Info("Translated label for Description")
    String getDescription();

    @Nonnull
    @Default("Invoice Number")
    @Info("Translated label for Invoice Number")
    String getInvoiceNumber();

    @Nonnull
    @Default("Please enter Invoice Number")
    @Info("Translated message for Invoice Number required")
    String getInvoiceNumberRequiredMessage();

    @Nonnull
    @Default("Invoice Due Date")
    @Info("Translated label for Invoice Due Date")
    String getInvoiceDueDate();

    @Nonnull
    @Default("Supplier Invoice Amount")
    @Info("Translated label for Supplier Invoice Amount")
    String getSupplierInvoiceAmount();

    @Nonnull
    @Default("Tax")
    @Info("Translated label for Tax")
    String getTax();

    @Nonnull
    @Default("Tax Amount")
    @Info("Translated label for Tax Amount")
    String getTaxAmount();

    @Nonnull
    @Default("Categorization")
    @Info("Translated label for Categorization")
    String getCategorization();

    @Nonnull
    @Default("Labor Cost")
    @Info("Translated label for Labor")
    String getLabor();

    @Nonnull
    @Default("Materials Cost")
    @Info("Translated label for Materials")
    String getMaterials();

    @Nonnull
    @Default("Equipment Cost")
    @Info("Translated label for Equipment")
    String getEquipment();

    @Nonnull
    @Default("Non-Billable Amount")
    @Info("Translated label for Non-Billable Amount")
    String getNonBillableAmount();

    @Nonnull
    @Default("Supplier Invoice Total")
    @Info("Translated label for Supplier Invoice Total")
    String getSupplierInvoiceTotal();

    @Nonnull
    @Default("Notes")
    @Info("Translated label for Notes")
    String getNotes();

    @Nonnull
    @Default("Mark-up")
    @Info("Translated label for Mark-up")
    String getMarkUp();

    @Nonnull
    @Default("Mark-up Amount")
    @Info("Translated label for Mark-up Amount")
    String getMarkUpAmount();

    @Nonnull
    @Default("Total Mark-up Amount")
    @Info("Translated label for Total Mark-up Amount")
    String getTotalMarkUpAmount();

    @Nonnull
    @Default("Billable Amount")
    @Info("Translated label for Billable Amount")
    String getBillableAmount();

    @Nonnull
    @Default("Total Billable Amount")
    @Info("Translated label for Total Billable Amount")
    String getTotalBillableAmount();

    @Nonnull
    @Default("Please enter Non-Billable amount")
    @Info("Translated message for Non-Billable required")
    String getNonBillableRequiredMessage();

    @Nonnull
    @Default("Status")
    @Info("Translated label for Status")
    String getStatus();

    @Nonnull
    @Default("Attach Files")
    @Info("Translated label for Attach Files")
    String getAttachFiles();

    @Nonnull
    @Default("Cancel")
    @Info("Translated label for Cancel")
    String getCancel();

    @Nonnull
    @Default("Save")
    @Info("Translated label for Save")
    String getSave();

    @Nonnull
    @Default("Edit")
    @Info("Translated label for Edit")
    String getEdit();

    @Nonnull
    @Default("Approve")
    @Info("Translated label for Approve")
    String getApprove();

    @Nonnull
    @Default("SubContractor expense saved successfully.")
    @Info("Translated message for save success")
    String getSuccessMessage();

    @Nonnull
    @Default("SubContractor expense approved successfully.")
    @Info("Translated message for approved success")
    String getApprovedSuccessMessage();

    @Nonnull
    @Default("SubContractor expense updated successfully.")
    @Info("Translated message for save success")
    String getUpdateSuccessMessage();

    @Nonnull
    @Default("Select Mark-Up Method")
    @Info("Translated Label for Select Mark-Up Method")
    String getSelectMarkUpMethod();

    @Nonnull
    @Default("Filter by: (status)")
    @Info("Translated Label for filter by label")
    String getFilterByStatusLabel();

    @Nonnull
    @Default("File Uploaded Successfully")
    @Info("Translated Label for File Uploaded Successfully")
    String getUploadSuccessMessage();

    @Nonnull
    @Default("SubContractor saved successfully")
    @Info("Translated Label for subcontractor saved Successfully")
    String getSubcontractorSuccessMessage();

    @Nonnull
    @Default("Categorization total can not be greater than Invoice Total")
    @Info("Translated message for Categorization validation")
    String getValidationMessageForCategorization();

    @Nonnull
    @Default("Message")
    @Info("Translated  Label for Message")
    String getSuccessMessageHeaderLabel();

}
