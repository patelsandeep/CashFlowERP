package com.cashflow.project.config.datarepository;

import com.cashflow.datarepository.config.AWSConfiguration;
import com.cashflow.frontend.support.staticcontent.DataRepositoryNameProvider;
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
 * @since Aug 30, 2015, 10:42:58 PM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "config-service.properties")
@AppContext(applicationModes = {LOCAL, DEVELOPMENT, TEST, LIVE})
public interface ProjectDataRepositoryConfigurationService extends AWSConfiguration, DataRepositoryNameProvider {

    @Nonnull
    @Override
    @Default("cashflow-azpm")
    @Info("The s3 bucket name suffix for storing static content")
    String getDataRepositoryName();
}
