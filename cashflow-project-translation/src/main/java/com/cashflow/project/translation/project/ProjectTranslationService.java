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
public interface ProjectTranslationService {

    @Nonnull
    @Default("Create a New Project")
    @Info("Translated Header Label for Create a New Project")
    String getCreateProjectHeader();

    @Nonnull
    @Default("Project Name")
    @Info("Translated label for Project Name")
    String getProjectNameLabel();

    @Nonnull
    @Default("Project Name Required")
    @Info("Translated Message  for Project Name Required")
    String getProjectNameRequiredMessage();

    @Nonnull
    @Default("Project ID")
    @Info("Translated label for Project ID")
    String getProjectIdLabel();

    @Nonnull
    @Default("Project ID Required")
    @Info("Translated Message for Project ID Required")
    String getProjectIdRequiredMessage();

    @Nonnull
    @Default("Customer Name")
    @Info("Translated label for Customer Name")
    String getCustomerNameLabel();

    @Nonnull
    @Default("Department Name")
    @Info("Translated label for Department Name")
    String getDepartmentNameLabel();

    @Nonnull
    @Default("Customer Name Required")
    @Info("Translated label for Customer Name")
    String getCustomerRequiredLabel();

    @Nonnull
    @Default("Customer ID")
    @Info("Translated  Label for Customer ID")
    String getCustomerIdLabel();

    @Nonnull
    @Default("Customer ID Required")
    @Info("Translated  Label for Customer ID Required")
    String getCustomerIdReuiredLabel();

    @Nonnull
    @Default("Department ID")
    @Info("Translated  Label for Department ID")
    String getDepartmentIdLabel();

    @Nonnull
    @Default("Project Start Date")
    @Info("Translated  Label for Start date")
    String getStartDateLabel();

    @Nonnull
    @Default("Project End date")
    @Info("Translated  Label for End date")
    String getEndDateLabel();

    @Nonnull
    @Default("Contract Type")
    @Info("Translated  Label for Contract Type")
    String getContractTypeLabel();

    @Nonnull
    @Default("Please Select Contract Type")
    @Info("Translated  Label for Contract Type Required")
    String getContractTypeRequiredLabel();

    @Nonnull
    @Default("Entered Deposit Amount is greater than Budget Cost")
    @Info("Translated  Label for Contract Type Required")
    String getCheckDepositAmount();

    @Nonnull
    @Default("Revenue Rec Method")
    @Info("Translated  Label for Revenue Rec Method")
    String getRevenueRecMethodLabel();

    @Nonnull
    @Default("Please Select Revenue Rec Method")
    @Info("Translated  Label for Revenue Rec Method Required")
    String getRevenueRecMethodRequiredLabel();

    @Nonnull
    @Default("Project Budgeted Cost")
    @Info("Translated  Label for Project Budgeted Cost")
    String getProjectBudgetedCostLabel();

    @Nonnull
    @Default("Project Budgeted Cost Required")
    @Info("Translated  Label for Project Budgeted Cost Required")
    String getProjectBudgetedCostRequiredLabel();

    @Nonnull
    @Default("Project Revenue")
    @Info("Translated  Label for Project Revenue")
    String getProjectRevenueLabel();

    @Nonnull
    @Default("Project Revenue Required")
    @Info("Translated  Label for Project Revenue Required")
    String getProjectRevenueRequiredLabel();

    @Nonnull
    @Default("Project Manager")
    @Info("Translated  Label for Project Manager")
    String getProjectManagerLabel();

    @Nonnull
    @Default("Project Manager Required")
    @Info("Translated  Label for Project Manager")
    String getProjectManagerRequiredLabel();

    @Nonnull
    @Default("Project Status")
    @Info("Translated  Label for Project Status")
    String getProjectStatusLabel();

    @Nonnull
    @Default("Project Status Required")
    @Info("Translated  Label for Project Status Required")
    String getProjectStatusRequiredLabel();

    @Nonnull
    @Default("Currency")
    @Info("Translated  Label for Currency")
    String getCurrencyLabel();

    @Nonnull
    @Default("Currency Required")
    @Info("Translated  Label for Currency Required")
    String getCurrencyRequiredLabel();

    @Nonnull
    @Default("General Info")
    @Info("Translated  Label for General Info")
    String getGeneralInfoLabel();

    @Nonnull
    @Default("Phases")
    @Info("Translated  Label for Phases")
    String getPhasesLabel();

    @Nonnull
    @Default("Milestones")
    @Info("Translated  Label for Milestones")
    String getMilestonesLabel();

