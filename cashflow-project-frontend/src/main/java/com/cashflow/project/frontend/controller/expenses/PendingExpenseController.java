package com.cashflow.project.frontend.controller.expenses;

import com.cashflow.accountpayables.domain.supplier.ContactPerson;
import com.cashflow.accountpayables.domain.supplier.Supplier;
import com.cashflow.accountpayables.domain.supplier.SupplierSearchContext;
import com.cashflow.accountpayables.service.api.supplier.SupplierService;
import com.cashflow.project.api.expense.ExpenseReportService;
import com.cashflow.project.domain.expenseinformation.ExpenseReportContext;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.expense.BillableType;
import com.cashflow.project.domain.project.expense.ExpenseDetail;
import com.cashflow.project.domain.project.expense.ExpenseReport;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.timesheet.ExpenseStatus;
import com.cashflow.project.domain.util.menuoption.MenuOption;
import com.cashflow.project.frontend.controller.model.ModelViewScopedController;
import com.cashflow.project.frontend.controller.model.PendingExpenseModel;
import com.cashflow.project.frontend.controller.model.SuccessMessageModel;
import com.cashflow.project.frontend.service.urlbuilder.StaticLinkUrlBuilder;
import com.cashflow.project.frontend.service.urlbuilder.UrlContext;
import com.cashflow.project.translation.expenses.PendingExpenseTranslationService;
import com.cashflow.useraccount.domain.businessuser.EmployeeInformation;
import com.cashflow.useraccount.domain.businessuser.employee.EmployeeInformationSearchContext;
import com.cashflow.useraccount.service.api.businessaccount.employee.EmployeeInformationService;
import com.anosym.common.Amount;
import com.anosym.profiler.Profile;
import com.anosym.urlbuilder.QueryParameter;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;

import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_MEMBER_UUID;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_MENU;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_UUID;

/**
 *
 * 
 * @since 4 Jan, 2017, 1:00:22 PM
 */
@ModelViewScopedController
public class PendingExpenseController implements Serializable {

    private static final String PROJECT_APPROVE_EXPENSE_URL = "/expenses/pending-expense-list.xhtml";

    private static final String PROJECT_EXPENSE_URL = "/expenses/expenses.xhtml";

    private static final String PROJECT_APPROVE_EXPENSE_DETAIL_URL = "/expenses/pending-expense-detail.xhtml";

    private static final long serialVersionUID = 1207742156797600231L;

    @Inject
    private StaticLinkUrlBuilder staticLinkUrlBuilder;

    @Inject
    private SupplierService supplierService;

    @Inject
    private ExpenseReportService expenseReportService;

    @Inject
    private PendingExpenseProcessor pendingExpenseProcessor;

    @Inject
    private PendingExpenseTranslationService pendingExpenseTranslationService;

    @Getter
    @Setter
    private SuccessMessageModel successMessageModel;

    @Getter
    @Setter
    @Nonnull
    private List<Calendar> dates = new ArrayList<>();

    @Getter
    @Setter
    @Nullable
    private List<Supplier> suppliers = new ArrayList<>();

    @Getter
    @Setter
    @Nullable
    private String sortBy = "People";

    @Getter
    @Setter
    @Nullable
    private Project project;

    @Inject
    private EmployeeInformationService employeeInformationService;

    @Getter
    @Setter
    @Nullable
    private Map<String, EmployeeInformation> users;

    @Getter
    @Setter
    @Nullable
    private Map<String, PendingExpenseModel> pendingExpenseInfo;

    @Getter
    @Setter
    @Nullable
    private Map<String, Boolean> checked = new HashMap<>();

    private Map<String, ProjectLevel<?>> levels;

