package com.cashflow.project.domain.project.personnel;

/**
 *
 * 
 * @since Nov 26, 2016, 4:52:17 PM
 */
public enum OrganizationLevel {

    COMPANY_LEVEL("Company Level"),
    BRACH_LEVEL("Branch Level"),
    BUSINESS_UNIT_LEVEL("Business Unit Level");

    private final String desc;

    private OrganizationLevel(final String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return desc;
    }

}
