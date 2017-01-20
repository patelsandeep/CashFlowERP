package com.cashflow.project.frontend.controller.labourexpense;

import com.cashflow.accountpayables.domain.supplier.ContactPerson;
import com.cashflow.accountpayables.domain.supplier.Supplier;
import com.cashflow.accountpayables.service.api.supplier.SupplierService;
import com.cashflow.frontend.support.ApplicationUrlService;
import com.cashflow.project.config.projectrole.ProjectProfessionalTypeMappingConfigurationService;
import com.cashflow.project.domain.project.expense.ExpenseReport;
import com.cashflow.project.domain.project.timesheet.TimeSheet;
import com.cashflow.project.frontend.service.urlbuilder.StaticLinkUrlBuilder;
import com.cashflow.project.frontend.service.urlbuilder.UrlContext;
import com.cashflow.project.translation.expenses.ExpenseTranslationService;
import com.cashflow.project.translation.project.tabtitle.TabTitle;
import com.cashflow.project.translation.timesheet.TimeEntryTranslationService;
import com.cashflow.useraccount.domain.businessuser.Employee;
import com.cashflow.useraccount.domain.businessuser.professional.Professional;
import com.cashflow.useraccount.service.api.EmployeeService;
import com.cashflow.useraccount.service.api.professional.ProfessionalService;
import com.anosym.email.service.EmailService;
import com.anosym.profiler.Profile;
import com.anosym.urlbuilder.QueryParameter;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_EXPENSE_REPORT_UUID;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_SELECTED_TAB;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_TIMESHEET_UUID;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_UUID;

/**
 *
 * 
 * @since 3 Jan, 2017, 2:18:21 PM
 */
@Dependent
public class LabourExpenseEmailProcessor implements Serializable {

    private static final long serialVersionUID = -5007571336605941566L;

    private static final String VIEW_PROJECT_TIMESHEET_URL = "/projects/project.xhtml";

    @Inject
    private EmailService emailService;

    @Inject
    private ApplicationUrlService applicationUrlService;

    @Inject
    private ProfessionalService professionalService;

    @Inject
    private EmployeeService employeeService;

    @Inject
    private ProjectProfessionalTypeMappingConfigurationService projectProfessionalTypeMappingConfigurationService;

    @Inject
    private ExpenseTranslationService expenseTranslationService;

    @Inject
    private TimeEntryTranslationService timeEntryTranslationService;

    @Inject
    private SupplierService supplierService;

    @Inject
    private StaticLinkUrlBuilder staticLinkUrlBuilder;

    @Profile
    public void sendTimeSheet(@Nonnull final TimeSheet timeSheet,
                              @Nonnull final String projectUUID,
                              @Nonnull final String status) {
        final String timeSheetURL = buildTimesheetViewUrl(timeSheet.getUuid(), projectUUID).startsWith("/")
                ? buildTimesheetViewUrl(timeSheet.getUuid(), projectUUID).substring(1)
                : buildTimesheetViewUrl(timeSheet.getUuid(), projectUUID);

        if (status.equals("reject")) {
            final StringBuilder message = new StringBuilder(timeEntryTranslationService.getYourTimeSheetReportLabel())
                    .append(" <a href = \"" + timeSheetURL + "\">")
                    .append(timeSheet.getTimeSheetID())
                    .append("</a>\n").append(timeEntryTranslationService
                    .getYourTimeSheetReportRejectedLabel());

            emailService.sendHtmlMail(expenseTranslationService.getExpenseReportLabel(),
                                      message.toString(),
                                      getEmployeeEmailId(timeSheet));
        } else {
            final StringBuilder message = new StringBuilder(timeEntryTranslationService.getYourTimeSheetReportLabel())
                    .append(" <a href = \"" + timeSheetURL + "\">")
                    .append(timeSheet.getTimeSheetID()).append(timeEntryTranslationService
                    .getYourTimeSheetReportApprovedLabel());

            emailService.sendHtmlMail(expenseTranslationService.getExpenseReportLabel(),
                                      message.toString(),
                                      getEmployeeEmailId(timeSheet));

        }
    }

    @Nonnull
    public String buildTimesheetViewUrl(@Nonnull final String uuid,
                                        @Nonnull final String projectUUID) {
        final String applicationURL = applicationUrlService.getApplicationUrl();
        final String url = applicationURL.concat(VIEW_PROJECT_TIMESHEET_URL);
        final List<QueryParameter<?>> projectUUIDParam = ImmutableList
                .<QueryParameter<?>>builder()
                .add(buildQueryParameter(SELECTED_PROJECT_SELECTED_TAB, TabTitle.TIME_AND_EXPENSES.name()))
                .add(buildQueryParameter(SELECTED_PROJECT_UUID, projectUUID))
                .add(buildQueryParameter(SELECTED_PROJECT_TIMESHEET_UUID, uuid))
                .build();
        final UrlContext urlContext = UrlContext
                .builder()
                .additionalParameters(projectUUIDParam)
                .forceFacesRedirect(true)
                .path(url)
                .build();
        return staticLinkUrlBuilder.buildURL(urlContext);

    }

