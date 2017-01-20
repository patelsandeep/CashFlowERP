package com.cashflow.project.api.task;

import com.cashflow.project.domain.project.budget.Budget;
import com.cashflow.project.domain.project.budget.Deposit;
import com.cashflow.project.domain.project.level.ProjectLevelProgress;
import com.cashflow.project.domain.project.task.ProjectTask;
import com.cashflow.project.domain.project.task.TaskContext;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * 
 * @since 23 Nov, 2016, 12:36:55 PM
 */
public interface ProjectTaskService {

    void saveProjectTask(@Nonnull final ProjectTask projectTask,
                         @Nonnull final Budget budget,
                         @Nonnull final Deposit deposit,
                         @Nonnull final ProjectLevelProgress projectLevelProgress);

    @Nullable
    ProjectTask getTask(@Nonnull final String projectTaskIdOrUUID);

    @Nonnull
    List<ProjectTask> findByMilestoneOrProjectUUIDs(@Nonnull final List<String> milestoneOrProjectUUIDs);

    @Nonnull
    List<ProjectTask> getTasks(@Nonnull final TaskContext taskContext);

    @Nonnull
    Integer count(@Nonnull final TaskContext taskContext);

}
