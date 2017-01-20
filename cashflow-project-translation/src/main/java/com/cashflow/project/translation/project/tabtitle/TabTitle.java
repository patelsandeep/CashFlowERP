package com.cashflow.project.translation.project.tabtitle;

import javax.annotation.Nonnull;

/**
 *
 * 
 * @since Nov 24, 2016, 7:17:37 PM
 */
public enum TabTitle {

    GENERAL_INFO("General Info", "/projects/general-info-tab.xhtml", false),
    PHASES("Phases", "/phase/phase-tab.xhtml", true),
    MILESTONES("Milestones", "/milestone/milestone-tab.xhtml", true),
    TASKS("Tasks", "/task/task-tab.xhtml", true),
    PEOPLE("People", "/people/people-tab.xhtml", true),
    TIME_AND_EXPENSES("Time & Expenses", "/timeandexpense/time-and-expenses-tab.xhtml", true),
    EQUIPMENT("Equipment", "/equipment/equipment-tab.xhtml", true),
    MATERIAL("Material", "/material/material-tab.xhtml", true),
    SUB_CONTRACTOR("Sub Contractors", "/sub-contractors/sub-contractors-tab.xhtml", true),
    INVOICES("Invoices", "/invoices/invoices-tab.xhtml", true);

    private final String name;

    private final String template;

    private final boolean projectRequired;

    private TabTitle(final String name, final String template, final boolean projectRequired) {
        this.name = name;
        this.template = template;
        this.projectRequired = projectRequired;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public String getTemplate() {
        return template;
    }

    public boolean isProjectRequired() {
        return projectRequired;
    }

    @Override
    public String toString() {
        return name;
    }

}
