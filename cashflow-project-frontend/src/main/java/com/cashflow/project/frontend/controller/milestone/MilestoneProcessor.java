package com.cashflow.project.frontend.controller.milestone;

import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.access.authorization.uniqueid.IdContext;
import com.cashflow.access.authorization.uniqueid.UniqueIdGenerator;
import com.cashflow.accessroles.domain.accessrole.AccessRole;
import com.cashflow.accessroles.service.accessrole.AccessRoleRequestContext;
import com.cashflow.accessroles.service.accessrole.AccessRoleService;
import com.cashflow.project.api.milestone.ProjectMilestoneService;
import com.cashflow.project.api.project.ProjectService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.ProjectStatus;
import com.cashflow.project.domain.project.budget.Budget;
import com.cashflow.project.domain.project.budget.Deposit;
import com.cashflow.project.domain.project.level.LevelStatus;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.level.ProjectLevelCategory;
import com.cashflow.project.domain.project.level.ProjectLevelProgress;
import com.cashflow.project.domain.project.milestone.ProjectMilestone;
import com.cashflow.project.domain.project.personnel.CostRateSource;
import com.cashflow.project.domain.project.personnel.MarkUpMethod;
import com.cashflow.project.domain.project.personnel.TeamMember;
import com.cashflow.project.domain.project.personnel.TeamMemberCategory;
import com.cashflow.project.domain.project.phase.ProjectPhase;
import com.cashflow.project.frontend.controller.model.ProjectMilestoneModel;
import com.cashflow.project.frontend.controller.model.ProjectRoleModel;
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

/**
 *
 * 
 * @since 22 Nov, 2016, 2:16:29 PM
 */
@Dependent
public class MilestoneProcessor implements Serializable {

    private static final long serialVersionUID = 3547932654526157723L;

    @Inject
    private Logger logger;

    @Inject
    @IdContext(forDomain = TeamMember.class, prefix = "E", length = 6)
    private UniqueIdGenerator uniqueIdGenerator;

    @Inject
    private ProjectMilestoneService projectMilestoneService;

    @Inject
    private AccessRoleService accessRoleService;

    @Inject
    private ProjectService projectService;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private ProjectTeamMemberProcessor memberProcessor;

    @Profile
    public void process(@Nonnull final ProjectMilestoneModel milestoneModel,
                        @Nonnull final ProjectPhase projectPhase) {

        checkNotNull(milestoneModel, "The milestoneModel must not be null");
        checkNotNull(projectPhase, "The projectPhase must not be null");

        final ProjectMilestone projectMilestone = new ProjectMilestone(milestoneModel.getMilestoneNumber());
        projectMilestone.setName(milestoneModel.getMilestoneName());
        projectMilestone.setId(milestoneModel.getMilestoneId());
        projectMilestone.setParentLevel(projectPhase);
        projectMilestone.setProjectLevelCategory(ProjectLevelCategory.MILESTONE);
        projectMilestone.setSummary(milestoneModel.getMilestoneSummary());
        projectMilestone.setDeliverables(milestoneModel.getMilestoneDeliverable());
        projectMilestone.setManager(milestoneModel.getMilestoneSupervisor().getSupervisorUUID());
        projectMilestone.setStartDate(milestoneModel.getStartDate());
        projectMilestone.setEndDate(milestoneModel.getEndDate());
        projectMilestone.setSafetyRating(milestoneModel.getSafetyRating());
        projectMilestone.setBusinessUnitUUID(projectPhase.getBusinessUnitUUID());
        projectMilestone.setDepartmentUUID(projectPhase.getDepartmentUUID());
        projectMilestone.setDocuments(milestoneModel.getMilestoneFileUrls());
        final List<String> projectLevelUUIDs = new ArrayList<>();
        addAllParentProjectLevelUUIDs(projectLevelUUIDs, projectMilestone);
        projectMilestone.setProjectLevelsUUIDs(projectLevelUUIDs);

        final Budget budget = Budget.builder()
                .projectLevel(projectMilestone)
                .budgetedCost(new Amount(milestoneModel.getCurrency(), milestoneModel.getMilestoneBudgetCost()))
                .budgetedRevenue(new Amount(milestoneModel.getCurrency(), milestoneModel.getMilestoneBudgetRevenue()))
                .budgetedGrossProfit(calculateBudgetedGrossProfit(milestoneModel))
                .build();

        final Deposit deposit = Deposit.builder()
                .projectLevel(projectMilestone)
                .amount(new Amount(milestoneModel.getCurrency(), milestoneModel.getDepositOrRetainers()))
                .build();

        final ProjectLevelProgress projectLevelProgress = ProjectLevelProgress.builder()
                .percentOfCompletion(milestoneModel.getPoc())
                .levelStatus(LevelStatus.IN_PROGRESS)
                .performanceStatus(milestoneModel.getPerformanceStatus())
                .physicalPercentageOfCompletion(milestoneModel.getPpc())
                .projectLevel(projectMilestone)
                .projectStatus(ProjectStatus.PENDING)
                .build();

        projectMilestoneService.saveProjectMilestone(projectMilestone, budget, deposit, projectLevelProgress);

        saveTeamMember(projectMilestone, projectPhase, milestoneModel);
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
    private Amount calculateBudgetedGrossProfit(@Nonnull final ProjectMilestoneModel milestoneModel) {
        return new Amount(milestoneModel.getCurrency(), milestoneModel.getMilestoneBudgetRevenue().subtract(
                          milestoneModel
                          .getMilestoneBudgetCost()));
    }

    private void saveTeamMember(@Nonnull final ProjectMilestone milestone,
                                @Nonnull final ProjectPhase projectPhase,
                                @Nonnull final ProjectMilestoneModel milestoneModel) {

        final Project project = projectService.getProject(projectPhase.getParentLevel().getUuid());

        final ProjectTeamMemberModel teamMemberModel = new ProjectTeamMemberModel();
        teamMemberModel.setMemberID(uniqueIdGenerator.nextId());
        teamMemberModel.setTeamMemberCategory(TeamMemberCategory.EMPLOYEE);

        teamMemberModel.setTeamMember(milestoneModel.getMilestoneSupervisor());
        teamMemberModel.setCurrency(milestoneModel.getCurrency());
        teamMemberModel.setMilestoneUUID(milestone.getUuid());

        final ProjectRoleModel projectRoleModel = new ProjectRoleModel();
        final Future<List<AccessRole>> accessRoles = asynchronousService.execute(()
                -> getAccessRoles());
        try {
            for (AccessRole accessRole : accessRoles.get()) {
                if (accessRole.getRoleName().equals("Milestone Supervisor")) {
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
