package com.cashflow.project.frontend.controller.project;

import com.cashflow.access.authorization.LoggedInBusinessAccount;
import com.cashflow.entitydomains.facade.OnCreateOrUpdate;
import com.cashflow.project.domain.project.Project;
import com.cashflow.useraccount.domain.businessaccount.BusinessAccount;
import com.anosym.profiler.Profile;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since Dec 8, 2016, 10:24:15 AM
 */
@ApplicationScoped
public class ProjectCacheService {

    private static final int CONCURRENCY_LEVEL = Runtime.getRuntime().availableProcessors() * 10;

    private static final Cache<ProjectKey, Project> PROJECT_CACHE = CacheBuilder
            .<ProjectKey, Project>newBuilder()
            .concurrencyLevel(CONCURRENCY_LEVEL)
            .maximumSize(1000)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build();

    @Inject
    @LoggedInBusinessAccount
    private Instance<BusinessAccount> businessAccount;

    @Nullable
    @Profile(logLevel = "FINE")
    public Project getCachedProject(@Nonnull final String projectIdOrUUID) {
        checkNotNull(projectIdOrUUID, "The projectIdOrUUID must not be null");

        final ProjectKey projectKey = new ProjectKey(businessAccount.get().getAccountId(), projectIdOrUUID);
        return PROJECT_CACHE.getIfPresent(projectKey);
    }

    public void setCachedProject(@Nonnull final Project project) {
        checkNotNull(project, "The project must not be null");
        final ProjectKey projectIdKey = new ProjectKey(businessAccount.get().getAccountId(), project.getId());
        final ProjectKey projectUuidKey = new ProjectKey(businessAccount.get().getAccountId(), project.getUuid());
        PROJECT_CACHE.put(projectIdKey, project);
        PROJECT_CACHE.put(projectUuidKey, project);
    }

    @Profile(logLevel = "FINE")
    void onProjectUpdated(
            @Observes(during = TransactionPhase.AFTER_SUCCESS) @OnCreateOrUpdate @Nonnull final Project updatedProject) {
        checkNotNull(updatedProject, "The updatedProject must not be null");

        final ProjectKey projectUuidKey = new ProjectKey(updatedProject.getBusinessAccountId(), updatedProject.getUuid());
        final ProjectKey projectIdKey = new ProjectKey(updatedProject.getBusinessAccountId(), updatedProject.getId());
        PROJECT_CACHE.invalidate(projectUuidKey);
        PROJECT_CACHE.invalidate(projectIdKey);
    }

    @EqualsAndHashCode
    @RequiredArgsConstructor
    static class ProjectKey {

        private final String businessAccountId;

        private final String projectIdOrUuid;

    }
}
