package com.cashflow.project.domain.project;

/**
 *
 * 
 * @since Nov 10, 2016, 11:31:29 PM
 */
public enum RevenueRecordingMethod {

    POC("Percentage of Completion"),
    APPROVED_COSTS("Approved Costs");

    private final String description;

    private RevenueRecordingMethod(final String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

}
