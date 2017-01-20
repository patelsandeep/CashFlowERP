package com.cashflow.project.frontend.controller.approval;

/**
 *
 * 
 * @since Dec 1, 2016, 7:04:21 PM
 */
public enum ApprovalPermission {
    TIME_EXPENSES("Timesheets Expense Reports Approver"),
    EQUIPMENT("Equipment Expenses Approver"),
    MATERIALS("Materials Expenses Approver"),
    SUB_CONTRACTORS("Sub-Contractor Expenses Approver"),
    OVERHEADS("Overheads Other Expenses Approver"),
    CHANGE_ORDERS("Change Order Approver"),
    PHASES("Phase Approver"),
    MILESTONES("Milestone Approver"),
    TASKS("Task Approver"),
    ALL("Project Manager");

    private final String description;

    private ApprovalPermission(final String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

}
