package com.cashflow.project.api.equipmentinformation;

import javax.annotation.Nonnull;

/**
 *
 * 
 * @since Dec 20, 2016, 12:19:09 PM
 */
public interface EquipmentExpenseInformationService {

    @Nonnull
    EquipmentExpenseInformationResult getEquipmentExpenseInformations(
            @Nonnull final EquipmentExpenseInformationRequest informationRequest);

}
