package com.cashflow.project.domain.project.personnel;

import com.cashflow.entitydomains.AbstractDomain;
import com.anosym.common.Amount;
import java.math.BigDecimal;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static javax.persistence.EnumType.STRING;

/**
 *
 * 
 * @since Nov 25, 2016, 12:31:30 PM
 */
@Entity
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectRole extends AbstractDomain {

    private static final long serialVersionUID = -1245355994816458846L;

    @Nonnull
    @Enumerated(STRING)
    private CostRateSource costRateSource;

    @Nonnull
    private String accessScopeUUID;

    @Nonnull
    private Amount costRate;

    @Nonnull
    private Amount billRate;

    @Nullable
    private MarkUpMethod markUpMethod;

    @Nullable
    private BigDecimal markUp;

}
