package com.cashflow.project.frontend.controller.project;

import com.cashflow.project.api.project.ProjectService;
import com.cashflow.project.domain.project.Project;
import javax.annotation.Nonnull;
import javax.annotation.Priority;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.interceptor.Interceptor;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * When one works on a project, he may not be changing between projects all the time, so it makes sense to cache the
 * current project.
 *
 * 
 * @since Dec 8, 2016, 10:19:20 AM
 */
@Dependent
@Decorator
@Priority(Interceptor.Priority.APPLICATION)
public abstract class ProjectServiceDelegate implements ProjectService {

    @Inject
    private ProjectCacheService projectCacheService;

    @Inject
    @Delegate
    private ProjectService projectService;

    @Override
    public Project getProject(@Nonnull final String projectIdOrUUID) {
        checkNotNull(projectIdOrUUID, "The projectIdOrUUID must not be null");

        final Project cachedProject = projectCacheService.getCachedProject(projectIdOrUUID);
        if (cachedProject != null) {
            return cachedProject;
        }

        final Project project = projectService.getProject(projectIdOrUUID);
        if (project != null) {
            projectCacheService.setCachedProject(project);
        }

        return project;
    }

}