    @Profile
    public void sendExpenseReport(@Nonnull final ExpenseReport expenseReport,
                                  @Nonnull final String projectUUID,
                                  @Nonnull final String status) {

        final String expenseURL = buildExpenseViewUrl(expenseReport.getUuid(), projectUUID).startsWith("/")
                ? buildExpenseViewUrl(expenseReport.getUuid(), projectUUID).substring(1)
                : buildExpenseViewUrl(expenseReport.getUuid(), projectUUID);

        StringBuilder message = new StringBuilder(expenseTranslationService.getYourExpenseReportLabel())
                .append(" <a href = \"" + expenseURL + "\">")
                .append(expenseReport.getExpenseReportId())
                .append("</a>\n");

        if (status.equals("reject")) {
            message = message.append(expenseTranslationService
                    .getYourExpenseReportRejectedLabel());
        } else {
            message = message.append(expenseTranslationService
                    .getYourExpenseReportApprovedLabel());

        }
        emailService.sendHtmlMail(expenseTranslationService.getExpenseReportLabel(),
                                  message.toString(),
                                  getEmployeeEmailId(expenseReport));
    }

    @Nonnull
    public String buildExpenseViewUrl(@Nonnull final String uuid,
                                      @Nonnull final String projectUUID) {
        final String applicationURL = applicationUrlService.getApplicationUrl();
        final String url = applicationURL.concat(VIEW_PROJECT_TIMESHEET_URL);
        final List<QueryParameter<?>> projectUUIDParam = ImmutableList
                .<QueryParameter<?>>builder()
                .add(buildQueryParameter(SELECTED_PROJECT_SELECTED_TAB, TabTitle.TIME_AND_EXPENSES.name()))
                .add(buildQueryParameter(SELECTED_PROJECT_UUID, projectUUID))
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

    @Nullable
    private String getEmployeeEmailId(@Nonnull final TimeSheet timeSheet) {

        if (null == timeSheet.getTeamMember()) {
            return getSubContractorEmailId(timeSheet);
        }
        final Employee employee = employeeService.findEmployee(timeSheet.getTeamMember().getEmployeeUUID());
        if (null == employee) {
            final List<Professional> professionals = professionalService.findProfessionals(
                    projectProfessionalTypeMappingConfigurationService
                            .getTypeMappingsForProfessionals());

            final Professional prof = professionals.stream()
                    .filter((professional) -> (professional.getUuid().equals(timeSheet.getTeamMember()
                    .getEmployeeUUID())))
                    .findFirst()
                    .orElse(null);
            return prof.getAddress().getEmailAddress();
        }
        return employee.getAddress().getEmailAddress();

    }

    @Nullable
    private String getSubContractorEmailId(@Nonnull final TimeSheet timeSheet) {
        final Supplier supplier = supplierService.findSupplier(timeSheet.getSubContractor().getSubContractorUUID());
        if (!supplier.getUuid().equals(timeSheet.getSubContractor().getSubContractorUUID())) {
            return null;
        }
        final List<ContactPerson> contactPersons = supplier.getOtherContactPersons();
        contactPersons.add(supplier.getContactPerson());
        final ContactPerson contact = contactPersons.stream()
                .filter((cp) -> (cp.getUuid().equals(timeSheet.getSubContractor().getMemberName())))
                .findFirst()
                .orElse(null);
        return contact.getAddress().getEmailAddress();
    }

    @Nonnull
    private QueryParameter<String> buildQueryParameter(@Nonnull final String parameter, @Nonnull String parameterValue) {
        return QueryParameter
                .<String>builder()
                .parameter(parameter)
                .parameterValue(parameterValue)
                .build();
    }

    @Nullable
    private String getEmployeeEmailId(@Nonnull final ExpenseReport expenseReport) {

        if (null == expenseReport.getTeamMember()) {
            return getSubContractorEmailId(expenseReport);

        }
        final Employee employee = employeeService.findEmployee(expenseReport.getTeamMember().getEmployeeUUID());
        if (null == employee) {
            final List<Professional> professionals = professionalService.findProfessionals(
                    projectProfessionalTypeMappingConfigurationService
                            .getTypeMappingsForProfessionals());

            final Professional prof = professionals.stream()
                    .filter((professional) -> (professional.getUuid().equals(expenseReport.getTeamMember()
                    .getEmployeeUUID())))
                    .findFirst()
                    .orElse(null);
            return prof.getAddress().getEmailAddress();
        }
        return employee.getAddress().getEmailAddress();

    }

    @Nullable
    private String getSubContractorEmailId(@Nonnull final ExpenseReport expenseReport) {
        final Supplier supplier = supplierService.findSupplier(expenseReport.getSubContractor().getSubContractorUUID());
        if (supplier.getUuid().equals(expenseReport.getSubContractor().getSubContractorUUID())) {
            return null;
        }
        final List<ContactPerson> contactPersons = supplier.getOtherContactPersons();
        contactPersons.add(supplier.getContactPerson());
        final ContactPerson contact = contactPersons.stream()
                .filter((cp) -> (cp.getUuid().equals(expenseReport.getSubContractor().getMemberName())))
                .findFirst()
                .orElse(null);
        return contact.getAddress().getEmailAddress();
    }

}
