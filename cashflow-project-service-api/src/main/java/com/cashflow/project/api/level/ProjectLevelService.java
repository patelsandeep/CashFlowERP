package com.cashflow.project.api.level;

import com.cashflow.project.domain.project.level.ProjectLevel;
import java.util.List;
import javax.annotation.Nonnull;

/**
 *
 * 
 * @since Dec 23, 2016, 5:58:39 PM
 */
public interface ProjectLevelService {

    @Nonnull
    List<ProjectLevel> findLevelsByUUID(@Nonnull final String uuid);

}
