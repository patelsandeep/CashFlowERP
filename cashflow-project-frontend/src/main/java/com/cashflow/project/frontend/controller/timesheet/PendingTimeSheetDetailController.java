package com.cashflow.project.frontend.controller.timesheet;

import com.cashflow.accountpayables.domain.supplier.ContactPerson;
import com.cashflow.accountpayables.domain.supplier.Supplier;
import com.cashflow.accountpayables.service.api.supplier.SupplierService;
import com.cashflow.frontend.support.httpparameter.HttpParameter;
import com.cashflow.project.api.people.ProjectSubContractorService;
import com.cashflow.project.api.people.ProjectTeamMemberService;
import com.cashflow.project.api.project.ProjectService;
import com.cashflow.project.api.timesheet.TimeSheetService;
import com.cashflow.project.config.projectrole.ProjectProfessionalTypeMappingConfigurationService;
import com.cashflow.project.domain.expenseinformation.TimesheetContext;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.personnel.SubContractor;
import com.cashflow.project.domain.project.personnel.TeamMember;
import com.cashflow.project.domain.project.timesheet.ExpenseStatus;
import com.cashflow.project.domain.project.timesheet.TimeSheet;
import com.cashflow.project.domain.project.timesheet.WorkTime;
import com.cashflow.project.domain.util.menuoption.MenuOption;
import com.cashflow.project.frontend.controller.model.ModelViewScopedController;
import com.cashflow.project.frontend.controller.model.PendingTimeSheetModel;
import com.cashflow.project.frontend.controller.model.SuccessMessageModel;
import com.cashflow.project.frontend.controller.model.TimeSheetInfoModel;
import com.cashflow.project.frontend.service.urlbuilder.StaticLinkUrlBuilder;
import com.cashflow.project.frontend.service.urlbuilder.UrlContext;
import com.cashflow.project.translation.timesheet.PendingTimeSheetTranslationService;
import com.cashflow.useraccount.service.api.EmployeeService;
import com.cashflow.useraccount.service.api.professional.ProfessionalService;
import com.anosym.email.service.EmailService;
import com.anosym.profiler.Profile;
import com.anosym.urlbuilder.QueryParameter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;

import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_MEMBER_UUID;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_MENU;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_UUID;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Iterables.isEmpty;

/**
 *
 * 
 * @since 27 Dec, 2016, 11:01:42 AM
 */
@ModelViewScopedController
public class PendingTimeSheetDetailController implements Serializable {

    private static final long serialVersionUID = 239767963564993573L;

    private static final String PROJECT_APPROVE_TIMESHEET_DETAIL_URL = "/timesheet/pending-timesheet-detail.xhtml";

    @Inject
    private Logger logger;

    @Getter
    @Setter
    private PendingTimeSheetModel pendingTimeSheetModel;

    @Getter
    @Setter
    private List<TimeSheet> timeSheets;

    @Inject
    private PendingTimeSheetTranslationService pendingTimeSheetTranslationService;

    @Inject
    private ProjectProfessionalTypeMappingConfigurationService projectProfessionalTypeMappingConfigurationService;

    @Inject
    private TimeSheetService timeSheetService;

    @Inject
    private PendingTimeSheetCalculationProcessor calculationProcessor;

    @Inject
    private StaticLinkUrlBuilder staticLinkUrlBuilder;

    @Inject
    private PendingTimeSheetDetailProcessor pendingTimeSheetDetailProcessor;

    @Inject
    private ProjectService projectService;

    @Inject
    private EmployeeService employeeService;

    @Inject
    private EmailService emailService;

    @Inject
    private SupplierService supplierService;

    @Inject
    private ProfessionalService professionalService;

    @Inject
    private ProjectTeamMemberService memberService;

    @Inject
    private ProjectSubContractorService contractorService;

    @Getter
    @Setter
    @Nullable
    private Map<String, Boolean> checked = new HashMap<>();

    @Inject
    @HttpParameter(SELECTED_PROJECT_UUID)
    private Instance<String> pUUID;

    @Inject
    @HttpParameter(SELECTED_MEMBER_UUID)
    private Instance<String> memberUUID;

    @Getter
    @Setter
    @Nullable
    private Map<Calendar, List<TimeSheetInfoModel>> pendingTimeSheetInfoModel;

    @Getter
    @Setter
    private SuccessMessageModel successMessageModel;

    @Getter
    @Setter
    @Nullable
    private String sortBy;

    @PostConstruct
    public void init_pendingTimeSheet() {

        pendingTimeSheetModel = new PendingTimeSheetModel();

        if (!isNullOrEmpty(pUUID.get())) {
            getProjectTimeSheets(pUUID.get());
            sortBy = "project";
        } else if (!isNullOrEmpty(memberUUID.get())) {
            getPeopleTimeSheets(memberUUID.get());
            sortBy = "people";
        }
        pendingTimeSheetInfoModel = new HashMap<>();
        pendingTimeSheetDetailProcessor.getTimeSheets(timeSheets, pendingTimeSheetInfoModel, checked,
                                                      pendingTimeSheetModel);
    }

