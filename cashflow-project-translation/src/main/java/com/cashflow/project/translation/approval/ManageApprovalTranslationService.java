package com.cashflow.project.translation.approval;

import com.cashflow.core.annotations.Config;
import com.cashflow.core.annotations.ConfigProject;
import com.cashflow.core.annotations.ConfigRoot;
import com.cashflow.core.annotations.Default;
import com.cashflow.core.annotations.Info;
import javax.annotation.Nonnull;

/**
 *
 * 
 * @since Nov 21, 2016, 7:14:22 PM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface ManageApprovalTranslationService {

    @Nonnull
    @Default("Manage Approvals")
    @Info("Manage Approvals header label")
    String getManageApprovals();

    @Nonnull
    @Default("Customer")
    @Info("Customer label")
    String getCustomer();

    @Nonnull
    @Default("Project")
    @Info("Project label")
    String getProject();

    @Nonnull
    @Default("Phase")
    @Info("Phase label")
    String getPhase();

    @Nonnull
    @Default("Phase ID")
    @Info("Phase ID label")
    String getPhaseID();

    @Nonnull
    @Default("Managing Approvals")
    @Info("Managing Approvals label")
    String getManagingApprovals();

    @Nonnull
    @Default("Projects Managers and Supervisors are automatically assigned the role of approving all Project, Phase, Milestone and Task Level expenses. You can make changes and specify other Team Members who can approve expenses. ")
    @Info("Managing Approvals description")
    String getManagingDesc();

    @Nonnull
    @Default("Approver")
    @Info("Approver label")
    String getApprover();

    @Nonnull
    @Default("Project Role")
    @Info("Project Role label")
    String getProjectRole();

    @Nonnull
    @Default("Approval Level")
    @Info("Approval Level label")
    String getApprovalLevel();

    @Nonnull
    @Default("Time & Expenses")
    @Info("Time & Expenses label")
    String getTimeExpenses();

    @Nonnull
    @Default("Equipment")
    @Info("Equipment label")
    String getEquipment();

    @Nonnull
    @Default("Materials")
    @Info("Materials label")
    String getMaterials();

    @Nonnull
    @Default("Sub-Contractors")
    @Info("Sub-Contractor label")
    String getSubContractors();

    @Nonnull
    @Default("Overheads")
    @Info("Overheads label")
    String getOverheads();

    @Nonnull
    @Default("Change Orders")
    @Info("Change Orders label")
    String getChangeOrders();

    @Nonnull
    @Default("PPC")
    @Info("PPC label")
    String getPpcLabel();

    @Nonnull
    @Default("Invoices")
    @Info("Invoices label")
    String getInvoices();

    @Nonnull
    @Default("Phases")
    @Info("Phases label")
    String getPhases();

    @Nonnull
    @Default("Milestones")
    @Info("Milestones label")
    String getMilestones();

    @Nonnull
    @Default("Tasks")
    @Info("Tasks label")
    String getTasks();

    @Nonnull
    @Default("All")
    @Info("All label")
    String getAll();

    @Nonnull
    @Default("Add a Line")
    @Info("Add a Line label")
    String getAddLine();

    @Nonnull
    @Default("Save")
    @Info("Save label")
    String getSave();

    @Nonnull
    @Default("Cancel")
    @Info("Cancel label")
    String getCancel();

}
