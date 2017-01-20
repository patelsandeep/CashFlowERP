package com.cashflow.project.domain.facade.level;

import com.cashflow.project.domain.facade.ProjectAbstractFacade;
import com.cashflow.project.domain.project.level.ProjectLevel;
import java.util.List;
import javax.annotation.Nonnull;
import javax.ejb.Stateless;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since Dec 22, 2016, 2:31:59 PM
 */
@Stateless
public class ProjectLevelFacade extends ProjectAbstractFacade<ProjectLevel> {

    public ProjectLevelFacade() {
        super(ProjectLevel.class);
    }

    @Nonnull
    public List<ProjectLevel<?>> findUUIDByNameorNumber(@Nonnull final String nameOrId,
                                                        @Nonnull final String businessAccountId) {
        checkNotNull(nameOrId, "The nameOrId must not be null");

        return getEntityManager()
                .createNamedQuery("ProjectLevel.findUUIDByNameOrId")
                .setParameter("name", matchesFromBeginning(nameOrId))
                .setParameter("businessAccountId", businessAccountId)
                .getResultList();
    }

    @Nonnull
    public List<ProjectLevel> findLevelsByUUID(@Nonnull final String uuid) {
        checkNotNull(uuid, "The uuid must not be null");

        return getEntityManager()
                .createNamedQuery("ProjectLevel.findLevelsByUUID", ProjectLevel.class)
                .setParameter("uuid", uuid)
                .getResultList();
    }
}