    @Profile
    public void getProjectTimeSheets(@Nonnull final String uuid) {
        final Project project = projectService.getProject(uuid);
        pendingTimeSheetModel.setName(project.getName());

        final TimesheetContext timesheetContext = TimesheetContext
                .builder()
                .projectUUID(uuid)
                .status(ExpenseStatus.SUBMITTED)
                .build();
        timeSheets = timeSheetService.getTimesheets(timesheetContext);

    }

    @Profile
    public void getPeopleTimeSheets(@Nonnull final String uuid) {
        final TeamMember teamMember = memberService.getTeamMember(uuid);
        final TimesheetContext timesheetContext = Optional
                .ofNullable(teamMember)
                .map((tm) -> buildTimesheetContext(tm))
                .orElseGet(() -> buildSubContractorTimesheetContext(uuid));

        timeSheets = timeSheetService.getTimesheets(timesheetContext);

    }

    @Nonnull
    public List<TimeSheetInfoModel> getTimeSheetInformation(@Nonnull final Calendar timeSheetDate) {
        final List<TimeSheetInfoModel> timeInfoModel = pendingTimeSheetInfoModel.get(timeSheetDate);
        String id = UUID.randomUUID().toString();
        final Set<ProjectLevel> projectLevels = timeInfoModel
                .stream()
                .map((ts) -> ts.getProjectLevel())
                .collect(Collectors.toSet());

        timeInfoModel.forEach((infoModel) -> {
            projectLevels.add(infoModel.getProjectLevel());
        });
        final List<TimeSheetInfoModel> information = new ArrayList<>();
        final Map<ProjectLevel, List<TimeSheetInfoModel>> infos = timeInfoModel
                .stream()
                .collect(Collectors.groupingBy(TimeSheetInfoModel::getProjectLevel));

        for (ProjectLevel level : projectLevels) {
            final List<TimeSheetInfoModel> infoLst = infos.get(level);
            for (TimeSheetInfoModel info : infoLst) {
                if (!id.equals(info.getProjectLevel().getUuid())) {
                    information.add(info);
                    id = info.getProjectLevel().getUuid();
                }
            }
        }

        return information;
    }

    @Nonnull
    public List<TimeSheetInfoModel> getTimeSheetInformationProject(@Nonnull final Calendar timeSheetDate) {
        final List<TimeSheetInfoModel> timeInfoModel = pendingTimeSheetInfoModel.get(timeSheetDate);
        String id = UUID.randomUUID().toString();
        final Set<String> members = timeInfoModel
                .stream()
                .map((ts) -> ts.getMemberId())
                .collect(Collectors.toSet());

        final Map<String, List<TimeSheetInfoModel>> infos = timeInfoModel
                .stream()
                .collect(Collectors.groupingBy(TimeSheetInfoModel::getMemberId));

        final List<TimeSheetInfoModel> information = new ArrayList<>();

        for (String memberId : members) {
            final List<TimeSheetInfoModel> infoLst = infos.get(memberId);
            for (TimeSheetInfoModel info : infoLst) {
                if (!id.equals(info.getMemberId())) {
                    information.add(info);
                    id = info.getMemberId();
                }
            }
        }

        return information;
    }

    @Nonnull
    public WorkTime calculateBillableHours(@Nonnull final Calendar timeSheetDate) {

        return calculationProcessor.calculateBillableHours(timeSheetDate, pendingTimeSheetInfoModel,
                                                           pendingTimeSheetModel);
    }

    @Nonnull
    public WorkTime calculateNonBillableHours(@Nonnull final Calendar timeSheetDate) {
        return calculationProcessor.calculateNonBillableHours(timeSheetDate, pendingTimeSheetInfoModel,
                                                              pendingTimeSheetModel);
    }

    @Nonnull
    public WorkTime calculateTotalHours(@Nonnull final Calendar timeSheetDate) {
        final WorkTime totalNonBillableHours = calculateNonBillableHours(timeSheetDate);
        final WorkTime totalBillableHours = calculateBillableHours(timeSheetDate);
        WorkTime totalHours = totalBillableHours.add(totalNonBillableHours);
        pendingTimeSheetModel.setTotalHours(pendingTimeSheetModel.getTotalHours().add(totalHours));
        return totalHours;
    }

    @Nonnull
    public WorkTime calculateProjectInfoBillableHours(@Nonnull final TimeSheetInfoModel infoModel) {
        return calculationProcessor.calculateProjectInfoBillableHours(infoModel, pendingTimeSheetInfoModel);
    }

    @Nonnull
    public WorkTime calculateProjectInfoNonBillableHours(@Nonnull final TimeSheetInfoModel infoModel) {
        return calculationProcessor.calculateProjectInfoNonBillableHours(infoModel, pendingTimeSheetInfoModel);
    }

