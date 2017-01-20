package com.cashflow.project.frontend.controller.expenses;

import com.cashflow.accountpayables.domain.supplier.ContactPerson;
import com.cashflow.accountpayables.domain.supplier.Supplier;
import com.cashflow.accountpayables.service.api.supplier.SupplierService;
import com.cashflow.frontend.support.ApplicationUrlService;
import com.cashflow.project.api.expense.ExpenseReportService;
import com.cashflow.project.config.projectrole.ProjectProfessionalTypeMappingConfigurationService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.expense.ExpenseReport;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.milestone.ProjectMilestone;
import com.cashflow.project.domain.project.personnel.SubContractor;
import com.cashflow.project.domain.project.personnel.TeamMember;
import com.cashflow.project.domain.project.phase.ProjectPhase;
import com.cashflow.project.domain.project.task.ProjectTask;
import com.cashflow.project.domain.project.timesheet.ExpenseStatus;
import com.cashflow.project.domain.util.menuoption.MenuOption;
import com.cashflow.project.frontend.controller.model.ExpenseInfoModel;
import com.cashflow.project.frontend.controller.model.PendingExpenseModel;
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
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_EXPENSE_REPORT_UUID;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_SELECTED_TAB;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_UUID;

/**
 *
 * 
 * @since 12 Jan, 2017, 3:07:36 PM
 */
@Dependent
public class PendingExpenseDetailProcessor implements Serializable {

    private static final long serialVersionUID = -32980610236744946L;

    private static final String VIEW_PROJECT_EXPENSE_URL = "/projects/project.xhtml";

    private static final String PROJECT_APPROVE_EXPENSE_URL = "/expenses/pending-expense-list.xhtml";

    @Inject
    private Logger logger;

    @Inject
    private ApplicationUrlService applicationUrlService;

    @Inject
    private StaticLinkUrlBuilder staticLinkUrlBuilder;

    @Inject
    private PendingExpenseTranslationService pendingExpenseTranslationService;

    @Inject
    private ProjectProfessionalTypeMappingConfigurationService projectProfessionalTypeMappingConfigurationService;

    @Inject
    private ExpenseReportService expenseReportService;

    @Inject
    private EmployeeService employeeService;

    @Inject
    private EmailService emailService;

    @Inject
    private SupplierService supplierService;

    @Inject
    private ProfessionalService professionalService;

    private final List<Calendar> dates = new ArrayList<>();

    private Map<String, ProjectLevel<?>> levels;

    @Nonnull
    public String redirectApproveExpenseList() {
        final QueryParameter parameter = QueryParameter
                .<String>builder()
                .parameter(SELECTED_MENU)
                .parameterValue(MenuOption.EXPENSES.name())
                .build();

        final UrlContext.Builder context = UrlContext
                .builder()
                .additionalParameter(parameter)
                .path(PROJECT_APPROVE_EXPENSE_URL)
                .forceFacesRedirect(true);
        return staticLinkUrlBuilder.buildURL(context.build());
    }

