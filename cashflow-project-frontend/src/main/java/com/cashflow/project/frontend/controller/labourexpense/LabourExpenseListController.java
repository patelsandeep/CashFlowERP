package com.cashflow.project.frontend.controller.labourexpense;

import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.accessroles.domain.accessrole.AccessRole;
import com.cashflow.accessroles.service.accessrole.AccessRoleRequestContext;
import com.cashflow.accessroles.service.accessrole.AccessRoleService;
import com.cashflow.frontend.support.cacheing.requestcached.RequestCached;
import com.cashflow.frontend.support.httpparameter.HttpParameter;
import com.cashflow.project.api.expenseinformation.LabourExpenseInformation;
import com.cashflow.project.api.expenseinformation.LabourExpenseInformationRequest;
import com.cashflow.project.api.expenseinformation.LabourExpenseInformationResult;
import com.cashflow.project.api.expenseinformation.LabourExpenseInformationService;
import com.cashflow.project.api.level.ProjectLevelService;
import com.cashflow.project.api.project.ProjectService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.expense.LabourExpenseType;
import com.cashflow.project.domain.project.filtering.Filter;
import com.cashflow.project.domain.project.filtering.FilterDomain;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.frontend.controller.model.ModelViewScopedController;
import com.cashflow.project.frontend.controller.pagination.PaginationModel;
import com.cashflow.project.frontend.service.urlbuilder.StaticLinkUrlBuilder;
import com.cashflow.project.frontend.service.urlbuilder.UrlContext;
import com.cashflow.project.translation.expenses.ExpenseTabTranslationService;
import com.cashflow.project.translation.expenses.LabourTypeTranslationService;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;

import static com.cashflow.frontend.support.jsfutil.SelectItemHelper.getSelectItems;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_UUID;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 *
 * 
 * @since Dec 12, 2016, 11:33:43 AM
 */
@ModelViewScopedController
public class LabourExpenseListController extends PaginationModel implements Serializable {

    private static final long serialVersionUID = 1519914912142664119L;

    private static final String PROJECT_URL = "projects/projects.xhtml";

    @Inject
    private StaticLinkUrlBuilder staticLinkUrlBuilder;

    @Inject
    private LabourExpenseInformationService labourExpenseInformationService;

    @Inject
    private LabourTypeTranslationService labourTypeTranslationService;

    @Inject
    private ExpenseTabTranslationService expenseTabTranslationService;

    @Inject
    private ProjectService projectService;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private ProjectLevelService projectLevelService;

    @Inject
    private AccessRoleService accessRoleService;

    @Inject
    @HttpParameter(SELECTED_PROJECT_UUID)
    private Instance<String> pUUID;

    @Getter
    private Map<String, LabourExpenseInformation> expenseInformation;

    @Getter
    @Setter
    private String projectUUID;

    @Getter
    @Setter
    private Project project;

    @Getter
    private final FilterModel filterModel = new FilterModel();

    @PostConstruct
    public void initMembersExpense() {
        projectUUID = checkNotNull(pUUID.get(), "Project UUID must not be null");
        final Future<Project> projectFuture = asynchronousService
                .execute(() -> checkNotNull(projectService.getProject(projectUUID),
                                            "Failed to load project for uuid: %s",
                                            projectUUID));
        final Future<List<AccessRole>> accessRoles
                = asynchronousService.execute(() -> getAccessRoles());

        final Future<List<ProjectLevel>> filterValuesRequest = asynchronousService
                .execute(() -> projectLevelService
                .findLevelsByUUID(projectUUID));

        loadExpense();
        try {
            project = projectFuture.get();
            filterModel.setAccessRoles(accessRoles.get());
            filterModel.setProjectLevels(filterValuesRequest.get());
        } catch (InterruptedException | ExecutionException ex) {
            throw Throwables.propagate(ex);
        }
    }

    public void loadExpense() {
        final LabourExpenseInformationRequest request = buildLabourExpenseInformationRequest();
        // adding asynch here as its called from @PostConstruct also.
        final Future<LabourExpenseInformationResult> resultRequest = asynchronousService
                .execute(() -> labourExpenseInformationService.getLabourExpenseInformation(request));
        try {
            this.expenseInformation = resultRequest.get().getLabourExpenseInformations();
            this.setCount(resultRequest.get().getCount());
        } catch (InterruptedException | ExecutionException ex) {
            throw Throwables.propagate(ex);
        }
    }

    @Nonnull
    private LabourExpenseInformationRequest buildLabourExpenseInformationRequest() {
        final Map<Filter, Object> filters = new HashMap<>();
        prepareFilters(filters);

        return LabourExpenseInformationRequest
                .builder()
                .filters(filters)
                .build();
    }

    private void prepareFilters(@Nonnull final Map<Filter, Object> filters) {
        filters.put(Filter.builder()
                .filterDomains(ImmutableList.of(FilterDomain.EXPENSE_REPORT_INFORMATION))
                .attribute("projectUUID")
                .name("projectUUID")
                .build(), null != filterModel.getProjectLevelUUID() ? filterModel.getProjectLevelUUID() : projectUUID);
        if (!isNullOrEmpty(filterModel.getSearchValue())) {
            filters.put(Filter.builder()
                    .filterDomains(ImmutableList.of(FilterDomain.EXPENSE_REPORT_INFORMATION))
                    .attribute("freeText")
                    .name("name")
                    .build(), filterModel.getSearchValue());
        }
        if (!isNullOrEmpty(filterModel.getAccessRoleUUID())) {
            filters.put(Filter.builder()
                    .filterDomains(ImmutableList.of(FilterDomain.EXPENSE_REPORT_INFORMATION))
                    .attribute("accessRoleUUID")
                    .name("accessRoleUUID")
                    .build(), filterModel.getAccessRoleUUID());
        }
        if (null != filterModel.getLabourExpenseType()) {
            filters.put(Filter.builder()
                    .filterDomains(ImmutableList.of(FilterDomain.EXPENSE_REPORT_INFORMATION))
                    .attribute("expenseType")
                    .name("expenseType")
                    .build(), filterModel.getLabourExpenseType());
        }
    }

    @Override
    public void loadData() {
        loadExpense();
    }

    @Nonnull
    public String redirectProjectSummary() {
        final UrlContext.Builder context = UrlContext
                .builder()
                .path(PROJECT_URL)
                .forceFacesRedirect(true);
        return staticLinkUrlBuilder.buildURL(context.build());
    }

    @Nonnull
    public List<AccessRole> getAccessRoles() {
        final AccessRoleRequestContext requestContext = AccessRoleRequestContext
                .builder()
                .applicationCode("AZPM")
                .build();
        return accessRoleService.getAccessRoles(requestContext);
    }

    @Nonnull
    @RequestCached
    public SelectItem[] getLabourExpenseTypes() {
        return getSelectItems(LabourExpenseType.values(),
                              true,
                              expenseTabTranslationService.getFilterByLabel(),
                              labourTypeTranslationService::getLabourExpenseTypeLabel);
    }

}
