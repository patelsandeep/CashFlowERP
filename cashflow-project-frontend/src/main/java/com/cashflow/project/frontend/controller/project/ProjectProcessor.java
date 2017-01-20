package com.cashflow.project.frontend.controller.project;

import com.cashflow.access.authorization.LoggedInAuthorizedUser;
import com.cashflow.access.authorization.LoggedInCompanyAccount;
import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.access.authorization.uniqueid.IdContext;
import com.cashflow.access.authorization.uniqueid.UniqueIdGenerator;
import com.cashflow.accessroles.domain.accessrole.AccessRole;
import com.cashflow.accessroles.service.accessrole.AccessRoleRequestContext;
import com.cashflow.accessroles.service.accessrole.AccessRoleService;
import com.cashflow.project.api.budget.BudgetService;
import com.cashflow.project.api.deposit.DepositService;
import com.cashflow.project.api.level.ProjectLevelSettingService;
import com.cashflow.project.api.progress.ProjectLevelProgressService;
import com.cashflow.project.api.project.ProjectService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.ProjectStatus;
import com.cashflow.project.domain.project.budget.Budget;
import com.cashflow.project.domain.project.budget.Deposit;
import com.cashflow.project.domain.project.level.LevelStatus;
import com.cashflow.project.domain.project.level.PerformanceStatus;
import com.cashflow.project.domain.project.level.ProjectLevelCategory;
import com.cashflow.project.domain.project.level.ProjectLevelProgress;
import com.cashflow.project.domain.project.level.ProjectLevelSetting;
import com.cashflow.project.domain.project.level.SafetyRating;
import com.cashflow.project.domain.project.personnel.CostRateSource;
import com.cashflow.project.domain.project.personnel.MarkUpMethod;
import com.cashflow.project.domain.project.personnel.TeamMember;
import com.cashflow.project.domain.project.personnel.TeamMemberCategory;
import com.cashflow.project.frontend.controller.businessunit.BusinessUnitEvent;
import com.cashflow.project.frontend.controller.model.GeneralInfoModel;
import com.cashflow.project.frontend.controller.model.ProjectModel;
import com.cashflow.project.frontend.controller.model.ProjectRoleModel;
import com.cashflow.project.frontend.controller.model.ProjectTeamMemberModel;
import com.cashflow.project.frontend.controller.people.ProjectTeamMemberProcessor;
import com.cashflow.project.frontend.controller.supervisor.SupervisorEntityModel;
import com.cashflow.project.frontend.controller.supervisor.SupervisorEntityType;
import com.cashflow.useraccount.domain.businessunit.BusinessUnit;
import com.cashflow.useraccount.domain.businessunit.CompanyAccount;
import com.cashflow.useraccount.domain.businessuser.AuthorizedUser;
import com.cashflow.useraccount.domain.businessuser.EmployeeInformation;
import com.cashflow.useraccount.service.api.businessaccount.employee.EmployeeInformationService;
import com.anosym.common.Amount;
import com.anosym.common.Currency;
import com.anosym.profiler.Profile;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import lombok.Getter;

import static com.cashflow.frontend.support.ObjectsUtil.whicheverNonNull;
import static com.cashflow.frontend.support.errormessage.ErrorMessagePreconditions.assertNotNull;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 */
@Dependent
public class ProjectProcessor implements Serializable {

    private static final long serialVersionUID = -9183334934245034504L;

    @Inject
    private Logger logger;

    @Inject
    private ProjectService projectService;

    @Inject
    private BudgetService budgetService;

    @Inject
    private DepositService depositService;

    @Inject
    private ProjectLevelSettingService levelSettingService;

    @Inject
    private ProjectLevelProgressService levelProgressService;

    @Inject
    private ProjectTeamMemberProcessor memberProcessor;

    @Inject
    private EmployeeInformationService employeeInformationService;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private AccessRoleService accessRoleService;

    @Inject
    @IdContext(forDomain = TeamMember.class, prefix = "E", length = 6)
    private UniqueIdGenerator uniqueIdGenerator;

    @Getter
    @Inject
    @LoggedInCompanyAccount
    private Instance<CompanyAccount> companyAccount;

    @Getter
    @Inject
    @LoggedInAuthorizedUser
    private Instance<AuthorizedUser> authorizedUser;

    private EmployeeInformation employeeInformation;

    @PostConstruct
    void init() {
        asynchronousService.execute(() -> {
            employeeInformation = employeeInformationService.getEmployeeInformation(authorizedUser.get().getUuid());
        });
    }

