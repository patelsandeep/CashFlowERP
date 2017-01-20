package com.cashflow.project.domain.facade.revenue;

import com.cashflow.project.domain.facade.ProjectAbstractFacade;
import com.cashflow.project.domain.project.revenue.DirectProjectRevenue;
import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.ejb.Stateless;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since 25 Nov, 2016, 12:31:36 PM
 */
@Stateless
public class DirectRevenueFacade extends ProjectAbstractFacade<DirectProjectRevenue> {

    public DirectRevenueFacade() {
        super(DirectProjectRevenue.class);
    }

    @Nonnull
    public List<DirectProjectRevenue> getDirectProjectRevenues(@Nonnull final List<String> projectLevelUUIDs) {
        checkNotNull(projectLevelUUIDs, "The projectLevelUUIDs must not be null");

        if (projectLevelUUIDs.isEmpty()) {
            return ImmutableList.of();
        }

        return entityManager
                .createNamedQuery("DirectProjectRevenue.findDirectProjectRevenues")
                .setParameter("projectLevelUUIDs", projectLevelUUIDs)
                .getResultList();
    }

}