    @Nonnull
    @Default("Tasks")
    @Info("Translated  Label for Tasks")
    String getTasksLabel();

    @Nonnull
    @Default("People")
    @Info("Translated  Label for People")
    String getPeopleLabel();

    @Nonnull
    @Default("Time & Expenses")
    @Info("Translated  Label for Time & Expenses")
    String getTimeAndExpensesLabel();

    @Nonnull
    @Default("Equipment")
    @Info("Translated  Label for Equipment")
    String getEquipmentLabel();

    @Nonnull
    @Default("Materials")
    @Info("Translated  Label for Materials")
    String getMaterialsLabel();

    @Nonnull
    @Default("Sub-Contractors")
    @Info("Translated  Label for Sub-Contractors")
    String getSubContractorsLabel();

    @Nonnull
    @Default("Invoices")
    @Info("Translated  Label for Invoices")
    String getInvoicesLabel();

    @Nonnull
    @Default("Cancel")
    @Info("Translated  Label for Cancel")
    String getCancelLabel();

    @Nonnull
    @Default("Save")
    @Info("Translated  Label for Save")
    String getSaveLabel();

    @Nonnull
    @Default("Approve")
    @Info("Translated  Label for Approve")
    String getApproveLabel();

    @Nonnull
    @Default("OK")
    @Info("Translated  Label for OK")
    String getOkLabel();

    @Nonnull
    @Default("Success Message")
    @Info("Translated  Label for Success Message")
    String getSuccessMessageHeaderLabel();

    @Nonnull
    @Default("Message")
    @Info("Translated  Label for Message")
    String getMessageLabel();

    @Nonnull
    @Default("Project General Information Saved Successfully")
    @Info("Translated  Sucess Message for Project General Information Saved")
    String getProjectGeneralInfoSavedSuccessfully();

    @Nonnull
    @Default("Project General Information Update Successfully")
    @Info("Translated  Sucess Message for Project General Information Saved")
    String getProjectGeneralInfoUpdateSuccessfully();

    @Nonnull
    @Default("Project General Information Approved Successfully")
    @Info("Translated  Sucess Message for Project General Information Approved")
    String getProjectGeneralInfoApprovedSuccessfully();

    @Nonnull
    @Default("File Uploaded Successfully!")
    @Info("success message for file upload")
    String getUploadSuccessMessage();

    @Nonnull
    @Default("This Project ID Already Registered.Please Enter Different Project ID")
    @Info("Translated Message for Already registered Project ID")
    String getProjectIdAlreadyRegistered();

    @Nonnull
    @Default("Select Contract Type")
    @Info("Translated Label for Select Contract Type")
    String getSelectContractTypeLabel();

    @Nonnull
    @Default("Select Currency")
    @Info("Translated Label for Select Currency")
    String getSelectCurrencyLabel();

    @Nonnull
    @Default("Select Project Level Category")
    @Info("Translated Label for Select Project Level Category")
    String getSelectProjectLevelCategoryLabel();

    @Nonnull
    @Default("Select Project Status")
    @Info("Translated Label for Select Project Status")
    String getSelectProjectStatusLabel();

    @Nonnull
    @Default("Select Revenue Recording Method")
    @Info("Translated Label for Select Revenue Recording Method")
    String getSelectRevenueRecordingMethodLabel();

    @Nonnull
    @Default("Project Attachments")
    @Info("Translated Label for Project Attachments")
    String getProjectAttachmentsLabel();

    @Nonnull
    @Default("No Customer Or Department Found")
    @Info("Translated Label for No Customer Or Department Found")
    String getNoCustomerOrDeptFoundMessage();

    @Nonnull
    @Default("No Project Manager Found")
    @Info("Translated Label for No Project Manager Found")
    String getNoProjectManagerFoundMessage();

    @Nonnull
    @Default("Type at least 3 characters to search customers or Departments")
    @Info("Translated Label for Type at least 3 characters to search customers or Departments")
    String getMessageForCustomerDeptSearch();

    @Nonnull
    @Default("Type at least 3 characters to search Project Managers")
    @Info("Translated Label for Type at least 3 characters to search Project Managers")
    String getMessageForProjectManagerSearch();

    @Nonnull
    @Default("Please save the information in the General Info tab first before proceeding to other tabs.")
    @Info("Translated message for first save general info tab")
    String getFirstSaveGeneralInfoMessage();

    @Nonnull
    @Default("Project Gross Profit")
    @Info("Translated Label for Project Gross Profit")
    String getProjectGrossProfitLabel();

}
