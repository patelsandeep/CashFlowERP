package com.cashflow.project.domain.service.projectinformation.indexing.report;

import com.cashflow.access.authorization.LoggedInBusinessAccount;
import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.indexing.service.search.QueryBuilderFactory;
import com.cashflow.indexing.service.search.SearchService;
import com.cashflow.useraccount.domain.businessaccount.BusinessAccount;
import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 *
 * 
 * @since Nov 16, 2016, 5:41:32 AM
 */
@ApplicationScoped
public class ProjectInformationReportService {

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private SearchService searchService;

    @Inject
    private QueryBuilderFactory queryBuilderFactory;

    @Inject
    @LoggedInBusinessAccount
    private Instance<BusinessAccount> businessAccount;

    @Nonnull
    public ProjectInformationReport getProjectInformationReport() {
        return ProjectInformationReport
                .builder()
                .build();
    }
}
