package com.cashflow.project.translation.material;

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
public interface MaterialExpenseTranslationService {

    @Nonnull
    @Default("Materials Expenses")
    @Info("Material Expenses Header")
    String getMaterialTabHeader();

    @Nonnull
    @Default("You can charge Inventory Materials from your Inventory Module directily to your project, "
             + "and you can also charge Materials purchased directly from suppliers to your project. "
             + "You can add various mark-ups to your Material changes when you add them to your project. "
             + "For detailed information and guidance on how to work with Materials expenses on your project, "
             + "please visit our ")
    @Info("Materials Expenses Info")
    String getMaterialExpensesInfo();

    @Nonnull
    @Default("and view")
    @Info("And View Translation")
    String getAndViewTranslation();

    @Nonnull
    @Default("Knowledge Base")
    @Info("Knowledge Base link text")
    String getKnowledgeBaseLinkText();

    @Nonnull
    @Default("LEMS-Materials Expenses")
    @Info("Material Expenses Knowledge Base Link text")
    String getMaterialExpenseKnowledgeBaseSection();

    @Nonnull
    @Default("Project ID")
    @Info("Project ID Label")
    String getProjectIdLabel();

    @Nonnull
    @Default("Phase ID")
    @Info("Phase ID Label")
    String getPhaseIdLabel();

    @Nonnull
    @Default("Milestone ID")
    @Info("Milestone ID Label")
    String getMilestoneIdLabel();

    @Nonnull
    @Default("Task ID")
    @Info("Task ID Label")
    String getTaskIdLabel();

    @Nonnull
    @Default("Material Name")
    @Info("Material Name Label")
    String getMaterialNameLabel();

    @Nonnull
    @Default("Material ID")
    @Info("Material ID Label")
    String getMaterialIdLabel();

    @Nonnull
    @Default("Inventory")
    @Info("Inventory Label")
    String getInventoryLabel();

    @Nonnull
    @Default("Cost Rate")
    @Info("Cost Rate Label")
    String getCostRateLabel();

    @Nonnull
    @Default("Bill Rate")
    @Info("Bill Rate Label")
    String getBillRateLabel();

    @Nonnull
    @Default("Units")
    @Info("Units Label")
    String getUnitsLabel();

    @Nonnull
    @Default("Amount")
    @Info("Amount Label")
    String getAmountLabel();

    @Nonnull
    @Default("Action")
    @Info("Action Label")
    String getActionLabel();

    @Nonnull
    @Default("Add New Materials Expenses")
    @Info("Add New Material Expenses Button Label")
    String getNewMaterialExpensesButtonLabel();

    @Nonnull
    @Default("Cancel")
    @Info("Cancel Button Label")
    String getCancelButtonLabel();

    @Nonnull
    @Default("Edit")
    @Info("Material Edit label")
    String getMaterialEditLabel();

    @Nonnull
    @Default("View")
    @Info("Material View Label")
    String getMaterialViewLabel();

}
