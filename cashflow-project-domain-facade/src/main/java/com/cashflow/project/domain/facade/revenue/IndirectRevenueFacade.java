package com.cashflow.project.domain.facade.revenue;

import com.cashflow.project.domain.facade.ProjectAbstractFacade;
import com.cashflow.project.domain.project.revenue.IndirectProjectRevenue;
import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.ejb.Stateless;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since 25 Nov, 2016, 12:32:48 PM
 */
@Stateless
public class IndirectRevenueFacade extends ProjectAbstractFacade<IndirectProjectRevenue> {

    public IndirectRevenueFacade() {
        super(IndirectProjectRevenue.class);
    }

    @Nonnull
    public List<IndirectProjectRevenue> getIndirectProjectRevenues(@Nonnull final List<String> projectLevelUUIDs) {
        checkNotNull(projectLevelUUIDs, "The projectLevelUUIDs must not be null");

        if (projectLevelUUIDs.isEmpty()) {
            return ImmutableList.of();
        }

        return entityManager
                .createNamedQuery("IndirectProjectRevenue.findIndirectProjectRevenues")
                .setParameter("projectLevelUUIDs", projectLevelUUIDs)
                .getResultList();
    }

}
