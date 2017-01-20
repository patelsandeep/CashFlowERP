package com.cashflow.project.frontend.controller.labourexpense;

import com.cashflow.access.authorization.LoggedInCompanyAccount;
import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.accountreceivables.domain.customer.Customer;
import com.cashflow.accountreceivables.service.api.customer.CustomerService;
import com.cashflow.frontend.support.cacheing.requestcached.RequestCached;
import com.cashflow.frontend.support.httpparameter.HttpParameter;
import com.cashflow.project.api.expense.ExpenseReportService;
import com.cashflow.project.api.project.ProjectService;
import com.cashflow.project.api.timesheet.TimeSheetService;
import com.cashflow.project.domain.expenseinformation.ExpenseReportContext;
import com.cashflow.project.domain.expenseinformation.TimesheetContext;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.ProjectType;
import com.cashflow.project.domain.project.expense.ExpenseReport;
import com.cashflow.project.domain.project.expense.LabourExpenseType;
import com.cashflow.project.domain.project.personnel.TeamMemberCategory;
import com.cashflow.project.domain.project.timesheet.ExpenseStatus;
import com.cashflow.project.domain.project.timesheet.TimeSheet;
import com.cashflow.project.frontend.controller.expenses.ExpenseModelConvertor;
import com.cashflow.project.frontend.controller.model.ExpenseReportDataModel;
import com.cashflow.project.frontend.controller.model.ExpenseReportModel;
import com.cashflow.project.frontend.controller.model.ModelViewScopedController;
import com.cashflow.project.frontend.controller.model.SuccessMessageModel;
import com.cashflow.project.frontend.controller.model.TimeSheetEntryModel;
import com.cashflow.project.frontend.controller.timesheet.TimesheetEntryModelConvertor;
import com.cashflow.project.translation.expenses.ExpenseTranslationService;
import com.cashflow.project.translation.expenses.LabourTypeTranslationService;
import com.cashflow.project.translation.timesheet.TimeEntryTranslationService;
import com.cashflow.salestax.api.taxrate.TaxRateRequestContext;
import com.cashflow.salestax.api.taxrate.TaxRateService;
import com.cashflow.salestax.domain.taxrate.TaxRate;
import com.cashflow.useraccount.domain.businessunit.CompanyAccount;
import com.cashflow.useraccount.domain.businessunit.Department;
import com.cashflow.useraccount.service.api.businessunit.department.DepartmentService;
import com.anosym.profiler.Profile;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;

import static com.cashflow.frontend.support.jsfutil.SelectItemHelper.getSelectItems;
import static com.cashflow.frontend.support.jsfutil.SuccessMessageHelper.addSuccessMessage;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_EXPENSE_REPORT_UUID;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_TIMESHEET_UUID;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_UUID;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 *
 * 
 * @since Dec 15, 2016, 12:12:11 PM
 */
@ModelViewScopedController
public class LabourExpenseViewController implements Serializable {

    private static final long serialVersionUID = 4086221046297570409L;

    @Inject
    @HttpParameter(SELECTED_PROJECT_UUID)
    private Instance<String> pUUID;

    @Inject
    @HttpParameter(SELECTED_PROJECT_TIMESHEET_UUID)
    private Instance<String> timeSheetUUID;

    @Inject
    @HttpParameter(SELECTED_PROJECT_EXPENSE_REPORT_UUID)
    private Instance<String> expenseReportUUID;

    @Inject
    @LoggedInCompanyAccount
    private Instance<CompanyAccount> companyAccount;

    @Getter
    @Setter
    private Project project;

    @Inject
    private LabourExpenseEmailProcessor labourExpenseEmailProcessor;

    @Inject
    private ProjectService projectService;

    @Inject
    private LabourTypeTranslationService labourTypeTranslationService;

    @Inject
    private ExpenseTranslationService expenseTranslationService;

    @Inject
    private TimeEntryTranslationService timeEntryTranslationService;

    @Inject
    private TimeSheetService timeSheetService;

    @Inject
    private ExpenseReportService expenseReportService;

    @Inject
    private TaxRateService taxRateService;

    @Inject
    private CustomerService customerService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private TimesheetEntryModelConvertor timesheetEntryModelConvertor;

    @Inject
    private ExpenseModelConvertor expenseModelConvertor;

