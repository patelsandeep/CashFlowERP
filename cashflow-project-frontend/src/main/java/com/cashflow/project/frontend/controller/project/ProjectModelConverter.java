package com.cashflow.project.frontend.controller.project;

import com.cashflow.access.authorization.LoggedInAuthorizedUser;
import com.cashflow.access.authorization.LoggedInCompanyAccount;
import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.accountreceivables.domain.customer.Customer;
import com.cashflow.accountreceivables.service.api.customer.CustomerService;
import com.cashflow.project.api.budget.BudgetService;
import com.cashflow.project.api.deposit.DepositService;
import com.cashflow.project.api.level.ProjectLevelSettingService;
import com.cashflow.project.api.progress.ProjectLevelProgressService;
import com.cashflow.project.api.project.ProjectService;
import com.cashflow.project.domain.project.ContractType;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.ProjectType;
import com.cashflow.project.domain.project.RevenueRecordingMethod;
import com.cashflow.project.domain.project.budget.Budget;
import com.cashflow.project.domain.project.budget.Deposit;
import com.cashflow.project.domain.project.level.ProjectLevelCategory;
import com.cashflow.project.domain.project.level.ProjectLevelProgress;
import com.cashflow.project.domain.project.level.ProjectLevelSetting;
import com.cashflow.project.frontend.controller.model.GeneralInfoModel;
import com.cashflow.project.frontend.controller.model.ProjectModel;
import com.cashflow.useraccount.domain.businessunit.CompanyAccount;
import com.cashflow.useraccount.domain.businessunit.Department;
import com.cashflow.useraccount.domain.businessuser.AuthorizedUser;
import com.cashflow.useraccount.domain.businessuser.Employee;
import com.cashflow.useraccount.service.api.EmployeeService;
import com.cashflow.useraccount.service.api.businessunit.department.DepartmentService;
import com.anosym.profiler.Profile;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import lombok.Getter;

import static com.cashflow.frontend.support.errormessage.ErrorMessagePreconditions.assertNotNull;

/**
 *
 * 
 */
@Dependent
public class ProjectModelConverter implements Serializable {

    private static final long serialVersionUID = -9183334934245034504L;

    @Inject
    private Logger logger;

    @Inject
    private CustomerService customerService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private ProjectService projectService;

    @Inject
    private BudgetService budgetService;

    @Inject
    private DepositService depositService;

    @Inject
    private EmployeeService employeeService;

    @Inject
    private ProjectLevelProgressService levelProgressService;

    @Inject
    private ProjectLevelSettingService levelSettingService;

    @Inject
    private AsynchronousService asynchronousService;

    @Getter
    @Inject
    @LoggedInCompanyAccount
    private Instance<CompanyAccount> companyAccount;

    @Getter
    @Inject
    @LoggedInAuthorizedUser
    private Instance<AuthorizedUser> authorizedUser;

    @Profile
    public void setProjectValues(@Nonnull final ProjectModel projectModel,
                                 @Nonnull final GeneralInfoModel generalInfoModel,
                                 @Nonnull final String projectUUID) {

        assertNotNull(projectUUID, "projectUUID can not be null");

        final List<String> projectLevelUUIDs = ImmutableList.of(projectUUID);

        final Future<Project> projectRequest = asynchronousService.execute(() -> projectService.getProject(projectUUID));

        final Future<Deposit> depositRequests = asynchronousService.execute(() -> {
            return depositService
                    .getDeposits(projectLevelUUIDs)
                    .stream()
                    .findFirst()
                    .orElse(null);
        });
        final Future<Budget> budgetRequests = asynchronousService.execute(() -> {
            return budgetService
                    .getBudgets(projectLevelUUIDs)
                    .stream()
                    .findFirst()
                    .orElse(null);
        });

        final Future<ProjectLevelProgress> projectLevelProgressRequests = asynchronousService.execute(
                () -> {
                    return levelProgressService
                    .getProjectLevelProgresss(projectLevelUUIDs)
                    .stream()
                    .findFirst()
                    .orElse(null);
                });

        final Future<ProjectLevelSetting> projectLevelSettingRequests = asynchronousService.execute(
                () -> levelSettingService.getProjectLevelSetting(projectUUID));

        final Budget budget;
        final Deposit deposit;
        final ProjectLevelProgress levelProgress;
        final ProjectLevelSetting levelSetting;
        try {
            final Project project = projectRequest.get();
            levelProgress = projectLevelProgressRequests.get();
            budget = budgetRequests.get();
            deposit = depositRequests.get();
            levelSetting = projectLevelSettingRequests.get();
            this.convertEntityToModel(project, budget, deposit, levelSetting, levelProgress, projectModel,
                                      generalInfoModel);
        } catch (InterruptedException | ExecutionException ex) {
            throw Throwables.propagate(ex);
        }

    }

