package com.cashflow.project.domain.facade.expense;

import com.cashflow.entitydomains.facade.context.ContextQueryBuilder;
import com.cashflow.project.domain.expenseinformation.ExpenseReportContext;
import com.cashflow.project.domain.project.expense.ExpenseReport;
import com.cashflow.project.domain.project.expense.ExpenseReport_;
import com.cashflow.project.domain.project.personnel.SubContractor_;
import com.cashflow.project.domain.project.personnel.TeamMember_;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since 4 Jan, 2017, 5:51:10 PM
 */
@ApplicationScoped
@ExpenseReportContextBuilder
public class ExpenseReportPeoplesQueryBuilder implements ContextQueryBuilder<ExpenseReportContext, ExpenseReport> {

    @Override
    public Optional<Predicate> buildPredicate(@Nonnull final ExpenseReportContext context,
                                              @Nonnull final CriteriaBuilder criteriaBuilder,
                                              @Nonnull final Root<ExpenseReport> queryRoot) {
        checkNotNull(context, "The context must not be null");
        checkNotNull(criteriaBuilder, "The criteriaBuilder must not be null");
        checkNotNull(queryRoot, "The queryRoot must not be null");

        final List<String> peopleUUIDs = context.getPeopleUUIDs();
        if (null == peopleUUIDs || peopleUUIDs.isEmpty()) {
            return Optional.empty();
        }
        final Expression<String> teamMemberUUIDPath = queryRoot
                .get(ExpenseReport_.teamMember)
                .get(TeamMember_.uuid);

        final Expression<String> subContractorUUIDPath = queryRoot
                .get(ExpenseReport_.subContractor)
                .get(SubContractor_.uuid);
        return Optional.of(criteriaBuilder.or(teamMemberUUIDPath.in(peopleUUIDs),
                                              (subContractorUUIDPath.in(peopleUUIDs))));

    }

}
