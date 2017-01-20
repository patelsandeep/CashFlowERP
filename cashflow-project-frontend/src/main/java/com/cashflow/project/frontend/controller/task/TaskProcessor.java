package com.cashflow.project.frontend.controller.task;

import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.access.authorization.uniqueid.IdContext;
import com.cashflow.access.authorization.uniqueid.UniqueIdGenerator;
import com.cashflow.accessroles.domain.accessrole.AccessRole;
import com.cashflow.accessroles.service.accessrole.AccessRoleRequestContext;
import com.cashflow.accessroles.service.accessrole.AccessRoleService;
import com.cashflow.project.api.task.ProjectTaskService;
import com.cashflow.project.api.taskbudget.TaskBudgetDetailService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.ProjectStatus;
import com.cashflow.project.domain.project.budget.Budget;
import com.cashflow.project.domain.project.budget.Deposit;
import com.cashflow.project.domain.project.costing.CostCategory;
import com.cashflow.project.domain.project.costing.CostClassification;
import com.cashflow.project.domain.project.costing.DirectProjectCosting;
import com.cashflow.project.domain.project.costing.IndirectProjectCosting;
import com.cashflow.project.domain.project.level.LevelStatus;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.level.ProjectLevelCategory;
import com.cashflow.project.domain.project.level.ProjectLevelProgress;
import com.cashflow.project.domain.project.milestone.ProjectMilestone;
import com.cashflow.project.domain.project.personnel.CostRateSource;
import com.cashflow.project.domain.project.personnel.MarkUpMethod;
import com.cashflow.project.domain.project.personnel.TeamMember;
import com.cashflow.project.domain.project.personnel.TeamMemberCategory;
import com.cashflow.project.domain.project.revenue.DirectProjectRevenue;
import com.cashflow.project.domain.project.revenue.IndirectProjectRevenue;
import com.cashflow.project.domain.project.revenue.RevenueCategory;
import com.cashflow.project.domain.project.task.ProjectTask;
import com.cashflow.project.frontend.controller.model.ProjectRoleModel;
import com.cashflow.project.frontend.controller.model.ProjectTaskModel;
import com.cashflow.project.frontend.controller.model.ProjectTeamMemberModel;
import com.cashflow.project.frontend.controller.people.ProjectTeamMemberProcessor;
import com.anosym.common.Amount;
import com.anosym.profiler.Profile;
import com.google.common.base.Throwables;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 *
 * 
 * @since 23 Nov, 2016, 4:42:07 PM
 */
@Dependent
public class TaskProcessor implements Serializable {

    private static final long serialVersionUID = 4507643760357318778L;

    @Inject
    private Logger logger;

    @Inject
    @IdContext(forDomain = TeamMember.class, prefix = "E", length = 6)
    private UniqueIdGenerator uniqueIdGenerator;

    @Inject
    private ProjectTaskService projectTaskService;

    @Inject
    private TaskBudgetDetailService budgetDetailService;

    @Inject
    private AccessRoleService accessRoleService;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private ProjectTeamMemberProcessor memberProcessor;

