package com.cashflow.project.domain.project.expense;

/**
 *
 * 
 * @since 5 Dec, 2016, 2:36:17 PM
 */
public enum ExpenseType {

    TRAVEL("Travel"),
    MEALS_AND_ENTERTAINMENT("Meals and Entertainment"),
    LODGING("Lodging"),
    OFFICE_SUPPLIES("Office Supplies"),
    GENERAL_SUPPLIES("General Supplies");

    private final String desc;

    private ExpenseType(final String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return desc;
    }

}
