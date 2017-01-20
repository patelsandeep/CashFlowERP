package com.cashflow.project.frontend.controller.timesheet;

import com.cashflow.accountpayables.domain.supplier.ContactPerson;
import com.cashflow.accountpayables.domain.supplier.Supplier;
import com.cashflow.accountpayables.service.api.supplier.SupplierService;
import com.cashflow.frontend.support.ApplicationUrlService;
import com.cashflow.project.api.timesheet.TimeSheetService;
import com.cashflow.project.config.projectrole.ProjectProfessionalTypeMappingConfigurationService;
import com.cashflow.project.domain.project.timesheet.ExpenseStatus;
import com.cashflow.project.domain.project.timesheet.TimeSheet;
import com.cashflow.project.domain.util.menuoption.MenuOption;
import com.cashflow.project.frontend.service.urlbuilder.StaticLinkUrlBuilder;
import com.cashflow.project.frontend.service.urlbuilder.UrlContext;
import com.cashflow.project.translation.project.tabtitle.TabTitle;
import com.cashflow.project.translation.timesheet.PendingTimeSheetTranslationService;
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
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_SELECTED_TAB;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_TIMESHEET_UUID;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_UUID;

/**
 *
 * 
 * @since 12 Jan, 2017, 6:28:06 PM
 */
@Dependent
public class ProjectTimeSheetProcessor implements Serializable {

    private static final long serialVersionUID = -7269970453804169306L;

    private static final String VIEW_PROJECT_TIMESHEET_URL = "/projects/project.xhtml";

    @Inject
    private ApplicationUrlService applicationUrlService;

    @Inject
    private StaticLinkUrlBuilder staticLinkUrlBuilder;

    @Inject
    private PendingTimeSheetTranslationService pendingTimeSheetTranslationService;

    @Inject
    private TimeSheetService timeSheetService;

    @Inject
    private EmailService emailService;

    @Inject
    private SupplierService supplierService;

    @Inject
    private EmployeeService employeeService;

    @Inject
    private ProfessionalService professionalService;

    @Inject
    private ProjectProfessionalTypeMappingConfigurationService projectProfessionalTypeMappingConfigurationService;

    @Getter
    @Setter
    @Nullable
    private String employeeEmail;

    @Profile
    public void approveTimeSheet(@Nonnull final List<TimeSheet> timesheets,
                                 @Nonnull final String action,
                                 @Nonnull final String projectUUID) {

        timesheets.forEach((timeSheet) -> {
            final String message;

            final String timeSheetURL = buildTimesheetViewUrl(timeSheet.getUuid(), projectUUID).startsWith("/") ? buildTimesheetViewUrl(
                    timeSheet.getUuid(), projectUUID).substring(1) : buildTimesheetViewUrl(timeSheet.getUuid(),
                                                                                           projectUUID);
            if (action.equals("approve")) {
                timeSheet.setStatus(ExpenseStatus.APPROVED);
                message = pendingTimeSheetTranslationService.getYourTimeSheetLabel()
                        + " <a href = \"" + timeSheetURL + "\">"
                        + timeSheet.getTimeSheetID()
                        + "</a>\n"
                        + pendingTimeSheetTranslationService
                                .getYourTimeSheetApprovedLabel();

            } else {
                timeSheet.setStatus(ExpenseStatus.SAVED);
                message = pendingTimeSheetTranslationService.getYourTimeSheetLabel()
                        + " <a href = \"" + timeSheetURL + "\">"
                        + timeSheet.getTimeSheetID()
                        + "</a>\n"
                        + pendingTimeSheetTranslationService
                                .getYourTimeSheetRejectedLabel();

            }
            timeSheet.setApprovalDate(Calendar.getInstance());
            getEmail(timeSheet);
            timeSheetService.create(timeSheet);
            emailService.sendHtmlMail(pendingTimeSheetTranslationService.getTimeSheetLabel(),
                                      message,
                                      employeeEmail);
        });

    }

    @Nonnull
    private String buildTimesheetViewUrl(@Nonnull final String uuid,
                                         @Nonnull final String projectUUID) {
        final String applicationURL = applicationUrlService.getApplicationUrl();
        final String url = applicationURL.concat(VIEW_PROJECT_TIMESHEET_URL);
        final List<QueryParameter<?>> projectUUIDParam = ImmutableList
                .<QueryParameter<?>>builder()
                .add(buildQueryParameter(SELECTED_PROJECT_SELECTED_TAB, TabTitle.TIME_AND_EXPENSES.name()))
                .add(buildQueryParameter(SELECTED_PROJECT_UUID, projectUUID))
                .add(buildQueryParameter(SELECTED_MENU, MenuOption.TIMESHEETS.name()))
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

    @Nonnull
    public QueryParameter<String> buildQueryParameter(@Nonnull final String parameter, @Nonnull String parameterValue) {
        return QueryParameter
                .<String>builder()
                .parameter(parameter)
                .parameterValue(parameterValue)
                .build();
    }

    private void getEmail(@Nonnull final TimeSheet timeSheet) {

        if (null != timeSheet.getTeamMember()) {
            final Employee employee = employeeService.findEmployee(timeSheet.getTeamMember().getEmployeeUUID());
            if (null != employee) {
                this.setEmployeeEmail(employee.getAddress().getEmailAddress());
            } else {
                final List<Professional> professionals = professionalService.findProfessionals(
                        projectProfessionalTypeMappingConfigurationService
                                .getTypeMappingsForProfessionals());

                professionals.stream()
                        .filter((professional) -> (professional.getUuid().equals(timeSheet.getTeamMember()
                        .getEmployeeUUID())))
                        .forEach((professional) -> {
                            this.setEmployeeEmail(professional.getAddress().getEmailAddress());
                        });
            }
        } else {
            getSubContractor(timeSheet);
        }
    }

    private void getSubContractor(@Nonnull final TimeSheet timeSheet) {
        final Supplier supplier = supplierService.findSupplier(timeSheet.getSubContractor().getSubContractorUUID());
        if (supplier.getUuid().equals(timeSheet.getSubContractor().getSubContractorUUID())) {
            final List<ContactPerson> contactPersons = supplier.getOtherContactPersons();
            contactPersons.add(supplier.getContactPerson());
            contactPersons.stream()
                    .filter((cp) -> (cp.getUuid().equals(timeSheet.getSubContractor().getMemberName())))
                    .forEachOrdered((cp) -> {
                        this.setEmployeeEmail(cp.getAddress().getEmailAddress());
                    });
        }
    }
}
