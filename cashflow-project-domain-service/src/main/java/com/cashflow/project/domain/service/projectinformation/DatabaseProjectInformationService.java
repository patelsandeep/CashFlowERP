package com.cashflow.project.domain.service.projectinformation;

import com.cashflow.access.authorization.LoggedInBusinessAccount;
import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.accountreceivables.domain.customer.Customer;
import com.cashflow.accountreceivables.service.api.customer.CustomerService;
import com.cashflow.backend.businessaccount.authorization.RequiresBusinessAccount;
import com.cashflow.project.api.projectinformation.ProjectInformation;
import com.cashflow.project.api.projectinformation.ProjectInformation.ProjectInformationBuilder;
import com.cashflow.project.api.projectinformation.ProjectInformationRequest;
import com.cashflow.project.api.projectinformation.ProjectInformationResult;
import com.cashflow.project.api.projectinformation.ProjectInformationService;
import com.cashflow.project.domain.expenseinformation.FilterProperty;
import com.cashflow.project.domain.facade.budget.BudgetFacade;
import com.cashflow.project.domain.facade.contextual.ProjectContext;
import com.cashflow.project.domain.facade.contextual.ProjectContextFacade;
import com.cashflow.project.domain.facade.invoice.InvoiceFacade;
import com.cashflow.project.domain.facade.level.ProjectLevelProgressFacade;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.budget.Budget;
import com.cashflow.project.domain.project.filtering.Filter;
import com.cashflow.project.domain.project.filtering.FilterDomain;
import com.cashflow.project.domain.project.invoice.Invoice;
import com.cashflow.project.domain.project.level.ProjectLevelProgress;
import com.cashflow.project.domain.service.DatabaseContext;
import com.cashflow.useraccount.domain.businessaccount.BusinessAccount;
import com.cashflow.useraccount.domain.businessunit.Branch;
import com.cashflow.useraccount.domain.businessunit.BusinessDivision;
import com.cashflow.useraccount.domain.businessunit.BusinessUnit;
import com.cashflow.useraccount.domain.businessunit.CompanyAccount;
import com.cashflow.useraccount.domain.businessunit.Department;
import com.cashflow.useraccount.domain.businessuser.Employee;
import com.cashflow.useraccount.domain.businessuser.employee.EmployeeInformationSearchContext;
import com.cashflow.useraccount.service.api.businessaccount.employee.EmployeeInformationService;
import com.cashflow.useraccount.service.api.businessunit.department.DepartmentService;
import com.anosym.common.Amount;
import com.anosym.common.Pair;
import com.anosym.profiler.Profile;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static java.lang.String.format;

/**
 *
 * 
 * @since Nov 12, 2016, 11:04:44 AM
 */
@Stateless
@Dependent
@DatabaseContext
@RequiresBusinessAccount
public class DatabaseProjectInformationService extends AbstractProjectInformationService implements ProjectInformationService {

    private static final Map<String, Field> PROJECT_CONTEXT_FIELD_MAPPING = ImmutableList
            .copyOf(ProjectContext.class.getDeclaredFields())
            .stream()
            .filter((field) -> field.isAnnotationPresent(FilterProperty.class))
            .collect(Collectors.toMap((field) -> {
                final FilterProperty fieldProperty = field.getAnnotation(FilterProperty.class);
                return firstNonNull(emptyToNull(fieldProperty.value()), field.getName());
            }, Function.identity()));

    @EJB
    private ProjectContextFacade projectContextFacade;

    @EJB
    private BudgetFacade budgetFacade;

    @EJB
    private ProjectLevelProgressFacade projectLevelProgressFacade;

    @EJB
    private InvoiceFacade invoiceFacade;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private EmployeeInformationService employeeInformationService;

    @Inject
    private CustomerService customerService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private Logger logger;

    @Inject
    @LoggedInBusinessAccount
    private Instance<BusinessAccount> businessAccount;

