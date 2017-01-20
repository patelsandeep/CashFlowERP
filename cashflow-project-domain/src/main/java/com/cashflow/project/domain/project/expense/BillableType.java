package com.cashflow.project.domain.project.expense;

/**
 *
 * 
 * @since 5 Dec, 2016, 3:51:51 PM
 */
public enum BillableType {

    YES("Yes"),
    NO("No");

    private final String desc;

    private BillableType(final String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return desc;
    }
}
