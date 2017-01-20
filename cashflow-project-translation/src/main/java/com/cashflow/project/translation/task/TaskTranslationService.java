package com.cashflow.project.translation.task;

import com.cashflow.core.annotations.Config;
import com.cashflow.core.annotations.ConfigProject;
import com.cashflow.core.annotations.ConfigRoot;
import com.cashflow.core.annotations.Default;
import com.cashflow.core.annotations.Info;
import javax.annotation.Nonnull;

/**
 *
 * 
 * @since Nov 23, 2016, 2:27:02 PM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface TaskTranslationService {

    @Nonnull
    @Default("Tasks")
    @Info("Project Tasks Tab header label")
    String getHeader();

    @Nonnull
    @Default("Add New Task")
    @Info("Add New Task label")
    String getAddNewTask();

    @Nonnull
    @Default("Count")
    @Info("Count label")
    String getCount();

    @Nonnull
    @Default("Task ID")
    @Info("Task ID label")
    String getTaskID();

    @Nonnull
    @Default("Task Name")
    @Info("Task Name label")
    String getTaskName();

    @Nonnull
    @Default("Start Date")
    @Info("Start Date label")
    String getStartDate();

    @Nonnull
    @Default("End Date")
    @Info("End Date label")
    String getEndDate();

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
    @Default("A Project Task is the lowest group of work activities required to accomplish a set project deliverable. Project budgets, costs and revenues are all captured at the Task Level. Tasks are mandatory for all projects. ")
    @Info("Task description line 1")
    String getTaskDetails1();

    @Nonnull
    @Default(" For detailed information and guidance on how to use Tasks on your projects please visit our ")
    @Info("Phase description line 2")
    String getTaskDetails2();

    @Nonnull
    @Default("Knowledge Base")
    @Info("Translated label for Knowledge Base")
    String getKnowledgeBaseLabel();

    @Nonnull
    @Default("Project Tasks.")
    @Info("Translated label for Project Tasks")
    String getProjectTasksLabel();

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