    @Getter
    @Setter
    private LabourExpenseModel labourExpenseModel;

    @Getter
    @Setter
    private SuccessMessageModel successMessageModel;

    @Getter
    @Setter
    private List<TaxRate> taxRates;

    @Getter
    @Setter
    private TimeSheetEntryModel timeSheetEntryModel;

    @Getter
    @Setter
    private ExpenseReportModel expenseReportModel;

    @PostConstruct
    void initViewLabor() {
        timeSheetEntryModel = new TimeSheetEntryModel();
        expenseReportModel = new ExpenseReportModel();
        final String pUUID_ = pUUID.get();
        if (!isNullOrEmpty(pUUID_)) {
            project = checkNotNull(projectService.getProject(pUUID_), "Failed to load project for uuid: %s",
                                   pUUID_);
            setProjectValues(project);
            setCurrentWeekDays();
        }
        if (!isNullOrEmpty(timeSheetUUID.get())) {
            updateTimesheet();
            RequestContext.getCurrentInstance().execute("PF('view_time_entry').show();");
        } else if (!isNullOrEmpty(expenseReportUUID.get())) {
            updateExpenseReport();
            RequestContext.getCurrentInstance().execute("PF('view_expense_report').show();");
        }

    }

    public void selectType(@Nonnull final String memberUUID,
                           @Nonnull final TeamMemberCategory teamMemberCategory) {
        labourExpenseModel = new LabourExpenseModel();
        checkNotNull(memberUUID, "Member UUID must not be null");
        checkNotNull(teamMemberCategory, "teamMemberCategory must not be null");
        labourExpenseModel.setMemberUUID(memberUUID);
        labourExpenseModel.setTeamMemberCategory(teamMemberCategory);
    }

    @Nonnull
    @RequestCached
    public SelectItem[] getLabourExpenseTypes() {
        return getSelectItems(LabourExpenseType.values(),
                              true,
                              expenseTranslationService.getLaborExpenseTypeLabel(),
                              labourTypeTranslationService::getLabourExpenseTypeLabel);
    }

    @Profile
    public void viewTimesheetReport() {
        final TimesheetContext.TimesheetContextBuilder builder = TimesheetContext
                .builder()
                .projectUUID(pUUID.get());
        if (labourExpenseModel.getTeamMemberCategory() == TeamMemberCategory.EMPLOYEE) {
            builder.teamMemberUUID(labourExpenseModel.getMemberUUID());
        } else {
            builder.subContractorUUID(labourExpenseModel.getMemberUUID());
        }
        final List<TimeSheet> timeSheets = timeSheetService
                .getTimesheets(builder.build());
        labourExpenseModel.setTimsheets(timeSheets);
        if (timeSheets.size() > 0) {
            timeSheetEntryModel.setTimeSheetID(timeSheets.get(0).getTimeSheetID());
        }
        this.updateTimesheetDetail();
    }

    @Profile
    public void viewExpenseReport() {

        final ExpenseReportContext.ExpenseReportContextBuilder builder = ExpenseReportContext
                .builder()
                .projectUUID(pUUID.get());

        final TaxRateRequestContext context = TaxRateRequestContext.builder()
                .country(companyAccount.get().getAddress().getCountry())
                .build();

        if (labourExpenseModel.getTeamMemberCategory() == TeamMemberCategory.EMPLOYEE) {
            builder.teamMemberUUID(labourExpenseModel.getMemberUUID());
        } else {
            builder.subContractorUUID(labourExpenseModel.getMemberUUID());
        }
        final Future<List<TaxRate>> taxRateReq = asynchronousService
                .execute(() -> taxRateService.findTaxRates(context));
        final Future<List<ExpenseReport>> expenseReportsReq = asynchronousService
                .execute(() -> expenseReportService
                .getExpenseReports(builder.build()));
        try {
            taxRates = taxRateReq.get();
            final List<ExpenseReport> expenseReports = expenseReportsReq.get();
            labourExpenseModel.setExpenseReports(expenseReports);
            if (expenseReports.size() > 0) {
                expenseReportModel.setExpenseReportId(expenseReports.get(0).getExpenseReportId());
            }
            this.updateExpenseDetail();
        } catch (InterruptedException | ExecutionException ex) {
            throw Throwables.propagate(ex);
        }

    }

