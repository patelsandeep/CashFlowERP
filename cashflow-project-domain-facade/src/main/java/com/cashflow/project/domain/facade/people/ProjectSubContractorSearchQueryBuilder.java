package com.cashflow.project.domain.facade.people;

import com.cashflow.entitydomains.facade.context.ContextQueryBuilder;
import com.cashflow.project.domain.project.personnel.SubContractor;
import com.cashflow.project.domain.project.personnel.SubContractor_;
import com.cashflow.project.domain.project.personnel.SubcontractorContext;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since 1 Dec, 2016, 11:48:57 AM
 */
@ApplicationScoped
@ProjectSubContractorContextBuilder
public class ProjectSubContractorSearchQueryBuilder implements ContextQueryBuilder<SubcontractorContext, SubContractor> {

    @Override
    public Optional<Predicate> buildPredicate(@Nonnull final SubcontractorContext context,
                                              @Nonnull final CriteriaBuilder criteriaBuilder,
                                              @Nonnull final Root<SubContractor> queryRoot) {
        checkNotNull(context, "The context must not be null");
        checkNotNull(criteriaBuilder, "The criteriaBuilder must not be null");
        checkNotNull(queryRoot, "The queryRoot must not be null");

        final String searchValue = context.getSearch();
        if (searchValue == null) {
            return Optional.empty();
        }
        final Path<String> memberId = queryRoot.
                get(SubContractor_.memberId);

        final Path<String> memberName = queryRoot.
                get(SubContractor_.memberName);

        return Optional.of(criteriaBuilder.or(
                criteriaBuilder.like(memberId, "%" + searchValue + "%"),
                criteriaBuilder.like(memberName, "%" + searchValue + "%")));
    }
}
