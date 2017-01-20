package com.cashflow.project.config.projectrole;


/**
 *
 * 
 * @since 5 Dec, 2016, 11:27:58 AM
 */

import com.cashflow.useraccount.domain.businessuser.FunctionalRole;
import com.cashflow.core.annotations.AppContext;
import com.cashflow.core.annotations.Config;
import com.cashflow.core.annotations.ConfigProject;
import com.cashflow.core.annotations.ConfigRoot;
import javax.annotation.Nonnull;

import static com.cashflow.core.ApplicationMode.DEVELOPMENT;
import static com.cashflow.core.ApplicationMode.LIVE;
import static com.cashflow.core.ApplicationMode.LOCAL;
import static com.cashflow.core.ApplicationMode.TEST;

@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "config-service.properties")
@AppContext(applicationModes = {LOCAL, DEVELOPMENT, TEST, LIVE})
public interface ProjectManagerRolesMappingConfigurationService {

    @Nonnull
    FunctionalRole[] getRoleMappingsForProjectManagers();

}
