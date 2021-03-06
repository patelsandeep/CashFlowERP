package com.cashflow.project.domain.project.revenue;

import com.cashflow.entitydomains.AbstractBusinessAccountDomain;
import com.cashflow.project.domain.project.Project;
import com.anosym.common.Amount;
import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * 
 * @since Nov 25, 2016, 12:31:50 PM
 */
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class ProjectRevenue extends AbstractBusinessAccountDomain {

    private static final long serialVersionUID = -3234259657301038813L;

    @Nonnull
    @ManyToOne
    private Project project;

    @Nonnull
    @Enumerated(EnumType.STRING)
    private RevenueCategory revenueCategory;

    @Setter
    @Nonnull
    private Amount costValue;
}
