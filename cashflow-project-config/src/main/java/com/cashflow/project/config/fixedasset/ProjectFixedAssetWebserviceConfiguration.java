package com.cashflow.project.config.fixedasset;

import com.cashflow.module.authentication.api.config.ProjectEndpoint;
import com.cashflow.module.authentication.api.config.WebserviceConfiguration;
import com.cashflow.core.annotations.AppContext;
import com.cashflow.core.annotations.Config;
import com.cashflow.core.annotations.ConfigProject;
import com.cashflow.core.annotations.ConfigRoot;

import static com.cashflow.core.ApplicationMode.DEVELOPMENT;
import static com.cashflow.core.ApplicationMode.LIVE;
import static com.cashflow.core.ApplicationMode.LOCAL;
import static com.cashflow.core.ApplicationMode.TEST;

/**
 *
 * 
 * @since 14 Dec, 2016, 7:11:24 PM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "config-service.properties")
@ProjectEndpoint(client = "AZPM", endpoint = "AZFA")
@AppContext(applicationModes = {LOCAL, DEVELOPMENT, TEST, LIVE})
public interface ProjectFixedAssetWebserviceConfiguration extends WebserviceConfiguration {

}
