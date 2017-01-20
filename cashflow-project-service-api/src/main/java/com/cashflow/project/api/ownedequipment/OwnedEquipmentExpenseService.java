package com.cashflow.project.api.ownedequipment;

import com.cashflow.project.domain.project.ownedequipment.OwnedEquipmentExpense;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * 
 * @since 12 Dec, 2016, 6:36:24 PM
 */
public interface OwnedEquipmentExpenseService {

    void save(@Nonnull final OwnedEquipmentExpense ownedEquipment);

    @Nullable
    OwnedEquipmentExpense getExpenseReport(@Nonnull final String ownedEquipmentIdOrUUID);
}