    @Profile
    public void process(@Nonnull final ProjectTaskModel projectTaskModel,
                        @Nullable final ProjectMilestone projectMilestone,
                        @Nonnull final Project project) {

        checkNotNull(projectTaskModel, "The projectTaskModel must not be null");
        checkNotNull(project, "The project must not be null");

        ProjectTask projectTask;
        if (!isNullOrEmpty(projectTaskModel.getTaskUUID())) {
            projectTask = projectTaskService.getTask(projectTaskModel.getTaskUUID());

        } else {
            projectTask = new ProjectTask(projectTaskModel.getTaskNumber());
        }

        projectTask.setName(projectTaskModel.getTaskName());
        projectTask.setId(projectTaskModel.getTaskId());
        projectTask.setProjectLevelCategory(ProjectLevelCategory.TASK);
        projectTask.setSummary(projectTaskModel.getTaskSummary());
        projectTask.setDeliverables(projectTaskModel.getTaskDeliverable());
        projectTask.setManager(projectTaskModel.getTaskSupervisor().getSupervisorUUID());
        projectTask.setStartDate(projectTaskModel.getStartDate());
        projectTask.setEndDate(projectTaskModel.getEndDate());
        projectTask.setSafetyRating(projectTaskModel.getSafetyRating());
        projectTask.setDocuments(projectTaskModel.getTaskFileUrls());
        if (project.getProjectLevelCategory() == ProjectLevelCategory.TASK) {
            projectTask.setParentLevel(project);
            projectTask.setBusinessUnitUUID(project.getBusinessUnitUUID());
            projectTask.setDepartmentUUID(project.getDepartmentUUID());
        } else {
            projectTask.setParentLevel(projectMilestone);
            projectTask.setBusinessUnitUUID(projectMilestone.getBusinessUnitUUID());
            projectTask.setDepartmentUUID(projectMilestone.getDepartmentUUID());
        }
        final List<String> projectLevelUUIDs = new ArrayList<>();
        addAllParentProjectLevelUUIDs(projectLevelUUIDs, projectTask);
        projectTask.setProjectLevelsUUIDs(projectLevelUUIDs);
        final Budget budget = Budget.builder()
                .projectLevel(projectTask)
                .budgetedCost(new Amount(projectTaskModel.getCurrency(), projectTaskModel.getTaskBudgetInformation()
                                         .getTaskBudgetCost()))
                .budgetedRevenue(
                        new Amount(projectTaskModel.getCurrency(), projectTaskModel.getTaskRevenueInformation()
                                   .getTaskBudgetRevenue()))
                .budgetedGrossProfit(calculateBudgetedGrossProfit(projectTaskModel))
                .build();

        final Deposit deposit = Deposit.builder()
                .projectLevel(projectTask)
                .amount(new Amount(projectTaskModel.getCurrency(), projectTaskModel.getTaskDepositModel()
                                   .getDepositOrRetainers()))
                .build();

        final ProjectLevelProgress projectLevelProgress = ProjectLevelProgress.builder()
                .percentOfCompletion(projectTaskModel.getPoc())
                .levelStatus(LevelStatus.IN_PROGRESS)
                .performanceStatus(projectTaskModel.getPerformanceStatus())
                .physicalPercentageOfCompletion(projectTaskModel.getPpc())
                .projectLevel(projectTask)
                .projectStatus(ProjectStatus.PENDING)
                .build();

        projectTaskService.saveProjectTask(projectTask, budget, deposit, projectLevelProgress);

        processBudgetDetails(projectTaskModel, project, projectTask);
    }

    private void addAllParentProjectLevelUUIDs(@Nonnull final List<String> projectLevelUUIDs,
                                               @Nullable final ProjectLevel<?> projectLevel) {
        if (projectLevel == null) {
            return;
        }
        projectLevelUUIDs.add(projectLevel.getUuid());
        addAllParentProjectLevelUUIDs(projectLevelUUIDs, projectLevel.getParentLevel());
    }

    @Nonnull
    private Amount calculateBudgetedGrossProfit(@Nonnull final ProjectTaskModel projectTaskModel) {
        return new Amount(projectTaskModel.getCurrency(), projectTaskModel.getTaskRevenueInformation()
                          .getTaskBudgetRevenue().subtract(
                                  projectTaskModel.getTaskBudgetInformation()
                                  .getTaskBudgetCost()));
    }

