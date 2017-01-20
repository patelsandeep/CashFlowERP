package com.cashflow.project.domain.project.asset;

import com.cashflow.entitydomains.AbstractBusinessAccountDomain;
import com.cashflow.project.domain.project.Project;
import java.math.BigDecimal;
import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * 
 * @since Nov 10, 2016, 8:22:34 PM
 */
@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectAsset extends AbstractBusinessAccountDomain {

    private static final long serialVersionUID = -1695023109458889242L;

    @Nonnull
    @ManyToOne
    private Project project;

    @Nonnull
    private String assetUUID;

    @Setter
    @Nonnull
    private BigDecimal billRate;
}
