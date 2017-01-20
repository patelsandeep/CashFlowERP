package com.cashflow.project.domain.facade.phase;

import com.cashflow.project.domain.facade.ProjectAbstractFacade;
import com.cashflow.project.domain.project.phase.ProjectPhase;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 *
 * 
 */
@Stateless
public class ProjectPhaseFacade extends ProjectAbstractFacade<ProjectPhase> {

    public ProjectPhaseFacade() {
        super(ProjectPhase.class);
    }

    @Nullable
    public ProjectPhase findByPhaseIdOrUUID(@Nonnull final String phaseIdOrProjectUUID,
                                            @Nonnull final String businessAccountId) {
        checkNotNull(phaseIdOrProjectUUID, "The phaseIdOrProjectUUID must not be null");

        final List<ProjectPhase> projectPhases = getEntityManager()
                .createNamedQuery("ProjectPhase.findByPhaseIdOrUUID", ProjectPhase.class)
                .setParameter("id", phaseIdOrProjectUUID)
                .setParameter("businessAccountId", businessAccountId)
                .getResultList();
        checkState(projectPhases.size() <= 1,
                   "Invalid lookup. Phase ID/uuid{%s} returns multiple results",
                   phaseIdOrProjectUUID);

        return projectPhases
                .stream()
                .findAny()
                .orElse(null);
    }

    @Nonnull
    public List<String> findUUIDByNameorNumber(@Nonnull final String phaseNameOrNumber,
                                               @Nonnull final String businessAccountId) {
        checkNotNull(phaseNameOrNumber, "The phaseNameOrNumber must not be null");

        return getEntityManager()
                .createNamedQuery("ProjectPhase.findUUIDByPhaseNameOrNumber", String.class)
                .setParameter("name", matchesFromBeginning(phaseNameOrNumber))
                .setParameter("businessAccountId", businessAccountId)
                .getResultList();
    }

}
