package com.cashflow.project.domain.service.project;

import com.cashflow.access.authorization.LoggedInBusinessAccount;
import com.cashflow.backend.businessaccount.authorization.RequiresBusinessAccount;
import com.cashflow.project.api.project.ProjectService;
import com.cashflow.project.domain.facade.budget.BudgetFacade;
import com.cashflow.project.domain.facade.deposit.DepositFacade;
import com.cashflow.project.domain.facade.level.ProjectLevelProgressFacade;
import com.cashflow.project.domain.facade.level.ProjectLevelSettingFacade;
import com.cashflow.project.domain.facade.project.ProjectFacade;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.budget.Budget;
import com.cashflow.project.domain.project.budget.Deposit;
import com.cashflow.project.domain.project.level.ProjectLevelProgress;
import com.cashflow.project.domain.project.level.ProjectLevelSetting;
import com.cashflow.project.domain.service.BusinessUnitUuidResolver;
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
 */
@Stateless
@Dependent
@RequiresBusinessAccount
@Local(ProjectService.class)
public class ProjectServiceImpl implements ProjectService {

    @EJB
    private ProjectFacade projectFacade;

    @EJB
    private BudgetFacade budgetFacade;

    @EJB
    private DepositFacade depositFacade;

    @EJB
    private ProjectLevelSettingFacade projectLevelSettingFacade;

    @EJB
    private ProjectLevelProgressFacade projectLevelProgressFacade;

    @Inject
    private BusinessUnitUuidResolver businessUnitUuidResolver;

    @Inject
    @LoggedInBusinessAccount
    private Instance<BusinessAccount> businessAccount;

    @Override
    public void saveProject(@Nonnull final Project project,
                            @Nonnull final Budget budget,
                            @Nonnull final Deposit deposit,
                            @Nonnull final ProjectLevelSetting projectLevelSetting,
                            @Nonnull final ProjectLevelProgress projectLevelProgress) {
        checkNotNull(project, "The project must not be null");
        checkNotNull(budget, "The budget must not be null");
        checkNotNull(deposit, "The deposit must not be null");
        checkNotNull(projectLevelSetting, "The projectLevelSetting must not be null");
        checkNotNull(projectLevelProgress, "The projectLevelProgress must not be null");

        final String businessUnitUUID = checkNotNull(project.getBusinessUnitUUID());
        final List<String> businessUnitUUIDs = businessUnitUuidResolver.getBusinessUnitUuidHierarchy(businessUnitUUID);
        project.setBusinessUnitUUIDs(businessUnitUUIDs);
        projectFacade.edit(project);

        budgetFacade.edit(budget);
        depositFacade.edit(deposit);
        projectLevelSettingFacade.edit(projectLevelSetting);
        projectLevelProgressFacade.edit(projectLevelProgress);
    }

    @Override
    @Nullable
    public Project getProject(@Nonnull final String projectIdOrUUID) {
        checkNotNull(projectIdOrUUID, "The projectIdOrUUID must not be null");
        return projectFacade
                .findByProjectIdOrUUID(projectIdOrUUID,
                                       businessAccount.get().getAccountId());
    }

}
