package com.cashflow.project.config.accessrole;

import com.cashflow.core.annotations.AppContext;
import com.cashflow.core.annotations.Config;
import com.cashflow.core.annotations.ConfigProject;
import com.cashflow.core.annotations.ConfigRoot;
import com.cashflow.core.annotations.Default;
import com.cashflow.core.annotations.Info;

import static com.cashflow.core.ApplicationMode.DEVELOPMENT;
import static com.cashflow.core.ApplicationMode.LIVE;
import static com.cashflow.core.ApplicationMode.LOCAL;
import static com.cashflow.core.ApplicationMode.TEST;

/**
 *
 * 
 * @since Nov 12, 2016, 6:51:50 AM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "config-service.properties")
@AppContext(applicationModes = {LOCAL, DEVELOPMENT, TEST, LIVE})
public interface ProjectAccessRoleConfigurationService {

    @Default("false")
    @Info("We may not be ready to activate access roles within the modules")
    boolean isAccessRoleCheckEnabled();
}
