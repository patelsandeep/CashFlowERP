
package com.cashflow.project.config.indexing;

import static com.cashflow.core.ApplicationMode.DEVELOPMENT;
import static com.cashflow.core.ApplicationMode.LIVE;
import static com.cashflow.core.ApplicationMode.LOCAL;
import static com.cashflow.core.ApplicationMode.TEST;
import com.cashflow.core.annotations.AppContext;
import com.cashflow.core.annotations.Config;
import com.cashflow.core.annotations.ConfigProject;
import com.cashflow.core.annotations.ConfigRoot;

import com.cashflow.frontend.configservice.httpconnection.HttpClientConfigurationService;

/**
 *
 * 
 */

@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "config-service.properties")
@AppContext(applicationModes = {LOCAL, DEVELOPMENT, TEST, LIVE})
public interface ProjectHttpClientConfigurationService extends HttpClientConfigurationService{
    
}
