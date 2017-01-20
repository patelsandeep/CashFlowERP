package com.cashflow.project.api.level;

import com.cashflow.project.domain.project.level.ProjectLevelSetting;
import javax.annotation.Nonnull;

/**
 *
 * 
 */
public interface ProjectLevelSettingService {

    void saveProjectLevelSetting(@Nonnull final ProjectLevelSetting projectLevelSetting);

    @Nonnull
    ProjectLevelSetting getProjectLevelSetting(@Nonnull final String projectUUID);
}