    public void approveExpense(@Nonnull final List<ExpenseReport> expenseReports,
                               @Nonnull final String action,
                               @Nonnull SuccessMessageModel successMessageModel,
                               @Nonnull final PendingExpenseModel pendingExpenseModel) {

        expenseReports.forEach((expenseReport) -> {
            StringBuilder message = new StringBuilder(pendingExpenseTranslationService.getYourExpenseReportLabel());
            final String expenseURL = buildExpenseViewUrl(expenseReport.getUuid(), pendingExpenseModel.getProjectUUID())
                    .startsWith("/") ? buildExpenseViewUrl(expenseReport.getUuid(), pendingExpenseModel.getProjectUUID())
                    .substring(1) : buildExpenseViewUrl(
                    expenseReport.getUuid(), pendingExpenseModel.getProjectUUID());
            if (action.equals("approve")) {
                expenseReport.setStatus(ExpenseStatus.APPROVED);

                message = message.append(" <a href = \"" + expenseURL + "\">")
                        .append(expenseReport.getExpenseReportId())
                        .append("</a>\n")
                        .append(pendingExpenseTranslationService
                                .getYourExpenseReportApprovedLabel());

            } else {
                expenseReport.setStatus(ExpenseStatus.SAVED);
                message = message.append(" <a href = \"" + expenseURL + "\">")
                        .append(expenseReport.getExpenseReportId())
                        .append("</a>\n")
                        .append(pendingExpenseTranslationService
                                .getYourExpenseReportRejectedLabel());

            }

            expenseReport.setApprovalDate(Calendar.getInstance());
            expenseReportService.save(expenseReport);
            pendingExpenseModel.setMessage(message.toString());
            sendEmailMember(expenseReport, message.toString());
        });
        successMessageModel = new SuccessMessageModel();
        if (action.equals("approve")) {
            successMessageModel.setSuccessMessage(pendingExpenseTranslationService.getApprovedSuccessMessage());
            addSuccessMessage(pendingExpenseTranslationService.getApprovedSuccessMessage());
        } else {
            successMessageModel.setSuccessMessage(pendingExpenseTranslationService.getRejectedSuccessMessage());
            addSuccessMessage(pendingExpenseTranslationService.getRejectedSuccessMessage());
        }
        final FacesContext facesContext = FacesContext.getCurrentInstance();
        final ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.getFlash().setKeepMessages(true);
        try {
            externalContext.redirect(redirectApproveExpenseList());
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

    }

    public void preparePendingExpenseInfoModel(@Nonnull final List<ExpenseReport> expenseReports,
                                               @Nonnull final Map<Calendar, List<ExpenseInfoModel>> pendingExpenseInfoModel,
                                               Map<String, Boolean> checked,
                                               @Nonnull final PendingExpenseModel pendingExpenseModel) {

        expenseReports.forEach((expense) -> {
            expense.getExpenseDetails()
                    .forEach((expenseInfo) -> {
                        final ExpenseInfoModel expenseInfoModel = new ExpenseInfoModel();
                        final List<ExpenseInfoModel> expenseInfos = new ArrayList<>();
                        dates.add(expenseInfo.getExpenseDate());

                        levels = new HashMap<>();
                        setProjectLevels(expense.getProjectLevel());
                        levels
                                .entrySet()
                                .forEach(level -> {
                                    if (level.getValue() instanceof Project) {
                                        pendingExpenseModel.setProjectUUID(((Project) level.getValue()).getUuid());
                                        pendingExpenseModel.setProject(((Project) level.getValue()).getName());
                                        expenseInfoModel.setCurrency(((Project) level.getValue()).getCurrency());
                                        expenseInfoModel.setProjectUUID(((Project) level.getValue()).getUuid());
                                        expenseInfoModel.setProjectName(((Project) level.getValue()).getName());
                                    }
                                    if (level.getValue() instanceof ProjectPhase) {
                                        expenseInfoModel.setPhaseName(((ProjectPhase) level.getValue()).getName());
                                        expenseInfoModel.setPhaseId(((ProjectPhase) level.getValue()).getId());
                                    }
                                    if (level.getValue() instanceof ProjectMilestone) {
                                        expenseInfoModel.setMilestoneName(((ProjectMilestone) level.getValue())
                                                .getName());
                                        expenseInfoModel.setMilestoneId(((ProjectMilestone) level.getValue()).getId());
                                    }
                                    if (level.getValue() instanceof ProjectTask) {
                                        expenseInfoModel.setTaskName(((ProjectTask) level.getValue()).getName());
                                        expenseInfoModel.setTaskId(((ProjectTask) level.getValue()).getId());
                                    }
                                });
                        setMemberValue(expense, expenseInfoModel, pendingExpenseModel);
                        pendingExpenseModel.setExpenseDate(Collections.min(dates));
                        pendingExpenseModel.setToExpenseDate(Collections.max(dates));
                        expenseInfoModel.setExpenseUUID(expense.getUuid());
                        expenseInfoModel.setExpenseDetailDate(expenseInfo.getExpenseDate());
                        expenseInfoModel.setBillableType(expenseInfo.getBillable());
                        expenseInfoModel.setProjectLevel(expense.getProjectLevel());
                        expenseInfoModel.setExpenseAmount(expenseInfo.getTotalExpense());
                        expenseInfoModel.setPendingUUID(expenseInfoModel.getExpenseDetailDate().getTime().toString()
                                .concat(expenseInfoModel.getMemberId()));

                        if (pendingExpenseInfoModel.containsKey(expenseInfo.getExpenseDate())) {
                            final List<ExpenseInfoModel> expenseInfoModels = pendingExpenseInfoModel.get(expenseInfo
                                    .getExpenseDate());
                            expenseInfoModels.add(expenseInfoModel);
                            pendingExpenseInfoModel.put(expenseInfo.getExpenseDate(), expenseInfoModels);
                        } else {

                            expenseInfos.add(expenseInfoModel);
                            pendingExpenseInfoModel.put(expenseInfo.getExpenseDate(), expenseInfos);
                        }
                        if (!checked.containsKey(expenseInfoModel.getPendingUUID())) {
                            checked.put(expenseInfoModel.getPendingUUID(), false);

                        }
                    });

        });

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
    @Profile
    public String getEmployeeEmailID(@Nonnull final ExpenseReport expenseReport) {

        if (null == expenseReport.getTeamMember()) {
            return getSubContractorEmailID(expenseReport);

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

    @Nonnull
    private String buildExpenseViewUrl(@Nonnull final String uuid,
                                       @Nonnull final String projectUUID) {
        final String applicationURL = applicationUrlService.getApplicationUrl();
        final String url = applicationURL.concat(VIEW_PROJECT_EXPENSE_URL);
        final List<QueryParameter<?>> projectUUIDParam = ImmutableList
                .<QueryParameter<?>>builder()
                .add(buildQueryParameter(SELECTED_PROJECT_SELECTED_TAB, TabTitle.TIME_AND_EXPENSES.name()))
                .add(buildQueryParameter(SELECTED_PROJECT_UUID, projectUUID))
                .add(buildQueryParameter(SELECTED_PROJECT_EXPENSE_REPORT_UUID, uuid))
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

    private void sendEmailMember(@Nonnull final ExpenseReport expenseReport,
                                 @Nonnull final String message) {
        emailService.sendHtmlMail(pendingExpenseTranslationService.getExpenseReportLabel(),
                                  message,
                                  null != getEmployeeEmailID(expenseReport) ? getEmployeeEmailID(expenseReport) : "");
    }

    @Nullable
    private String getSubContractorEmailID(@Nonnull final ExpenseReport expenseReport) {
        final Supplier supplier = supplierService.findSupplier(expenseReport.getSubContractor().getSubContractorUUID());
        if (!supplier.getUuid().equals(expenseReport.getSubContractor().getSubContractorUUID())) {
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

    private void setMemberValue(@Nonnull final ExpenseReport expenseReport,
                                @Nonnull final ExpenseInfoModel expenseInfoModel,
                                @Nonnull final PendingExpenseModel pendingExpenseModel) {

        if (null != expenseReport.getTeamMember()) {
            expenseInfoModel.setMemberName(setTeamMemberName(expenseReport.getTeamMember(), pendingExpenseModel));
            expenseInfoModel.setMemberId(expenseReport.getTeamMember().getMemberId());
        } else {
            final SubContractor subContractor = expenseReport.getSubContractor();
            expenseInfoModel.setMemberName(setSubContractor(subContractor));
            expenseInfoModel.setMemberId(expenseReport
                    .getSubContractor().getMemberId());
        }
    }

    @Nullable
    private String setTeamMemberName(@Nonnull final TeamMember teamMember,
                                     @Nonnull final PendingExpenseModel pendingExpenseModel) {
        final Employee employee = employeeService.findEmployee(teamMember.getEmployeeUUID());
        if (null != employee) {
            pendingExpenseModel.setName(employee.getName());
            return employee.getName();
        } else {
            final List<Professional> professionals = professionalService.findProfessionals(
                    projectProfessionalTypeMappingConfigurationService
                            .getTypeMappingsForProfessionals());

            Professional proffessional = professionals.stream()
                    .filter((professional) -> (professional.getUuid().equals(teamMember.getEmployeeUUID())))
                    .findFirst()
                    .orElse(null);
            pendingExpenseModel.setName(proffessional.getName());
            return proffessional.getName();
        }
    }

    private void setProjectLevels(@Nullable final ProjectLevel<?> level) {
        if (level == null) {
            return;
        }
        levels.put(level.getUuid(), level);
        setProjectLevels(level.getParentLevel());
    }
}
