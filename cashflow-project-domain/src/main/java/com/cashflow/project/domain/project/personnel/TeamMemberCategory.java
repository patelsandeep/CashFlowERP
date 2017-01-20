package com.cashflow.project.domain.project.personnel;

/**
 *
 * 
 * @since Nov 26, 2016, 11:50:52 AM
 */
public enum TeamMemberCategory {

    EMPLOYEE("Employee"),
    SUB_CONTRACTOR("Sub-Contractor"),
    CONSULTANT("Consultant");

    private final String desc;

    private TeamMemberCategory(final String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return desc;
    }

    public static TeamMemberCategory valueOfEnum(final String value) {
        for (TeamMemberCategory cat : TeamMemberCategory.values()) {
            if (cat.toString().equals(value)) {
                return cat;
            }
        }
        return null;
    }

}