    @Nullable
    @Profile
    public Map<String, PendingExpenseModel> getPendingExpenseReport() {

        final ExpenseReportContext expenseReportContext = ExpenseReportContext
                .builder()
                .status(ExpenseStatus.SUBMITTED)
                .build();

        final List<ExpenseReport> expenses = expenseReportService.getExpenseReports(expenseReportContext);
        Map<String, List<ExpenseReport>> expensesMap = new HashMap<>();
        if (sortBy.equals("People")) {
            expensesMap = expenses
                    .stream()
                    .collect(Collectors.groupingBy((expense) -> getExpenseMemberUUID(expense)));
        } else {
            expensesMap = expenses
                    .stream()
                    .collect(Collectors.groupingBy((expense) -> getExpenseProjectUUID(expense)));
        }
        final List<PendingExpenseModel> pendingExpenseList = this.getExpenseInfo(expensesMap);

        if (sortBy.equals("People")) {
            pendingExpenseInfo = pendingExpenseList
                    .stream()
                    .collect(Collectors.toMap((model) -> model.getMemberUUID(),
                                              (model) -> prepareValue(model, model.getMemberUUID())));

        } else {
            pendingExpenseInfo = pendingExpenseList
                    .stream()
                    .collect(Collectors.toMap((model) -> model.getProjectUUID(),
                                              (model) -> prepareValue(model, model.getProjectUUID())));
        }

        checked = pendingExpenseInfo
                .entrySet()
                .stream()
                .collect(Collectors.toMap((model) -> model.getKey(),
                                          (model) -> model.getValue().isCheck()));

        return pendingExpenseInfo;
    }

    @Nonnull
    public PendingExpenseModel prepareAllExpenseInfo(@Nonnull final List<ExpenseReport> expenseReports) {

        final PendingExpenseModel info = new PendingExpenseModel();

        loadMembers(expenseReports);
        loadSuppliers(expenseReports);

        BigDecimal billableAmount = BigDecimal.ZERO;
        BigDecimal nonBillableAmount = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (ExpenseReport report : expenseReports) {
            levels = new HashMap<>();
            setProjectLevels(report.getProjectLevel());
            levels
                    .entrySet()
                    .forEach(level -> {
                        if (level.getValue() instanceof Project) {
                            info.setProjectUUID(((Project) level.getValue()).getUuid());
                            info.setProject(((Project) level.getValue()).getName());
                        }
                    });
            info.setExpenseUUID(report.getUuid());
            if (null != report.getTeamMember()) {
                final EmployeeInformation employeeInfo = users.get(report.getTeamMember().getEmployeeUUID());

                info.setMember(employeeInfo.getEmployee().getName());
                info.setMemberUUID(report.getTeamMember().getUuid());
            } else {
                for (Supplier supplier : suppliers) {
                    if (supplier.getUuid().equals(report.getSubContractor().getSubContractorUUID())) {
                        final List<ContactPerson> contactPersons = supplier.getOtherContactPersons();
                        contactPersons.add(supplier.getContactPerson());
                        contactPersons.stream()
                                .filter((cp) -> (cp.getUuid().equals(report.getSubContractor().getMemberName())))
                                .forEachOrdered((cp) -> {
                                    info.setMember(cp.getName());
                                });
                    }
                }
                info.setMemberUUID(report.getSubContractor().getUuid());
            }

            for (ExpenseDetail expenseInfo : report.getExpenseDetails()) {
                dates.add(expenseInfo.getExpenseDate());
                if (expenseInfo.getBillable() == BillableType.YES) {
                    billableAmount = billableAmount.add(expenseInfo.getTotalExpense().getValue());
                }
                if (expenseInfo.getBillable() == BillableType.NO) {
                    nonBillableAmount = nonBillableAmount.add(expenseInfo.getTotalExpense().getValue());
                }
                info.setCurrency(expenseInfo.getTotalExpense().getCurrency());
            }

            info.setExpenseDate(Collections.min(dates));
            info.setToExpenseDate(Collections.max(dates));

            info.setNonBillableExpense(new Amount(info.getCurrency(), nonBillableAmount).scale(2));
            info.setBillableExpense(new Amount(info.getCurrency(), billableAmount).scale(2));
            totalAmount = billableAmount.add(nonBillableAmount);
            info.setTotalExpense(new Amount(info.getCurrency(), totalAmount).scale(2));
        }
        return info;
    }

