package com.cashflow.project.domain.facade.rentedequipment;

import com.cashflow.project.domain.facade.ProjectAbstractFacade;
import com.cashflow.project.domain.project.rentedequipment.RentedEquipmentExpense;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 *
 * 
 * @since 16 Dec, 2016, 6:53:07 PM
 */
@Stateless
public class RentedEquipmentExpenseFacade extends ProjectAbstractFacade<RentedEquipmentExpense> {

    public RentedEquipmentExpenseFacade() {
        super(RentedEquipmentExpense.class);
    }

    @Nullable
    public RentedEquipmentExpense findByRentedEquipmentIdOrUUID(@Nonnull final String rentedEquipmentsIdOrUUID,
                                                                @Nonnull final String businessAccountId) {
        checkNotNull(rentedEquipmentsIdOrUUID, "The rentedEquipmentsIdOrUUID must not be null");

        final List<RentedEquipmentExpense> rentedEquipments = getEntityManager()
                .createNamedQuery("RentedEquipmentExpense.findByRentedEquipmentIdOrUUID", RentedEquipmentExpense.class)
                .setParameter("id", rentedEquipmentsIdOrUUID)
                .setParameter("businessAccountId", businessAccountId)
                .getResultList();
        checkState(rentedEquipments.size() <= 1,
                   "Invalid lookup.RentedEquipmentExpense ID/uuid{%s} returns multiple results",
                   rentedEquipmentsIdOrUUID);

        return rentedEquipments
                .stream()
                .findAny()
                .orElse(null);
    }
}
