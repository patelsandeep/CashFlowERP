package com.cashflow.project.frontend.controller.model;

import com.cashflow.project.domain.project.personnel.CostRateSource;
import com.cashflow.project.domain.project.personnel.MarkUpMethod;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * 
 * @since Nov 26, 2016, 11:57:15 AM
 */
@Getter
@Setter
public class ProjectRoleModel implements Serializable {

    private static final long serialVersionUID = -8691025194944358411L;

    @Nonnull
    private String projectRoleUUID;

    @Nonnull
    private CostRateSource costRateSource = CostRateSource.MANUAL;

    @Nonnull
    private BigDecimal costRate = BigDecimal.ZERO;

    @Nonnull
    private BigDecimal billRate = BigDecimal.ZERO;

    @Nonnull
    private MarkUpMethod markUpMethod = MarkUpMethod.PERCENTAGE;

    @Nonnull
    private BigDecimal markUpValue = BigDecimal.ZERO;

}
