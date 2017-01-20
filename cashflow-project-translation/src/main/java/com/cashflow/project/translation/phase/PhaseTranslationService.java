package com.cashflow.project.translation.phase;

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
public interface PhaseTranslationService {

    @Nonnull
    @Default("Project Phases")
    @Info("Project Phase Tab header label")
    String getHeader();

    @Nonnull
    @Default("Add New Phase")
    @Info("Add New Phase label")
    String getAddNewPhase();

    @Nonnull
    @Default("Count")
    @Info("Count label")
    String getCount();

    @Nonnull
    @Default("Phase ID")
    @Info("Phase ID label")
    String getPhaseID();

    @Nonnull
    @Default("Phase Name")
    @Info("Phase Name label")
    String getPhaseName();

    @Nonnull
    @Default("Start Date")
    @Info("Start Date label")
    String getStartDate();

    @Nonnull
    @Default("End Date")
    @Info("End Date label")
    String getEndDate();

    @Nonnull
    @Default("Milestones")
    @Info("Milestones label")
    String getMilestones();

    @Nonnull
    @Default("Tasks")
    @Info("Tasks label")
    String getTasks();

    @Nonnull
    @Default("Progress")
    @Info("Progress label")
    String getProgress();

    @Nonnull
    @Default("Status")
    @Info("Status label")
    String getStatus();

    @Nonnull
    @Default("Alerts")
    @Info("Alerts label")
    String getAlerts();

    @Nonnull
    @Default("Action")
    @Info("Action label")
    String getAction();

    @Nonnull
    @Default("Project Phases provide a high-level grouping of major project deliverables into stages to help Project Managers and Customers to better manage and monitor project progress towards completion. Phases can be made up of Milestones and Tasks, or may be made up of Tasks only.")
    @Info("Phase description line 1")
    String getPhaseDetails1();

    @Nonnull
    @Default(" For detailed information and guidance on how to use Phases on your projects please visit our ")
    @Info("Phase description line 2")
    String getPhaseDetails2();

    @Nonnull
    @Default("Knowledge Base")
    @Info("Translated label for Knowledge Base")
    String getKnowledgeBaseLabel();

    @Nonnull
    @Default("Project Structure.")
    @Info("Translated label for Project Structure")
    String getProjectStructureLabel();

    @Nonnull
    @Default("Cancel")
    @Info("Cancel label")
    String getCancel();
}
