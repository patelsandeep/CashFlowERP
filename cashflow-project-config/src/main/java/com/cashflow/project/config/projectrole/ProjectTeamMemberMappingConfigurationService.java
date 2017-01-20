package com.cashflow.project.config.projectrole;

import com.cashflow.useraccount.domain.businessuser.FunctionalRole;
import com.cashflow.core.annotations.AppContext;
import com.cashflow.core.annotations.Config;
import com.cashflow.core.annotations.ConfigProject;
import com.cashflow.core.annotations.ConfigRoot;
import com.cashflow.core.annotations.Default;
import javax.annotation.Nonnull;

import static com.cashflow.core.ApplicationMode.DEVELOPMENT;
import static com.cashflow.core.ApplicationMode.LIVE;
import static com.cashflow.core.ApplicationMode.LOCAL;
import static com.cashflow.core.ApplicationMode.TEST;

/**
 *
 * 
 * @since Nov 28, 2016, 10:19:07 AM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "config-service.properties")
@AppContext(applicationModes = {LOCAL, DEVELOPMENT, TEST, LIVE})
public interface ProjectTeamMemberMappingConfigurationService {

    @Nonnull
    @Default("[PROJECT_TEAM_MEMBER]")
    FunctionalRole[] getRoleMappingsForProjectManagers();

}
