package com.cashflow.project.translation.people;

import com.cashflow.core.annotations.Config;
import com.cashflow.core.annotations.ConfigProject;
import com.cashflow.core.annotations.ConfigRoot;
import com.cashflow.core.annotations.Default;
import com.cashflow.core.annotations.Info;
import javax.annotation.Nonnull;

/**
 *
 * 
 * @since Nov 25, 2016, 7:02:15 PM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface ProjectTeamMemberTranslationService {

    @Nonnull
    @Default("Select Team Member Category")
    @Info("Select Team Member Category label")
    String getSelectTeamMemberCategory();

    @Nonnull
    @Default("Please Select Team Member Category")
    @Info("Please Select Team Member Category message")
    String getSelectTeamMemberCategoryRquiredMessage();

    @Nonnull
    @Default("Category")
    @Info("Category label")
    String getCategory();

    @Nonnull
    @Default("Cancel")
    @Info("Cancel label")
    String getCancel();

    @Nonnull
    @Default("Next")
    @Info("Next label")
    String getNext();

    @Nonnull
    @Default("Add New Team Member - Employee")
    @Info("Add New Team Member label")
    String getAddNewTeamMemberEmployee();

    @Nonnull
    @Default("Add New Team Member - Sub-Contractor/Consultant")
    @Info("Add New Team Member Sub-Contractor/Consultant label")
    String getAddNewTeamMemberSubContractor();

    @Nonnull
    @Default("Team Member Detail")
    @Info("Team Member Detail label")
    String getTeamMemberDetail();

    @Nonnull
    @Default("Customer")
    @Info("Translated label for Customer")
    String getCustomerLabel();

    @Nonnull
    @Default("Project")
    @Info("Translated label for Project")
    String getProjectLabel();

    @Nonnull
    @Default("Phase")
    @Info("Translated Label for Phase")
    String getPhaseLabel();

    @Nonnull
    @Default("Select Phase")
    @Info("Translated Label for Select Phase")
    String getSelectPhaseLabel();

    @Nonnull
    @Default("Milestone")
    @Info("Translated Label for Milestone")
    String getMilestoneLabel();

    @Nonnull
    @Default("Select Milestone")
    @Info("Translated Label for Select Milestone")
    String getSelectMilestoneLabel();

    @Nonnull
    @Default("Task")
    @Info("Translated Label for Task")
    String getTaskLabel();

    @Nonnull
    @Default("Select Task")
    @Info("Translated Label for Select Task")
    String getSelectTaskLabel();

    @Nonnull
    @Default("Task ID")
    @Info("Translated Label for Task ID")
    String getTaskIdLabel();

    @Nonnull
    @Default("Department")
    @Info("Translated Label for Department")
    String getDepartment();

    @Nonnull
    @Default("Select Department")
    @Info("Translated Label for Select Department")
    String getSelectDepartment();

    @Nonnull
    @Default("Organization Level")
    @Info("Translated Label for Organization Level")
    String getOrganizationLevel();

    @Nonnull
    @Default("Select Organization Level")
    @Info("Translated Label for Select Organization Level")
    String getSelectOrganizationLevel();

    @Nonnull
    @Default("Name")
    @Info("Translated Label for Name")
    String getName();

    @Nonnull
    @Default("Employee ID")
    @Info("Translated Label for Employee ID")
    String getEmployeeID();

    @Nonnull
    @Default("Supplier")
    @Info("Translated Label for Supplier")
    String getSupplier();

    @Nonnull
    @Default("Select Supplier")
    @Info("Translated Label for Select Supplier")
    String getSelectSupplier();

    @Nonnull
    @Default("Supplier ID")
    @Info("Translated Label for Supplier ID")
    String getSupplierID();

    @Nonnull
    @Default("Team Member ID")
    @Info("Translated Label for Team Member ID")
    String getTeamMemberID();

    @Nonnull
    @Default("Platform Role")
    @Info("Translated Label for Platform Role")
    String getPlatformRole();

    @Nonnull
    @Default("Project Role")
    @Info("Translated Label for Project Role")
    String getProjectRole();

    @Nonnull
    @Default("Select Project Role")
    @Info("Translated Label for Select Project Role")
    String getSelectProjectRole();

    @Nonnull
    @Default("Cost Rate Source")
    @Info("Translated Label for Cost Rate Source")
    String getCostRateSource();

    @Nonnull
    @Default("Select Cost Rate Source")
    @Info("Translated Label for Select Cost Rate Source")
    String getSelectCostRateSource();

    @Nonnull
    @Default("Select mark-up method")
    @Info("Translated Label for Select mark-up method")
    String getSelectMarkUpMethod();

    @Nonnull
    @Default("Cost Rate")
    @Info("Translated Label for Cost Rate")
    String getCostRate();

    @Nonnull
    @Default("Bill Rate")
    @Info("Translated Label for Bill Rate")
    String getBillRate();

    @Nonnull
    @Default("Mark-up Method")
    @Info("Translated Label for Mark-up Method")
    String getMarkupMethod();

    @Nonnull
    @Default("Add Project Role")
    @Info("Translated Label for Add Project Role")
    String getAddProjectRole();

    @Nonnull
    @Default("Add New Project Role")
    @Info("Translated Label for Add Project Role")
    String getAddNewProjectRole();

    @Nonnull
    @Default("Approval Permissions")
    @Info("Translated Label for Approval Permissions")
    String getApprovalPermissions();

    @Nonnull
    @Default("Save")
    @Info("Translated Label for Save")
    String getSave();

    @Nonnull
    @Default("Update")
    @Info("Translated Label for Update")
    String getUpdate();

    @Nonnull
    @Default("Edit")
    @Info("Translated Label for Edit")
    String getEdit();

    @Nonnull
    @Default("Approve")
    @Info("Translated Label for Approve")
    String getApprove();

    @Nonnull
    @Default("Success Message")
    @Info("Translated  Label for Success Message")
    String getSuccessMessageHeaderLabel();

    @Nonnull
    @Default("OK")
    @Info("Translated Label for OK")
    String getOkLabel();

    @Nonnull
    @Default("Project Team Member Added Successfully")
    @Info("success message for Project Team Member Added Successfully")
    String getProjectTeamMemberSuccessMessage();

    @Nonnull
    @Default("Project Team Member Updated Successfully")
    @Info("success message for Project Team Member Updated Successfully")
    String getProjectTeamMemberUpdateSuccessMessage();

    @Nonnull
    @Default("No employee found with specified name")
    @Info("No employee found message")
    String getNoEmployeeFoundMessage();

    @Nonnull
    @Default("Type at least 3 characters to search Employees")
    @Info("Translated Label for Type at least 3 characters to search Employees")
    String getMessageForEmployeesSearch();

    @Nonnull
    @Default("Please Select Project")
    @Info("Please Select Project message")
    String getSelectProjectRequiredMessage();

    @Nonnull
    @Default("Please Select Employee")
    @Info("Please Select Employee message")
    String getSelectEmployeeRequiredMessage();

    @Nonnull
    @Default("Please enter Team Member ID")
    @Info("Please enter Team Member ID message")
    String getMemberIDRequiredMessage();

    @Nonnull
    @Default("Please Select Supplier")
    @Info("Please enter Supplier message")
    String getSupplierRequiredMessage();

    @Nonnull
    @Default("Please add atleat one project role")
    @Info("Please add atleat one project role message")
    String getAddAtleastOneProjectRoleMessage();

    @Nonnull
    @Default("Please Select Project Role")
    @Info("Please Select Project Role")
    String getSelectProjectRoleRequired();

    @Nonnull
    @Default("Please Select Team Member name")
    @Info("Please Select Team Member name message")
    String getSelectMemberRequiredMessage();

    @Nonnull
    @Default("New Project Role Name")
    @Info("New Project Role Name label")
    String getNewProjectRoleName();

    @Nonnull
    @Default("Project Role Level")
    @Info("Project Role Level label")
    String getProjectRoleLevel();

    @Nonnull
    @Default("New Project Role Saved Successfully")
    @Info("Project Role Saved Successfully message")
    String getProjectRoleSuccessMessage();

    @Nonnull
    @Default("Select Project Role Level")
    @Info("select Project Role Level label")
    String getSelectProjectRoleLevel();

}
