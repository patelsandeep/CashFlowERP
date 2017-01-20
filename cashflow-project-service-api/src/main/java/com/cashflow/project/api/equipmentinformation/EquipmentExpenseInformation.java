package com.cashflow.project.api.equipmentinformation;

import com.cashflow.project.domain.project.ownedequipment.EquipmentType;
import com.anosym.common.Amount;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
 * 
 * @since Dec 20, 2016, 11:37:56 AM
 */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EquipmentExpenseInformation {

    @Nonnull
    private EquipmentType equipmentType;

    @Nonnull
    private String equipmentUuid;

    @Nonnull
    private String phaseId;

    @Nonnull
    private String milestoneId;

    @Nonnull
    private String taskId;

    @Nonnull
    private String equipmentName;

    @Nonnull
    private String equipmentID;

    @Nullable
    private Amount costRate;

    @Nonnull
    private Amount billRate;

    @Nonnull
    private Integer units;

    @Nonnull
    private Amount totalAmount;

}