    @Nonnull
    @Profile
    public String process(@Nonnull final ProjectModel projectModel,
                          @Nonnull final GeneralInfoModel generalInfoModel,
                          @Nullable final BusinessUnitEvent businessUnitEvent,
                          @Nonnull final String action) {
        checkNotNull(projectModel, "The projectModel must not be null");
        checkNotNull(generalInfoModel, "The generalInfoModel must not be null");
        final Project project;
        if (null != projectModel.getProjectUUID()) {
            project = projectService.getProject(projectModel.getProjectUUID());
            saveProjectDetails(project, projectModel, generalInfoModel, businessUnitEvent);
        } else {
            project = new Project();
            saveProjectDetails(project, projectModel, generalInfoModel, businessUnitEvent);

        }
        final List<String> projectLevelUUIDs = ImmutableList.of(project.getUuid());
        final Currency currency = assertNotNull(projectModel.getCurrency(), "currency required");
        Budget budget = getBudget(projectLevelUUIDs);
        if (null == budget) {
            budget = Budget.builder()
                    .projectLevel(project)
                    .budgetedCost(getAmount(currency, projectModel.getProjectBudgedCost()))
                    .budgetedRevenue(getAmount(currency, projectModel.getProjectRevenue()))
                    .budgetedGrossProfit(calculateBudgetedGrossProfit(projectModel))
                    .build();
        } else {
            budget.setBudgetedCost(getAmount(currency, projectModel.getProjectBudgedCost()));
            budget.setBudgetedRevenue(getAmount(currency, projectModel.getProjectRevenue()));
            budget.setBudgetedGrossProfit(calculateBudgetedGrossProfit(projectModel));
        }

        Deposit deposit = getDeposit(projectLevelUUIDs);
        if (null == deposit) {
            deposit = Deposit.builder()
                    .projectLevel(project)
                    .amount(getAmount(currency, generalInfoModel.getCustomerDeposit()))
                    .build();
        } else {
            deposit.setAmount(getAmount(currency, generalInfoModel.getCustomerDeposit()));

        }

        final List<ProjectLevelCategory> projectLevelCategories = new ArrayList<>();
        projectLevelCategories.add(ProjectLevelCategory.TASK);
        if (generalInfoModel.isIncludePhases()) {
            projectLevelCategories.add(ProjectLevelCategory.PHASE);
        }
        if (generalInfoModel.isIncludeMilestones()) {
            projectLevelCategories.add(ProjectLevelCategory.MILESTONE);
        }
        ProjectLevelSetting levelSetting;
        if (null == projectModel.getProjectUUID()) {
            levelSetting = ProjectLevelSetting.builder()
                    .customerDepositRequired(generalInfoModel.isIncludeCustomerDeposit())
                    .includeSubContractors(generalInfoModel.isIncludeSubContractors())
                    .project(project)
                    .projectLevelCategories(projectLevelCategories)
                    .customerDepositLevel(generalInfoModel.getLevelCategory())
                    .build();
        } else {
            levelSetting = getProjectLevel(projectModel.getProjectUUID());
            levelSetting.setCustomerDepositLevel(generalInfoModel.getLevelCategory());
            levelSetting.setProjectLevelCategories(projectLevelCategories);
            levelSetting.setProject(project);
            levelSetting.setIncludeSubContractors(generalInfoModel.isIncludeSubContractors());
            levelSetting.setCustomerDepositRequired(generalInfoModel.isIncludeCustomerDeposit());
        }
        ProjectLevelProgress progress = getProjectProgress(projectLevelUUIDs);
        if (null == progress) {
            progress = ProjectLevelProgress.builder()
                    .percentOfCompletion(BigDecimal.ZERO)
                    .levelStatus(LevelStatus.PLANNED)
                    .performanceStatus(PerformanceStatus.ON_TIME)
                    .physicalPercentageOfCompletion(BigDecimal.ZERO)
                    .projectLevel(project)
                    .projectStatus(action.equals("approve") ? ProjectStatus.APPROVED : ProjectStatus.PENDING)
                    .build();
        } else {
            progress.setPercentOfCompletion(BigDecimal.ZERO);
            progress.setLevelStatus(LevelStatus.PLANNED);
            progress.setPerformanceStatus(PerformanceStatus.ON_TIME);
            progress.setPhysicalPercentageOfCompletion(BigDecimal.ZERO);
            progress.setProjectLevel(project);
            progress.setProjectStatus(action.equals("approve") ? ProjectStatus.APPROVED : ProjectStatus.PENDING);
        }
        projectService.saveProject(project, budget, deposit, levelSetting, progress);
        if (null == projectModel.getProjectUUID()) {
            saveTeamMember(project, projectModel);
        }
        projectModel.setProjectUUID(project.getUuid());
        return project.getUuid();
    }

