package com.cashflow.project.frontend.controller.project;

import com.cashflow.access.authorization.LoggedInAuthorizedUser;
import com.cashflow.access.authorization.LoggedInCompanyAccount;
import com.cashflow.project.api.projectinformation.ProjectInformation;
import com.cashflow.project.api.projectinformation.ProjectInformationRequest;
import com.cashflow.project.api.projectinformation.ProjectInformationResult;
import com.cashflow.project.api.projectinformation.ProjectInformationService;
import com.cashflow.project.domain.project.filtering.Filter;
import com.cashflow.project.domain.project.filtering.FilterDomain;
import com.cashflow.project.domain.project.invoice.Invoice;
import com.cashflow.project.frontend.controller.businessunit.BusinessUnitEvent;
import com.cashflow.project.frontend.controller.businessunit.OnBusinessUnitSelected;
import com.cashflow.project.frontend.controller.pagination.PaginationModel;
import com.cashflow.project.frontend.service.urlbuilder.StaticLinkUrlBuilder;
import com.cashflow.project.frontend.service.urlbuilder.UrlContext;
import com.cashflow.useraccount.domain.businessunit.Branch;
import com.cashflow.useraccount.domain.businessunit.BusinessDivision;
import com.cashflow.useraccount.domain.businessunit.BusinessUnit;
import com.cashflow.useraccount.domain.businessunit.CompanyAccount;
import com.cashflow.useraccount.domain.businessunit.Department;
import com.cashflow.useraccount.domain.businessuser.AuthorizedUser;
import com.cashflow.useraccount.domain.businessuser.EmployeeInformation;
import com.cashflow.useraccount.service.api.businessaccount.employee.EmployeeInformationService;
import com.anosym.common.Amount;
import com.anosym.urlbuilder.QueryParameter;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Instance;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;

import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_UUID;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 */
@Named
@ViewScoped
public class ProjectListController extends PaginationModel implements Serializable {

    private static final long serialVersionUID = 5880651971270442057L;

    private static final String PROJECT_URL = "projects/project.xhtml";

    @Inject
    private ProjectInformationService projectInformationService;

    @Inject
    @LoggedInAuthorizedUser
    private Instance<AuthorizedUser> authorizedUser;

    @Inject
    private EmployeeInformationService employeeInformationService;

    @Inject
    @LoggedInCompanyAccount
    private Instance<CompanyAccount> companyAccount;

    @Inject
    private StaticLinkUrlBuilder staticLinkUrlBuilder;

    @Getter
    private List<ProjectInformation> projectInformations;

    private BusinessUnitEvent businessUnitSelectionEvent;

    @Getter
    @Setter
    @Nullable
    private String freeSearch;

    @Getter
    @Setter
    @Nullable
    private Calendar startDate;

    @Getter
    @Setter
    @Nullable
    private Calendar endDate;

    @PostConstruct
    public void loadProjects() {
        if (businessUnitSelectionEvent != null) {
            onBusinessUnitSelectionEvent(businessUnitSelectionEvent);
        } else {
            final ProjectInformationRequest requestContext = buildProjectInformationRequest();
            loadProjects(requestContext);
        }
    }

    private void loadProjects(@Nonnull final ProjectInformationRequest requestContext) {
        final ProjectInformationResult projectInformationResult = projectInformationService
                .getProjectInformations(requestContext);
        this.projectInformations = projectInformationResult.getProjectInformations();
        this.setCount(projectInformationResult.getCount());
    }

    public void onBusinessUnitSelectionEvent(
            @Observes(notifyObserver = Reception.IF_EXISTS) @OnBusinessUnitSelected final BusinessUnitEvent businessUnitSelectionEvent) {
        this.businessUnitSelectionEvent = businessUnitSelectionEvent;

        final ProjectInformationRequest.Builder requestContext = buildProjectInformationRequest().toBuilder();

        final Department department = businessUnitSelectionEvent.getSelectedDepartment();
        requestContext.departmentUUID(null != department ? department.getUuid() : null);

        final Branch branch = businessUnitSelectionEvent.getSelectedBranch();
        final BusinessDivision businessDivision = businessUnitSelectionEvent.getSelectedBusinessDivision();
        if (branch != null) {
            requestContext.businessUnitUUID(branch.getUuid());
        } else if (businessDivision != null) {
            requestContext.businessUnitUUID(businessDivision.getUuid());
        }

        loadProjects(requestContext.build());
    }

