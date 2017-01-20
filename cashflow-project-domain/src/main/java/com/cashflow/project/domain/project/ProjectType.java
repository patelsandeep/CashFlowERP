package com.cashflow.project.domain.project;

/**
 *
 * 
 * @since Nov 10, 2016, 8:33:53 PM
 */
public enum ProjectType {

    CUSTOMER_PROJECT("Customer Project"),
    INTERNAL_PROJECT("Internal Project");

    private final String description;

    private ProjectType(final String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

}
