package com.cashflow.project.config.inventory;

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
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "config-service.properties")
@ProjectEndpoint(client = "AZPM", endpoint = "AZIM")
@AppContext(applicationModes = {LOCAL, DEVELOPMENT, TEST, LIVE})
public interface ProjectInventoryWebserviceConfiguration extends WebserviceConfiguration {
}