    public void updateTimesheetDetail() {
        final TimeSheet time = labourExpenseModel.getTimsheets()
                .stream()
                .filter((timesheet) -> timeSheetEntryModel.getTimeSheetID()
                .equals(timesheet.getTimeSheetID()))
                .findFirst()
                .orElse(null);
        timesheetEntryModelConvertor
                .setToModel(time, timeSheetEntryModel);
    }

    public void updateExpenseDetail() {
        final ExpenseReport expenseReport = labourExpenseModel.getExpenseReports()
                .stream()
                .filter((report) -> expenseReportModel.getExpenseReportId()
                .equals(report.getExpenseReportId()))
                .findFirst()
                .orElse(null);

        expenseModelConvertor
                .converExpenseDetailsToModel(expenseReport, expenseReportModel);
    }

    public void viewFilesForExpense(@Nonnull final ExpenseReportDataModel expenseReportDataModel) {
        labourExpenseModel.setFileUrls(expenseReportDataModel.getProjectFileUrls());
    }

    public void submitTimesheet() {
        final TimeSheet time = labourExpenseModel.getTimsheets()
                .stream()
                .filter((timesheet) -> timeSheetEntryModel.getTimeSheetID()
                .equals(timesheet.getTimeSheetID()))
                .findFirst()
                .orElse(null);
        time.setStatus(ExpenseStatus.SUBMITTED);
        timeSheetService.create(time);
        timeSheetEntryModel.setTimesheetStatus(ExpenseStatus.SUBMITTED);
        successMessageModel = new SuccessMessageModel();
        successMessageModel.setSuccessMessage(timeEntryTranslationService.getSubmitSuccessMessage());
        addSuccessMessage(timeEntryTranslationService.getSubmitSuccessMessage());
    }

    public void approveTimesheet() {
        final TimeSheet time = labourExpenseModel.getTimsheets()
                .stream()
                .filter((timesheet) -> timeSheetEntryModel.getTimeSheetID()
                .equals(timesheet.getTimeSheetID()))
                .findFirst()
                .orElse(null);
        time.setStatus(ExpenseStatus.APPROVED);
        time.setApprovalDate(Calendar.getInstance());
        timeSheetService.create(time);
        labourExpenseEmailProcessor.sendTimeSheet(time, pUUID.get(), "approve");
        timeSheetEntryModel.setTimesheetStatus(ExpenseStatus.APPROVED);
        successMessageModel = new SuccessMessageModel();
        successMessageModel.setSuccessMessage(timeEntryTranslationService.getApprovedSuccessMessage());
        addSuccessMessage(timeEntryTranslationService.getApprovedSuccessMessage());
    }

    public void rejectTimesheet() {
        final TimeSheet time = labourExpenseModel.getTimsheets()
                .stream()
                .filter((timesheet) -> timeSheetEntryModel.getTimeSheetID()
                .equals(timesheet.getTimeSheetID()))
                .findFirst()
                .orElse(null);
        time.setStatus(ExpenseStatus.SAVED);
        timeSheetService.create(time);
        labourExpenseEmailProcessor.sendTimeSheet(time, pUUID.get(), "reject");
        timeSheetEntryModel.setTimesheetStatus(ExpenseStatus.SAVED);
        successMessageModel = new SuccessMessageModel();
        successMessageModel.setSuccessMessage(timeEntryTranslationService.getRejectSuccessMessage());
        addSuccessMessage(timeEntryTranslationService.getRejectSuccessMessage());
    }

    public void submitExpense() {
        final ExpenseReport expense = labourExpenseModel.getExpenseReports()
                .stream()
                .filter((timesheet) -> expenseReportModel.getExpenseReportId()
                .equals(timesheet.getExpenseReportId()))
                .findFirst()
                .orElse(null);
        expense.setStatus(ExpenseStatus.SUBMITTED);
        expenseReportService.save(expense);
        expenseReportModel.setExpenseReportStatus(ExpenseStatus.SUBMITTED);
        successMessageModel = new SuccessMessageModel();
        successMessageModel.setSuccessMessage(expenseTranslationService.getSubmitSuccessMessage());
        addSuccessMessage(expenseTranslationService.getSubmitSuccessMessage());
    }

