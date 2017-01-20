package com.cashflow.project.domain.service.milestone;

import com.cashflow.access.authorization.LoggedInBusinessAccount;
import com.cashflow.backend.businessaccount.authorization.RequiresBusinessAccount;
import com.cashflow.project.api.milestone.ProjectMilestoneService;
import com.cashflow.project.domain.facade.budget.BudgetFacade;
import com.cashflow.project.domain.facade.deposit.DepositFacade;
import com.cashflow.project.domain.facade.level.ProjectLevelProgressFacade;
import com.cashflow.project.domain.facade.milestone.MilestoneContextFacade;
import com.cashflow.project.domain.facade.milestone.ProjectMilestoneFacade;
import com.cashflow.project.domain.project.budget.Budget;
import com.cashflow.project.domain.project.budget.Deposit;
import com.cashflow.project.domain.project.level.ProjectLevelProgress;
import com.cashflow.project.domain.project.milestone.MilestoneContext;
import com.cashflow.project.domain.project.milestone.ProjectMilestone;
import com.cashflow.useraccount.domain.businessaccount.BusinessAccount;
import java.util.List;
import javax.annotation.Nonnull;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since 21 Nov, 2016, 7:20:03 PM
 */
@Stateless
@Dependent
@RequiresBusinessAccount
@Local(ProjectMilestoneService.class)
public class ProjectMilestoneServiceImpl implements ProjectMilestoneService {

    @EJB
    private ProjectMilestoneFacade milestoneFacade;

    @EJB
    private MilestoneContextFacade milestoneContextFacade;

    @EJB
    private BudgetFacade budgetFacade;

    @EJB
    private DepositFacade depositFacade;

    @EJB
    private ProjectLevelProgressFacade projectLevelProgressFacade;

    @Inject
    @LoggedInBusinessAccount
    private Instance<BusinessAccount> businessAccount;

    @Override
    public void saveProjectMilestone(@Nonnull final ProjectMilestone projectMilestone,
                                     @Nonnull final Budget budget,
                                     @Nonnull final Deposit deposit,
                                     @Nonnull final ProjectLevelProgress projectLevelProgress) {
        checkNotNull(projectMilestone, "The projectMilestone must not be null");
        checkNotNull(budget, "The budget must not be null");
        checkNotNull(deposit, "The deposit must not be null");
        checkNotNull(projectLevelProgress, "The projectLevelProgress must not be null");

        milestoneFacade.edit(projectMilestone);
        budgetFacade.edit(budget);
        depositFacade.edit(deposit);
        projectLevelProgressFacade.edit(projectLevelProgress);
    }

    @Override
    public ProjectMilestone getMilestone(@Nonnull final String projectPhaseIdOrUUID) {
        return milestoneFacade
                .findByMilestoneIdOrUUID(projectPhaseIdOrUUID,
                                         businessAccount.get().getAccountId());
    }

    @Override
    public List<ProjectMilestone> findByPhaseUUIDs(@Nonnull final List<String> phaseUUIDs) {
        checkNotNull(phaseUUIDs, "The phaseUUID must not be null");

        return milestoneFacade
                .findByPhaseUUIDs(phaseUUIDs);
    }

    @Override
    @Nonnull
    public List<ProjectMilestone> getMilestones(@Nonnull final MilestoneContext milestoneContext) {
        checkNotNull(milestoneContext, "The milestoneContext must not be null");

        return milestoneContextFacade.find(businessAccount.get().getAccountId(),
                                           milestoneContext);
    }

}
