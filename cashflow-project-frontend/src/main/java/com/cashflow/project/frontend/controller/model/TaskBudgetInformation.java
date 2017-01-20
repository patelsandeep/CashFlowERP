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
 * @since 30 Nov, 2016, 10:40:47 AM
 */
@Getter
@Setter
public class TaskBudgetInformation implements Serializable {

    private static final long serialVersionUID = 3554260057444067537L;

    @Nonnull
    private BigDecimal taskBudgetCost = BigDecimal.ZERO;

    @Nullable
    private BigDecimal budgetLabourCost = BigDecimal.ZERO;

    @Nullable
    private BigDecimal budgetEquipmentCost = BigDecimal.ZERO;

    @Nullable
    private BigDecimal budgetMaterialCost = BigDecimal.ZERO;

    @Nullable
    private BigDecimal budgetSubContractorCost = BigDecimal.ZERO;

    @Nullable
    private BigDecimal taskBudgetGrossProfit = BigDecimal.ZERO;

    @Nullable
    private BigDecimal costAllocation = BigDecimal.ZERO;

    @Nullable
    private BigDecimal otherIndirectCost = BigDecimal.ZERO;

}
