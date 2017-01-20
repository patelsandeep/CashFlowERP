package com.cashflow.project.domain.facade.costing;

import com.cashflow.project.domain.facade.ProjectAbstractFacade;
import com.cashflow.project.domain.project.costing.DirectProjectCosting;
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
public class DirectCostingFacade extends ProjectAbstractFacade<DirectProjectCosting> {

    public DirectCostingFacade() {
        super(DirectProjectCosting.class);
    }

    @Nonnull
    public List<DirectProjectCosting> getDirectProjectCostings(@Nonnull final List<String> projectLevelUUIDs) {
        checkNotNull(projectLevelUUIDs, "The projectLevelUUIDs must not be null");

        if (projectLevelUUIDs.isEmpty()) {
            return ImmutableList.of();
        }

        return entityManager
                .createNamedQuery("DirectProjectCosting.findDirectProjectCostings")
                .setParameter("projectLevelUUIDs", projectLevelUUIDs)
                .getResultList();
    }
}
