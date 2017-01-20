package com.cashflow.project.domain.service.equipmentinformation;

import com.cashflow.fixedasset.api.FixedAssetService;
import com.cashflow.fixedasset.domain.FixedAsset;
import com.cashflow.project.api.equipmentinformation.EquipmentExpenseInformation;
import com.cashflow.project.api.equipmentinformation.EquipmentExpenseInformation.EquipmentExpenseInformationBuilder;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.milestone.ProjectMilestone;
import com.cashflow.project.domain.project.ownedequipment.EquipmentType;
import com.cashflow.project.domain.project.ownedequipment.OwnedEquipmentDetail;
import com.cashflow.project.domain.project.ownedequipment.OwnedEquipmentExpense;
import com.cashflow.project.domain.project.personnel.MarkUpMethod;
import com.cashflow.project.domain.project.phase.ProjectPhase;
import com.cashflow.project.domain.project.rentedequipment.RentedEquipmentExpense;
import com.cashflow.project.domain.project.task.ProjectTask;
import com.anosym.common.Amount;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 *
 * 
 * @since Dec 21, 2016, 10:26:08 AM
 */
@Dependent
public class EquipmentInfoBuilder implements Serializable {

    private static final long serialVersionUID = 933213102091402744L;

    @Inject
    private FixedAssetService fixedAssetService;

    private Map<String, ProjectLevel<?>> levels;

    @Nonnull
    public EquipmentExpenseInformation prepareRentedInfo(@Nonnull final RentedEquipmentExpense rentedEquipment) {
        EquipmentExpenseInformationBuilder info = EquipmentExpenseInformation.builder();

        info.equipmentUuid(rentedEquipment.getUuid());
        info.equipmentType(EquipmentType.RENTED);

        info.phaseId("All");
        info.milestoneId("All");
        info.taskId("All");

        levels = new HashMap<>();
        levels = setProjectLevels(rentedEquipment.getProjectLevel());

        levels
                .entrySet()
                .forEach(level -> {
                    if (level.getValue() instanceof ProjectPhase) {
                        info.phaseId(((ProjectPhase) level.getValue()).getId());
                    } else if (level.getValue() instanceof ProjectMilestone) {
                        info.milestoneId(((ProjectMilestone) level.getValue()).getId());
                    } else if (level.getValue() instanceof ProjectTask) {
                        info.taskId(((ProjectTask) level.getValue()).getId());
                    }
                });

        info.equipmentName(rentedEquipment.getEquipmentName());
        info.equipmentID(rentedEquipment.getEquipmentId());

        final Amount billRateAmount = calculateBillableRate(rentedEquipment);

        info.costRate(rentedEquipment.getCostRate());
        info.units(rentedEquipment.getBillableQty());
        info.billRate(billRateAmount);
        info.totalAmount(billRateAmount.multiply(rentedEquipment.getBillableQty()));

        return info.build();
    }

    @Nonnull
    public List<EquipmentExpenseInformation> prepareOwnedInfos(@Nonnull final OwnedEquipmentExpense ownedEquipment) {
        return ownedEquipment
                .getOwnedEquipmentDetails()
                .stream()
                .map((detail) -> prepareOwnedInfoDetail(ownedEquipment, detail))
                .collect(Collectors.toList());
    }

    private EquipmentExpenseInformation prepareOwnedInfoDetail(@Nonnull final OwnedEquipmentExpense ownedEquipment,
                                                               @Nonnull final OwnedEquipmentDetail ownedEquipmentDetail) {
        EquipmentExpenseInformationBuilder info = EquipmentExpenseInformation.builder();

        info.equipmentUuid(ownedEquipment.getUuid());
        info.equipmentType(EquipmentType.OWNED);

        info.phaseId("All");
        info.milestoneId("All");
        info.taskId("All");

        levels = new HashMap<>();
        levels = setProjectLevels(ownedEquipment.getProjectLevel());

        levels
                .entrySet()
                .forEach(level -> {
                    if (level.getValue() instanceof ProjectPhase) {
                        info.phaseId(((ProjectPhase) level.getValue()).getId());
                    } else if (level.getValue() instanceof ProjectMilestone) {
                        info.milestoneId(((ProjectMilestone) level.getValue()).getId());
                    } else if (level.getValue() instanceof ProjectTask) {
                        info.taskId(((ProjectTask) level.getValue()).getId());
                    }
                });

        // there is no service to find fixedassets by array of UUIDs
        final FixedAsset asset = fixedAssetService
                .findByUUIDOrAssetIDOrSerialNumber(ownedEquipmentDetail.getFixedAssetUUID());
        if (asset != null) {
            info.equipmentName(asset.getAssetName());
            info.equipmentID(asset.getAssetItemID());
            info.costRate(asset.getCostRateAmount());
        }

        info.units(ownedEquipmentDetail.getUnit());
        info.billRate(ownedEquipmentDetail.getBillRate());
        info.totalAmount(ownedEquipmentDetail.getBillRate().multiply(ownedEquipmentDetail.getUnit()));

        return info.build();
    }

    @Nonnull
    public Amount calculateBillableRate(@Nonnull final RentedEquipmentExpense rentedEquipment) {
        if (rentedEquipment.getMarkUpMethod() == MarkUpMethod.ABSOLUTE) {
            return rentedEquipment.getCostRate()
                    .add(new Amount(rentedEquipment.getCostRate().getCurrency(), rentedEquipment.getMarkUp()));
        } else if (rentedEquipment.getMarkUpMethod() == MarkUpMethod.PERCENTAGE) {
            return rentedEquipment.getCostRate()
                    .add(rentedEquipment.getCostRate()
                            .multiply(rentedEquipment.getMarkUp())
                            .multiply(0.01)).scale(2);
        }
        return new Amount(rentedEquipment.getCostRate().getCurrency(), 0);
    }

    @Nonnull
    private Map<String, ProjectLevel<?>> setProjectLevels(@Nullable final ProjectLevel<?> level) {
        if (level == null) {
            return levels;
        }
        levels.put(level.getUuid(), level);
        setProjectLevels(level.getParentLevel());
        return levels;
    }

}
