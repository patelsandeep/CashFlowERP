package com.cashflow.project.domain.facade.ownedequipment;

import com.cashflow.project.domain.facade.ProjectAbstractFacade;
import com.cashflow.project.domain.project.ownedequipment.OwnedEquipmentExpense;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 *
 * 
 * @since 12 Dec, 2016, 6:23:50 PM
 */
@Stateless
public class OwnedEquipmentExpenseFacade extends ProjectAbstractFacade<OwnedEquipmentExpense> {

    public OwnedEquipmentExpenseFacade() {
        super(OwnedEquipmentExpense.class);
    }

    @Nullable
    public OwnedEquipmentExpense findByOwnedEquipmentIdOrUUID(@Nonnull final String ownedEquipmentsIdOrUUID,
                                                              @Nonnull final String businessAccountId) {
        checkNotNull(ownedEquipmentsIdOrUUID, "The ownedEquipmentsIdOrUUID must not be null");

        final List<OwnedEquipmentExpense> ownedEquipments = getEntityManager()
                .createNamedQuery("OwnedEquipmentExpense.findByOwnedEquipmentIdOrUUID", OwnedEquipmentExpense.class)
                .setParameter("id", ownedEquipmentsIdOrUUID)
                .setParameter("businessAccountId", businessAccountId)
                .getResultList();
        checkState(ownedEquipments.size() <= 1,
                   "Invalid lookup.OwnedEquipmentExpense ID/uuid{%s} returns multiple results",
                   ownedEquipmentsIdOrUUID);

        return ownedEquipments
                .stream()
                .findAny()
                .orElse(null);
    }
}