    @Profile
    public void processBudgetDetails(@Nonnull final ProjectTaskModel projectTaskModel,
                                     @Nonnull final Project project,
                                     @Nonnull final ProjectTask projectTask) {

        checkNotNull(projectTaskModel, "The projectTaskModel must not be null");
        checkNotNull(project, "The project must not be null");
        checkNotNull(projectTask, "The projectTask must not be null");

        final List<DirectProjectCosting> directCostingList = new ArrayList<>();
        final List<IndirectProjectCosting> indirectCostingList = new ArrayList<>();

        final DirectProjectCosting directProjectLabourCosting = new DirectProjectCosting();
        directProjectLabourCosting.setProject(project);
        directProjectLabourCosting.setProjectLevel(projectTask);
        directProjectLabourCosting.setCostCategory(CostCategory.DIRECT_COST);
        directProjectLabourCosting.setCostClassification(CostClassification.LABOUR);
        directProjectLabourCosting.setCostValue(new Amount(projectTaskModel.getCurrency(), projectTaskModel
                                                           .getTaskBudgetInformation()
                                                           .getBudgetLabourCost()));

        directCostingList.add(directProjectLabourCosting);

        final DirectProjectCosting directProjectEquipCosting = new DirectProjectCosting();
        directProjectEquipCosting.setProject(project);
        directProjectEquipCosting.setProjectLevel(projectTask);
        directProjectEquipCosting.setCostCategory(CostCategory.DIRECT_COST);
        directProjectEquipCosting.setCostClassification(CostClassification.EQUIPMENT);
        directProjectEquipCosting.setCostValue(new Amount(projectTaskModel.getCurrency(), projectTaskModel
                                                          .getTaskBudgetInformation()
                                                          .getBudgetEquipmentCost()));

        directCostingList.add(directProjectEquipCosting);

        final DirectProjectCosting directProjectMaterialCosting = new DirectProjectCosting();
        directProjectMaterialCosting.setProject(project);
        directProjectMaterialCosting.setProjectLevel(projectTask);
        directProjectMaterialCosting.setCostCategory(CostCategory.DIRECT_COST);
        directProjectMaterialCosting.setCostClassification(CostClassification.MATERIALS);
        directProjectMaterialCosting.setCostValue(new Amount(projectTaskModel.getCurrency(), projectTaskModel
                                                             .getTaskBudgetInformation()
                                                             .getBudgetMaterialCost()));

        directCostingList.add(directProjectMaterialCosting);

        final DirectProjectCosting directProjectSubContCosting = new DirectProjectCosting();
        directProjectSubContCosting.setProject(project);
        directProjectSubContCosting.setProjectLevel(projectTask);
        directProjectSubContCosting.setCostCategory(CostCategory.DIRECT_COST);
        directProjectSubContCosting.setCostClassification(CostClassification.SUB_CONTRACTORS);
        directProjectSubContCosting.setCostValue(new Amount(projectTaskModel.getCurrency(), projectTaskModel
                                                            .getTaskBudgetInformation()
                                                            .getBudgetSubContractorCost()));

        directCostingList.add(directProjectSubContCosting);

        final IndirectProjectCosting inDirectProjectAllocationCosting = new IndirectProjectCosting();
        inDirectProjectAllocationCosting.setProject(project);
        inDirectProjectAllocationCosting.setProjectLevel(projectTask);
        inDirectProjectAllocationCosting.setCostCategory(CostCategory.INDIRECT_COST);
        inDirectProjectAllocationCosting.setCostClassification(CostClassification.ALLOCATION);
        inDirectProjectAllocationCosting.setCostValue(new Amount(projectTaskModel.getCurrency(), projectTaskModel
                                                                 .getTaskBudgetInformation()
                                                                 .getCostAllocation()));

        indirectCostingList.add(inDirectProjectAllocationCosting);

        final IndirectProjectCosting inDirectProjectOtherCosting = new IndirectProjectCosting();
        inDirectProjectOtherCosting.setProject(project);
        inDirectProjectOtherCosting.setProjectLevel(projectTask);
        inDirectProjectOtherCosting.setCostCategory(CostCategory.INDIRECT_COST);
        inDirectProjectOtherCosting.setCostClassification(CostClassification.OTHER);
        inDirectProjectOtherCosting.setCostValue(new Amount(projectTaskModel.getCurrency(), projectTaskModel
                                                            .getTaskBudgetInformation()
                                                            .getOtherIndirectCost()));

        indirectCostingList.add(inDirectProjectOtherCosting);

        processBudgetRevenueDetails(projectTaskModel, project, projectTask, directCostingList, indirectCostingList);
    }

