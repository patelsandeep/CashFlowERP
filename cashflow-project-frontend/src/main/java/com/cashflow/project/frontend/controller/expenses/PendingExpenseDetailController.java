package com.cashflow.project.frontend.controller.expenses;

import com.cashflow.frontend.support.httpparameter.HttpParameter;
import com.cashflow.project.api.expense.ExpenseReportService;
import com.cashflow.project.api.people.ProjectSubContractorService;
import com.cashflow.project.api.people.ProjectTeamMemberService;
import com.cashflow.project.api.project.ProjectService;
import com.cashflow.project.domain.expenseinformation.ExpenseReportContext;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.expense.ExpenseReport;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.personnel.SubContractor;
import com.cashflow.project.domain.project.personnel.TeamMember;
import com.cashflow.project.domain.project.timesheet.ExpenseStatus;
import com.cashflow.project.frontend.controller.model.ExpenseInfoModel;
import com.cashflow.project.frontend.controller.model.ModelViewScopedController;
import com.cashflow.project.frontend.controller.model.PendingExpenseModel;
import com.cashflow.project.frontend.controller.model.SuccessMessageModel;
import com.cashflow.project.translation.expenses.PendingExpenseTranslationService;
import com.anosym.common.Amount;
import com.anosym.email.service.EmailService;
import com.anosym.profiler.Profile;
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
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_UUID;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Iterables.isEmpty;

/**
 *
 * 
 * @since 9 Jan, 2017, 7:00:31 PM
 */
@ModelViewScopedController
public class PendingExpenseDetailController implements Serializable {

    private static final long serialVersionUID = 8440285353800687020L;

    @Inject
    private Logger logger;

    @Getter
    @Setter
    private PendingExpenseModel pendingExpenseModel;

    @Getter
    @Setter
    private List<ExpenseReport> expenseReports;

    @Inject
    private PendingExpenseTranslationService pendingExpenseTranslationService;

    @Inject
    private PendingExpenseDetailProcessor pendingExpenseDetailProcessor;

    @Inject
    private ExpenseReportService expenseReportService;

    @Inject
    private PendingExpenseCalculationProcessor pendingExpenseCalculationProcessor;

    @Inject
    private ProjectService projectService;

    @Inject
    private EmailService emailService;

    @Inject
    private ProjectTeamMemberService memberService;

    @Inject
    private ProjectSubContractorService contractorService;

    @Inject
    @HttpParameter(SELECTED_PROJECT_UUID)
    private Instance<String> pUUID;

    @Inject
    @HttpParameter(SELECTED_MEMBER_UUID)
    private Instance<String> memberUUID;

    @Getter
    @Setter
    @Nullable
    private Map<String, Boolean> checked = new HashMap<>();

    @Getter
    @Setter
    @Nullable
    private Map<Calendar, List<ExpenseInfoModel>> pendingExpenseInfoModel;

    @Getter
    @Setter
    private SuccessMessageModel successMessageModel;

    @Getter
    @Setter
    @Nullable
    private String sortBy;

    @PostConstruct
    public void init_pendingExpenseReport() {

        pendingExpenseModel = new PendingExpenseModel();

        if (!isNullOrEmpty(pUUID.get())) {
            getProjectExpenseReports(pUUID.get());
            sortBy = "project";
        } else if (!isNullOrEmpty(memberUUID.get())) {
            getPeopleExpenseReports(memberUUID.get());
            sortBy = "people";
        }

    }

    @Profile
    public void getProjectExpenseReports(@Nonnull final String uuid) {
        final Project project = projectService.getProject(uuid);
        pendingExpenseModel.setName(project.getName());

        final ExpenseReportContext expenseContext = ExpenseReportContext
                .builder()
                .projectUUID(uuid)
                .status(ExpenseStatus.SUBMITTED)
                .build();
        expenseReports = expenseReportService.getExpenseReports(expenseContext);
        preparePendingExpenseInfoModel(expenseReports);
    }

    @Profile
    public void getPeopleExpenseReports(@Nonnull final String uuid) {
        final TeamMember teamMember = memberService.getTeamMember(uuid);
        final ExpenseReportContext expenseContext = Optional
                .ofNullable(teamMember)
                .map((tm) -> buildExpenseContext(tm))
                .orElseGet(() -> buildSubContractorExpenseContext(uuid));

        expenseReports = expenseReportService.getExpenseReports(expenseContext);

        preparePendingExpenseInfoModel(expenseReports);

    }