    @Profile
    @Override
    public ProjectInformationResult getProjectInformations(
            @Nonnull final ProjectInformationRequest projectInformationRequest) {
        checkNotNull(projectInformationRequest, "The projectInformationRequest must not be null");

        final String businessAccountId = checkNotNull(businessAccount.get().getAccountId());
        final Map<String, BusinessUnit<?>> businessUnits = getBusinessUnits();
        final ProjectContext projectContext = buildProjectContext(projectInformationRequest, businessUnits);
        final Future<Integer> countRequest = asynchronousService.execute(() -> {
            return projectContextFacade.count(businessAccountId, projectContext);
        });

        final List<Project> projects = projectContextFacade.find(businessAccountId, projectContext);
        final List<String> projectUUIDs = projects
                .stream()
                .map(Project::getUuid)
                .collect(Collectors.toList());

        final Future<Map<String, Budget>> budgetRequests = asynchronousService.execute(() -> {
            return budgetFacade
                    .getBudgets(projectUUIDs)
                    .stream()
                    .collect(Collectors.toMap((budget) -> budget.getProjectLevel().getUuid(), Function.identity()));
        });

        final Future<Map<String, ProjectLevelProgress>> projectLevelProgressRequests = asynchronousService.execute(
                () -> {
                    return projectLevelProgressFacade
                    .getProjectLevelProgresss(projectUUIDs)
                    .stream()
                    .collect(Collectors.toMap((plp) -> plp.getProjectLevel().getUuid(), Function.identity()));
                });

        final Future<Map<String, List<Invoice>>> invoicesRequests = asynchronousService.execute(
                () -> {
                    return invoiceFacade
                    .getInvoices(projectUUIDs)
                    .stream()
                    .collect(Collectors.groupingBy((plp) -> plp.getProjectLevel().getUuid()));
                });

        final List<String> employeeUUIDs = Stream
                .concat(projects.stream().map(Project::getAuthorizedUserUUID),
                        projects.stream().map(Project::getManager))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        final Future<Map<String, Employee>> employeeRequests = asynchronousService.execute(() -> {
            final EmployeeInformationSearchContext searchContext = EmployeeInformationSearchContext
                    .builder()
                    .employeeNumbersOrUuids(employeeUUIDs)
                    .build();
            return employeeInformationService
                    .getEmployeeInformations(searchContext)
                    .getEmployeeInformations()
                    .stream()
                    .map((ei) -> ei.getEmployee())
                    .collect(Collectors.toMap(Employee::getUuid, Function.identity()));
        });

        try {
            final Map<String, Project> projectsByUUIDs = projects
                    .stream()
                    .collect(Collectors.toMap(Project::getUuid, Function.identity()));
            final Integer count = countRequest.get();
            final Map<String, Budget> budgets = budgetRequests.get();
            final Map<String, ProjectLevelProgress> projectLevelProgresses = projectLevelProgressRequests.get();
            final Map<String, List<Invoice>> invoices = invoicesRequests.get();
            final Map<String, Employee> employees = employeeRequests.get();
            final List<ProjectInformation> projectInformations = projects
                    .stream()
                    .map((p) -> Pair.of(createProjectInformationBuilder(p, employees, businessUnits), p.getUuid()))
                    .map((pInfo) -> Pair.of(setBudgets(pInfo.getFirst(), pInfo.getSecond(), budgets), pInfo.getSecond()))
                    .map((pInfo) -> Pair.of(setInvoices(pInfo.getFirst(),
                                                        projectsByUUIDs.get(pInfo.getSecond()),
                                                        invoices), pInfo.getSecond()))
                    .map((pInfo) -> Pair.of(setProjectLevelProgress(pInfo.getFirst(),
                                                                    pInfo.getSecond(),
                                                                    budgets,
                                                                    projectLevelProgresses), pInfo.getSecond()))
                    .map((pInfo) -> Pair.of(setCostsLTD(pInfo.getFirst(),
                                                        projectsByUUIDs.get(pInfo.getSecond())), pInfo.getSecond()))
                    .map((pInfo) -> pInfo.getFirst().build())
                    .collect(Collectors.toList());

            return ProjectInformationResult
                    .builder()
                    .count(count)
                    .projectInformations(projectInformations)
                    .build();
        } catch (final InterruptedException | ExecutionException e) {
            throw Throwables.propagate(e);
        }

    }

    @Nonnull
    private ProjectInformationBuilder createProjectInformationBuilder(@Nonnull final Project project,
                                                                      @Nonnull final Map<String, Employee> employees,
                                                                      @Nonnull final Map<String, BusinessUnit<?>> businessUnits) {
        final ProjectInformationBuilder projectInformationBuilder = ProjectInformation
                .builder()
                .projectUUID(project.getUuid())
                .projectId(project.getId())
                .projectName(project.getName())
                .createdDate(project.getCreatedDate())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .projectType(project.getProjectType())
                .contractType(project.getContractType())
                .customer(getCustomer(project.getCustomerUUID()))
                .customerDepartment(getDepartment(project.getCustomerUUID()))
                .cityLocation(project.getCityLocation())
                .projectAdmin(employees.get(project.getAuthorizedUserUUID()))
                .projectManager(employees.get(project.getManager()))
                .businessUnitUUIDs(project.getBusinessUnitUUIDs());

        final String businessUnitUUID = checkNotNull(project.getBusinessUnitUUID());
        projectInformationBuilder.businessUnitUUID(businessUnitUUID);

        final BusinessUnit<?> businessUnit = businessUnits.get(businessUnitUUID);
        if (businessUnit instanceof CompanyAccount) {
            projectInformationBuilder.companyAccount((CompanyAccount) businessUnit);
        } else if (businessUnit instanceof BusinessDivision) {
            final CompanyAccount companyAccount = checkNotNull((CompanyAccount) businessUnit.getParentUnit());
            projectInformationBuilder.businessDivision((BusinessDivision) businessUnit);
            projectInformationBuilder.companyAccount(companyAccount);
            projectInformationBuilder.businessUnitUUID(companyAccount.getUuid());
        } else if (businessUnit instanceof Branch) {
            projectInformationBuilder.branch((Branch) businessUnit);

            final BusinessUnit<?> parentBusinessUnit = checkNotNull(businessUnit.getParentUnit());
            projectInformationBuilder.businessUnitUUID(parentBusinessUnit.getUuid());

            if (parentBusinessUnit instanceof CompanyAccount) {
                projectInformationBuilder.companyAccount((CompanyAccount) parentBusinessUnit);
            } else {
                final CompanyAccount companyAccount = checkNotNull((CompanyAccount) parentBusinessUnit.getParentUnit());
                projectInformationBuilder.businessDivision((BusinessDivision) parentBusinessUnit);
                projectInformationBuilder.companyAccount(companyAccount);
                projectInformationBuilder.businessUnitUUID(companyAccount.getUuid());
            }
        }

        return projectInformationBuilder;
    }

