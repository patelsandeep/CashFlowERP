package com.cashflow.project.frontend.controller.subcontractor;

import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.accountpayables.domain.supplier.Supplier;
import com.cashflow.accountpayables.domain.supplier.SupplierSearchContext;
import com.cashflow.accountpayables.service.api.supplier.SupplierService;
import com.cashflow.frontend.support.cacheing.requestcached.RequestCached;
import com.cashflow.frontend.support.httpparameter.HttpParameter;
import com.cashflow.project.api.expense.subcontractor.SubContractorExpenseInformation;
import com.cashflow.project.api.expense.subcontractor.SubContractorExpenseInformationRequest;
import com.cashflow.project.api.expense.subcontractor.SubContractorExpenseInformationResult;
import com.cashflow.project.api.expense.subcontractor.SubContractorExpenseInformationService;
import com.cashflow.project.api.project.ProjectService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.filtering.Filter;
import com.cashflow.project.domain.project.filtering.FilterDomain;
import com.cashflow.project.domain.project.timesheet.ExpenseStatus;
import com.cashflow.project.frontend.controller.model.ModelViewScopedController;
import com.cashflow.project.frontend.controller.pagination.PaginationModel;
import com.cashflow.project.frontend.service.urlbuilder.StaticLinkUrlBuilder;
import com.cashflow.project.frontend.service.urlbuilder.UrlContext;
import com.cashflow.project.translation.expenses.ExpenseStatusTranslationService;
import com.cashflow.project.translation.subcontractor.SubContractorExpenseTranslationService;
import com.anosym.profiler.Profile;
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

/**
 *
 * 
 * @since Jan 3, 2017, 12:02:40 PM
 */
@ModelViewScopedController
public class SubContractorExpenseListController extends PaginationModel implements Serializable {

    private static final long serialVersionUID = -3667679880984034749L;

    private static final String PROJECT_URL = "/projects/projects.xhtml";

    @Inject
    private StaticLinkUrlBuilder staticLinkUrlBuilder;

    @Inject
    private SubContractorExpenseInformationService expenseInformationService;

    @Inject
    private SubContractorExpenseTranslationService subContractorExpenseTranslationService;

    @Inject
    private ExpenseStatusTranslationService expenseStatusTranslationService;

    @Inject
    private SupplierService supplierService;

    @Inject
    @HttpParameter(SELECTED_PROJECT_UUID)
    private Instance<String> pUUID;

    @Inject
    private ProjectService projectService;

    @Inject
    private AsynchronousService asynchronousService;

    private String projectUUID;

    @Getter
    @Setter
    private List<Supplier> suppliers;

    @Getter
    @Setter
    private String projectLevelUUID;

    @Getter
    @Setter
    private String searchValue;

    @Getter
    @Setter
    private String searchSupplierUUID;

    @Getter
    @Setter
    private ExpenseStatus searchExpenseStatus;

    @Getter
    private Project project;

    @PostConstruct
    public void initExpense() {
        projectUUID = checkNotNull(pUUID.get(), "Project UUID must not be null");

        final Future<Project> projectRequest = asynchronousService
                .execute(() -> checkNotNull(projectService.getProject(projectUUID),
                                            "Failed to load project for uuid: %s",
                                            projectUUID));
        final Future<List<Supplier>> suppliersRequest = asynchronousService
                .execute(() -> supplierService.findSuppliers(SupplierSearchContext
                        .builder()
                        .build()));
        try {
            project = projectRequest.get();
            suppliers = suppliersRequest.get();
        } catch (InterruptedException | ExecutionException ex) {
            throw Throwables.propagate(ex);
        }

    }

    @Profile
    @Nonnull
    public List<SubContractorExpenseInformation> getSubContractorExpenses() {

        final SubContractorExpenseInformationRequest informationRequest = buildExpenseInformationRequest();

        final SubContractorExpenseInformationResult result = expenseInformationService
                .getSubContractorExpenseInformations(informationRequest);
        this.setCount(result.getCount());

        return result.getSubContractorExpenseInformations();
    }

    @Override
    public void loadData() {
        getSubContractorExpenses();
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
    private SubContractorExpenseInformationRequest buildExpenseInformationRequest() {
        final Map<Filter, Object> filters = new HashMap<>();
        prepareFilters(filters);

        return SubContractorExpenseInformationRequest
                .builder()
                .filters(filters)
                .build();
    }

    private void prepareFilters(@Nonnull final Map<Filter, Object> filters) {
        filters.put(Filter.builder()
                .filterDomains(ImmutableList.of(FilterDomain.SUB_CONTRACTOR_EXPENSE_INFORMATION))
                .attribute("offset")
                .name("offset")
                .build(), getPage() * getLimit());
        filters.put(Filter.builder()
                .filterDomains(ImmutableList.of(FilterDomain.SUB_CONTRACTOR_EXPENSE_INFORMATION))
                .attribute("limit")
                .name("limit")
                .build(), getLimit());
        filters.put(Filter.builder()
                .filterDomains(ImmutableList.of(FilterDomain.SUB_CONTRACTOR_EXPENSE_INFORMATION))
                .attribute("projectLevelUUID")
                .name("projectLevelUUID")
                .build(), null != projectLevelUUID ? projectLevelUUID : projectUUID);
        if (null != searchValue) {
            filters.put(Filter.builder()
                    .filterDomains(ImmutableList.of(FilterDomain.SUB_CONTRACTOR_EXPENSE_INFORMATION))
                    .attribute("freeText")
                    .name("name")
                    .build(), searchValue);
        }
        if (null != searchExpenseStatus) {
            filters.put(Filter.builder()
                    .filterDomains(ImmutableList.of(FilterDomain.SUB_CONTRACTOR_EXPENSE_INFORMATION))
                    .attribute("expenseStatus")
                    .name("expenseStatus")
                    .build(), searchExpenseStatus);
        }
        if (null != searchSupplierUUID) {
            filters.put(Filter.builder()
                    .filterDomains(ImmutableList.of(FilterDomain.SUB_CONTRACTOR_EXPENSE_INFORMATION))
                    .attribute("supplierUUID")
                    .name("supplierUUID")
                    .build(), searchSupplierUUID);
        }
    }

    @Nonnull
    @RequestCached
    public SelectItem[] getExpenseStatuses() {
        return getSelectItems(ExpenseStatus.values(),
                              true,
                              subContractorExpenseTranslationService.getFilterByStatusLabel(),
                              expenseStatusTranslationService::getExpenseStatusLabel);
    }

}