    @Nonnull
    private Amount getAmount(@Nonnull final Currency currency, @Nullable final BigDecimal value) {
        return Optional
                .ofNullable(value)
                .map((val) -> new Amount(currency, val))
                .orElseGet(() -> new Amount(currency, new BigDecimal("0.00")));
    }

    private void saveProjectDetails(@Nonnull final Project project,
                                    @Nonnull final ProjectModel projectModel,
                                    @Nonnull final GeneralInfoModel generalInfoModel,
                                    @Nullable final BusinessUnitEvent businessUnitEvent) {

        final Calendar startDate;
        final Calendar endDate;

        startDate = Calendar.getInstance();
        startDate.setTime(projectModel.getStartDate());

        endDate = Calendar.getInstance();
        endDate.setTime(projectModel.getEndDate());

        project.setName(projectModel.getProjectName());
        project.setContractType(projectModel.getContractType());
        project.setProjectType(projectModel.getProjectType());
        project.setId(projectModel.getProjectId());
        project.setCustomerUUID(projectModel.getCustomerOrDeptUUID());
        project.setCurrency(projectModel.getCurrency());
        project.setCityLocation(generalInfoModel.getLocation());
        project.setEndDate(endDate);
        project.setStartDate(startDate);
        project.setManager(projectModel.getProjectManager().getUuid());
        project.setSummary(generalInfoModel.getProjectSummary());
        project.setProjectLevelsUUIDs(ImmutableList.of(project.getUuid()));
        project.setAuthorizedUserUUID(authorizedUser.get().getUuid());
        final String businessUnitUUID = Optional
                .ofNullable(businessUnitEvent)
                .map((bue) -> whicheverNonNull(bue.getSelectedBranch(), bue.getSelectedBusinessDivision()))
                .map((bu) -> bu.getUuid())
                .orElseGet(() -> getUserBusinessUnitUUID());
        project.setBusinessUnitUUID(businessUnitUUID);

        final String departmentUUID = Optional
                .ofNullable(businessUnitEvent)
                .map((bue) -> bue.getSelectedDepartment())
                .filter(Objects::nonNull)
                .map((dep) -> dep.getUuid())
                .orElseGet(() -> getUserDepartmentUUID());
        project.setDepartmentUUID(departmentUUID);
        // As Saftey Rating and Deliverable were nonnull fields and
        // this fields are not being used for general info.
        // So i have kept this values static
        project.setSafetyRating(SafetyRating.SAFE);
        project.setDeliverables("Yes");
        project.setDocuments(generalInfoModel.getProjectFileUrls());

        if (generalInfoModel.getLocationPermission().equals("Yes")) {
            project.setLocationPermission(true);
        }
        if (generalInfoModel.getProjectInsurance().equals("Yes")) {
            project.setInsuranceRequired(true);
        }
        if (generalInfoModel.isIncludeTasks()) {
            project.setProjectLevelCategory(ProjectLevelCategory.TASK);
        }
        if (generalInfoModel.isIncludeMilestones()) {
            project.setProjectLevelCategory(ProjectLevelCategory.MILESTONE);
        }
        if (generalInfoModel.isIncludePhases()) {
            project.setProjectLevelCategory(ProjectLevelCategory.PHASE);
        }

    }

    @Nonnull
    private Amount calculateBudgetedGrossProfit(@Nonnull final ProjectModel projectModel) {
        final BigDecimal profit = Optional
                .ofNullable(projectModel.getProjectRevenue())
                .map((revenue) -> revenue.subtract(Optional
                        .ofNullable(projectModel.getProjectBudgedCost())
                        .orElse(new BigDecimal("0.00"))))
                .orElseGet(() -> new BigDecimal("0.00"));
        return new Amount(projectModel.getCurrency(), profit);
    }

    @Nonnull
    private String getUserBusinessUnitUUID() {
        final AuthorizedUser authUser = checkNotNull(this.authorizedUser.get());
        final BusinessUnit<?> businessUnit = authUser.getBusinessUnit();
        if (isInCurrentCompanyAccount(businessUnit)) {
            return businessUnit.getUuid();
        }

        return companyAccount.get().getUuid();
    }

    private boolean isInCurrentCompanyAccount(@Nonnull final BusinessUnit<?> businessUnit) {
        if (Objects.equals(companyAccount.get().getUuid(),
                           businessUnit.getUuid())) {
            return true;
        }

        final BusinessUnit<?> parentUnit = businessUnit.getParentUnit();
        if (parentUnit == null) {
            return false;
        }

        return isInCurrentCompanyAccount(parentUnit);
    }

