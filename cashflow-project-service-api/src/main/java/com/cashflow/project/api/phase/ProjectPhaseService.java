package com.cashflow.project.api.phase;

import com.cashflow.project.domain.project.budget.Budget;
import com.cashflow.project.domain.project.budget.Deposit;
import com.cashflow.project.domain.project.level.ProjectLevelProgress;
import com.cashflow.project.domain.project.phase.PhaseContext;
import com.cashflow.project.domain.project.phase.ProjectPhase;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * 
 * @since Nov 17, 2016, 10:55:25 PM
 */
public interface ProjectPhaseService {

    @Nonnull
    List<ProjectPhase> getProjectPhases(@Nonnull final PhaseContext phaseContext);

    @Nonnull
    Integer count(@Nonnull final PhaseContext phaseContext);

    void saveProjectPhase(@Nonnull final ProjectPhase projectPhase,
                          @Nonnull final Budget budget,
                          @Nonnull final Deposit deposit,
                          @Nonnull final ProjectLevelProgress projectLevelProgress);

    @Nullable
    ProjectPhase getPhase(@Nonnull final String projectPhaseIdOrUUID);

}