    @Nonnull
    public Amount calculateBillableAmount(@Nonnull final Calendar expenseDate) {

        final List<ExpenseInfoModel> expenseInfos = pendingExpenseInfoModel.get(expenseDate);
        return pendingExpenseCalculationProcessor.calculateBillableAmount(expenseInfos,
                                                                          pendingExpenseModel);

    }

    @Nonnull
    public Amount calculateNonBillableAmount(@Nonnull final Calendar expenseDate) {

        final List<ExpenseInfoModel> expenseInfos = pendingExpenseInfoModel.get(expenseDate);
        return pendingExpenseCalculationProcessor.calculateNonBillableAmount(expenseInfos,
                                                                             pendingExpenseModel);

    }

    @Nonnull
    public Amount calculateTotalAmount(@Nonnull final Calendar expenseDate) {
        final List<ExpenseInfoModel> expenseInfos = pendingExpenseInfoModel.get(expenseDate);
        return pendingExpenseCalculationProcessor.calculateTotalAmount(expenseInfos,
                                                                       pendingExpenseModel);
    }

    @Nonnull
    public List<ExpenseInfoModel> getExpenseInformation(@Nonnull final Calendar expenseDate) {
        final List<ExpenseInfoModel> timeInfoModel = pendingExpenseInfoModel.get(expenseDate);
        String id = UUID.randomUUID().toString();
        final Set<ProjectLevel> projectLevels = timeInfoModel
                .stream()
                .map((ts) -> ts.getProjectLevel())
                .collect(Collectors.toSet());

        timeInfoModel.forEach((infoModel) -> {
            projectLevels.add(infoModel.getProjectLevel());
        });
        final List<ExpenseInfoModel> information = new ArrayList<>();
        final Map<ProjectLevel, List<ExpenseInfoModel>> infos = timeInfoModel
                .stream()
                .collect(Collectors.groupingBy(ExpenseInfoModel::getProjectLevel));

        for (ProjectLevel level : projectLevels) {
            final List<ExpenseInfoModel> infoLst = infos.get(level);
            for (ExpenseInfoModel info : infoLst) {
                if (!id.equals(info.getProjectLevel().getUuid())) {
                    information.add(info);
                    id = info.getProjectLevel().getUuid();
                }
            }
        }

        return information;
    }

    @Nonnull
    public List<ExpenseInfoModel> getExpenseInformationProject(@Nonnull final Calendar expenseDate) {
        final List<ExpenseInfoModel> timeInfoModel = pendingExpenseInfoModel.get(expenseDate);
        String id = UUID.randomUUID().toString();
        final Set<String> members = timeInfoModel
                .stream()
                .map((ts) -> ts.getMemberId())
                .collect(Collectors.toSet());

        final Map<String, List<ExpenseInfoModel>> infos = timeInfoModel
                .stream()
                .collect(Collectors.groupingBy(ExpenseInfoModel::getMemberId));

        final List<ExpenseInfoModel> information = new ArrayList<>();

        for (String memberId : members) {
            final List<ExpenseInfoModel> infoLst = infos.get(memberId);
            for (ExpenseInfoModel info : infoLst) {
                if (!id.equals(info.getMemberId())) {
                    information.add(info);
                    id = info.getMemberId();
                }
            }
        }

        return information;
    }

    @Nonnull
    public Amount calculateProjectInfoBillableAmount(@Nonnull final ExpenseInfoModel infoModel) {
        final List<ExpenseInfoModel> expenseInfos = pendingExpenseInfoModel.get(infoModel.getExpenseDetailDate());
        return pendingExpenseCalculationProcessor.calculateProjectInfoBillableAmount(infoModel, expenseInfos);
    }

    @Nonnull
    public Amount calculateProjectInfoNonBillableAmount(@Nonnull final ExpenseInfoModel infoModel) {

        final List<ExpenseInfoModel> expenseInfos = pendingExpenseInfoModel.get(infoModel.getExpenseDetailDate());
        return pendingExpenseCalculationProcessor.calculateProjectInfoNonBillableAmount(infoModel, expenseInfos);
    }

    @Nonnull
    public Amount calculateProjectInfoTotalAmount(@Nonnull final ExpenseInfoModel infoModel) {

        final Amount billable = calculateProjectInfoBillableAmount(infoModel);
        final Amount nonBillable = calculateProjectInfoNonBillableAmount(infoModel);
        final Amount total = billable.add(nonBillable);
        return total;
    }

