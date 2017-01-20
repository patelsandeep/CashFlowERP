package com.cashflow.project.domain.facade.level;

import com.cashflow.project.domain.facade.ProjectAbstractFacade;
import com.cashflow.project.domain.project.level.ProjectLevelSetting;
import javax.annotation.Nonnull;
import javax.ejb.Stateless;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 *
 */
@Stateless
public class ProjectLevelSettingFacade extends ProjectAbstractFacade<ProjectLevelSetting> {

    public ProjectLevelSettingFacade() {
        super(ProjectLevelSetting.class);
    }

    @Nonnull
    public ProjectLevelSetting getProjectLevelSetting(@Nonnull final String projectUUID) {
        checkNotNull(projectUUID, "The projectUUID must not be null");

        return entityManager
                .createNamedQuery("ProjectLevelSetting.findProjectLevelSetting", ProjectLevelSetting.class)
                .setParameter("projectUUID", projectUUID)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }

}
