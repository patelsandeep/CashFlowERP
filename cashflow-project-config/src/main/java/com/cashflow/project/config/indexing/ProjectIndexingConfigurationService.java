package com.cashflow.project.config.indexing;

import com.cashflow.indexing.config.IndexingConfigurationService;
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
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "config-service.properties")
@AppContext(applicationModes = {LOCAL, DEVELOPMENT, TEST, LIVE})
public interface ProjectIndexingConfigurationService extends IndexingConfigurationService {

    @Default("false")
    @Info("If true, then the search is done on indexing service by default. If indexing service fails, we will still try database fallback.")
    boolean isSearchEnabledOnIndex();

    @Default("false")
    @Info("If true, then project level data will be indexed into the indexing services")
    boolean isIndexingEnabled();
}