    @Nonnull
    public Amount calculateMemberInfoBillableAmount(@Nonnull final ExpenseInfoModel infoModel) {

        final List<ExpenseInfoModel> expenseInfos = pendingExpenseInfoModel.get(infoModel.getExpenseDetailDate());
        return pendingExpenseCalculationProcessor.calculateMemberInfoBillableAmount(infoModel, expenseInfos);
    }

    @Nonnull
    public Amount calculateMemberInfoNonBillableAmount(@Nonnull final ExpenseInfoModel infoModel) {

        final List<ExpenseInfoModel> expenseInfos = pendingExpenseInfoModel.get(infoModel.getExpenseDetailDate());
        return pendingExpenseCalculationProcessor.calculateMemberInfoNonBillableAmount(infoModel, expenseInfos);
    }

    @Nonnull
    public Amount calculateMemberInfoTotalAmount(@Nonnull final ExpenseInfoModel infoModel) {

        final Amount billable = calculateMemberInfoBillableAmount(infoModel);
        final Amount nonBillable = calculateMemberInfoNonBillableAmount(infoModel);
        final Amount total = billable.add(nonBillable);
        return total;
    }

    @Profile
    public void sendEmail() {
        final Set<String> memberEmailIDs = new HashSet<>();
        expenseReports.forEach((exp) -> {
            memberEmailIDs.add(getEmployeeEmailID(exp));
        });
        for (String emailId : memberEmailIDs) {
            emailService.sendHtmlMail(pendingExpenseTranslationService.getExpenseReportLabel(),
                                      pendingExpenseModel.getMessage(),
                                      emailId);
        }
        successMessageModel = new SuccessMessageModel();
        successMessageModel.setSuccessMessage(pendingExpenseTranslationService.getEmailSendSuccessMessage());
    }

    @Profile
    public void approveSelectedPendingExpense() {
        if (null == checked && checked.containsValue(false)) {
            return;
        }
        final Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap();
        final String action = params.get("action");

        final Collection<List<ExpenseInfoModel>> expensesInfoModel = pendingExpenseInfoModel.values();

        expensesInfoModel.forEach((e) -> {
            final List<String> uuids = e.stream()
                    .filter((expenseInfo) -> (checked.get(expenseInfo.getPendingUUID())))
                    .map((teammember) -> teammember.getExpenseUUID())
                    .collect(Collectors.toList());
            if (!isEmpty(uuids)) {
                final ExpenseReportContext expenseReportContext = ExpenseReportContext
                        .builder()
                        .expenseUUIDs(uuids)
                        .build();
                final List<ExpenseReport> expenseReports = expenseReportService.getExpenseReports(expenseReportContext);
                approveExpense(expenseReports, action);
            }
        });

    }

    @Profile
    public void approvePendingExpenses() {
        final Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap();
        final String action = params.get("action");
        approveExpense(expenseReports, action);
    }

    @Nonnull
    public String redirectApproveExpenseList() {
        return pendingExpenseDetailProcessor.redirectApproveExpenseList();
    }

    private void preparePendingExpenseInfoModel(@Nonnull final List<ExpenseReport> expenseReports) {

        pendingExpenseInfoModel = new HashMap<>();
        pendingExpenseDetailProcessor.preparePendingExpenseInfoModel(expenseReports, pendingExpenseInfoModel, checked,
                                                                     pendingExpenseModel);
    }

    @Nonnull
    private ExpenseReportContext buildExpenseContext(@Nonnull final TeamMember teamMember) {
        return ExpenseReportContext
                .builder()
                .teamMemberUUID(teamMember.getUuid())
                .status(ExpenseStatus.SUBMITTED)
                .build();
    }

    @Nonnull
    private ExpenseReportContext buildSubContractorExpenseContext(@Nonnull final String uuid) {
        final SubContractor subContractor = contractorService.getSubContractor(uuid);
        pendingExpenseModel.setName(setSubContractor(subContractor));

        return ExpenseReportContext
                .builder()
                .subContractorUUID(subContractor.getUuid())
                .status(ExpenseStatus.SUBMITTED)
                .build();
    }

    @Nullable
    private String setSubContractor(@Nonnull final SubContractor subContractor) {
        return pendingExpenseDetailProcessor.setSubContractor(subContractor);
    }

    @Nullable
    private String getEmployeeEmailID(@Nonnull final ExpenseReport expenseReport) {
        return pendingExpenseDetailProcessor.getEmployeeEmailID(expenseReport);
    }

    private void approveExpense(@Nonnull final List<ExpenseReport> expenseReports,
                                @Nonnull final String action) {

        pendingExpenseDetailProcessor.approveExpense(expenseReports, action, successMessageModel, pendingExpenseModel);
    }
}