    @Nonnull
    public String redirectApproveExpense() {
        final List<QueryParameter<?>> projectUUIDParam = ImmutableList
                .<QueryParameter<?>>builder()
                .add(buildQueryParameter(SELECTED_MENU, MenuOption.EXPENSES.name()))
                .build();

        final UrlContext.Builder context = UrlContext
                .builder()
                .additionalParameters(projectUUIDParam)
                .path(PROJECT_APPROVE_EXPENSE_URL)
                .forceFacesRedirect(true);
        return staticLinkUrlBuilder.buildURL(context.build());
    }

    public void setPeopleSorting() {
        this.setSortBy("People");
    }

    public void setProjectSorting() {
        this.setSortBy("Project");
    }

    @Profile
    public void approvePendingExpense() {
        if (null == checked && checked.containsValue(false)) {
            return;
        }
        final Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap();
        final String action = params.get("action");
        final List<ExpenseReport> expenseReports;
        if (sortBy.equals("People")) {
            final List<String> peopleUUID = pendingExpenseInfo.values()
                    .stream()
                    .filter((member) -> checked.get(member.getUuid()))
                    .map((teammember) -> teammember.getUuid())
                    .collect(Collectors.toList());

            final ExpenseReportContext expenseReportContext = ExpenseReportContext
                    .builder()
                    .peopleUUIDs(peopleUUID)
                    .build();
            expenseReports = expenseReportService.getExpenseReports(expenseReportContext);
        } else {
            final List<String> expenseUUIDs = pendingExpenseInfo.values()
                    .stream()
                    .filter((member) -> checked.get(member.getUuid()))
                    .map((teammember) -> teammember.getExpenseUUID())
                    .collect(Collectors.toList());

            final ExpenseReportContext expenseReportContext = ExpenseReportContext
                    .builder()
                    .expenseUUIDs(expenseUUIDs)
                    .build();
            expenseReports = expenseReportService.getExpenseReports(expenseReportContext);
        }
        pendingExpenseProcessor.approvePendingExpense(expenseReports, successMessageModel, project, action);
        successMessageModel = new SuccessMessageModel();
        if (action.equals("approve")) {
            successMessageModel.setSuccessMessage(pendingExpenseTranslationService
                    .getApprovedSuccessMessage());
        } else {
            successMessageModel.setSuccessMessage(pendingExpenseTranslationService
                    .getRejectedSuccessMessage());
        }
    }

    @Nonnull
    public String redirectExpense() {
        final QueryParameter parameter = QueryParameter
                .<String>builder()
                .parameter(SELECTED_MENU)
                .parameterValue(MenuOption.EXPENSES.name())
                .build();
        final UrlContext.Builder context = UrlContext
                .builder()
                .additionalParameter(parameter)
                .path(PROJECT_EXPENSE_URL)
                .forceFacesRedirect(true);
        return staticLinkUrlBuilder.buildURL(context.build());
    }

    @Nonnull
    public String buildExpenseDetailUrl(@Nonnull final String uuid) {
        final List<QueryParameter<?>> pendingExpenseUUIDParams;
        if (sortBy.equals("People")) {
            pendingExpenseUUIDParams = ImmutableList
                    .<QueryParameter<?>>builder()
                    .add(buildQueryParameter(SELECTED_MENU, MenuOption.EXPENSES.name()))
                    .add(buildQueryParameter(SELECTED_MEMBER_UUID, uuid))
                    .build();

        } else {
            pendingExpenseUUIDParams = ImmutableList
                    .<QueryParameter<?>>builder()
                    .add(buildQueryParameter(SELECTED_MENU, MenuOption.EXPENSES.name()))
                    .add(buildQueryParameter(SELECTED_PROJECT_UUID, uuid))
                    .build();

        }

        final UrlContext urlContext = UrlContext
                .builder()
                .additionalParameters(pendingExpenseUUIDParams)
                .forceFacesRedirect(true)
                .path(PROJECT_APPROVE_EXPENSE_DETAIL_URL)
                .build();
        return staticLinkUrlBuilder.buildURL(urlContext);
    }

