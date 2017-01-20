package com.cashflow.project.domain.service.level;

import com.cashflow.backend.businessaccount.authorization.RequiresBusinessAccount;
import com.cashflow.project.api.level.ProjectLevelSettingService;
import com.cashflow.project.domain.facade.level.ProjectLevelSettingFacade;
import com.cashflow.project.domain.project.level.ProjectLevelSetting;
import javax.annotation.Nonnull;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 */
@Stateless
@Dependent
@RequiresBusinessAccount
@Local(ProjectLevelSettingService.class)
public class ProjectLevelSettingServiceImpl implements ProjectLevelSettingService {

    @EJB
    private ProjectLevelSettingFacade levelSettingFacade;

    @Override
    public void saveProjectLevelSetting(@Nonnull final ProjectLevelSetting projectLeveSetting) {
        checkNotNull(projectLeveSetting, "The projectLeveSetting must not be null");

        levelSettingFacade.edit(projectLeveSetting);
    }

    @Override
    @Nonnull
    public ProjectLevelSetting getProjectLevelSetting(@Nonnull final String projectUUID) {
        checkNotNull(projectUUID, "The projectUUID must not be null");

        return levelSettingFacade.getProjectLevelSetting(projectUUID);
    }
}
