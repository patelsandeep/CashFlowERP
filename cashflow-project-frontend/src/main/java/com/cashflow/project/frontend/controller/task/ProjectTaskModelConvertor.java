package com.cashflow.project.frontend.controller.task;

import com.cashflow.access.authorization.LoggedInAuthorizedUser;
import com.cashflow.access.authorization.LoggedInCompanyAccount;
import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.project.api.budget.BudgetService;
import com.cashflow.project.api.deposit.DepositService;
import com.cashflow.project.api.milestone.ProjectMilestoneService;
import com.cashflow.project.api.phase.ProjectPhaseService;
import com.cashflow.project.api.progress.ProjectLevelProgressService;
import com.cashflow.project.api.task.ProjectTaskService;
import com.cashflow.project.api.taskbudget.TaskBudgetDetailService;
import com.cashflow.project.domain.project.budget.Budget;
import com.cashflow.project.domain.project.budget.Deposit;
import com.cashflow.project.domain.project.costing.CostClassification;
import com.cashflow.project.domain.project.costing.DirectProjectCosting;
import com.cashflow.project.domain.project.costing.IndirectProjectCosting;
import com.cashflow.project.domain.project.level.ProjectLevelProgress;
import com.cashflow.project.domain.project.level.SafetyRating;
import com.cashflow.project.domain.project.milestone.ProjectMilestone;
import com.cashflow.project.domain.project.phase.ProjectPhase;
import com.cashflow.project.domain.project.revenue.DirectProjectRevenue;
import com.cashflow.project.domain.project.revenue.IndirectProjectRevenue;
import com.cashflow.project.domain.project.task.ProjectTask;
import com.cashflow.project.frontend.controller.model.ProjectTaskModel;
import com.cashflow.project.frontend.controller.model.TaskBudgetInformation;
import com.cashflow.project.frontend.controller.model.TaskDepositModel;
import com.cashflow.project.frontend.controller.model.TaskRevenueInformation;
import com.cashflow.project.frontend.controller.supervisor.SupervisorEntityModel;
import com.cashflow.project.frontend.controller.supervisor.SupervisorEntityType;
import com.cashflow.useraccount.domain.businessunit.CompanyAccount;
import com.cashflow.useraccount.domain.businessuser.AuthorizedUser;
import com.cashflow.useraccount.domain.businessuser.Employee;
import com.cashflow.useraccount.domain.businessuser.professional.Professional;
import com.cashflow.useraccount.service.api.EmployeeService;
import com.cashflow.useraccount.service.api.search.AuthorizedUserSearchService;
import com.cashflow.useraccount.service.api.search.SearchContext;
import com.anosym.profiler.Profile;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
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
 * @since 25 Nov, 2016, 4:55:54 PM
 */
@Dependent
public class ProjectTaskModelConvertor implements Serializable {

    private static final long serialVersionUID = 6394945955013337646L;

    @Inject
    private Logger logger;

    @Inject
    private ProjectTaskService projectTaskService;

    @Inject
    private ProjectMilestoneService milestoneService;

    @Inject
    private ProjectPhaseService projectPhaseService;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private DepositService depositService;

    @Inject
    private BudgetService budgetService;

    @Inject
    private EmployeeService employeeService;

    @Inject
    private AuthorizedUserSearchService authorizedUserSearchService;

    @Inject
    private ProjectLevelProgressService levelProgressService;

    @Inject
    private TaskBudgetDetailService budgetDetailService;

    @Getter
    @Inject
    @LoggedInCompanyAccount
    private Instance<CompanyAccount> companyAccount;

    @Getter
    @Inject
    @LoggedInAuthorizedUser
    private Instance<AuthorizedUser> authorizedUser;

    @Profile
    public void setProjectTaskValues(@Nonnull final ProjectTaskModel projectTaskModel,
                                     @Nonnull final TaskBudgetInformation budgetInformation,
                                     @Nonnull final TaskRevenueInformation revenueInformation,
                                     @Nonnull final String taskUUID) {

        assertNotNull(taskUUID, "taskUUID can not be null");

        final List<String> projectLevelUUIDs = ImmutableList.of(taskUUID);

        final ProjectTask projectTask = projectTaskService.getTask(taskUUID);

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

        final Budget budget;
        final Deposit deposit;
        final ProjectLevelProgress levelProgress;

        try {
            levelProgress = projectLevelProgressRequests.get();
            budget = budgetRequests.get();
            deposit = depositRequests.get();

            this.convertEntityToModel(projectTask, budget, deposit, levelProgress, projectTaskModel, budgetInformation,
                                      revenueInformation);
        } catch (InterruptedException | ExecutionException ex) {
            throw Throwables.propagate(ex);
        }

    }

