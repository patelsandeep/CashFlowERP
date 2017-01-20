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
 * @since 29 Nov, 2016, 11:08:02 AM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface ProjectTeamMemberListTranslationService {

    @Nonnull
    @Default("People")
    @Info("Transleted Header of People")
    String getPeopleHeader();

    @Nonnull
    @Default("People working on a project have two types of user roles. They have their "
            + "regular Platform User Roles e.g. Accountant User Role or Sales Rep User Role;"
            + " and they also have project specific user roles known as Project User Roles,"
            + " e.g. Project Manager or Project Consultant. While an People has only one Platform User Role,"
            + " he/she can have multiple Project User Roles.")
    @Info("Transleted Message for People Details")
    String getPeopleDetail1();

    @Nonnull
    @Default("Each Project User Role has a specific Cost Rate and Billing Rate."
            + " The Cost Rate can be a fully burdened labor cost rate retrieved automatically "
            + "from your payroll module, or it can be entered manually. The Billing Rate can be "
            + "a simple percentage mark-up on the Cost Rate or it can be an absolute amount that"
            + " you can enter manually. For detailed information and guidance on how to utilize"
            + " and manage People on your project, please visit our")
    @Info("Transleted Message for People Details")
    String getPeopleDetail2();

    @Nonnull
    @Default("Knowledge Base")
    @Info("Transleted Message of Knowledge Base")
    String getKnowledgeBase();

    @Nonnull
    @Default("and view")
    @Info("Transleted Message of people detail")
    String getPeopleDetail3();

    @Nonnull
    @Default("Managing People on a Project.")
    @Info("Transleted Message of people detail")
    String getPeopleDetail4();

    @Nonnull
    @Default("Search")
    @Info("Transleted Message of Search")
    String getSearch();

    @Nonnull
    @Default("Filter by")
    @Info("Transleted Message of Filter by")
    String getFilterBy();

    @Nonnull
    @Default("Add New Team Member")
    @Info("Transleted Message of Add New Team Member")
    String getAddNewTeamMember();

    @Nonnull
    @Default("Project ID")
    @Info("Transleted Message of Project ID")
    String getProjectId();

    @Nonnull
    @Default("Phase ID")
    @Info("Transleted Message of Phase ID")
    String getPhaseId();

    @Nonnull
    @Default("Milestone ID")
    @Info("Transleted Message of Milestone ID")
    String getMilestoneId();

    @Nonnull
    @Default("Task ID")
    @Info("Transleted Message of Task ID")
    String getTaskId();

    @Nonnull
    @Default("Team Member")
    @Info("Transleted Message of Team Member")
    String getTeamMember();

    @Nonnull
    @Default("Member ID")
    @Info("Transleted Message of Member ID")
    String getMemberId();

    @Nonnull
    @Default("Platform Role")
    @Info("Transleted Message of Platform Role")
    String getPlatformRole();

    @Nonnull
    @Default("Department")
    @Info("Transleted Message of Department")
    String getDepartment();

    @Nonnull
    @Default("Project Role")
    @Info("Transleted Message of Project Role")
    String getProjectRole();

    @Nonnull
    @Default("Cost Source")
    @Info("Transleted Message of Cost Source")
    String getCostSource();

    @Nonnull
    @Default("Cost Rate")
    @Info("Transleted Message of Cost Rate")
    String getCostRate();

    @Nonnull
    @Default("Mark-Up")
    @Info("Transleted Message of Mark-Up")
    String getMarkUp();

    @Nonnull
    @Default("Billing Rate")
    @Info("Transleted Message of Billing Rate")
    String getBillingRate();

    @Nonnull
    @Default("Action")
    @Info("Transleted Message of Action")
    String getAction();

    @Nonnull
    @Default("Cancel")
    @Info("Transleted Message of Cancel")
    String getCancel();

    @Nonnull
    @Default("Next")
    @Info("Transleted Message of Next")
    String getNext();

    @Nonnull
    @Default("Previous")
    @Info("Transleted Message of Previous")
    String getPrevious();

}
