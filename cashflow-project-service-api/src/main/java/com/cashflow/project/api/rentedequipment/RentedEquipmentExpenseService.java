package com.cashflow.project.api.rentedequipment;

import com.cashflow.project.domain.project.rentedequipment.RentedEquipmentExpense;
import com.cashflow.project.domain.project.rentedequipment.RentedEquipment;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * 
 * @since 16 Dec, 2016, 7:20:17 PM
 */
public interface RentedEquipmentExpenseService {

    void save(@Nonnull final RentedEquipmentExpense rentedEquipment);

    @Nullable
    RentedEquipmentExpense getRentedEquipmentExpense(@Nonnull final String rentedEquipmentIdOrUUID);

    @Nonnull
    List<RentedEquipmentExpense> getRentedEquipmentExpenses(@Nonnull final RentedEquipment rentedEquipmentContext);
}