    @Nonnull
    public WorkTime calculateProjectInfoTotalHours(@Nonnull final TimeSheetInfoModel infoModel) {
        final WorkTime totalNonBillableHours = calculateProjectInfoNonBillableHours(infoModel);
        final WorkTime totalBillableHours = calculateProjectInfoBillableHours(infoModel);
        WorkTime totalHours = totalBillableHours.add(totalNonBillableHours);
        return totalHours;
    }

    @Nonnull
    public WorkTime calculateMemberInfoBillableHours(@Nonnull final TimeSheetInfoModel infoModel) {
        return calculationProcessor.calculateMemberInfoBillableHours(infoModel, pendingTimeSheetInfoModel);
    }

    @Nonnull
    public WorkTime calculateMemberInfoNonBillableHours(@Nonnull final TimeSheetInfoModel infoModel) {
        return calculationProcessor.calculateMemberInfoNonBillableHours(infoModel, pendingTimeSheetInfoModel);
    }

    @Nonnull
    public WorkTime calculateMemberInfoTotalHours(@Nonnull final TimeSheetInfoModel infoModel) {
        final WorkTime totalNonBillableHours = calculateMemberInfoNonBillableHours(infoModel);
        final WorkTime totalBillableHours = calculateMemberInfoBillableHours(infoModel);
        WorkTime totalHours = totalBillableHours.add(totalNonBillableHours);
        return totalHours;
    }

    @Profile
    public void approvePendingTimeSheet() {
        final Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap();
        final String action = params.get("action");
        pendingTimeSheetDetailProcessor.approveTimeSheets(timeSheets,
                                                          successMessageModel,
                                                          pendingTimeSheetModel,
                                                          action);
    }

    @Profile
    public void approveSelectedPendingTimeSheet() {
        if (null == checked && checked.containsValue(false)) {
            return;
        }
        final Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap();
        final String action = params.get("action");
        final Collection<List<TimeSheetInfoModel>> expensesInfoModel = pendingTimeSheetInfoModel.values();

        expensesInfoModel.forEach((e) -> {
            final List<String> uuids = e.stream()
                    .filter((expenseInfo) -> (checked.get(expenseInfo.getPendingUUID())))
                    .map((teammember) -> teammember.getTimeSheetUUID())
                    .collect(Collectors.toList());
            if (!isEmpty(uuids)) {
                final TimesheetContext timeSheetContext = TimesheetContext
                        .builder()
                        .timesheetUUIDs(uuids)
                        .build();
                final List<TimeSheet> timeSheets = timeSheetService.getTimesheets(timeSheetContext);

                pendingTimeSheetDetailProcessor.approveTimeSheets(timeSheets,
                                                                  successMessageModel,
                                                                  pendingTimeSheetModel,
                                                                  action);

            }
        });

    }

    @Profile
    public void sendEmail() {
        final Set<String> memberEmailIDs = new HashSet<>();
        timeSheets.forEach((timeSheet) -> {
            memberEmailIDs.add(getEmployeeEmailID(timeSheet));
        });
        for (String emailId : memberEmailIDs) {
            emailService.sendHtmlMail(pendingTimeSheetTranslationService.getTimeSheetLabel(),
                                      pendingTimeSheetModel.getMessage(),
                                      emailId);
        }

        successMessageModel = new SuccessMessageModel();
        successMessageModel.setSuccessMessage(pendingTimeSheetTranslationService.getEmailSentSuccessMessage());
    }

    @Nonnull
    public String redirectApproveTimeSheet() {
        final QueryParameter parameter = QueryParameter
                .<String>builder()
                .parameter(SELECTED_MENU)
                .parameterValue(MenuOption.TIMESHEETS.name())
                .build();

        final UrlContext.Builder context = UrlContext
                .builder()
                .additionalParameter(parameter)
                .path(PROJECT_APPROVE_TIMESHEET_DETAIL_URL)
                .forceFacesRedirect(true);
        return staticLinkUrlBuilder.buildURL(context.build());
    }

    @Nonnull
    public String redirectApproveTimeSheetList() {
        return pendingTimeSheetDetailProcessor.redirectApproveTimeSheetList();
    }

    @Nullable
    private String getEmployeeEmailID(@Nonnull final TimeSheet timeSheet) {
        return pendingTimeSheetDetailProcessor.getEmployeeEmailID(timeSheet);
    }

    @Nonnull
    private TimesheetContext buildTimesheetContext(@Nonnull final TeamMember teamMember) {
        return TimesheetContext
                .builder()
                .teamMemberUUID(teamMember.getUuid())
                .status(ExpenseStatus.SUBMITTED)
                .build();
    }

    @Nonnull
    private TimesheetContext buildSubContractorTimesheetContext(@Nonnull final String uuid) {
        final SubContractor subContractor = contractorService.getSubContractor(uuid);
        pendingTimeSheetModel.setName(setSubContractor(subContractor));

        return TimesheetContext
                .builder()
                .subContractorUUID(subContractor.getUuid())
                .status(ExpenseStatus.SUBMITTED)
                .build();
    }

    @Nullable
    private String setSubContractor(@Nonnull final SubContractor subContractor) {
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

}