    private void convertEntityToModel(@Nonnull final Project project,
                                      @Nonnull final Budget budget,
                                      @Nonnull final Deposit deposit,
                                      @Nonnull final ProjectLevelSetting projectLevelSetting,
                                      @Nonnull final ProjectLevelProgress projectLevelProgress,
                                      @Nonnull final ProjectModel projectModel,
                                      @Nonnull final GeneralInfoModel generalInfoModel) {

        projectModel.setProjectUUID(project.getUuid());
        projectModel.setProjectName(project.getName());
        projectModel.setProjectId(project.getId());
        projectModel.setStartDate(project.getStartDate().getTime());
        projectModel.setEndDate(project.getEndDate().getTime());
        if (project.getProjectType() == ProjectType.CUSTOMER_PROJECT) {
            projectModel.setProjectType(ProjectType.CUSTOMER_PROJECT);
            final Future<Customer> customer = asynchronousService.execute(() -> customerService.getCustomer(project
                    .getCustomerUUID()));
            try {
                projectModel.setCustomerId(customer.get().getCustomerNumber());
                projectModel.setCustomerOrDeptUUID(customer.get().getUuid());
                final ProjectTypeEntityModel entityModel = new ProjectTypeEntityModel(customer.get().getUuid(), customer
                                                                                      .get().getName(),
                                                                                      ProjectEntityType.CUSTOMER);
                projectModel.setProjectTypeEntityModel(entityModel);
            } catch (InterruptedException | ExecutionException ex) {
                throw Throwables.propagate(ex);
            }
        }
        if (project.getProjectType() == ProjectType.INTERNAL_PROJECT) {
            projectModel.setProjectType(ProjectType.INTERNAL_PROJECT);
            final Future<Department> department = asynchronousService
                    .execute(() -> departmentService.findDepartment(project.getCustomerUUID()));
            try {
                projectModel.setDepartmentId(department.get().getCode());
                projectModel.setCustomerOrDeptUUID(department.get().getUuid());
                final ProjectTypeEntityModel entityModel = new ProjectTypeEntityModel(department.get().getUuid(),
                                                                                      department.get().getName(),
                                                                                      ProjectEntityType.DEPARTMENT);
                projectModel.setProjectTypeEntityModel(entityModel);
            } catch (InterruptedException | ExecutionException ex) {
                throw Throwables.propagate(ex);
            }

        }
        final Future<Employee> employee = asynchronousService
                .execute(() -> employeeService.findEmployee(project.getManager()));
        try {
            projectModel.setProjectManager(employee.get());
        } catch (InterruptedException | ExecutionException ex) {
            throw Throwables.propagate(ex);
        }
        projectModel.setContractType(project.getContractType());
        if (project.getContractType() == ContractType.FIXED_PRICE) {
            projectModel.setRevenueRecMethod(RevenueRecordingMethod.POC);
        } else {
            projectModel.setRevenueRecMethod(RevenueRecordingMethod.APPROVED_COSTS);
        }
        projectModel.setProjectBudgedCost(budget.getBudgetedCost().getValue());
        projectModel.setProjectRevenue(budget.getBudgetedRevenue().getValue());
        projectModel.setCurrency(budget.getBudgetedRevenue().getCurrency());
        projectModel.setProjectStatus(projectLevelProgress.getProjectStatus());

        generalInfoModel.setProjectSummary(project.getSummary());
        generalInfoModel.setLocation(project.getCityLocation());
        generalInfoModel.setLocationPermission(project.isLocationPermission() ? "Yes" : "No");
        generalInfoModel.setProjectInsurance(project.isInsuranceRequired() ? "Yes" : "No");
        generalInfoModel.setCustomerDeposit(deposit.getAmount().getValue());
        generalInfoModel.setCustomerDepositReq(projectLevelSetting.isCustomerDepositRequired() ? "Yes" : "No");

        if (project.getProjectLevelCategory() == ProjectLevelCategory.PHASE) {
            generalInfoModel.setIncludePhases(true);
            generalInfoModel.setIncludeMilestones(true);
            generalInfoModel.setIncludeTasks(true);
        }
        if (project.getProjectLevelCategory() == ProjectLevelCategory.MILESTONE) {
            generalInfoModel.setIncludePhases(false);
            generalInfoModel.setIncludeMilestones(true);
            generalInfoModel.setIncludeTasks(true);
        }
        if (project.getProjectLevelCategory() == ProjectLevelCategory.TASK) {
            generalInfoModel.setIncludePhases(false);
            generalInfoModel.setIncludeMilestones(false);
            generalInfoModel.setIncludeTasks(true);
        }

        generalInfoModel.setLevelCategory(projectLevelSetting.getCustomerDepositLevel());
        generalInfoModel.setIncludeSubContractors(projectLevelSetting.isIncludeSubContractors());
        generalInfoModel.setIncludeCustomerDeposit(projectLevelSetting.isCustomerDepositRequired());
        generalInfoModel.setProjectFileUrls(new ArrayList<>(project.getDocuments()));

    }

}
