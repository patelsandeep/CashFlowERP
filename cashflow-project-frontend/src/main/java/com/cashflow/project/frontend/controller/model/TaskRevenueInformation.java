package com.cashflow.project.frontend.controller.model;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * 
 * @since 30 Nov, 2016, 10:47:13 AM
 */
@Getter
@Setter
public class TaskRevenueInformation implements Serializable {

    private static final long serialVersionUID = -3254533780182594122L;

    @Nullable
    private BigDecimal budgetLabourRevenue = BigDecimal.ZERO;

    @Nullable
    private BigDecimal budgetEquipmentRevenue = BigDecimal.ZERO;

    @Nullable
    private BigDecimal budgetMaterialRevenue = BigDecimal.ZERO;

    @Nullable
    private BigDecimal budgetSubContractorRevenue = BigDecimal.ZERO;

    @Nonnull
    private BigDecimal revenueAllocation = BigDecimal.ZERO;

    @Nonnull
    private BigDecimal otherIndirectRevenue = BigDecimal.ZERO;

    @Nullable
    private BigDecimal taskBudgetRevenue = BigDecimal.ZERO;

    private boolean includeBudgetRevenueDetails;

}