    @Nullable
    private String getUserDepartmentUUID() {
        return Optional
                .ofNullable(employeeInformation)
                .map((ei) -> ei.getDepartment())
                .filter(Objects::nonNull)
                .map((dep) -> dep.getUuid())
                .orElse(null);
    }

    private void saveTeamMember(@Nonnull final Project project,
                                @Nonnull final ProjectModel projectModel) {

        final ProjectTeamMemberModel teamMemberModel = new ProjectTeamMemberModel();
        teamMemberModel.setMemberID(uniqueIdGenerator.nextId());
        teamMemberModel.setTeamMemberCategory(TeamMemberCategory.EMPLOYEE);
        final SupervisorEntityModel supervisorEntityModel = new SupervisorEntityModel(projectModel.getProjectManager()
                .getUuid(),
                                                                                      projectModel.getProjectManager()
                                                                                      .getName(),
                                                                                      SupervisorEntityType.EMPLOYEE);
        teamMemberModel.setTeamMember(supervisorEntityModel);
        teamMemberModel.setCurrency(projectModel.getCurrency());

        final ProjectRoleModel projectRoleModel = new ProjectRoleModel();
        final Future<List<AccessRole>> accessRoles = asynchronousService.execute(()
                -> getAccessRoles()
        );

        try {
            for (AccessRole accessRole : accessRoles.get()) {
                if (accessRole.getRoleName().equals("Project Manager")) {
                    projectRoleModel.setProjectRoleUUID(accessRole.getUuid());
                }
            }
        } catch (InterruptedException | ExecutionException ex) {
            throw Throwables.propagate(ex);
        }

        projectRoleModel.setBillRate(BigDecimal.ZERO);
        projectRoleModel.setCostRate(BigDecimal.ZERO);
        projectRoleModel.setMarkUpMethod(MarkUpMethod.ABSOLUTE);
        projectRoleModel.setMarkUpValue(BigDecimal.ZERO);
        projectRoleModel.setCostRateSource(CostRateSource.MANUAL);

        teamMemberModel.getProjectRoleModels().add(projectRoleModel);

        memberProcessor.saveMember(teamMemberModel, project);

    }

    @Nonnull
    public List<AccessRole> getAccessRoles() {
        final AccessRoleRequestContext requestContext = AccessRoleRequestContext
                .builder()
                .applicationCode("AZPM")
                .build();
        return accessRoleService.getAccessRoles(requestContext);
    }

    @Nullable
    private Budget getBudget(@Nonnull final List<String> projectLevelUUIDs) {
        final Future<Budget> budgetRequests = asynchronousService.execute(() -> {
            return budgetService
                    .getBudgets(projectLevelUUIDs)
                    .stream()
                    .findFirst()
                    .orElse(null);
        });

        try {
            return budgetRequests.get();
        } catch (InterruptedException | ExecutionException ex) {
            throw Throwables.propagate(ex);
        }
    }

    @Nullable
    private Deposit getDeposit(@Nonnull final List<String> projectLevelUUIDs) {
        final Future<Deposit> depositRequest = asynchronousService.execute(() -> {
            return depositService
                    .getDeposits(projectLevelUUIDs)
                    .stream()
                    .findFirst()
                    .orElse(null);
        });

        try {
            return depositRequest.get();
        } catch (InterruptedException | ExecutionException ex) {
            throw Throwables.propagate(ex);
        }
    }

    @Nullable
    private ProjectLevelSetting getProjectLevel(@Nonnull final String projectUUID) {
        final Future<ProjectLevelSetting> projectLevelRequest = asynchronousService.execute(() -> {
            return levelSettingService.getProjectLevelSetting(projectUUID);
        });

        try {
            return projectLevelRequest.get();
        } catch (InterruptedException | ExecutionException ex) {
            throw Throwables.propagate(ex);
        }
    }

    @Nullable
    private ProjectLevelProgress getProjectProgress(@Nonnull final List<String> projectUUIDs) {
        final Future<ProjectLevelProgress> projectLevelRequest = asynchronousService.execute(() -> {
            return levelProgressService.getProjectLevelProgresss(projectUUIDs)
                    .stream()
                    .findFirst()
                    .orElse(null);
        });

        try {
            return projectLevelRequest.get();
        } catch (InterruptedException | ExecutionException ex) {
            throw Throwables.propagate(ex);
        }
    }
}
