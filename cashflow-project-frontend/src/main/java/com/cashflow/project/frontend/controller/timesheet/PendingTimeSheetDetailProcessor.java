package com.cashflow.project.frontend.controller.timesheet;

import com.cashflow.accountpayables.domain.supplier.ContactPerson;
import com.cashflow.accountpayables.domain.supplier.Supplier;
import com.cashflow.accountpayables.service.api.supplier.SupplierService;
import com.cashflow.frontend.support.ApplicationUrlService;
import com.cashflow.project.api.timesheet.TimeSheetService;
import com.cashflow.project.config.projectrole.ProjectProfessionalTypeMappingConfigurationService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.milestone.ProjectMilestone;
import com.cashflow.project.domain.project.personnel.SubContractor;
import com.cashflow.project.domain.project.personnel.TeamMember;
import com.cashflow.project.domain.project.phase.ProjectPhase;
import com.cashflow.project.domain.project.task.ProjectTask;
import com.cashflow.project.domain.project.timesheet.ExpenseStatus;
import com.cashflow.project.domain.project.timesheet.TimeSheet;
import com.cashflow.project.domain.util.menuoption.MenuOption;
import com.cashflow.project.frontend.controller.model.PendingTimeSheetModel;
import com.cashflow.project.frontend.controller.model.SuccessMessageModel;
import com.cashflow.project.frontend.controller.model.TimeSheetInfoModel;
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
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.Dependent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import static com.cashflow.frontend.support.jsfutil.SuccessMessageHelper.addSuccessMessage;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_MENU;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_SELECTED_TAB;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_TIMESHEET_UUID;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_UUID;

/**
 *
 * 
 * @since 12 Jan, 2017, 5:40:07 PM
 */
@Dependent
public class PendingTimeSheetDetailProcessor implements Serializable {

    private static final long serialVersionUID = -6791430301831922543L;

    private static final String VIEW_PROJECT_TIMESHEET_URL = "/projects/project.xhtml";

    private static final String PROJECT_APPROVE_TIMESHEET_URL = "/timesheet/pending-timesheet-list.xhtml";
    @Inject
    private Logger logger;

    @Inject
    private PendingTimeSheetTranslationService pendingTimeSheetTranslationService;

    @Inject
    private StaticLinkUrlBuilder staticLinkUrlBuilder;

    @Inject
    private SupplierService supplierService;

    @Inject
    private EmployeeService employeeService;

    @Inject
    private EmailService emailService;

    @Inject
    private TimeSheetService timeSheetService;

    @Inject
    private ProfessionalService professionalService;

    @Inject
    private ApplicationUrlService applicationUrlService;

    @Inject
    private ProjectProfessionalTypeMappingConfigurationService projectProfessionalTypeMappingConfigurationService;

    private final List<Calendar> dates = new ArrayList<>();

    private Map<String, ProjectLevel<?>> levels;

