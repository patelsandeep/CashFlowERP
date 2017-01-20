package com.cashflow.project.domain.project.budget;

import com.cashflow.entitydomains.AbstractBusinessAccountDomain;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.anosym.common.Amount;
import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * 
 * @since Nov 10, 2016, 8:08:11 PM
 */
@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NamedQueries({
    @NamedQuery(name = "Budget.findBudgets",
                query = "SELECT b FROM Budget b WHERE b.projectLevel.uuid IN :projectLevelUUIDs")
})
public class Budget extends AbstractBusinessAccountDomain {

    private static final long serialVersionUID = -5316524180173662127L;

    @Nonnull
    @OneToOne
    private ProjectLevel<?> projectLevel;

    @Setter
    @Nonnull
    private Amount budgetedCost;

    @Setter
    @Nonnull
    private Amount budgetedRevenue;

    @Setter
    @Nonnull
    private Amount budgetedGrossProfit;

}
