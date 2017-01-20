package com.cashflow.project.frontend.controller.material;

import com.anosym.common.Amount;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * 
 */
@Getter
@Setter
public class MaterialExpenseModel {

    @Nullable
    private String projectId;

    @Nullable
    private String phaseId;

    @Nullable
    private String milestoneId;

    @Nullable
    private String taskId;

    @Nullable
    private String materialName;

    @Nullable
    private String materialId;

    @Nullable
    private String inventory;

    @Nullable
    private Amount costRate;

    @Nullable
    private Amount billRate;

    @Nullable
    private int units;

    @Nullable
    private Amount amount;
}
