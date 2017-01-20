package com.cashflow.project.domain.service.task;

import com.cashflow.access.authorization.LoggedInBusinessAccount;
import com.cashflow.backend.businessaccount.authorization.RequiresBusinessAccount;
import com.cashflow.project.api.task.ProjectTaskService;
import com.cashflow.project.domain.facade.budget.BudgetFacade;
import com.cashflow.project.domain.facade.deposit.DepositFacade;
import com.cashflow.project.domain.facade.level.ProjectLevelProgressFacade;
import com.cashflow.project.domain.facade.task.ProjectTaskFacade;
import com.cashflow.project.domain.facade.task.TaskContextFacade;
import com.cashflow.project.domain.project.budget.Budget;
import com.cashflow.project.domain.project.budget.Deposit;
import com.cashflow.project.domain.project.level.ProjectLevelProgress;
import com.cashflow.project.domain.project.task.ProjectTask;
import com.cashflow.project.domain.project.task.TaskContext;
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
 * @since 23 Nov, 2016, 12:39:28 PM
 */
@Stateless
@Dependent
@RequiresBusinessAccount
@Local(ProjectTaskService.class)
public class ProjectTaskServiceImpl implements ProjectTaskService {

    @EJB
    private ProjectTaskFacade projectTaskFacade;

    @EJB
    private TaskContextFacade taskContextFacade;

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
    public void saveProjectTask(@Nonnull final ProjectTask projectTask,
                                @Nonnull final Budget budget,
                                @Nonnull final Deposit deposit,
                                @Nonnull final ProjectLevelProgress projectLevelProgress) {
        checkNotNull(projectTask, "The projectTask must not be null");
        checkNotNull(budget, "The budget must not be null");
        checkNotNull(deposit, "The deposit must not be null");
        checkNotNull(projectLevelProgress, "The projectLevelProgress must not be null");

        projectTaskFacade.edit(projectTask);

        budgetFacade.edit(budget);

        depositFacade.edit(deposit);

        projectLevelProgressFacade.edit(projectLevelProgress);
    }

    @Override
    public ProjectTask getTask(@Nonnull final String projectTaskIdOrUUID) {
        checkNotNull(projectTaskIdOrUUID, "The projectTaskIdOrUUID must not be null");
        return projectTaskFacade
                .findByTaskIdOrUUID(projectTaskIdOrUUID,
                                    businessAccount.get().getAccountId());
    }

    @Override
    public List<ProjectTask> findByMilestoneOrProjectUUIDs(@Nonnull final List<String> milestoneOrProjectUUIDs) {
        checkNotNull(milestoneOrProjectUUIDs, "The milestoneOrProjectUUIDs must not be null");
        return projectTaskFacade
                .findByMilestoneOrProjectUUIDs(milestoneOrProjectUUIDs);
    }

    @Override
    @Nonnull
    public List<ProjectTask> getTasks(@Nonnull final TaskContext taskContext) {
        checkNotNull(taskContext, "The taskContext must not be null");

        return taskContextFacade.find(businessAccount.get().getAccountId(),
                                      taskContext);
    }

    @Override
    @Nonnull
    public Integer count(@Nonnull final TaskContext taskContext) {
        checkNotNull(taskContext, "The taskContext must not be null");

        return taskContextFacade.count(businessAccount.get().getAccountId(),
                                       taskContext);
    }

}
