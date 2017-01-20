package com.cashflow.project.config.salestax;

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
 * @since Aug 24, 2015, 2:00:01 AM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "config-service.properties")
@ProjectEndpoint(client = "AZPM", endpoint = "AZST")
@AppContext(applicationModes = {LOCAL, DEVELOPMENT, TEST, LIVE})
public interface ProjectSalesTaxWebconfigurationService extends WebserviceConfiguration {
}
