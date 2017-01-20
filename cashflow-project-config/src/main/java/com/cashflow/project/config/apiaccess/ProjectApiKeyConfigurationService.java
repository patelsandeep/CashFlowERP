package com.cashflow.project.config.apiaccess;

import com.cashflow.access.authorization.service.client.initialization.apiaccess.ApiAccessConfigurationService;
import com.cashflow.core.annotations.AppContext;
import com.cashflow.core.annotations.Config;
import com.cashflow.core.annotations.ConfigProject;
import com.cashflow.core.annotations.ConfigRoot;
import com.cashflow.core.annotations.Default;
import com.cashflow.core.annotations.Info;
import javax.annotation.Nonnull;

import static com.cashflow.core.ApplicationMode.DEVELOPMENT;
import static com.cashflow.core.ApplicationMode.LIVE;
import static com.cashflow.core.ApplicationMode.LOCAL;
import static com.cashflow.core.ApplicationMode.TEST;

/**
 *
 * 
 * @since Jul 23, 2015, 9:52:19 PM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "config-service.properties")
@AppContext(applicationModes = {LOCAL, DEVELOPMENT, TEST, LIVE})
public interface ProjectApiKeyConfigurationService extends ApiAccessConfigurationService {

    @Nonnull
    @Override
    @Default("hsdsj-34339s-s82bhas-34378abs-f736gas-346agas-34ys-DEVELOPMENT")
    @Info("API Key for the AZPM. The key is used to authenticate all request to project module")
    String getApiKey();
}
