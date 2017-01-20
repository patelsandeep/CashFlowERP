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
public interface ProjectListTranslationService {

    @Nonnull
    @Default("Projects Summary")
    @Info("Project listing header label")
    String getHeader();

    @Nonnull
    @Default("Create a New Project")
    @Info("create project label")
    String getCreateProject();

    @Nonnull
    @Default("Project Name")
    @Info("Project name label")
    String getProject();

    @Nonnull
    @Default("Project ID")
    @Info("Project ID label")
    String getProjectID();

    @Nonnull
    @Default("Project Manager")
    @Info("Project Manager label")
    String getProjectManager();

    @Nonnull
    @Default("Start Date")
    @Info("Start Date label")
    String getStartDate();

    @Nonnull
    @Default("End Date")
    @Info("End Date label")
    String getEndDate();

    @Nonnull
    @Default("Project Budgeted Cost")
    @Info("Project Budgeted Cost label")
    String getProjectBudgetedCost();

    @Nonnull
    @Default("Cost LTD")
    @Info("Cost LTD label")
    String getCostLTD();

    @Nonnull
    @Default("Cost Life To Date")
    @Info("Cost LTD full form label")
    String getCostLTDDesc();

    @Nonnull
    @Default("% Complete")
    @Info("% Complete label")
    String getPercentComplete();

    @Nonnull
    @Default("Revenue LTD")
    @Info("Revenue LTD label")
    String getRevenueLTD();

    @Nonnull
    @Default("Revenue Life To Date")
    @Info("Revenue LTD full form label")
    String getRevenueLTDDesc();

    @Nonnull
    @Default("Invoiced LTD")
    @Info("Invoiced LTD label")
    String getInvoicedLTD();

    @Nonnull
    @Default("Invoiced Life To Date")
    @Info("Invoiced LTD full form label")
    String getInvoicedLTDDesc();

    @Nonnull
    @Default("Project Budgeted Revenue")
    @Info("Project Budgeted Revenue label")
    String getProjectBudgetedRevenue();
    
    @Nonnull
    @Default("Customer")
    @Info("Customer label")
    String getCustomer();

    @Nonnull
    @Default("Status")
    @Info("Status label")
    String getStatus();

    @Nonnull
    @Default("Action")
    @Info("Action label")
    String getAction();

    @Nonnull
    @Default("Search")
    @Info("Search label")
    String getSearch();

    @Nonnull
    @Default("From")
    @Info("From label")
    String getFrom();

    @Nonnull
    @Default("To")
    @Info("To label")
    String getTo();
    
    @Nonnull
    @Default("View this project")
    @Info("View this project label")
    String getViewThisProject();
    
    @Nonnull
    @Default("Edit this project")
    @Info("Edit this project label")
    String getEditThisProject();

    @Nonnull
    @Default("Type at least 3 characters to search by Manager")
    @Info("Manager Placeholder label")
    String getManagerPlaceholderlabel();

    @Nonnull
    @Default("No manager found with specified Name")
    @Info("No manager found with specified Name message")
    String getNoManagerFound();

}
