package com.cashflow.project.domain.project.rentedequipment;

import javax.annotation.Nonnull;

/**
 *
 * 
 * @since 16 Dec, 2016, 6:37:47 PM
 */
public enum BillUnit {

    PER_HOUR("Per Hour"),
    PER_MILE("Per Mile"),
    PER_KM("Per KM"),
    PER_DAY("Per Day"),
    PER_WEEK("Per Week"),
    PER_MONTH("Per Month");

    private final String desc;

    BillUnit(@Nonnull final String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return desc;
    }

}
