package com.cashflow.project.domain.service.revenue;

import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.backend.businessaccount.authorization.RequiresBusinessAccount;
import com.cashflow.project.api.taskbudget.TaskBudgetDetailService;
import com.cashflow.project.domain.facade.costing.DirectCostingFacade;
import com.cashflow.project.domain.facade.costing.IndirectCostingFacade;
import com.cashflow.project.domain.facade.revenue.DirectRevenueFacade;
import com.cashflow.project.domain.facade.revenue.IndirectRevenueFacade;
import com.cashflow.project.domain.project.costing.DirectProjectCosting;
import com.cashflow.project.domain.project.costing.IndirectProjectCosting;
import com.cashflow.project.domain.project.revenue.DirectProjectRevenue;
import com.cashflow.project.domain.project.revenue.IndirectProjectRevenue;
import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since 25 Nov, 2016, 12:36:39 PM
 */
@Stateless
@Dependent
@RequiresBusinessAccount
@Local(TaskBudgetDetailService.class)
public class TaskBudgetDetailServiceImpl implements TaskBudgetDetailService {

    @EJB
    private IndirectRevenueFacade indirectRevenueFacade;

    @EJB
    private DirectRevenueFacade directRevenueFacade;

    @EJB
    private AsynchronousService asynchronousService;

    @EJB
    private IndirectCostingFacade indirectCostingFacade;

    @EJB
    private DirectCostingFacade directCostingFacade;

    @Override
    public void saveTaskBudgetDetails(List<DirectProjectCosting> directProjectCostings,
                                      List<IndirectProjectCosting> indirectProjectCostings,
                                      List<DirectProjectRevenue> directProjectRevenues,
                                      List<IndirectProjectRevenue> indirectProjectRevenues) {
        checkNotNull(directProjectCostings, "The directProjectCostings must not be null");
        checkNotNull(indirectProjectCostings, "The indirectProjectCostings must not be null");
        checkNotNull(directProjectRevenues, "The directProjectRevenues must not be null");
        checkNotNull(indirectProjectRevenues, "The indirectProjectRevenues must not be null");

        directProjectCostings.forEach((costing) -> {
            asynchronousService.execute(() -> {
                directCostingFacade.edit(costing);
            });
        });

        indirectProjectCostings.forEach((costing) -> {
            asynchronousService.execute(() -> {
                indirectCostingFacade.edit(costing);
            });

        });

        directProjectRevenues.forEach((revenue) -> {

            asynchronousService.execute(() -> {
                directRevenueFacade.edit(revenue);
            });

        });

        indirectProjectRevenues.forEach((revenue) -> {

            asynchronousService.execute(() -> {
                indirectRevenueFacade.edit(revenue);
            });

        });

    }

    @Override
    public List<DirectProjectCosting> getDirectCostings(@Nonnull final List<String> projectLevelUUIDs) {
        checkNotNull(projectLevelUUIDs, "The projectLevelUUIDs must not be null");
        if (projectLevelUUIDs.isEmpty()) {
            return ImmutableList.of();
        }
        return directCostingFacade.getDirectProjectCostings(projectLevelUUIDs);
    }

    @Override
    public List<IndirectProjectCosting> getIndirectCostings(@Nonnull final List<String> projectLevelUUIDs) {
        checkNotNull(projectLevelUUIDs, "The projectLevelUUIDs must not be null");
        if (projectLevelUUIDs.isEmpty()) {
            return ImmutableList.of();
        }
        return indirectCostingFacade.getIndirectProjectCostings(projectLevelUUIDs);
    }

    @Override
    public List<DirectProjectRevenue> getDirectRevenues(@Nonnull final List<String> projectLevelUUIDs) {
        checkNotNull(projectLevelUUIDs, "The projectLevelUUIDs must not be null");
        if (projectLevelUUIDs.isEmpty()) {
            return ImmutableList.of();
        }
        return directRevenueFacade.getDirectProjectRevenues(projectLevelUUIDs);
    }

    @Override
    public List<IndirectProjectRevenue> getIndirectRevenues(@Nonnull final List<String> projectLevelUUIDs) {
        checkNotNull(projectLevelUUIDs, "The projectLevelUUIDs must not be null");
        if (projectLevelUUIDs.isEmpty()) {
            return ImmutableList.of();
        }
        return indirectRevenueFacade.getIndirectProjectRevenues(projectLevelUUIDs);
    }

}