    public void approveExpense() {
        final ExpenseReport expense = labourExpenseModel.getExpenseReports()
                .stream()
                .filter((timesheet) -> expenseReportModel.getExpenseReportId()
                .equals(timesheet.getExpenseReportId()))
                .findFirst()
                .orElse(null);
        expense.setStatus(ExpenseStatus.APPROVED);
        expense.setApprovalDate(Calendar.getInstance());
        expenseReportService.save(expense);
        labourExpenseEmailProcessor.sendExpenseReport(expense, pUUID.get(), "approve");
        expenseReportModel.setExpenseReportStatus(ExpenseStatus.APPROVED);
        successMessageModel = new SuccessMessageModel();
        successMessageModel.setSuccessMessage(expenseTranslationService.getApprovedSuccessMessage());
        addSuccessMessage(expenseTranslationService.getApprovedSuccessMessage());
    }

    public void rejectExpense() {
        final ExpenseReport expense = labourExpenseModel.getExpenseReports()
                .stream()
                .filter((timesheet) -> expenseReportModel.getExpenseReportId()
                .equals(timesheet.getExpenseReportId()))
                .findFirst()
                .orElse(null);
        expense.setStatus(ExpenseStatus.SAVED);
        expenseReportService.save(expense);
        labourExpenseEmailProcessor.sendExpenseReport(expense, pUUID.get(), "reject");
        expenseReportModel.setExpenseReportStatus(ExpenseStatus.SAVED);
        successMessageModel = new SuccessMessageModel();
        successMessageModel.setSuccessMessage(expenseTranslationService.getRejectSuccessMessage());
        addSuccessMessage(expenseTranslationService.getRejectSuccessMessage());
    }

    @Profile
    public void updateExpenseReport() {
        final ExpenseReport expenseReport = expenseReportService.getExpenseReport(expenseReportUUID.get());
        labourExpenseModel = new LabourExpenseModel();
        final List<ExpenseReport> expenseReports = ImmutableList.of(expenseReport);
        labourExpenseModel.setExpenseReports(expenseReports);

        final TaxRateRequestContext context = TaxRateRequestContext.builder()
                .country(companyAccount.get().getAddress().getCountry())
                .build();

        taxRates = taxRateService.findTaxRates(context);

        expenseModelConvertor.converExpenseDetailsToModel(expenseReport, expenseReportModel);
    }

    private void updateTimesheet() {
        final TimeSheet time = timeSheetService.getTimesheet(timeSheetUUID.get());
        labourExpenseModel = new LabourExpenseModel();
        final List<TimeSheet> timeSheets = ImmutableList.of(time);
        labourExpenseModel.setTimsheets(timeSheets);
        timesheetEntryModelConvertor
                .setToModel(time, timeSheetEntryModel);
    }

    private void setProjectValues(@Nonnull
            final Project project) {
        timeSheetEntryModel.setProject(project.getName());
        expenseReportModel.setProject(project.getName());
        expenseReportModel.setExpenseCurrency(project.getCurrency());
        if (project.getProjectType() == ProjectType.CUSTOMER_PROJECT) {
            final Customer customer = customerService
                    .getCustomer(project.getCustomerUUID());
            timeSheetEntryModel.setCustomer(customer.getName());
            expenseReportModel.setCustomer(customer.getName());
        }

        if (project.getProjectType() == ProjectType.INTERNAL_PROJECT) {
            final Department department = departmentService
                    .findDepartment(
                            project.getCustomerUUID());
            timeSheetEntryModel.setCustomer(department.getName());
            expenseReportModel.setCustomer(department.getName());
        }
    }

    private void setCurrentWeekDays() {
        final Calendar start = Calendar.getInstance();
        start.set(Calendar.DAY_OF_WEEK, start.getFirstDayOfWeek());
        timeSheetEntryModel.setWeekStartDate(start);
        expenseReportModel.setWeekStartDate(start);
        final Calendar end = Calendar.getInstance();
        end.set(Calendar.DAY_OF_WEEK, end.getFirstDayOfWeek());
        end.add(Calendar.DAY_OF_MONTH, 6);
        timeSheetEntryModel.setWeekEndDate(end);
        expenseReportModel.setWeekEndingDate(end);
    }
}
