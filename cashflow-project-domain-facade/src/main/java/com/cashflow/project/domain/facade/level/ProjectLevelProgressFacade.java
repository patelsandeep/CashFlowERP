package com.cashflow.project.domain.facade.level;

import com.cashflow.project.domain.facade.ProjectAbstractFacade;
import com.cashflow.project.domain.project.level.ProjectLevelProgress;
import java.util.List;
import javax.annotation.Nonnull;
import javax.ejb.Stateless;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;

/**
 *
 * 
 * @since Nov 12, 2016, 4:05:09 PM
 */
@Stateless
public class ProjectLevelProgressFacade extends ProjectAbstractFacade<ProjectLevelProgress> {

    public ProjectLevelProgressFacade() {
        super(ProjectLevelProgress.class);
    }

    @Nonnull
    public List<ProjectLevelProgress> getProjectLevelProgresss(@Nonnull final List<String> projectLevelUUIDs) {
        checkNotNull(projectLevelUUIDs, "The projectLevelUUIDs must not be null");

        if (projectLevelUUIDs.isEmpty()) {
            return ImmutableList.of();
        }

        return entityManager
                .createNamedQuery("ProjectLevelProgress.findProjectLevelProgress")
                .setParameter("projectLevelUUIDs", projectLevelUUIDs)
                .getResultList();
    }
}
