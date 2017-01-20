package com.cashflow.project.domain.project.ownedequipment;

/**
 *
 * 
 * @since 14 Dec, 2016, 10:15:41 AM
 */
public enum EquipmentType {

    OWNED("Owned"),
    RENTED("Rented");

    private final String desc;

    private EquipmentType(final String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return desc;
    }
}
