package com.cashflow.project.translation.milestone;

import com.cashflow.core.annotations.Config;
import com.cashflow.core.annotations.ConfigProject;
import com.cashflow.core.annotations.ConfigRoot;
import com.cashflow.core.annotations.Default;
import com.cashflow.core.annotations.Info;
import javax.annotation.Nonnull;

/**
 *
 * 
 * @since Nov 22, 2016, 2:19:28 PM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface MileStoneTranslationService {

    @Nonnull
    @Default("Milestones")
    @Info("Translated Header Label for Milestone")
    String getHeader();

    @Nonnull
    @Default("Project Milestones are specific deliverables along the project timeline. Milestones can be included at the Project Level, or at the Phase Level. Each Milestone can be made up of several Tasks or a single Task required to deliver the Milestone.")
    @Info("Milestones description line 1")
    String getMilestoneDetails1();

    @Nonnull
    @Default(" For detailed information and guidance on how to use Milestones on your projects please visit our ")
    @Info("Milestones description line 2")
    String getMilestoneDetails2();

    @Nonnull
    @Default("Knowledge Base")
    @Info("Translated label for Knowledge Base")
    String getKnowledgeBaseLabel();

    @Nonnull
    @Default("Project Milestones.")
    @Info("Translated label for Project Milestones")
    String getProjectMilestonesLabel();

    @Nonnull
    @Default("Add New Milestone")
    @Info("Translated Label for Add new Milestone")
    String getAddNewMilestone();

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
    @Default("Cancel")
    @Info("Cancel label")
    String getCancel();

    @Nonnull
    @Default("View task")
    @Info("View task label")
    String getViewTaskLabel();

    @Nonnull
    @Default("Edit task")
    @Info("Edit task label")
    String getEditTaskLabel();

}