    @Profile
    private void convertEntityToModel(@Nonnull final ProjectTask projectTask,
                                      @Nonnull final Budget budget,
                                      @Nonnull final Deposit deposit,
                                      @Nonnull final ProjectLevelProgress projectLevelProgress,
                                      @Nonnull final ProjectTaskModel projectTaskModel,
                                      @Nonnull final TaskBudgetInformation budgetInformation,
                                      @Nonnull final TaskRevenueInformation revenueInformation) {

        projectTaskModel.setTaskUUID(projectTask.getUuid());
        projectTaskModel.setTaskId(projectTask.getId());
        projectTaskModel.setTaskUUID(projectTask.getUuid());
        projectTaskModel.setTaskName(projectTask.getName());
        projectTaskModel.setTaskSummary(projectTask.getSummary());
        projectTaskModel.setTaskDeliverable(projectTask.getDeliverables());
        projectTaskModel.setStartDate(projectTask.getStartDate());
        projectTaskModel.setEndDate(projectTask.getEndDate());
        projectTaskModel.setTaskNumber(projectTask.getNumber());
        projectTaskModel.setTaskFileUrls(projectTask.getDocuments());

        final Employee employee = employeeService.findEmployee(projectTask.getManager());
        if (null != employee) {
            final SupervisorEntityModel supervisorEntityModel = new SupervisorEntityModel(employee.getUuid(),
                                                                                          employee.getName(),
                                                                                          SupervisorEntityType.EMPLOYEE);

            projectTaskModel.setTaskSupervisor(supervisorEntityModel);
            projectTaskModel.setTaskSupervisorName(supervisorEntityModel.getSupervisorName());
        } else {

            final SearchContext context = SearchContext.builder()
                    .userUUID(projectTask.getManager())
                    .offset(0)
                    .limit(10)
                    .build();
            final AuthorizedUser professional = authorizedUserSearchService.search(context)
                    .stream().findFirst()
                    .orElse(null);
            if (professional instanceof Professional) {
                final SupervisorEntityModel supervisorEntityModel = new SupervisorEntityModel(professional.getUuid(),
                                                                                              professional.getName(),
                                                                                              SupervisorEntityType.PROFESSIONANL);

                projectTaskModel.setTaskSupervisor(supervisorEntityModel);
                projectTaskModel.setTaskSupervisorName(supervisorEntityModel.getSupervisorName());
            }

        }

        if (projectTask.getParentLevel() instanceof ProjectMilestone) {

            final ProjectMilestone milestone = milestoneService.getMilestone(projectTask.getParentLevel().getUuid());
            projectTaskModel.setMilestoneId(milestone.getId());

            projectTaskModel.setProjectMilestoneName(milestone.getName());

            final ProjectPhase phase = projectPhaseService.getPhase(milestone.getParentLevel().getUuid());
            projectTaskModel.setPhaseId(phase.getId());
            projectTaskModel.setProjectPhase(phase.getUuid());
            projectTaskModel.setProjectPhaseName(phase.getName());

            final List<ProjectMilestone> projectMilestones = milestoneService
                    .findByPhaseUUIDs(ImmutableList.of(phase.getUuid()));
            projectTaskModel.setProjectMilestones(projectMilestones);
            projectTaskModel.setProjectMilestone(milestone.getUuid());
            projectTaskModel.setProjectMilestoneName(milestone.getName());

        }

        final TaskDepositModel depositModel = new TaskDepositModel();
        budgetInformation.setTaskBudgetCost(budget.getBudgetedCost().getValue());
        revenueInformation.setTaskBudgetRevenue(budget.getBudgetedRevenue().getValue());
        budgetInformation.setTaskBudgetGrossProfit(budget.getBudgetedGrossProfit().getValue());
        depositModel.setDepositOrRetainers(deposit.getAmount().getValue());
        projectTaskModel.setTaskDepositModel(depositModel);
        projectTaskModel.setTaskBudgetInformation(budgetInformation);
        projectTaskModel.setTaskRevenueInformation(revenueInformation);
        projectTaskModel.setSafetyRating(SafetyRating.SAFE);
        projectTaskModel.setPerformanceStatus(projectLevelProgress.getPerformanceStatus());
        projectTaskModel.setTaskStatus(projectLevelProgress.getProjectStatus());
        projectTaskModel.setPpc(projectLevelProgress.getPhysicalPercentageOfCompletion());
        projectTaskModel.setPoc(projectLevelProgress.getPercentOfCompletion());
    }

    @Profile
    public void setProjectTaskBudgetValues(@Nonnull final ProjectTaskModel projectTaskModel,
                                           @Nonnull final TaskBudgetInformation budgetInformation,
                                           @Nonnull final TaskRevenueInformation revenueInformation,
                                           @Nonnull final String taskUUID) {

        assertNotNull(taskUUID, "taskUUID can not be null");

        final List<String> projectLevelUUIDs = ImmutableList.of(taskUUID);

        final Future<List<DirectProjectCosting>> directProjectCostingsRequest = asynchronousService
                .execute(() -> budgetDetailService
                .getDirectCostings(projectLevelUUIDs));
        final Future<List<IndirectProjectCosting>> indirectProjectCostingsRequest = asynchronousService
                .execute(() -> budgetDetailService
                .getIndirectCostings(projectLevelUUIDs));
        final Future<List<DirectProjectRevenue>> directProjectRevenuesRequest = asynchronousService
                .execute(() -> budgetDetailService
                .getDirectRevenues(projectLevelUUIDs));
        final Future<List<IndirectProjectRevenue>> indirectProjectRevenuesRequest = asynchronousService
                .execute(() -> budgetDetailService
                .getIndirectRevenues(projectLevelUUIDs));

        try {
            this.convertTaskBudgetEntityToModel(directProjectCostingsRequest.get(),
                                                indirectProjectCostingsRequest.get(),
                                                directProjectRevenuesRequest.get(),
                                                indirectProjectRevenuesRequest.get(),
                                                projectTaskModel,
                                                budgetInformation,
                                                revenueInformation);
        } catch (InterruptedException | ExecutionException ex) {
            throw Throwables.propagate(ex);
        }

    }

