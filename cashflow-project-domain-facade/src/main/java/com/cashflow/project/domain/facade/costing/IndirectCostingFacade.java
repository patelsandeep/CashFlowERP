package com.cashflow.project.domain.facade.costing;

import com.cashflow.project.domain.facade.ProjectAbstractFacade;
import com.cashflow.project.domain.project.costing.IndirectProjectCosting;
import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.ejb.Stateless;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since 25 Nov, 2016, 12:29:26 PM
 */
@Stateless
public class IndirectCostingFacade extends ProjectAbstractFacade<IndirectProjectCosting> {

    public IndirectCostingFacade() {
        super(IndirectProjectCosting.class);
    }

    @Nonnull
    public List<IndirectProjectCosting> getIndirectProjectCostings(@Nonnull final List<String> projectLevelUUIDs) {
        checkNotNull(projectLevelUUIDs, "The projectLevelUUIDs must not be null");

        if (projectLevelUUIDs.isEmpty()) {
            return ImmutableList.of();
        }

        return entityManager
                .createNamedQuery("IndirectProjectCosting.findIndirectProjectCostings")
                .setParameter("projectLevelUUIDs", projectLevelUUIDs)
                .getResultList();
    }
}
