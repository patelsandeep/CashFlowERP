package com.cashflow.project.domain.project.expense;

/**
 *
 * 
 * @since 5 Dec, 2016, 10:38:11 AM
 */
public enum LabourExpenseType {

    TIMESHEET("Timesheet"),
    EXPENSE("Expense");

    private final String desc;

    private LabourExpenseType(final String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return desc;
    }
}
