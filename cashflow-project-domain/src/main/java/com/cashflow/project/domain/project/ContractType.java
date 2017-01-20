package com.cashflow.project.domain.project;

import javax.annotation.Nonnull;

/**
 *
 * 
 * @since Nov 10, 2016, 10:48:21 PM
 */
public enum ContractType {

    FIXED_PRICE("Fixed Price", RevenueRecordingMethod.POC),
    COST_PLUS("Cost Plus", RevenueRecordingMethod.APPROVED_COSTS),
    TIME_AND_MATERIALS("Time & Materials", RevenueRecordingMethod.APPROVED_COSTS);

    private final String description;

    private final RevenueRecordingMethod revenueRecordingMethod;

    private ContractType(final String description, final RevenueRecordingMethod revenueRecordingMethod) {
        this.description = description;
        this.revenueRecordingMethod = revenueRecordingMethod;
    }

    @Nonnull
    public RevenueRecordingMethod getRevenueRecordingMethod() {
        return revenueRecordingMethod;
    }

    @Override
    public String toString() {
        return description;
    }

}
