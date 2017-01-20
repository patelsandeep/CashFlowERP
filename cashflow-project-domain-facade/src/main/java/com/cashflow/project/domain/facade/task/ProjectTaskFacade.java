package com.cashflow.project.domain.facade.task;

import com.cashflow.project.domain.facade.ProjectAbstractFacade;
import com.cashflow.project.domain.project.task.ProjectTask;
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
 * @since 23 Nov, 2016, 12:32:36 PM
 */
@Stateless
public class ProjectTaskFacade extends ProjectAbstractFacade<ProjectTask> {

    public ProjectTaskFacade() {
        super(ProjectTask.class);
    }

    @Nullable
    public ProjectTask findByTaskIdOrUUID(@Nonnull final String taskIdOrUUID,
                                          @Nonnull final String businessAccountId) {
        checkNotNull(taskIdOrUUID, "The taskIdOrUUID must not be null");

        final List<ProjectTask> projectTasks = getEntityManager()
                .createNamedQuery("ProjectTask.findByTaskIdOrUUID", ProjectTask.class)
                .setParameter("id", taskIdOrUUID)
                .setParameter("businessAccountId", businessAccountId)
                .getResultList();
        checkState(projectTasks.size() <= 1,
                   "Invalid lookup. Task ID/uuid{%s} returns multiple results",
                   taskIdOrUUID);

        return projectTasks
                .stream()
                .findAny()
                .orElse(null);
    }

    @Nonnull
    public List<ProjectTask> findByMilestoneOrProjectUUIDs(@Nonnull final List<String> milestoneOrProjectUUIDs) {
        checkNotNull(milestoneOrProjectUUIDs, "The milestoneOrProjectUUIDs must not be null");
        if (milestoneOrProjectUUIDs.isEmpty()) {
            return ImmutableList.of();
        }

        return getEntityManager()
                .createNamedQuery("ProjectTask.findByMilestoneOrProjectUUIDs", ProjectTask.class)
                .setParameter("parentLevelUUIDs", milestoneOrProjectUUIDs)
                .getResultList();

    }

    @Nonnull
    public List<String> findUUIDByNameorNumber(@Nonnull final String nameOrNumber,
                                               @Nonnull final String businessAccountId) {
        checkNotNull(nameOrNumber, "The nameOrNumber must not be null");

        return getEntityManager()
                .createNamedQuery("ProjectTask.findUUIDByNameOrNumber", String.class)
                .setParameter("name", matchesFromBeginning(nameOrNumber))
                .setParameter("businessAccountId", businessAccountId)
                .getResultList();
    }

}
