package com.cashflow.project.domain.service.expenseinformation;

import com.cashflow.access.authorization.LoggedInBusinessAccount;
import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.accessroles.domain.accessrole.AccessRole;
import com.cashflow.accessroles.service.accessrole.AccessRoleRequestContext;
import com.cashflow.accessroles.service.accessrole.AccessRoleService;
import com.cashflow.accountpayables.domain.supplier.Supplier;
import com.cashflow.accountpayables.domain.supplier.SupplierSearchContext;
import com.cashflow.accountpayables.service.api.supplier.SupplierService;
import com.cashflow.backend.businessaccount.authorization.RequiresBusinessAccount;
import com.cashflow.project.api.expenseinformation.LabourExpenseInformation;
import com.cashflow.project.api.expenseinformation.LabourExpenseInformation.LabourExpenseInformationBuilder;
import com.cashflow.project.api.expenseinformation.LabourExpenseInformationRequest;
import com.cashflow.project.api.expenseinformation.LabourExpenseInformationResult;
import com.cashflow.project.api.expenseinformation.LabourExpenseInformationService;
import com.cashflow.project.domain.expenseinformation.ExpenseReportContext;
import com.cashflow.project.domain.expenseinformation.FilterProperty;
import com.cashflow.project.domain.expenseinformation.TimesheetContext;
import com.cashflow.project.domain.facade.expense.ExpenseReportContextFacade;
import com.cashflow.project.domain.facade.timesheet.TimesheetContextFacade;
import com.cashflow.project.domain.project.expense.ExpenseReport;
import com.cashflow.project.domain.project.expense.LabourExpenseType;
import com.cashflow.project.domain.project.filtering.Filter;
import com.cashflow.project.domain.project.filtering.FilterDomain;
import com.cashflow.project.domain.project.timesheet.TimeSheet;
import com.cashflow.useraccount.domain.businessaccount.BusinessAccount;
import com.cashflow.useraccount.domain.businessuser.EmployeeInformation;
import com.cashflow.useraccount.domain.businessuser.employee.EmployeeInformationSearchContext;
import com.cashflow.useraccount.service.api.businessaccount.employee.EmployeeInformationService;
import com.anosym.profiler.Profile;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.Field;
import java.util.ArrayList;
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
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;

/**
 *
 * 
 * @since Dec 12, 2016, 12:52:38 PM
 */
@Stateless
@Dependent
@RequiresBusinessAccount
public class ExpenseInformationServiceImpl implements LabourExpenseInformationService {

    private static final Map<String, Field> EXPENSE_CONTEXT_FIELD_MAPPING = ImmutableList
            .copyOf(ExpenseReportContext.class.getDeclaredFields())
            .stream()
            .filter((field) -> field.isAnnotationPresent(FilterProperty.class))
            .collect(Collectors.toMap((field) -> {
                final FilterProperty fieldProperty = field.getAnnotation(FilterProperty.class);
                return firstNonNull(emptyToNull(fieldProperty.value()), field.getName());
            }, Function.identity()));

    private static final Map<String, Field> TIMESHEET_CONTEXT_FIELD_MAPPING = ImmutableList
            .copyOf(TimesheetContext.class.getDeclaredFields())
            .stream()
            .filter((field) -> field.isAnnotationPresent(FilterProperty.class))
            .collect(Collectors.toMap((field) -> {
                final FilterProperty fieldProperty = field.getAnnotation(FilterProperty.class);
                return firstNonNull(emptyToNull(fieldProperty.value()), field.getName());
            }, Function.identity()));

    @EJB
    private ExpenseReportContextFacade expenseReportContextFacade;

    @EJB
    private TimesheetContextFacade timesheetContextFacade;

    @Inject
    private EmployeeInformationService employeeInformationService;

    @Inject
    private LabourExpenseInfoBuilder labourExpenseInfoBuilder;

    @Inject
    private SupplierService supplierService;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private AccessRoleService accessRoleService;

    @Inject
    private ProjectLevelUUIDCollector projectLevelUUIDCollector;

    @Inject
    private Logger logger;

    @Inject
    @LoggedInBusinessAccount
    private Instance<BusinessAccount> businessAccount;

