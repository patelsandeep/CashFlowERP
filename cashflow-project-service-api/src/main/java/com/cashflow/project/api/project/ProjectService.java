package com.cashflow.project.api.project;

import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.budget.Budget;
import com.cashflow.project.domain.project.budget.Deposit;
import com.cashflow.project.domain.project.level.ProjectLevelProgress;
import com.cashflow.project.domain.project.level.ProjectLevelSetting;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * 
 */
public interface ProjectService {

    void saveProject(@Nonnull final Project project,
                     @Nonnull final Budget budget,
                     @Nonnull final Deposit deposit,
                     @Nonnull final ProjectLevelSetting projectLevelSetting,
                     @Nonnull final ProjectLevelProgress projectLevelProgress);

    @Nullable
    Project getProject(@Nonnull final String projectIdOrUUID);

}
