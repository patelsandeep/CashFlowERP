package com.cashflow.project.config.serviceapi.authentication;

import com.cashflow.module.authentication.config.AuthenticationConfigurationService;
import com.cashflow.core.annotations.AppContext;
import com.cashflow.core.annotations.Config;
import com.cashflow.core.annotations.ConfigProject;
import com.cashflow.core.annotations.ConfigRoot;

import static com.cashflow.core.ApplicationMode.ALPHA;
import static com.cashflow.core.ApplicationMode.BETA;
import static com.cashflow.core.ApplicationMode.DEVELOPMENT;
import static com.cashflow.core.ApplicationMode.INTEGRATION;
import static com.cashflow.core.ApplicationMode.LIVE;
import static com.cashflow.core.ApplicationMode.LOCAL;
import static com.cashflow.core.ApplicationMode.RELEASE;
import static com.cashflow.core.ApplicationMode.TEST;

/**
 *
 * 
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "config-service.properties")
@AppContext(applicationModes = {ALPHA, BETA, DEVELOPMENT, INTEGRATION, LIVE, LOCAL, RELEASE, TEST})
public interface ProjectAuthenticationConfigurationService extends AuthenticationConfigurationService {
}
