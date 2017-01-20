package com.cashflow.project.domain.approval;

/**
 *
 * 
 * @since Nov 21, 2016, 4:13:49 PM
 */
public enum ApprovalType {

    TIME_EXPENSES("Time and Expenses"),
    EQUIPMENT("Equipment"),
    MATERIALS("Materials"),
    SUB_CONTRACTORS("Sub-Contractors"),
    OVERHEADS("Overheads"),
    CHANGE_ORDERS("Change Orders"),
    PPC("PPC"),
    INVOICES("Invoices"),
    PHASES("Phases"),
    MILESTONES("Milestones"),
    TASKS("Tasks"),
    ALL("All");

    private final String description;

    private ApprovalType(final String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

}
