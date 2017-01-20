package com.cashflow.project.config.accountjournal;

import com.cashflow.module.authentication.api.config.ProjectEndpoint;
import com.cashflow.module.authentication.api.config.WebserviceConfiguration;
import static com.cashflow.core.ApplicationMode.DEVELOPMENT;
import static com.cashflow.core.ApplicationMode.LIVE;
import static com.cashflow.core.ApplicationMode.LOCAL;
import static com.cashflow.core.ApplicationMode.TEST;
import com.cashflow.core.annotations.AppContext;
import com.cashflow.core.annotations.Config;
import com.cashflow.core.annotations.ConfigProject;
import com.cashflow.core.annotations.ConfigRoot;

/**
 *
 * 
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "config-service.properties")
@ProjectEndpoint(client = "AZPM", endpoint = "AGL")
@AppContext(applicationModes = {LOCAL, DEVELOPMENT, TEST, LIVE})
public interface ProjectAccountJournalWebserviceConfiguration extends WebserviceConfiguration{
    
}
