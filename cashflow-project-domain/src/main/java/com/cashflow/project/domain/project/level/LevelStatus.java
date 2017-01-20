package com.cashflow.project.domain.project.level;

/**
 *
 * 
 * @since Nov 11, 2016, 7:24:45 PM
 */
public enum LevelStatus {

    PLANNED("Planned"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String description;

    private LevelStatus(final String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

}
