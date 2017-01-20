package com.cashflow.project.domain.facade.project;

import com.cashflow.project.domain.facade.ProjectAbstractFacade;
import com.cashflow.project.domain.project.Project;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;

/**
 *
 * 
 */
@Stateless
public class ProjectFacade extends ProjectAbstractFacade<Project> {

    public ProjectFacade() {
        super(Project.class);
    }
    
    @Nullable
    public Project findByProjectIdOrUUID(@Nonnull final String projectIdOrProjectUUID, 
            @Nonnull final String businessAccountId) {
        checkNotNull(projectIdOrProjectUUID, "The projectIdOrProjectUUID must not be null");

        final List<Project> projects = getEntityManager()
                .createNamedQuery("Project.findByProjectIdOrUUID", Project.class)
                .setParameter("id", projectIdOrProjectUUID)
                .setParameter("businessAccountId", businessAccountId)
                .getResultList();
        checkState(projects.size() <= 1,
                "Invalid lookup. Project ID/uuid{%s} returns multiple results",
                projectIdOrProjectUUID);

        return projects
                .stream()
                .findAny()
                .orElse(null);
    }

}