    @Profile
    public void getTimeSheets(@Nonnull final List<TimeSheet> timeSheets,
                              @Nonnull final Map<Calendar, List<TimeSheetInfoModel>> pendingTimeSheetInfoModel,
                              @Nonnull final Map<String, Boolean> checked,
                              @Nonnull final PendingTimeSheetModel pendingTimeSheetModel) {

        timeSheets.forEach((timeSheet) -> {
            timeSheet.getTimeSheetInfos()
                    .forEach((timeSheetInfo) -> {
                        final TimeSheetInfoModel timeSheetInfoModel = new TimeSheetInfoModel();
                        final List<TimeSheetInfoModel> timeInfos = new ArrayList<>();
                        dates.add(timeSheetInfo.getTimeSheetDate());

                        levels = new HashMap<>();
                        setProjectLevels(timeSheet.getProjectLevel());
                        levels
                                .entrySet()
                                .forEach(level -> {
                                    if (level.getValue() instanceof Project) {
                                        pendingTimeSheetModel.setProjectUUID(((Project) level.getValue()).getUuid());
                                        pendingTimeSheetModel.setProject(((Project) level.getValue()).getName());

                                        timeSheetInfoModel.setProjectUUID(((Project) level.getValue()).getUuid());
                                        timeSheetInfoModel.setProjectName(((Project) level.getValue()).getName());
                                    }
                                    if (level.getValue() instanceof ProjectPhase) {
                                        timeSheetInfoModel.setPhaseName(((ProjectPhase) level.getValue()).getName());
                                        timeSheetInfoModel.setPhaseId(((ProjectPhase) level.getValue()).getId());
                                    }
                                    if (level.getValue() instanceof ProjectMilestone) {
                                        timeSheetInfoModel.setMilestoneName(((ProjectMilestone) level.getValue())
                                                .getName());
                                        timeSheetInfoModel.setMilestoneId(((ProjectMilestone) level.getValue()).getId());
                                    }
                                    if (level.getValue() instanceof ProjectTask) {
                                        timeSheetInfoModel.setTaskName(((ProjectTask) level.getValue()).getName());
                                        timeSheetInfoModel.setTaskId(((ProjectTask) level.getValue()).getId());
                                    }
                                });
                        setMemberValue(timeSheet, timeSheetInfoModel, pendingTimeSheetModel);
                        pendingTimeSheetModel.setTimesheetDate(Collections.min(dates));
                        pendingTimeSheetModel.setToTimesheetDate(Collections.max(dates));
                        timeSheetInfoModel.setTimeSheetUUID(timeSheet.getUuid());
                        timeSheetInfoModel.setProjectLevel(timeSheet.getProjectLevel());
                        timeSheetInfoModel.setTimeSheetInfoDate(timeSheetInfo.getTimeSheetDate());
                        timeSheetInfoModel.setBillableType(timeSheetInfo.getBillableType());
                        timeSheetInfoModel.setRegularTime(timeSheetInfo.getRegularTime());
                        timeSheetInfoModel.setPtoTime(timeSheetInfo.getPtoTime());
                        timeSheetInfoModel.setOverTime(timeSheetInfo.getOverTime());
                        timeSheetInfoModel.setPendingUUID(timeSheetInfoModel.getTimeSheetInfoDate().getTime().toString()
                                .concat(timeSheetInfoModel.getMemberId()));

                        if (pendingTimeSheetInfoModel.containsKey(timeSheetInfo.getTimeSheetDate())) {
                            final List<TimeSheetInfoModel> timeInfoModel = pendingTimeSheetInfoModel.get(timeSheetInfo
                                    .getTimeSheetDate());
                            timeInfoModel.add(timeSheetInfoModel);
                            pendingTimeSheetInfoModel.put(timeSheetInfo.getTimeSheetDate(), timeInfoModel);
                        } else {

                            timeInfos.add(timeSheetInfoModel);
                            pendingTimeSheetInfoModel.put(timeSheetInfo.getTimeSheetDate(), timeInfos);
                        }
                        if (!checked.containsKey(timeSheetInfoModel.getPendingUUID())) {
                            checked.put(timeSheetInfoModel.getPendingUUID(), false);
                        }
                    });
        });
    }