    @Profile
    @Override
    @Nonnull
    public LabourExpenseInformationResult getLabourExpenseInformation(
            @Nonnull final LabourExpenseInformationRequest informationRequest) {
        checkNotNull(informationRequest, "The informationRequest must not be null");

        final String businessAccountId = checkNotNull(businessAccount.get().getAccountId());

        String freeText = null;
        String accessRoleUUID = null;
        LabourExpenseType labourExpenseType = null;

        for (Map.Entry<Filter, Object> each : informationRequest.getFilters().entrySet()) {
            if (each.getKey().getAttribute().equals("freeText")) {
                freeText = each.getValue().toString();
            }
            if (each.getKey().getAttribute().equals("accessRoleUUID")) {
                accessRoleUUID = each.getValue().toString();
            }
            if (each.getKey().getAttribute().equals("expenseType")) {
                labourExpenseType = (LabourExpenseType) each.getValue();
            }
        }

        final List<String> levelUUIDs = projectLevelUUIDCollector
                .collectLevelUUIDsForFreeText(freeText, businessAccountId);

        final ExpenseReportContext expenseReportContext = buildExpenseReportContext(informationRequest, levelUUIDs);

        final TimesheetContext timesheetContext = buildTimesheetContext(informationRequest, levelUUIDs);
        Future<List<ExpenseReport>> expensesRequest = null;

        if (null == labourExpenseType || LabourExpenseType.EXPENSE == labourExpenseType) {
            expensesRequest = asynchronousService.execute(() -> {
                return expenseReportContextFacade
                        .find(businessAccountId, expenseReportContext);
            });
        }

        Future<List<TimeSheet>> timeSheetsRequest = null;

        if (null == labourExpenseType || labourExpenseType == LabourExpenseType.TIMESHEET) {
            timeSheetsRequest = asynchronousService.execute(() -> {
                return timesheetContextFacade
                        .find(businessAccountId, timesheetContext);
            });
        }

        final Future<Map<String, AccessRole>> accessRolesRequest = asynchronousService.execute(() -> {
            return accessRoleService.getAccessRoles(AccessRoleRequestContext
                    .builder()
                    .applicationCode("AZPM")
                    .build())
                    .stream()
                    .collect(Collectors.toMap(AccessRole::getUuid, Function.identity()));
        });

        try {

            List<ExpenseReport> expenses = new ArrayList<>();
            List<TimeSheet> timesheets = new ArrayList<>();
            if (null != expensesRequest) {
                expenses = expensesRequest.get();
            }
            if (null != timeSheetsRequest) {
                timesheets = timeSheetsRequest.get();
            }

            final List<String> employeeUUIDs = prepareEmployeeUUIDs(expenses, timesheets);

            final List<String> supplierUUIDs = prepareSupplierUUIDs(expenses, timesheets);

            final Future<Map<String, EmployeeInformation>> employeeRequests = asynchronousService.execute(() -> {
                final EmployeeInformationSearchContext searchContext
                        = EmployeeInformationSearchContext
                        .builder()
                        .employeeNumbersOrUuids(employeeUUIDs)
                        .build();

                return employeeInformationService
                        .getEmployeeInformations(searchContext)
                        .getEmployeeInformations()
                        .stream()
                        .collect(Collectors.toMap((emp) -> emp.getEmployee().getUuid(), Function.identity()));
            });

            final Future<Map<String, Supplier>> supplierRequests = asynchronousService.execute(() -> {
                final SupplierSearchContext searchContext = SupplierSearchContext
                        .builder()
                        .supplierUUIDs(supplierUUIDs)
                        .build();
                return supplierService
                        .findSuppliers(searchContext)
                        .stream()
                        .collect(Collectors.toMap(Supplier::getUuid, Function.identity()));
            });

            //per member expenses
            final Map<String, List<ExpenseReport>> expensesMap = expenses
                    .stream()
                    .collect(Collectors.groupingBy((expense) -> getExpenseMemberUUID(expense)));

            //per member timesheets
            final Map<String, List<TimeSheet>> timeSheetsMap = timesheets
                    .stream()
                    .collect(Collectors.groupingBy((timesheet) -> getTimesheetMemberUUID(timesheet)));

            final Map<String, EmployeeInformation> employees = employeeRequests.get();

            final Map<String, Supplier> suppliers = supplierRequests.get();

            final Map<String, LabourExpenseInformationBuilder> expenseInfo = this
                    .getExpenseInfo(expensesMap, employees, suppliers, accessRolesRequest.get(),
                                    accessRoleUUID);
            final Map<String, LabourExpenseInformationBuilder> timesInfo = this
                    .getTimesheetInfo(expenseInfo, timeSheetsMap, employees, suppliers, accessRolesRequest.get(),
                                      accessRoleUUID);
            timesInfo
                    .entrySet()
                    .stream()
                    .forEach((e) -> {
                        if (expenseInfo.containsKey(e.getKey())) {
                            expenseInfo.remove(e.getKey());
                        }
                    });

            final Map<String, LabourExpenseInformation> labourExpenseInformations
                    = Stream
                    .concat(expenseInfo.entrySet().stream(),
                            timesInfo.entrySet().stream())
                    .collect(Collectors.toMap(
                            entry -> entry.getKey(),
                            entry -> entry.getValue().build()));

            return LabourExpenseInformationResult
                    .builder()
                    .count(labourExpenseInformations.size())
                    .labourExpenseInformations(labourExpenseInformations)
                    .build();

        } catch (InterruptedException | ExecutionException ex) {
            throw Throwables.propagate(ex);
        }
    }

