package com.cashflow.project.domain.facade.expense;

import com.cashflow.entitydomains.facade.context.ContextQueryBuilder;
import com.cashflow.project.domain.expenseinformation.ExpenseReportContext;
import com.cashflow.project.domain.project.expense.ExpenseReport;
import com.cashflow.project.domain.project.expense.ExpenseReport_;
import com.cashflow.project.domain.project.personnel.SubContractor_;
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
 * @since 15 Dec, 2016, 1:33:37 PM
 */
@ApplicationScoped
@ExpenseReportContextBuilder
public class ExpenseSubcontractorUUIDQueryBuilder implements ContextQueryBuilder<ExpenseReportContext, ExpenseReport> {

    @Override
    public Optional<Predicate> buildPredicate(@Nonnull final ExpenseReportContext context,
                                              @Nonnull final CriteriaBuilder criteriaBuilder,
                                              @Nonnull final Root<ExpenseReport> queryRoot) {
        checkNotNull(context, "The context must not be null");
        checkNotNull(criteriaBuilder, "The criteriaBuilder must not be null");
        checkNotNull(queryRoot, "The queryRoot must not be null");

        final String subContractorUUID = context.getSubContractorUUID();
        if (subContractorUUID == null) {
            return Optional.empty();
        }

        final Expression<String> subContractorUUIDPath = queryRoot
                .get(ExpenseReport_.subContractor)
                .get(SubContractor_.uuid);
        return Optional.of(criteriaBuilder
                .equal(subContractorUUIDPath, subContractorUUID));

    }

}