    @Profile
    public void approveTimeSheets(@Nonnull final List<TimeSheet> timeSheets,
                                  @Nonnull SuccessMessageModel successMessageModel,
                                  @Nonnull PendingTimeSheetModel pendingTimeSheetModel,
                                  @Nonnull final String action) {
        timeSheets.forEach((timeSheet) -> {
            StringBuilder message = new StringBuilder(pendingTimeSheetTranslationService.getYourTimeSheetLabel());

            final String timeSheetURL = buildTimesheetViewUrl(timeSheet.getUuid(), pendingTimeSheetModel)
                    .startsWith("/") ? buildTimesheetViewUrl(
                    timeSheet.getUuid(), pendingTimeSheetModel).substring(1) : buildTimesheetViewUrl(timeSheet.getUuid(),
                                                                                                     pendingTimeSheetModel);
            if (action.equals("approve")) {
                timeSheet.setStatus(ExpenseStatus.APPROVED);

                message = message.append(" <a href = \"" + timeSheetURL + "\">")
                        .append(timeSheet.getTimeSheetID())
                        .append("</a>\n")
                        .append(pendingTimeSheetTranslationService
                                .getYourTimeSheetApprovedLabel());

            } else {
                timeSheet.setStatus(ExpenseStatus.SAVED);
                message = message.append(" <a href = \"" + timeSheetURL + "\">")
                        .append(timeSheet.getTimeSheetID())
                        .append("</a>\n")
                        .append(pendingTimeSheetTranslationService
                                .getYourTimeSheetRejectedLabel());

            }

            timeSheet.setApprovalDate(Calendar.getInstance());
            timeSheetService.create(timeSheet);
            sendEmailMember(timeSheet, message.toString());
        });
        successMessageModel = new SuccessMessageModel();
        if (action.equals("approve")) {
            addSuccessMessage(pendingTimeSheetTranslationService.getApprovedSuccessMessageLabel());
            successMessageModel.setSuccessMessage(pendingTimeSheetTranslationService
                    .getApprovedSuccessMessageLabel());
        } else {
            addSuccessMessage(pendingTimeSheetTranslationService.getRejectedSuccessMessageLabel());
            successMessageModel.setSuccessMessage(pendingTimeSheetTranslationService
                    .getRejectedSuccessMessageLabel());
        }
        final FacesContext facesContext = FacesContext.getCurrentInstance();
        final ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.getFlash().setKeepMessages(true);
        try {
            externalContext.redirect(redirectApproveTimeSheetList());
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    @Nonnull
    public String redirectApproveTimeSheetList() {
        final QueryParameter parameter = QueryParameter
                .<String>builder()
                .parameter(SELECTED_MENU)
                .parameterValue(MenuOption.TIMESHEETS.name())
                .build();

        final UrlContext.Builder context = UrlContext
                .builder()
                .additionalParameter(parameter)
                .path(PROJECT_APPROVE_TIMESHEET_URL)
                .forceFacesRedirect(true);
        return staticLinkUrlBuilder.buildURL(context.build());
    }

    @Nullable
    public String setSubContractor(@Nonnull final SubContractor subContractor) {
        final Supplier supplier = supplierService.findSupplier(subContractor.getSubContractorUUID());
        if (supplier.getUuid().equals(subContractor.getSubContractorUUID())) {
            final List<ContactPerson> contactPersons = supplier.getOtherContactPersons();
            contactPersons.add(supplier.getContactPerson());
            ContactPerson contact = contactPersons.stream()
                    .filter((cp) -> (cp.getUuid().equals(subContractor.getMemberName())))
                    .findFirst()
                    .orElse(null);

            return contact.getName();
        }

        return null;
    }

    @Nullable
    public String getEmployeeEmailID(@Nonnull final TimeSheet timeSheet) {

        if (null == timeSheet.getTeamMember()) {
            return getSubContractorEmailID(timeSheet);

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
    private String getSubContractorEmailID(@Nonnull final TimeSheet timeSheet) {
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

    private void setProjectLevels(@Nullable final ProjectLevel<?> level) {
        if (level == null) {
            return;
        }
        levels.put(level.getUuid(), level);
        setProjectLevels(level.getParentLevel());
    }

    private void setMemberValue(@Nonnull final TimeSheet timeSheet,
                                @Nonnull final TimeSheetInfoModel timeSheetInfoModel,
                                @Nonnull final PendingTimeSheetModel pendingTimeSheetModel) {

        if (null != timeSheet.getTeamMember()) {
            timeSheetInfoModel.setMemberName(setTeamMemberName(timeSheet.getTeamMember(),
                                                               pendingTimeSheetModel));
            timeSheetInfoModel.setMemberId(timeSheet.getTeamMember().getMemberId());
        } else {
            final SubContractor subContractor = timeSheet.getSubContractor();
            timeSheetInfoModel.setMemberName(setSubContractor(subContractor));
            timeSheetInfoModel.setMemberId(timeSheet
                    .getSubContractor().getMemberId());
        }
    }

    @Nullable
    private String setTeamMemberName(@Nonnull final TeamMember teamMember,
                                     @Nonnull final PendingTimeSheetModel pendingTimeSheetModel) {
        final Employee employee = employeeService.findEmployee(teamMember.getEmployeeUUID());
        if (null != employee) {
            pendingTimeSheetModel.setName(employee.getName());
            return employee.getName();
        } else {
            final List<Professional> professionals = professionalService.findProfessionals(
                    projectProfessionalTypeMappingConfigurationService
                            .getTypeMappingsForProfessionals());

            Professional proffessional = professionals.stream()
                    .filter((professional) -> (professional.getUuid().equals(teamMember.getEmployeeUUID())))
                    .findFirst()
                    .orElse(null);
            pendingTimeSheetModel.setName(employee.getName());
            return proffessional.getName();
        }
    }

    @Nonnull
    private String buildTimesheetViewUrl(@Nonnull final String uuid,
                                         @Nonnull final PendingTimeSheetModel pendingTimeSheetModel) {
        final String applicationURL = applicationUrlService.getApplicationUrl();
        final String url = applicationURL.concat(VIEW_PROJECT_TIMESHEET_URL);
        final List<QueryParameter<?>> projectUUIDParam = ImmutableList
                .<QueryParameter<?>>builder()
                .add(buildQueryParameter(SELECTED_PROJECT_SELECTED_TAB, TabTitle.TIME_AND_EXPENSES.name()))
                .add(buildQueryParameter(SELECTED_PROJECT_UUID, pendingTimeSheetModel.getProjectUUID()))
                .add(buildQueryParameter(SELECTED_PROJECT_TIMESHEET_UUID, uuid))
                .add(buildQueryParameter(SELECTED_MENU, MenuOption.PROJECTS.name()))
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
    private QueryParameter<String> buildQueryParameter(@Nonnull final String parameter, @Nonnull String parameterValue) {
        return QueryParameter
                .<String>builder()
                .parameter(parameter)
                .parameterValue(parameterValue)
                .build();
    }

    @Profile
    private void sendEmailMember(@Nonnull final TimeSheet timeSheet,
                                 @Nonnull final String message) {
        emailService.sendHtmlMail(pendingTimeSheetTranslationService.getTimeSheetLabel(),
                                  message,
                                  null != getEmployeeEmailID(timeSheet) ? getEmployeeEmailID(timeSheet) : "");
    }
}
