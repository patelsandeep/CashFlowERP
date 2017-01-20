package com.cashflow.project.frontend.controller.model;

import com.anosym.common.Amount;
import com.anosym.common.Currency;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * 
 * @since 14 Dec, 2016, 10:32:35 AM
 */
@Getter
@Setter
public class OwnedEquipmentExpenseModel implements Serializable {

    private static final long serialVersionUID = 6161281558369553411L;

    @Nonnull
    private String OwnedEquipmentId;

    @Nullable
    private String OwnedEquipmentUUID;

    @Nonnull
    private String customer;

    @Nonnull
    private String project;

    @Nullable
    private String phase;

    @Nullable
    private String milestone;

    @Nullable
    private String task;

    @Nullable
    private String phaseName;

    @Nullable
    private String milestoneName;

    @Nullable
    private String taskName;

    @Nullable
    private String taskId;

    @Nullable
    private Calendar weekStartDate;

    @Nullable
    private Calendar weekEndingDate;

    @Nullable
    private Currency currency;

    @Nullable
    private Amount costRateTotal;

    @Nullable
    private Amount billRateTotal;

    private int totalUnits = 0;

    @Nullable
    private Amount totalAmount;

    @Nonnull
    private List<EquipmentDetailModel> equipmentDetailModels = new ArrayList<>();

}
