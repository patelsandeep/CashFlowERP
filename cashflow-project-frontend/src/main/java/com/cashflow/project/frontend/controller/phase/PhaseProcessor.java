package com.cashflow.project.frontend.controller.phase;

import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.access.authorization.uniqueid.IdContext;
import com.cashflow.access.authorization.uniqueid.UniqueIdGenerator;
import com.cashflow.accessroles.domain.accessrole.AccessRole;
import com.cashflow.accessroles.service.accessrole.AccessRoleRequestContext;
import com.cashflow.accessroles.service.accessrole.AccessRoleService;
import com.cashflow.project.api.phase.ProjectPhaseService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.ProjectStatus;
import com.cashflow.project.domain.project.budget.Budget;
import com.cashflow.project.domain.project.budget.Deposit;
import com.cashflow.project.domain.project.level.LevelStatus;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.level.ProjectLevelCategory;
import com.cashflow.project.domain.project.level.ProjectLevelProgress;
import com.cashflow.project.domain.project.personnel.CostRateSource;
import com.cashflow.project.domain.project.personnel.MarkUpMethod;
import com.cashflow.project.domain.project.personnel.TeamMember;
import com.cashflow.project.domain.project.personnel.TeamMemberCategory;
import com.cashflow.project.domain.project.phase.ProjectPhase;
import com.cashflow.project.frontend.controller.model.ProjectPhaseModel;
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
 */
@Dependent
public class PhaseProcessor implements Serializable {

    private static final long serialVersionUID = 9199661073214227126L;

    @Inject
    private Logger logger;

    @Inject
    @IdContext(forDomain = TeamMember.class, prefix = "E", length = 6)
    private UniqueIdGenerator uniqueIdGenerator;

    @Inject
    private ProjectPhaseService projectPhaseService;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private AccessRoleService accessRoleService;

    @Inject
    private ProjectTeamMemberProcessor memberProcessor;

    @Profile
    public void process(@Nonnull final ProjectPhaseModel phaseModel,
                        @Nonnull final Project project) {

        checkNotNull(phaseModel, "The phaseModel must not be null");
        checkNotNull(project, "The project must not be null");

        final ProjectPhase projectPhase = new ProjectPhase(phaseModel.getPhaseNumber());
        projectPhase.setName(phaseModel.getPhaseName());
        projectPhase.setId(phaseModel.getPhaseId());
        projectPhase.setParentLevel(project);
        projectPhase.setProjectLevelCategory(ProjectLevelCategory.PHASE);
        projectPhase.setSummary(phaseModel.getPhaseSummary());
        projectPhase.setDeliverables(phaseModel.getPhaseDeliverable());
        projectPhase.setManager(phaseModel.getPhaseSupervisor().getSupervisorUUID());
        projectPhase.setStartDate(phaseModel.getStartDate());
        projectPhase.setEndDate(phaseModel.getEndDate());
        projectPhase.setSafetyRating(phaseModel.getSafetyRating());
        projectPhase.setBusinessUnitUUID(project.getBusinessUnitUUID());
        projectPhase.setDepartmentUUID(project.getDepartmentUUID());
        projectPhase.setDocuments(phaseModel.getPhaseFileUrls());
        final List<String> projectLevelUUIDs = new ArrayList<>();
        addAllParentProjectLevelUUIDs(projectLevelUUIDs, projectPhase);
        projectPhase.setProjectLevelsUUIDs(projectLevelUUIDs);

        final Budget budget = Budget.builder()
                .projectLevel(projectPhase)
                .budgetedCost(new Amount(phaseModel.getCurrency(), phaseModel.getPhaseBudgetCost()))
                .budgetedRevenue(new Amount(phaseModel.getCurrency(), phaseModel.getPhaseBudgetRevenue()))
                .budgetedGrossProfit(calculateBudgetedGrossProfit(phaseModel))
                .build();

        final Deposit deposit = Deposit.builder()
                .projectLevel(projectPhase)
                .amount(new Amount(phaseModel.getCurrency(), phaseModel.getDepositOrRetainers()))
                .build();

        final ProjectLevelProgress projectLevelProgress = ProjectLevelProgress.builder()
                .percentOfCompletion(phaseModel.getPoc())
                .levelStatus(LevelStatus.IN_PROGRESS)
                .performanceStatus(phaseModel.getPerformanceStatus())
                .physicalPercentageOfCompletion(phaseModel.getPpc())
                .projectLevel(projectPhase)
                .projectStatus(ProjectStatus.PENDING)
                .build();

        projectPhaseService.saveProjectPhase(projectPhase, budget, deposit, projectLevelProgress);
        saveTeamMember(project, projectPhase, phaseModel);
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
    private Amount calculateBudgetedGrossProfit(@Nonnull final ProjectPhaseModel phaseModel) {
        return new Amount(phaseModel.getCurrency(), phaseModel.getPhaseBudgetRevenue().subtract(phaseModel
                          .getPhaseBudgetCost()));
    }

    private void saveTeamMember(@Nonnull final Project project,
                                @Nonnull final ProjectPhase projectPhase,
                                @Nonnull final ProjectPhaseModel phaseModel) {

        final ProjectTeamMemberModel teamMemberModel = new ProjectTeamMemberModel();
        teamMemberModel.setMemberID(uniqueIdGenerator.nextId());
        teamMemberModel.setTeamMemberCategory(TeamMemberCategory.EMPLOYEE);

        teamMemberModel.setTeamMember(phaseModel.getPhaseSupervisor());
        teamMemberModel.setCurrency(phaseModel.getCurrency());
        teamMemberModel.setPhaseUUID(projectPhase.getUuid());

        final ProjectRoleModel projectRoleModel = new ProjectRoleModel();
        final Future<List<AccessRole>> accessRoles = asynchronousService.execute(()
                -> getAccessRoles()
        );

        try {
            for (AccessRole accessRole : accessRoles.get()) {
                if (accessRole.getRoleName().equals("Phase Supervisor")) {
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
