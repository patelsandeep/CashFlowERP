package com.cashflow.project.domain.project.level;

/**
 *
 * 
 * @since Nov 10, 2016, 11:46:01 PM
 */
public enum ProjectLevelCategory {

    PROJECT("Project Level"),
    PHASE("Phase Level"),
    MILESTONE("Milestone Level"),
    TASK("Task Level");

    private final String description;

    private ProjectLevelCategory(final String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

}
