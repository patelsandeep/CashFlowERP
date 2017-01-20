package com.cashflow.project.domain.project.timesheet;

/**
 *
 * 
 * @since Dec 5, 2016, 3:52:02 PM
 */
public enum PTOCategory {

    STATE_HOLIDAYS("State Holidays"),
    VACATION_TIME("Vacation Time"),
    PERSONAL_TIME("Personal Time"),
    SICK_TIME("Sick Time"),
    PARENTAL_LEAVE("Parental Leave"),
    DISABILITY_LEAVE("Disability Leave"),
    BEREAVEMENT_LEAVE("Bereavement Leave");

    private final String description;

    private PTOCategory(final String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

}
