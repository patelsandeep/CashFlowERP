package com.cashflow.project.api.milestone;

import com.cashflow.project.domain.project.budget.Budget;
import com.cashflow.project.domain.project.budget.Deposit;
import com.cashflow.project.domain.project.level.ProjectLevelProgress;
import com.cashflow.project.domain.project.milestone.MilestoneContext;
import com.cashflow.project.domain.project.milestone.ProjectMilestone;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * 
 * @since 21 Nov, 2016, 7:16:43 PM
 */
public interface ProjectMilestoneService {

    void saveProjectMilestone(@Nonnull final ProjectMilestone projectMilestone,
                              @Nonnull final Budget budget,
                              @Nonnull final Deposit deposit,
                              @Nonnull final ProjectLevelProgress projectLevelProgress);

    @Nullable
    ProjectMilestone getMilestone(@Nonnull final String projectPhaseIdOrUUID);

    @Nonnull
    List<ProjectMilestone> findByPhaseUUIDs(@Nonnull final List<String> phaseUUIDs);

    @Nonnull
    List<ProjectMilestone> getMilestones(@Nonnull final MilestoneContext milestoneContext);

}
