package com.cashflow.project.translation.equipment;

import com.cashflow.core.annotations.Config;
import com.cashflow.core.annotations.ConfigProject;
import com.cashflow.core.annotations.ConfigRoot;
import com.cashflow.core.annotations.Default;
import com.cashflow.core.annotations.Info;
import javax.annotation.Nonnull;

/**
 *
 * 
 * @since Dec 20, 2016, 3:28:19 PM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface EquipmentTabTranslationService {

    @Nonnull
    @Default("Equipment Expenses")
    @Info("Translated Header Label for Equipment Expenses")
    String getEquipmentExpenses();

    @Nonnull
    @Default("Equipment Expenses are recorded and accumulated through Equipment Charge Schedules and through direct invoices from suppliers. Equipment expanses can be charged to a project with no mark-up or with a mark-up.")
    @Info("Expense description line 1")
    String getExpenseDetails1();

    @Nonnull
    @Default(" For detailed information and guidance on how to work with Equipment expenses on your project, please visit our ")
    @Info("Expense description line 2")
    String getExpenseDetails2();

    @Nonnull
    @Default("Knowledge Base")
    @Info("Translated label for Knowledge Base")
    String getKnowledgeBaseLabel();

    @Nonnull
    @Default("LEMS - Equipment Expenses.")
    @Info("Translated label for LEMS - Equipment Expense.")
    String getEquipmentLEMSLabel();

    @Nonnull
    @Default("Add New Equipment Charge Schedule")
    @Info("Translated label for Add New Equipment Charge Schedule")
    String getAddNewEquipmentChargeSchedule();

    @Nonnull
    @Default("Phase ID")
    @Info("Transleted label of Phase ID")
    String getPhaseId();

    @Nonnull
    @Default("Milestone ID")
    @Info("Transleted label of Milestone ID")
    String getMilestoneId();

    @Nonnull
    @Default("Task ID")
    @Info("Transleted label of Task ID")
    String getTaskId();

    @Nonnull
    @Default("Equipment Name")
    @Info("Transleted label of Equipment Name")
    String getEquipmentName();

    @Nonnull
    @Default("Equipment ID")
    @Info("Transleted label of Equipment ID")
    String getEquipmentID();

    @Nonnull
    @Default("Rented")
    @Info("Transleted label of Rented")
    String getRented();

    @Nonnull
    @Default("Cost Rate")
    @Info("Transleted label of Cost Rate")
    String getCostRate();

    @Nonnull
    @Default("Bill Rate")
    @Info("Transleted label of Bill Rate")
    String getBillRate();

    @Nonnull
    @Default("Units")
    @Info("Transleted label of Units")
    String getUnits();

    @Nonnull
    @Default("Amount")
    @Info("Transleted label of Amount")
    String getAmount();

    @Nonnull
    @Default("Action")
    @Info("Transleted label of Action")
    String getAction();

    @Nonnull
    @Default("Cancel")
    @Info("Transleted label of Cancel")
    String getCancel();

    @Nonnull
    @Default("Filter by: (All)")
    @Info("Translated Label for filter by")
    String getFilterByLabel();

}
