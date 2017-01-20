package com.cashflow.project.domain.facade.expense.subcontractor;

import com.cashflow.entitydomains.facade.context.ContextQueryBuilder;
import com.cashflow.project.domain.project.expense.subcontractor.SubContractorExpense;
import com.cashflow.project.domain.project.expense.subcontractor.SubContractorExpenseContext;
import com.cashflow.project.domain.project.expense.subcontractor.SubContractorExpense_;
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
 * @since Jan 16, 2017, 3:16:18 PM
 */
@ApplicationScoped
@SubContractorExpenseContextBuilder
public class SubContractorExpenseSupplierQueryBuilder implements ContextQueryBuilder<SubContractorExpenseContext, SubContractorExpense> {

    @Override
    public Optional<Predicate> buildPredicate(@Nonnull final SubContractorExpenseContext context,
                                              @Nonnull final CriteriaBuilder criteriaBuilder,
                                              @Nonnull final Root<SubContractorExpense> queryRoot) {
        checkNotNull(context, "The context must not be null");
        checkNotNull(criteriaBuilder, "The criteriaBuilder must not be null");
        checkNotNull(queryRoot, "The queryRoot must not be null");

        final String supplierUUID = context.getSupplierUUID();
        if (supplierUUID == null) {
            return Optional.empty();
        }
        final Expression<String> supplierUUIDPath = queryRoot
                .get(SubContractorExpense_.supplierUUID);
        return Optional.of(criteriaBuilder
                .equal(supplierUUIDPath, supplierUUID));

    }

}
