package com.cashflow.project.frontend.controller.expenses;

import com.cashflow.accountpayables.domain.supplier.ContactPerson;
import com.cashflow.accountpayables.domain.supplier.Supplier;
import com.cashflow.accountpayables.service.api.supplier.SupplierService;
import com.cashflow.frontend.support.ApplicationUrlService;
import com.cashflow.project.api.expense.ExpenseReportService;
import com.cashflow.project.config.projectrole.ProjectProfessionalTypeMappingConfigurationService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.expense.ExpenseReport;
import com.cashflow.project.domain.project.timesheet.ExpenseStatus;
import com.cashflow.project.domain.util.menuoption.MenuOption;
import com.cashflow.project.frontend.controller.model.SuccessMessageModel;
import com.cashflow.project.frontend.service.urlbuilder.StaticLinkUrlBuilder;
import com.cashflow.project.frontend.service.urlbuilder.UrlContext;
import com.cashflow.project.translation.expenses.PendingExpenseTranslationService;
import com.cashflow.project.translation.project.tabtitle.TabTitle;
import com.cashflow.useraccount.domain.businessuser.Employee;
import com.cashflow.useraccount.domain.businessuser.professional.Professional;
import com.cashflow.useraccount.service.api.EmployeeService;
import com.cashflow.useraccount.service.api.professional.ProfessionalService;
import com.anosym.email.service.EmailService;
import com.anosym.profiler.Profile;
import com.anosym.urlbuilder.QueryParameter;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;

import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_MENU;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_EXPENSE_REPORT_UUID;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_SELECTED_TAB;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_UUID;

/**
 *
 * 
 * @since 12 Jan, 2017, 4:25:11 PM
 */
@Dependent
public class PendingExpenseProcessor implements Serializable {

    private static final long serialVersionUID = -32980610236744946L;

    private static final String VIEW_PROJECT_EXPENSE_URL = "/projects/project.xhtml";

    @Inject
    private PendingExpenseTranslationService pendingExpenseTranslationService;

    @Inject
    private ApplicationUrlService applicationUrlService;

    @Inject
    private ExpenseReportService expenseReportService;

    @Inject
    private StaticLinkUrlBuilder staticLinkUrlBuilder;

    @Inject
    private EmailService emailService;

    @Inject
    private EmployeeService employeeService;

    @Inject
    private SupplierService supplierService;

    @Inject
    private ProfessionalService professionalService;

    @Inject
    private ProjectProfessionalTypeMappingConfigurationService projectProfessionalTypeMappingConfigurationService;

    @Getter
    @Setter
    @Nullable
    private String employeeEmail;

    @Profile
    public void approvePendingExpense(@Nonnull final List<ExpenseReport> expenseReports,
                                      @Nonnull SuccessMessageModel successMessageModel,
                                      @Nonnull final Project project,
                                      @Nonnull final String action) {

        expenseReports.forEach((expense) -> {
            final String message;

            final String expenseURL = viewExpenseReport(expense.getUuid(), project.getUuid()).startsWith("/") ? viewExpenseReport(
                    expense.getUuid(), project.getUuid()).substring(1) : viewExpenseReport(expense.getUuid(), project
                                                                                           .getUuid());
            if (action.equals("approve")) {
                expense.setStatus(ExpenseStatus.APPROVED);
                message = pendingExpenseTranslationService.getYourExpenseReportLabel()
                        + " <a href = \"" + expenseURL + "\">"
                        + expense.getExpenseReportId()
                        + "</a>\n"
                        + pendingExpenseTranslationService.getYourExpenseReportApprovedLabel();

            } else {
                expense.setStatus(ExpenseStatus.SAVED);
                message = pendingExpenseTranslationService.getYourExpenseReportLabel()
                        + " <a href = \"" + expenseURL + "\">"
                        + expense.getExpenseReportId()
                        + "</a>\n"
                        + pendingExpenseTranslationService
                                .getYourExpenseReportRejectedLabel();

            }
            expense.setApprovalDate(Calendar.getInstance());
            getEmail(expense);
            expenseReportService.save(expense);
            emailService.sendHtmlMail(pendingExpenseTranslationService.getExpenseDetailsLabel(),
                                      message,
                                      employeeEmail);
        });

    }

    @Nonnull
    public String viewExpenseReport(@Nonnull final String uuid,
                                    @Nonnull final String projectUUID) {
        final String applicationURL = applicationUrlService.getApplicationUrl();
        final String url = applicationURL.concat(VIEW_PROJECT_EXPENSE_URL);
        final List<QueryParameter<?>> projectUUIDParam = ImmutableList
                .<QueryParameter<?>>builder()
                .add(buildQueryParameter(SELECTED_PROJECT_SELECTED_TAB, TabTitle.TIME_AND_EXPENSES.name()))
                .add(buildQueryParameter(SELECTED_PROJECT_UUID, projectUUID))
                .add(buildQueryParameter(SELECTED_MENU, MenuOption.PROJECTS.name()))
                .add(buildQueryParameter(SELECTED_PROJECT_EXPENSE_REPORT_UUID, uuid))
                .build();
        final UrlContext urlContext = UrlContext
                .builder()
                .additionalParameters(projectUUIDParam)
                .forceFacesRedirect(true)
                .path(url)
                .build();
        return staticLinkUrlBuilder.buildURL(urlContext);

    }

    @Nonnull
    public QueryParameter<String> buildQueryParameter(@Nonnull final String parameter, @Nonnull String parameterValue) {
        return QueryParameter
                .<String>builder()
                .parameter(parameter)
                .parameterValue(parameterValue)
                .build();
    }

    private void getEmail(@Nonnull final ExpenseReport expenseReport) {

        if (null != expenseReport.getTeamMember()) {
            final Employee employee = employeeService.findEmployee(expenseReport.getTeamMember().getEmployeeUUID());
            if (null != employee) {
                this.setEmployeeEmail(employee.getAddress().getEmailAddress());
            } else {
                final List<Professional> professionals = professionalService.findProfessionals(
                        projectProfessionalTypeMappingConfigurationService
                                .getTypeMappingsForProfessionals());

                professionals.stream()
                        .filter((professional) -> (professional.getUuid().equals(expenseReport.getTeamMember()
                        .getEmployeeUUID())))
                        .forEach((professional) -> {
                            this.setEmployeeEmail(professional.getAddress().getEmailAddress());
                        });
            }
        } else {
            getSubContractor(expenseReport);
        }
    }

    private void getSubContractor(@Nonnull final ExpenseReport expenseReport) {
        final Supplier supplier = supplierService.findSupplier(expenseReport.getSubContractor().getSubContractorUUID());
        if (supplier.getUuid().equals(expenseReport.getSubContractor().getSubContractorUUID())) {
            final List<ContactPerson> contactPersons = supplier.getOtherContactPersons();
            contactPersons.add(supplier.getContactPerson());
            contactPersons.stream()
                    .filter((cp) -> (cp.getUuid().equals(expenseReport.getSubContractor().getMemberName())))
                    .forEachOrdered((cp) -> {
                        this.setEmployeeEmail(cp.getAddress().getEmailAddress());
                    });
        }
    }
}
