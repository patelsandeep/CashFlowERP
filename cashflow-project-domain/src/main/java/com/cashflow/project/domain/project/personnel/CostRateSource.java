package com.cashflow.project.domain.project.personnel;

/**
 *
 * 
 * @since Nov 25, 2016, 11:52:07 AM
 */
public enum CostRateSource {

    MANUAL("Manual"),
    PAYROLL_FEED("Payroll Feed");

    private final String description;

    private CostRateSource(final String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

}
