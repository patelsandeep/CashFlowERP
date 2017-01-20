package com.cashflow.project.api.progress;

import com.cashflow.project.domain.project.level.ProjectLevelProgress;
import java.util.List;
import javax.annotation.Nonnull;

/**
 *
 * 
 * @since Nov 18, 2016, 4:00:57 PM
 */
public interface ProjectLevelProgressService {

    @Nonnull
    List<ProjectLevelProgress> getProjectLevelProgresss(@Nonnull final List<String> projectLevelUUIDs);

}
