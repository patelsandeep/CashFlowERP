package com.cashflow.project.frontend.controller.expenses;

import java.io.Serializable;
import javax.enterprise.inject.Model;

/**
 *
 * 
 * @since 14 Dec, 2016, 3:07:39 PM
 */
@Model
public class AccessScopeID implements Serializable {

    private static final long serialVersionUID = -4890499983643422838L;

    public static final String PROJECT_ADMIN = "project.admin";
    public static final String PROJECT_MANAGER = "project.manager";
    public static final String PROJECT_MILESTONE_MANAGER = "project.milestone.manager";
    public static final String PROJECT_PHASE_MANAGER = "project.phase.manager";
    public static final String PROJECT_TASK_MANAGER = "project.task.manager";
    public static final String PROJECTS_ADMINISTRATOR = "projects.administrator";
    public static final String PROJECTS_CHANGEORDER_APPROVE = "projects.changeOrder.approve";
    public static final String PROJECTS_EQUIPMENT_APPROVE = "projects.equipment.approve";
    public static final String PROJECTS_MANAGER = "projects.manager";
    public static final String PROJECTS_MATERIAL_APPROVE = "projects.material.approve";
    public static final String PROJECTS_MILESTONE_MANAGER = "projects.milestone.manager";
    public static final String PROJECTS_MILESTONES_APPROVE = "projects.milestones.approve";
    public static final String PROJECTS_MILESTONES_MANAGER = "projects.milestones.manager";
    public static final String PROJECTS_OVERHEADSANDOTHEREXPENSES_APPROVE = "projects.overheadsAndOtherExpenses.approve";
    public static final String PROJECTS_PHASE_MANAGER = "projects.phase.manager";
    public static final String PROJECTS_PHASES_APPROVE = "projects.phases.approve";
    public static final String PROJECTS_PHASES_MANAGER = "projects.phases.manager";
    public static final String PROJECTS_SUBCONTRACTOREXPENSES_APPROVE = "projects.subcontractorExpenses.approve";
    public static final String PROJECTS_TASK_MANAGER = "projects.task.manager";
    public static final String PROJECTS_TASKS_APPROVE = "projects.tasks.approve";
    public static final String PROJECTS_TASKS_MANAGER = "projects.tasks.manager";
    public static final String PROJECTS_TASKS_VIEWINFO = "projects.tasks.viewInfo";
    public static final String PROJECTS_TIMESHEETSANDEXPENSES_APPROVE = "projects.timesheetsAndExpenses.approve";
    public static final String PROJECTS_TIMESHEETSANDEXPENSES_SUBMIT = "projects.timesheetsAndExpenses.submit";
    public static final String PROJECTS_VIEWINFO = "projects.viewInfo";

}
