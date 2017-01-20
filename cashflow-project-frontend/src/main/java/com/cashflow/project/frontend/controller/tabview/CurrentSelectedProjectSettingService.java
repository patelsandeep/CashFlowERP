package com.cashflow.project.frontend.controller.tabview;

import com.cashflow.frontend.support.cacheing.requestcached.RequestCached;
import com.cashflow.frontend.support.httpparameter.HttpParameter;
import com.cashflow.project.api.level.ProjectLevelSettingService;
import com.cashflow.project.api.project.ProjectService;
import com.cashflow.project.domain.project.level.ProjectLevelSetting;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_UUID;

/**
 *
 * 
 * @since Nov 23, 2016, 6:23:39 AM
 */
@ApplicationScoped
public class CurrentSelectedProjectSettingService {

    @Inject
    private ProjectService projectService;

    @Inject
    private ProjectLevelSettingService projectLevelSettingService;

    @Inject
    @HttpParameter(SELECTED_PROJECT_UUID)
    private Instance<String> pUUID;

    @Nonnull
    @RequestCached
    public Optional<ProjectLevelSetting> getProjectLevelSetting() {
        return Optional
                .ofNullable(pUUID.get())
                .map(projectLevelSettingService::getProjectLevelSetting);
    }
}
