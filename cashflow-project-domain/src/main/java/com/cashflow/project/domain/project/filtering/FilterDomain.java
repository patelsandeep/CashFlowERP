package com.cashflow.project.domain.project.filtering;

import com.cashflow.project.domain.project.Project;

/**
 * Currently, there are two context to filter domains, which can be markedly differently on how they are structured.
 * Database representation and indexing representation differ in how they represent the same kind of information. e.g.
 * {@link  #PROJECT} domain points to the database {@link Project} domain model, while {@link #PROJECT_INFORMATION}
 * points to {@link ProjectInformation} model.
 *
 * 
 * @since Nov 12, 2016, 10:10:33 AM
 */
public enum FilterDomain {

    PROJECT,
    PROJECT_INFORMATION,
    PHASE,
    PHASE_INFORMATION,
    MILESTONE,
    MILESTONE_INFORMATION,
    TASK,
    TASK_INFORMATION,
    EXPENSE_REPORT_INFORMATION,
    EQUIPMENT_EXPENSE_INFORMATION,
    SUB_CONTRACTOR_EXPENSE_INFORMATION;
}
