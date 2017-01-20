package com.cashflow.project.frontend.controller.timesheet;

/**
 *
 * 
 * @since Jan 18, 2017, 4:00:25 PM
 */
public enum TimesheetViewMode {
    DAY("Day"),
    WEEK("Week");

    private final String desc;

    private TimesheetViewMode(final String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return desc;
    }
}
