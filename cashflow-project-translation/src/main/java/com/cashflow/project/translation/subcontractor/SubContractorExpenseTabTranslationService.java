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
 * @since Dec 26, 2016, 6:09:36 PM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface SubContractorExpenseTabTranslationService {

    @Nonnull
    @Default("Sub-contractor Expenses")
    @Info("Translated Header Label for Sub-contractor Expenses")
    String getSubContractorExpenses();

    @Nonnull
    @Default("You can add various mark-ups to your Sub-Contractor charges when you add them to your project.")
    @Info("Expense description line 1")
    String getExpenseDetails1();

    @Nonnull
    @Default(" For detailed information and guidance on how to work with Sub-Contractor expenses on your project, please visit our ")
    @Info("Expense description line 2")
    String getExpenseDetails2();

    @Nonnull
    @Default("Knowledge Base")
    @Info("Translated label for Knowledge Base")
    String getKnowledgeBaseLabel();

    @Nonnull
    @Default("LEMS - Sub-Contractor Expenses.")
    @Info("Translated label for LEMS - Sub-Contractor Expense.")
    String getSubContractorLEMSLabel();

    @Nonnull
    @Default("Add New Sub-Contractor Expenses")
    @Info("Translated label for Add New Sub-Contractor Expenses")
    String getAddNewSubContractorExpenses();

    @Nonnull
    @Default("Project ID")
    @Info("Translated label for Project ID")
    String getProjectID();

    @Nonnull
    @Default("Sub-Contractor")
    @Info("Translated label for Sub-Contractor")
    String getSubContractor();

    @Nonnull
    @Default("Supplier ID")
    @Info("Translated label for Supplier ID")
    String getSupplierID();

    @Nonnull
    @Default("Invoice Date")
    @Info("Translated label for Invoice Date")
    String getInvoiceDate();

    @Nonnull
    @Default("Invoice Number")
    @Info("Translated label for Invoice Number")
    String getInvoiceNumber();

    @Nonnull
    @Default("Cost Amount")
    @Info("Translated label for Cost Amount")
    String getCostAmount();

    @Nonnull
    @Default("Mark-up")
    @Info("Translated label for Mark-up")
    String getMarkUp();

    @Nonnull
    @Default("Mark-up Amount")
    @Info("Translated label for Mark-up Amount")
    String getMarkUpAmount();

    @Nonnull
    @Default("Bill Amount")
    @Info("Translated label for Bill Amount")
    String getBillAmount();

    @Nonnull
    @Default("Status")
    @Info("Translated label for Status")
    String getStatus();

    @Nonnull
    @Default("Action")
    @Info("Translated label for Action")
    String getAction();

    @Nonnull
    @Default("Cancel")
    @Info("Translated label for Cancel")
    String getCancel();

    @Nonnull
    @Default("Filter by: (Sub-Contractor Name)")
    @Info("Translated label for Filter by: (Sub-Contractor Name)")
    String getFilterByLabel();

}
