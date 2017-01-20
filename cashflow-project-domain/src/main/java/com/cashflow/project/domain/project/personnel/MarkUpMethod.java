package com.cashflow.project.domain.project.personnel;

/**
 *
 * 
 * @since Nov 25, 2016, 12:45:51 PM
 */
public enum MarkUpMethod {

    PERCENTAGE("% Mark-up"),
    ABSOLUTE("Absolute Mark-up");

    private final String description;

    private MarkUpMethod(final String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

}
