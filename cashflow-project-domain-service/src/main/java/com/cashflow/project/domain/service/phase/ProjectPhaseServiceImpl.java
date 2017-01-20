package com.cashflow.project.domain.service.phase;

import com.cashflow.access.authorization.LoggedInBusinessAccount;
import com.cashflow.backend.businessaccount.authorization.RequiresBusinessAccount;
import com.cashflow.project.api.phase.ProjectPhaseService;
import com.cashflow.project.domain.facade.budget.BudgetFacade;
import com.cashflow.project.domain.facade.deposit.DepositFacade;
import com.cashflow.project.domain.facade.level.ProjectLevelProgressFacade;
import com.cashflow.project.domain.facade.phase.PhaseContextFacade;
import com.cashflow.project.domain.facade.phase.ProjectPhaseFacade;
import com.cashflow.project.domain.project.budget.Budget;
import com.cashflow.project.domain.project.budget.Deposit;
import com.cashflow.project.domain.project.level.ProjectLevelProgress;
import com.cashflow.project.domain.project.phase.PhaseContext;
import com.cashflow.project.domain.project.phase.ProjectPhase;
import com.cashflow.useraccount.domain.businessaccount.BusinessAccount;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
 * @since Nov 17, 2016, 11:05:28 PM
 */
@Stateless
@Dependent
@RequiresBusinessAccount
@Local(ProjectPhaseService.class)
public class ProjectPhaseServiceImpl implements ProjectPhaseService {

    @EJB
    private PhaseContextFacade phaseContextFacade;

    @EJB
    private ProjectPhaseFacade projectPhaseFacade;

    @EJB
    private BudgetFacade budgetFacade;

    @EJB
    private DepositFacade depositFacade;

    @EJB
    private ProjectLevelProgressFacade projectLevelProgressFacade;

    @Inject
    @LoggedInBusinessAccount
    private Instance<BusinessAccount> businessAccount;

    @Nonnull
    @Override
    public List<ProjectPhase> getProjectPhases(@Nonnull final PhaseContext phaseContext) {
        return phaseContextFacade.find(businessAccount.get().getAccountId(),
                                       phaseContext);
    }

    @Nonnull
    @Override
    public Integer count(@Nonnull final PhaseContext phaseContext) {
        return phaseContextFacade.count(businessAccount.get().getAccountId(),
                                        phaseContext);
    }

    @Override
    public void saveProjectPhase(@Nonnull final ProjectPhase projectPhase,
                                 @Nonnull final Budget budget,
                                 @Nonnull final Deposit deposit,
                                 @Nonnull final ProjectLevelProgress projectLevelProgress) {
        checkNotNull(projectPhase, "The projectPhase must not be null");
        checkNotNull(budget, "The budget must not be null");
        checkNotNull(deposit, "The deposit must not be null");
        checkNotNull(projectLevelProgress, "The projectLevelProgress must not be null");

        projectPhaseFacade.edit(projectPhase);
        budgetFacade.edit(budget);
        depositFacade.edit(deposit);
        projectLevelProgressFacade.edit(projectLevelProgress);
    }

    @Override
    @Nullable
    public ProjectPhase getPhase(@Nonnull final String phaseIdOrUUID) {
        checkNotNull(phaseIdOrUUID, "The phaseIdOrUUID must not be null");
        return projectPhaseFacade
                .findByPhaseIdOrUUID(phaseIdOrUUID,
                                     businessAccount.get().getAccountId());
    }

}
