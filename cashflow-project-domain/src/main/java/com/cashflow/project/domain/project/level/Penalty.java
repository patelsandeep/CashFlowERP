package com.cashflow.project.domain.project.level;

import com.cashflow.entitydomains.AbstractBusinessAccountDomain;
import com.anosym.common.Amount;
import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
 * 
 * @since Nov 11, 2016, 7:20:09 PM
 */
@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Penalty extends AbstractBusinessAccountDomain {

    private static final long serialVersionUID = -1218743831697458226L;

    @Nonnull
    @ManyToOne
    private ProjectLevel<?> projectLevel;

    @Nonnull
    private Amount cost;

    @Nonnull
    @Column(length = 1024)
    private String description;
}
