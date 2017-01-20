package com.cashflow.project.domain.service.projectinformation.indexing;

import com.cashflow.access.authorization.LoggedInBusinessAccount;
import com.cashflow.indexing.service.search.QueryBuilder;
import com.cashflow.indexing.service.search.QueryBuilderFactory;
import com.cashflow.indexing.service.search.SearchResult;
import com.cashflow.indexing.service.search.SearchService;
import com.cashflow.project.api.projectinformation.ProjectInformation;
import com.cashflow.project.api.projectinformation.ProjectInformationRequest;
import com.cashflow.project.api.projectinformation.ProjectInformationResult;
import com.cashflow.project.api.projectinformation.ProjectInformationService;
import com.cashflow.project.config.indexing.ProjectIndexingConfigurationService;
import com.cashflow.project.domain.project.filtering.Filter;
import com.cashflow.project.domain.service.DatabaseContext;
import com.cashflow.useraccount.domain.businessaccount.BusinessAccount;
import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since Nov 12, 2016, 11:03:16 AM
 */
@Default
@ApplicationScoped
public class IndexingProjectInformationService implements ProjectInformationService {

    @Inject
    private ProjectIndexingConfigurationService projectIndexingConfigurationService;

    @Inject
    @DatabaseContext
    private ProjectInformationService databaseProjectInformationService;

    @Inject
    private SearchService searchService;

    @Inject
    private QueryBuilderFactory queryBuilderFactory;

    @Inject
    @LoggedInBusinessAccount
    private Instance<BusinessAccount> businessAccount;

    @Nonnull
    @Override
    public ProjectInformationResult getProjectInformations(
            @Nonnull final ProjectInformationRequest projectInformationRequestContext) {
        checkNotNull(projectInformationRequestContext, "The projectInformationRequestContext must not be null");

        if (!projectIndexingConfigurationService.isSearchEnabledOnIndex()) {
            return databaseProjectInformationService.getProjectInformations(projectInformationRequestContext);
        }

        final String index = checkNotNull(businessAccount.get().getAccountId());
        final QueryBuilder queryBuilder = queryBuilderFactory.createQueryBuilder();
        final Class<ProjectInformation> indexTypeClass = ProjectInformation.class;
        final String businessUnitUUID = projectInformationRequestContext.getBusinessUnitUUID();
        queryBuilder.equals("businessUnitUUIDs", businessUnitUUID);

        final String departmentUUID = projectInformationRequestContext.getDepartmentUUID();
        if (departmentUUID != null) {
            queryBuilder.equals("department.uuid", departmentUUID);
        }

        projectInformationRequestContext
                .getFilters()
                .forEach((filter, value) -> queryBuilder.equals(getFilterPath(filter), value));

        projectInformationRequestContext
                .getSortOrders()
                .forEach((filter, sortOder) -> queryBuilder.sort(getFilterPath(filter), sortOder));

        final SearchResult<ProjectInformation> searchResult = searchService.get(index, indexTypeClass, queryBuilder);

        return ProjectInformationResult
                .builder()
                .count(searchResult.getCount())
                .projectInformations(searchResult.getResult())
                .build();
    }

    @Nonnull
    private String getFilterPath(@Nonnull final Filter filter) {
        final StringBuilder builder = new StringBuilder();
        builder.append(filter.getAttribute());

        final Filter childFilter = filter.getChildFilter();
        if (childFilter != null) {
            return builder.append(".").append(getFilterPath(childFilter)).toString();
        }

        return builder.toString();
    }

}
