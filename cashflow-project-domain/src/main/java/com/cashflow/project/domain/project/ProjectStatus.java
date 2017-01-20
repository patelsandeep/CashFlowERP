package com.cashflow.project.domain.project;

/**
 *
 * 
 */
public enum ProjectStatus {

    NEW("New"),
    PENDING("Pending"),
    APPROVED("Approved"),
    ON_TIME("On-Time"),
    ALERT("Alert"),
    HOLD("Hold"),
    COMPLETE("Complete");

    private final String value;

    private ProjectStatus(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}