    @Profile
    private void convertTaskBudgetEntityToModel(@Nonnull final List<DirectProjectCosting> directProjectCostings,
                                                @Nonnull final List<IndirectProjectCosting> indirectProjectCostings,
                                                @Nonnull final List<DirectProjectRevenue> directProjectRevenues,
                                                @Nonnull final List<IndirectProjectRevenue> indirectProjectRevenues,
                                                @Nonnull final ProjectTaskModel projectTaskModel,
                                                @Nonnull final TaskBudgetInformation budgetInformation,
                                                @Nonnull final TaskRevenueInformation revenueInformation) {

        directProjectCostings.stream()
                .map((directProjectCosting) -> {
                    if (directProjectCosting.getCostClassification() == CostClassification.LABOUR) {
                        budgetInformation.setBudgetLabourCost(directProjectCosting
                                .getCostValue().getValue());
                    }
                    return directProjectCosting;
                })
                .map((directProjectCosting) -> {
                    if (directProjectCosting.getCostClassification() == CostClassification.EQUIPMENT) {
                        budgetInformation.setBudgetEquipmentCost(directProjectCosting
                                .getCostValue().getValue());
                    }
                    return directProjectCosting;
                })
                .map((directProjectCosting) -> {
                    if (directProjectCosting.getCostClassification() == CostClassification.MATERIALS) {
                        budgetInformation.setBudgetMaterialCost(directProjectCosting
                                .getCostValue().getValue());
                    }
                    return directProjectCosting;
                })
                .filter((directProjectCosting) -> (directProjectCosting.getCostClassification() == CostClassification.SUB_CONTRACTORS))
                .forEach((directProjectCosting) -> {
                    budgetInformation.setBudgetSubContractorCost(directProjectCosting
                            .getCostValue().getValue());
                });

        directProjectRevenues.stream()
                .map((directProjectRevenue) -> {
                    if (directProjectRevenue.getCostClassification() == CostClassification.LABOUR) {
                        revenueInformation.setIncludeBudgetRevenueDetails(true);
                        revenueInformation.setBudgetLabourRevenue(directProjectRevenue
                                .getCostValue().getValue());
                    }
                    return directProjectRevenue;
                })
                .map((directProjectRevenue) -> {
                    if (directProjectRevenue.getCostClassification() == CostClassification.EQUIPMENT) {
                        revenueInformation.setBudgetEquipmentRevenue(directProjectRevenue
                                .getCostValue().getValue());
                    }
                    return directProjectRevenue;
                })
                .map((directProjectRevenue) -> {
                    if (directProjectRevenue.getCostClassification() == CostClassification.MATERIALS) {
                        revenueInformation.setBudgetMaterialRevenue(directProjectRevenue
                                .getCostValue().getValue());
                    }
                    return directProjectRevenue;
                })
                .filter((directProjectRevenue) -> (directProjectRevenue.getCostClassification() == CostClassification.SUB_CONTRACTORS))
                .forEach((directProjectRevenue) -> {
                    revenueInformation.setBudgetSubContractorRevenue(directProjectRevenue
                            .getCostValue().getValue());
                });

        indirectProjectCostings.stream()
                .map((indirectProjectCosting) -> {
                    if (indirectProjectCosting.getCostClassification() == CostClassification.ALLOCATION) {
                        budgetInformation.setCostAllocation(indirectProjectCosting
                                .getCostValue().getValue());
                    }
                    return indirectProjectCosting;
                })
                .filter((indirectProjectCosting) -> (indirectProjectCosting.getCostClassification() == CostClassification.OTHER))
                .forEach((indirectProjectCosting) -> {
                    budgetInformation.setOtherIndirectCost(indirectProjectCosting
                            .getCostValue().getValue());
                });

        indirectProjectRevenues.stream()
                .map((indirectProjectRevenue) -> {
                    if (indirectProjectRevenue.getCostClassification() == CostClassification.ALLOCATION) {
                        revenueInformation.setRevenueAllocation(indirectProjectRevenue
                                .getCostValue().getValue());
                    }
                    return indirectProjectRevenue;
                })
                .filter((indirectProjectRevenue) -> (indirectProjectRevenue.getCostClassification() == CostClassification.OTHER))
                .forEach((indirectProjectRevenue) -> {
                    revenueInformation.setOtherIndirectRevenue(indirectProjectRevenue
                            .getCostValue().getValue());
                });

        projectTaskModel.setTaskBudgetInformation(budgetInformation);
        projectTaskModel.setTaskRevenueInformation(revenueInformation);

    }

}
