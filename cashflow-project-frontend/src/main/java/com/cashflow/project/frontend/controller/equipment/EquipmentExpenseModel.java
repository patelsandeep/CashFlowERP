package com.cashflow.project.frontend.controller.equipment;

import com.cashflow.project.domain.project.ownedequipment.EquipmentType;
import java.io.Serializable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * 
 * @since Dec 24, 2016, 11:52:16 AM
 */
@Getter
@Setter
public class EquipmentExpenseModel implements Serializable {

    private static final long serialVersionUID = 4243918721871393261L;

    @Nonnull
    private String equipmentUUID;

    @Nullable
    private EquipmentType equipmentType;

}