    @Nonnull
    private ProjectInformationRequest buildProjectInformationRequest() {
        final Map<Filter, Object> filters = new HashMap<>();
        prepareFilters(filters);

        return ProjectInformationRequest
                .builder()
                .filters(filters)
                .businessUnitUUID(getBusinessUnitUUID())
                .departmentUUID(getDepartmentUUID())
                .build();
    }

    private void prepareFilters(@Nonnull final Map<Filter, Object> filters) {
        filters.put(Filter.builder()
                .filterDomains(ImmutableList.of(FilterDomain.PROJECT))
                .attribute("offset")
                .name("offset")
                .build(), getPage() * getLimit());
        filters.put(Filter.builder()
                .filterDomains(ImmutableList.of(FilterDomain.PROJECT))
                .attribute("limit")
                .name("limit")
                .build(), getLimit());
        if (null != freeSearch) {
            filters.put(Filter.builder()
                    .filterDomains(ImmutableList.of(FilterDomain.PROJECT))
                    .attribute("freeText")
                    .name("name")
                    .build(), freeSearch);
        }
        Calendar fromDate;
        if (null != startDate) {
            fromDate = startDate;
            filters.put(Filter.builder()
                    .filterDomains(ImmutableList.of(FilterDomain.PROJECT))
                    .attribute("beginningFromDate")
                    .name("start Date")
                    .build(), fromDate);
        }
        if (null != endDate) {
            fromDate = endDate;
            filters.put(Filter.builder()
                    .filterDomains(ImmutableList.of(FilterDomain.PROJECT))
                    .attribute("beginningFromDate")
                    .name("end Date")
                    .build(), fromDate);
        }
    }

    @Nonnull
    private String getBusinessUnitUUID() {
        final AuthorizedUser authorizedUser = checkNotNull(this.authorizedUser.get());
        final BusinessUnit<?> businessUnit = authorizedUser.getBusinessUnit();
        if (isInCurrentCompanyAccount(businessUnit)) {
            return businessUnit.getUuid();
        }

        return companyAccount.get().getUuid();
    }

    @Nullable
    private String getDepartmentUUID() {
        final EmployeeInformation emp = employeeInformationService
                .getEmployeeInformation(authorizedUser.get().getUuid());
        if (null != emp.getDepartment()) {
            return emp.getDepartment().getUuid();
        }
        return null;
    }

    private boolean isInCurrentCompanyAccount(@Nonnull final BusinessUnit<?> businessUnit) {
        if (Objects.equals(companyAccount.get().getUuid(),
                           businessUnit.getUuid())) {
            return true;
        }

        final BusinessUnit<?> parentUnit = businessUnit.getParentUnit();
        if (parentUnit == null) {
            return false;
        }

        return isInCurrentCompanyAccount(parentUnit);
    }

    @Override
    public void loadData() {
        loadProjects();
    }

    @Nonnull
    public Amount getRevenueLTD(@Nonnull final ProjectInformation pi) {
        final Amount zero = new Amount(pi.getBudgetAmount().getCurrency(), BigDecimal.ZERO);
        if (null != pi.getCostLTD()
                && null != pi.getProgress()) {
            final Amount revenueLTD = pi.getCostLTD()
                    .multiply(pi.getProgress().getPercentOfCompletion());
            return revenueLTD;
        }
        return zero;
    }

    public Amount getInvoicedLTD(@Nonnull final ProjectInformation pi) {
        final Amount zero = new Amount(pi.getBudgetAmount().getCurrency(), BigDecimal.ZERO);
        if (null != pi.getInvoices()) {
            final Amount revenueInvoiced = pi.getInvoices()
                    .stream()
                    .map(Invoice::getAmount)
                    .reduce(zero, (am1, am2) -> am1.add(am2));
            return revenueInvoiced;
        }
        return zero;
    }

    @Nonnull
    public String createProject() {
        final UrlContext.Builder context = UrlContext.builder()
                .path(PROJECT_URL)
                .forceFacesRedirect(true);
        return staticLinkUrlBuilder.buildURL(context.build());
    }

    @Nonnull
    public String viewProject(@Nonnull final String projectUUID) {
        final QueryParameter<String> projectUUIDParam = QueryParameter
                .<String>builder()
                .parameter(SELECTED_PROJECT_UUID)
                .parameterValue(projectUUID)
                .build();
        final UrlContext urlContext = UrlContext
                .builder()
                .additionalParameter(projectUUIDParam)
                .forceFacesRedirect(true)
                .path(PROJECT_URL)
                .build();
        return staticLinkUrlBuilder.buildURL(urlContext);

    }

}