    @Nonnull
    private Map<String, LabourExpenseInformationBuilder> getExpenseInfo(
            @Nonnull final Map<String, List<ExpenseReport>> expensesMap,
            @Nonnull final Map<String, EmployeeInformation> employees,
            @Nonnull final Map<String, Supplier> suppliers,
            @Nonnull final Map<String, AccessRole> accessRoles,
            @Nullable final String accessRoleUUID) {
        return expensesMap.entrySet()
                .stream()
                .filter((entry) -> checkRoleFilterExpense(entry.getValue(),
                                                          accessRoleUUID))
                .collect(Collectors.toMap((report) -> report.getKey(),
                                          (report) -> labourExpenseInfoBuilder
                                          .preapreInfo(report.getValue(),
                                                       employees,
                                                       suppliers,
                                                       accessRoles)));
    }

    @Nonnull
    private Map<String, LabourExpenseInformationBuilder> getTimesheetInfo(
            @Nonnull final Map<String, LabourExpenseInformationBuilder> expenseInfo,
            @Nonnull final Map<String, List<TimeSheet>> timesMap,
            @Nonnull final Map<String, EmployeeInformation> employees,
            @Nonnull final Map<String, Supplier> suppliers,
            @Nonnull final Map<String, AccessRole> accessRoles,
            @Nullable final String accessRoleUUID) {
        return timesMap.entrySet()
                .stream()
                .filter((entry) -> checkRoleFilterTimesheet(entry.getValue(),
                                                            accessRoleUUID))
                .collect(Collectors.toMap((report) -> report.getKey(),
                                          (report) -> expenseInfo.containsKey(report.getKey())
                                          ? labourExpenseInfoBuilder
                                          .preapreOnlyTimeHours(report.getValue(),
                                                                expenseInfo.get(report.getKey()))
                                          : labourExpenseInfoBuilder
                                          .prepareAllTimeInfo(report.getValue(),
                                                              employees,
                                                              suppliers,
                                                              accessRoles)));
    }