    @Nonnull
    private QueryParameter<String> buildQueryParameter(@Nonnull final String parameter, @Nonnull String parameterValue) {
        return pendingExpenseProcessor.buildQueryParameter(parameter, parameterValue);
    }

    private void setProjectLevels(@Nullable final ProjectLevel<?> level) {
        if (level == null) {
            return;
        }
        levels.put(level.getUuid(), level);
        setProjectLevels(level.getParentLevel());
    }

    @Nonnull
    private String getExpenseMemberUUID(@Nonnull final ExpenseReport expenseReport) {
        levels = new HashMap<>();
        setProjectLevels(expenseReport.getProjectLevel());
        levels
                .entrySet()
                .forEach(level -> {
                    if (level.getValue() instanceof Project) {
                        project = (Project) level.getValue();
                    }
                });
        if (expenseReport.getTeamMember() != null) {
            return expenseReport.getTeamMember().getUuid();
        } else {
            return expenseReport.getSubContractor().getUuid();
        }
    }

    @Nonnull
    private String getExpenseProjectUUID(@Nonnull final ExpenseReport expenseReport) {
        levels = new HashMap<>();
        setProjectLevels(expenseReport.getProjectLevel());

        for (ProjectLevel projectLevel : levels.values()) {
            if (projectLevel instanceof Project) {
                project = (Project) projectLevel;
                return project.getUuid();
            }
        }
        return null;

    }

    @Nonnull
    private PendingExpenseModel prepareValue(@Nonnull final PendingExpenseModel pendingExpenseModel,
                                             @Nonnull final String uuid) {
        pendingExpenseModel.setUuid(uuid);
        pendingExpenseModel.setCheck(false);
        return pendingExpenseModel;
    }

    @Nonnull
    private List<PendingExpenseModel> getExpenseInfo(@Nonnull final Map<String, List<ExpenseReport>> expenseMap) {
        return expenseMap.entrySet()
                .stream()
                .map((report) -> this.prepareAllExpenseInfo(report.getValue()))
                .collect(Collectors.toList());
    }

    private void loadMembers(@Nonnull final List<ExpenseReport> expenseReports) {
        final List<String> employeeUUIDs = expenseReports
                .stream()
                .filter((report) -> null != report.getTeamMember())
                .map((teammember) -> teammember.getTeamMember().getEmployeeUUID())
                .collect(Collectors.toList());

        final EmployeeInformationSearchContext searchContext = EmployeeInformationSearchContext
                .builder()
                .employeeNumbersOrUuids(employeeUUIDs)
                .build();
        final List<EmployeeInformation> employees = employeeInformationService.getEmployeeInformations(searchContext)
                .getEmployeeInformations();
        users = employees
                .stream()
                .collect(Collectors.toMap((ei) -> ei.getEmployee().getUuid(), Function.identity()));
    }

    private void loadSuppliers(@Nonnull final List<ExpenseReport> expenseReports) {
        final List<String> supplierUUIDs = expenseReports
                .stream()
                .filter((sheet) -> null != sheet.getSubContractor())
                .map((sc) -> sc.getSubContractor().getSubContractorUUID())
                .collect(Collectors.toList());
        final SupplierSearchContext searchContext = SupplierSearchContext.builder()
                .supplierUUIDs(supplierUUIDs)
                .offset(0)
                .limit(100)
                .build();

        suppliers = supplierService.findSuppliers(searchContext);
    }

}
