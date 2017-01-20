package com.cashflow.project.domain.project.level;

/**
 *
 * 
 * @since Nov 11, 2016, 7:23:42 PM
 */
public enum PerformanceStatus {

    ON_TIME("On Time"),
    RISK("Risk"),
    ALERT("Alert");

    private final String description;

    private PerformanceStatus(final String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

}