    @Profile
    public void processBudgetRevenueDetails(@Nonnull final ProjectTaskModel projectTaskModel,
                                            @Nonnull final Project project,
                                            @Nonnull final ProjectTask projectTask,
                                            @Nonnull final List<DirectProjectCosting> directCostingList,
                                            @Nonnull final List<IndirectProjectCosting> indirectCostingList) {

        checkNotNull(projectTaskModel, "The projectTaskModel must not be null");
        checkNotNull(project, "The project must not be null");

        final List<DirectProjectRevenue> directRevenueList = new ArrayList<>();
        final List<IndirectProjectRevenue> indirectRevenueList = new ArrayList<>();

        final DirectProjectRevenue directProjectLabourRevenue = new DirectProjectRevenue();
        directProjectLabourRevenue.setProject(project);
        directProjectLabourRevenue.setProjectLevel(projectTask);
        directProjectLabourRevenue.setRevenueCategory(RevenueCategory.DIRECT_REVENUE);
        directProjectLabourRevenue.setCostClassification(CostClassification.LABOUR);
        directProjectLabourRevenue.setCostValue(new Amount(projectTaskModel.getCurrency(), projectTaskModel
                                                           .getTaskRevenueInformation()
                                                           .getBudgetLabourRevenue()));

        directRevenueList.add(directProjectLabourRevenue);

        final DirectProjectRevenue directProjectEquipRevenue = new DirectProjectRevenue();
        directProjectEquipRevenue.setProject(project);
        directProjectEquipRevenue.setProjectLevel(projectTask);
        directProjectEquipRevenue.setRevenueCategory(RevenueCategory.DIRECT_REVENUE);
        directProjectEquipRevenue.setCostClassification(CostClassification.EQUIPMENT);
        directProjectEquipRevenue.setCostValue(new Amount(projectTaskModel.getCurrency(), projectTaskModel
                                                          .getTaskRevenueInformation()
                                                          .getBudgetEquipmentRevenue()));

        directRevenueList.add(directProjectEquipRevenue);

        final DirectProjectRevenue directProjectMaterialRevenue = new DirectProjectRevenue();
        directProjectMaterialRevenue.setProject(project);
        directProjectMaterialRevenue.setProjectLevel(projectTask);
        directProjectMaterialRevenue.setRevenueCategory(RevenueCategory.DIRECT_REVENUE);
        directProjectMaterialRevenue.setCostClassification(CostClassification.MATERIALS);
        directProjectMaterialRevenue.setCostValue(new Amount(projectTaskModel.getCurrency(), projectTaskModel
                                                             .getTaskRevenueInformation()
                                                             .getBudgetMaterialRevenue()));

        directRevenueList.add(directProjectMaterialRevenue);

        final DirectProjectRevenue directProjectSubContRevenue = new DirectProjectRevenue();
        directProjectSubContRevenue.setProject(project);
        directProjectSubContRevenue.setProjectLevel(projectTask);
        directProjectSubContRevenue.setRevenueCategory(RevenueCategory.DIRECT_REVENUE);
        directProjectSubContRevenue.setCostClassification(CostClassification.SUB_CONTRACTORS);
        directProjectSubContRevenue.setCostValue(new Amount(projectTaskModel.getCurrency(), projectTaskModel
                                                            .getTaskRevenueInformation()
                                                            .getBudgetSubContractorRevenue()));

        directRevenueList.add(directProjectSubContRevenue);

        final IndirectProjectRevenue inDirectProjectAllocationRevenue = new IndirectProjectRevenue();
        inDirectProjectAllocationRevenue.setProject(project);
        inDirectProjectAllocationRevenue.setProjectLevel(projectTask);
        inDirectProjectAllocationRevenue.setRevenueCategory(RevenueCategory.INDIRECT_REVENUE);
        inDirectProjectAllocationRevenue.setCostClassification(CostClassification.ALLOCATION);
        inDirectProjectAllocationRevenue.setCostValue(new Amount(projectTaskModel.getCurrency(), projectTaskModel
                                                                 .getTaskRevenueInformation()
                                                                 .getRevenueAllocation()));

        indirectRevenueList.add(inDirectProjectAllocationRevenue);

        final IndirectProjectRevenue inDirectProjectOtherRevenue = new IndirectProjectRevenue();
        inDirectProjectOtherRevenue.setProject(project);
        inDirectProjectOtherRevenue.setProjectLevel(projectTask);
        inDirectProjectOtherRevenue.setRevenueCategory(RevenueCategory.INDIRECT_REVENUE);
        inDirectProjectOtherRevenue.setCostClassification(CostClassification.OTHER);
        inDirectProjectOtherRevenue.setCostValue(new Amount(projectTaskModel.getCurrency(), projectTaskModel
                                                            .getTaskRevenueInformation()
                                                            .getOtherIndirectRevenue()));

        indirectRevenueList.add(inDirectProjectOtherRevenue);
        budgetDetailService.saveTaskBudgetDetails(directCostingList, indirectCostingList, directRevenueList,
                                                  indirectRevenueList);

        saveTeamMember(projectTask, project, projectTaskModel);

    }

    private void saveTeamMember(@Nonnull final ProjectTask projectTask,
                                @Nonnull final Project project,
                                @Nonnull final ProjectTaskModel projectTaskModel) {

        final ProjectTeamMemberModel teamMemberModel = new ProjectTeamMemberModel();
        teamMemberModel.setMemberID(uniqueIdGenerator.nextId());
        teamMemberModel.setTeamMemberCategory(TeamMemberCategory.EMPLOYEE);

        teamMemberModel.setTeamMember(projectTaskModel.getTaskSupervisor());
        teamMemberModel.setCurrency(projectTaskModel.getCurrency());
        teamMemberModel.setTaskUUID(projectTask.getUuid());

        final ProjectRoleModel projectRoleModel = new ProjectRoleModel();
        final Future<List<AccessRole>> accessRoles = asynchronousService.execute(()
                -> getAccessRoles()
        );

        try {
            for (AccessRole accessRole : accessRoles.get()) {
                if (accessRole.getRoleName().equals("Task Supervisor")) {
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
}