    @Nonnull
    private List<String> prepareEmployeeUUIDs(@Nonnull final List<ExpenseReport> expenses,
                                              @Nonnull final List<TimeSheet> timeSheets) {
        return Stream
                .concat(expenses
                        .stream()
                        .map((report) -> null != report.getTeamMember()
                                ? report.getTeamMember().getEmployeeUUID() : null),
                        timeSheets
                        .stream()
                        .map((ts) -> null != ts.getTeamMember()
                                ? ts.getTeamMember().getEmployeeUUID() : null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Nonnull
    private List<String> prepareSupplierUUIDs(@Nonnull final List<ExpenseReport> expenses,
                                              @Nonnull final List<TimeSheet> timeSheets) {
        return Stream
                .concat(expenses
                        .stream()
                        .map((report) -> null != report.getSubContractor()
                                ? report.getSubContractor().getSubContractorUUID() : null),
                        timeSheets
                        .stream()
                        .map((ts) -> null != ts.getSubContractor()
                                ? ts.getSubContractor().getSubContractorUUID() : null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Nonnull
    private String getExpenseMemberUUID(@Nonnull final ExpenseReport expenseReport) {
        if (expenseReport.getTeamMember() != null) {
            return expenseReport.getTeamMember().getUuid();
        } else {
            return expenseReport.getSubContractor().getUuid();
        }
    }

    @Nonnull
    private String getTimesheetMemberUUID(@Nonnull final TimeSheet timeSheet) {
        if (timeSheet.getTeamMember() != null) {
            return timeSheet.getTeamMember().getUuid();
        } else {
            return timeSheet.getSubContractor().getUuid();
        }
    }

    @Nonnull
    private ExpenseReportContext buildExpenseReportContext(
            @Nonnull final LabourExpenseInformationRequest informationRequest,
            @Nonnull final List<String> levelUUIDs) {
        final ExpenseReportContext reportContext = new ExpenseReportContext();
        informationRequest
                .getFilters()
                .forEach((filter, value) -> setExpenseFilter(filter, value, reportContext));

        return reportContext
                .toBuilder()
                .projectLevelUUIDs(levelUUIDs)
                .build();
    }

    @Nonnull
    private TimesheetContext buildTimesheetContext(
            @Nonnull final LabourExpenseInformationRequest informationRequest,
            @Nonnull final List<String> levelUUIDs) {
        final TimesheetContext timesheetContext = new TimesheetContext();
        informationRequest
                .getFilters()
                .forEach((filter, value) -> setTimesheetFilter(filter, value, timesheetContext));

        return timesheetContext
                .toBuilder()
                .projectLevelUUIDs(levelUUIDs)
                .build();
    }

    private void setExpenseFilter(@Nonnull final Filter filter,
                                  @Nonnull final Object value,
                                  @Nonnull final Object context) {
        try {
            if (filter.getFilterDomains().stream().noneMatch(
                    (domain) -> domain == FilterDomain.EXPENSE_REPORT_INFORMATION)) {
                return;
            }

            final String filterId = filter.getAttribute();
            final Field field = EXPENSE_CONTEXT_FIELD_MAPPING.get(filterId);
            if (field == null) {
                logger.warning(format("Unrecognized filter id: %s for Filter Domain: ExpenseReport", filterId));
                return;
            }

            field.setAccessible(true);
            field.set(context, value);
        } catch (final IllegalArgumentException | IllegalAccessException ex) {
            throw Throwables.propagate(ex);
        }
    }

    private void setTimesheetFilter(@Nonnull final Filter filter,
                                    @Nonnull final Object value,
                                    @Nonnull final Object context) {
        try {
            if (filter.getFilterDomains().stream().noneMatch(
                    (domain) -> domain == FilterDomain.EXPENSE_REPORT_INFORMATION)) {
                return;
            }

            final String filterId = filter.getAttribute();
            final Field field = TIMESHEET_CONTEXT_FIELD_MAPPING.get(filterId);
            if (field == null) {
                logger.warning(format("Unrecognized filter id: %s for Filter Domain: Timesheet", filterId));
                return;
            }

            field.setAccessible(true);
            field.set(context, value);
        } catch (final IllegalArgumentException | IllegalAccessException ex) {
            throw Throwables.propagate(ex);
        }
    }

    private boolean checkRoleFilterExpense(@Nonnull final List<ExpenseReport> reports,
                                           @Nullable final String roleUUID) {
        if (isNullOrEmpty(roleUUID)) {
            return true;
        }
        reports
                .stream()
                .anyMatch(report -> (report.getTeamMember() != null)
                        ? report.getTeamMember()
                        .getProjectRoles()
                        .stream()
                        .anyMatch((role) -> (role.getAccessScopeUUID().equals(roleUUID)))
                        : report.getSubContractor()
                        .getProjectRoles()
                        .stream()
                        .anyMatch((role) -> (role.getAccessScopeUUID().equals(roleUUID))));

        return false;
    }

    private boolean checkRoleFilterTimesheet(@Nonnull final List<TimeSheet> reports,
                                             @Nullable final String roleUUID) {
        if (isNullOrEmpty(roleUUID)) {
            return true;
        }
        reports
                .stream()
                .anyMatch(report -> (report.getTeamMember() != null)
                        ? report.getTeamMember()
                        .getProjectRoles()
                        .stream()
                        .anyMatch((role) -> (role.getAccessScopeUUID().equals(roleUUID)))
                        : report.getSubContractor()
                        .getProjectRoles()
                        .stream()
                        .anyMatch((role) -> (role.getAccessScopeUUID().equals(roleUUID))));
        return false;
    }
}
