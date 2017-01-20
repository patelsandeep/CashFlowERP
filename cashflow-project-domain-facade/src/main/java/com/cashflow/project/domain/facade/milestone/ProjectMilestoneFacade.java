package com.cashflow.project.domain.facade.milestone;

import com.cashflow.project.domain.facade.ProjectAbstractFacade;
import com.cashflow.project.domain.project.milestone.ProjectMilestone;
import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 *
 * 
 * @since 21 Nov, 2016, 6:43:27 PM
 */
@Stateless
public class ProjectMilestoneFacade extends ProjectAbstractFacade<ProjectMilestone> {

    public ProjectMilestoneFacade() {
        super(ProjectMilestone.class);
    }

    @Nullable
    public ProjectMilestone findByMilestoneIdOrUUID(@Nonnull final String milestoneIdOrUUID,
                                                    @Nonnull final String businessAccountId) {
        checkNotNull(milestoneIdOrUUID, "The milestoneIdOrUUID must not be null");

        final List<ProjectMilestone> projectMilestones = getEntityManager()
                .createNamedQuery("ProjectMilestone.findByMilestoneIdOrUUID", ProjectMilestone.class)
                .setParameter("id", milestoneIdOrUUID)
                .setParameter("businessAccountId", businessAccountId)
                .getResultList();
        checkState(projectMilestones.size() <= 1,
                   "Invalid lookup. Milestone ID/uuid{%s} returns multiple results",
                   milestoneIdOrUUID);

        return projectMilestones
                .stream()
                .findAny()
                .orElse(null);
    }

    @Nonnull
    public List<ProjectMilestone> findByPhaseUUIDs(@Nonnull final List<String> phaseUUIDs) {
        checkNotNull(phaseUUIDs, "The phaseUUIDs must not be null");

        if (phaseUUIDs.isEmpty()) {
            return ImmutableList.of();
        }

        return getEntityManager()
                .createNamedQuery("ProjectMilestone.findByPhaseUUIDs", ProjectMilestone.class)
                .setParameter("projectLevelUUIDs", phaseUUIDs)
                .getResultList();

    }

    @Nonnull
    public List<String> findUUIDByNameorNumber(@Nonnull final String nameOrNumber,
                                               @Nonnull final String businessAccountId) {
        checkNotNull(nameOrNumber, "The nameOrNumber must not be null");

        return getEntityManager()
                .createNamedQuery("ProjectMilestone.findUUIDByNameOrNumber", String.class)
                .setParameter("name", matchesFromBeginning(nameOrNumber))
                .setParameter("businessAccountId", businessAccountId)
                .getResultList();
    }

}