    @Nullable
    private Customer getCustomer(@Nonnull final String customerUUID) {
        return customerService.getCustomer(customerUUID);
    }

    @Nullable
    private Department getDepartment(@Nonnull final String customerDeptUUID) {
        return departmentService.findDepartment(customerDeptUUID);
    }

    @Nonnull
    private ProjectInformationBuilder setBudgets(@Nonnull final ProjectInformationBuilder projectInformationBuilder,
                                                 @Nonnull final String projectUUID,
                                                 @Nonnull final Map<String, Budget> budgets) {
        final Budget budget = budgets.get(projectUUID);
        if (budget == null) {
            return projectInformationBuilder;
        }

        return projectInformationBuilder
                .budget(budget)
                .budgetAmount(budget.getBudgetedCost())
                .budgetedRevenue(budget.getBudgetedRevenue().getValue());
    }

    @Nonnull
    private ProjectInformationBuilder setProjectLevelProgress(
            @Nonnull final ProjectInformationBuilder projectInformationBuilder,
            @Nonnull final String projectUUID,
            @Nonnull final Map<String, Budget> budgets,
            @Nonnull final Map<String, ProjectLevelProgress> projectLevelProgresses) {
        final ProjectLevelProgress projectLevelProgress = projectLevelProgresses.get(projectUUID);
        final Budget budget = budgets.get(projectUUID);
        if (projectLevelProgress == null || budget == null) {
            return projectInformationBuilder;
        }

        final Amount budgetedRevenue = budget.getBudgetedRevenue();
        final BigDecimal poc = projectLevelProgress.getPercentOfCompletion();
        final Amount revenueLtd = budgetedRevenue.multiply(poc);
        return projectInformationBuilder
                .revenueLTD(revenueLtd.getValue())
                .progress(projectLevelProgress);
    }

    @Nonnull
    private ProjectInformationBuilder setInvoices(@Nonnull final ProjectInformationBuilder projectInformationBuilder,
                                                  @Nonnull final Project project,
                                                  @Nonnull final Map<String, List<Invoice>> invoices) {
        final List<Invoice> projectInvoices = invoices.get(project.getUuid());
        if (projectInvoices == null) {
            return projectInformationBuilder;
        }

        final Amount zero = new Amount(project.getCurrency(), new BigDecimal("0.00"));
        final Amount revenueInvoiced = projectInvoices
                .stream()
                .map(Invoice::getAmount)
                .reduce(zero, (am1, am2) -> am1.add(am2));
        return projectInformationBuilder
                .revenueInvoicedLTD(revenueInvoiced.getValue())
                .invoices(projectInvoices);
    }

    // currently there is no entity to calcuate the cost so setting zero.
    @Nonnull
    private ProjectInformationBuilder setCostsLTD(@Nonnull final ProjectInformationBuilder projectInformationBuilder,
                                                  @Nonnull final Project project) {
        final Amount zero = new Amount(project.getCurrency(), new BigDecimal("0.00"));
        return projectInformationBuilder
                .costLTD(zero);
    }

    @Nonnull
    private ProjectContext buildProjectContext(@Nonnull final ProjectInformationRequest projectInformationRequest,
                                               @Nonnull final Map<String, BusinessUnit<?>> businessUnits) {
        final ProjectContext projectContext = new ProjectContext();
        projectInformationRequest
                .getFilters()
                .forEach((filter, value) -> setFilter(filter, value, projectContext));

        //For business unit requests
        final String businessUnitUUID = projectInformationRequest.getBusinessUnitUUID();
        return projectContext
                .toBuilder()
                .businessUnitUUID(businessUnitUUID)
                .departmentUUID(projectInformationRequest.getDepartmentUUID())
                .build();
    }

    @VisibleForTesting
    void setFilter(@Nonnull final Filter filter,
                   @Nonnull final Object value,
                   @Nonnull final Object context) {
        try {
            if (filter.getFilterDomains().stream().noneMatch((domain) -> domain == FilterDomain.PROJECT)) {
                return;
            }

            final String filterId = filter.getAttribute();
            final Field field = PROJECT_CONTEXT_FIELD_MAPPING.get(filterId);
            if (field == null) {
                logger.warning(format("Unrecognized filter id: %s for Filter Domain: PROJECT", filterId));
                return;
            }

            field.setAccessible(true);
            field.set(context, value);
        } catch (final IllegalArgumentException | IllegalAccessException ex) {
            throw Throwables.propagate(ex);
        }
    }

}
