package com.cashflow.project.domain.facade.people;

import com.cashflow.entitydomains.facade.context.ContextQueryBuilder;
import com.cashflow.project.domain.project.personnel.TeamMember;
import com.cashflow.project.domain.project.personnel.TeamMemberContext;
import com.cashflow.project.domain.project.personnel.TeamMember_;
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
 * @since 1 Dec, 2016, 3:30:03 PM
 */
@ApplicationScoped
@ProjectTeamMemberContextBuilder
public class ProjectTeamMemberSearchQueryBuilder implements ContextQueryBuilder<TeamMemberContext, TeamMember> {

    @Override
    public Optional<Predicate> buildPredicate(@Nonnull final TeamMemberContext context,
                                              @Nonnull final CriteriaBuilder criteriaBuilder,
                                              @Nonnull final Root<TeamMember> queryRoot) {
        checkNotNull(context, "The context must not be null");
        checkNotNull(criteriaBuilder, "The criteriaBuilder must not be null");
        checkNotNull(queryRoot, "The queryRoot must not be null");

        final String searchValue = context.getSearch();
        if (searchValue == null) {
            return Optional.empty();
        }
        final Path<String> memberId = queryRoot.
                get(TeamMember_.memberId);

        final Path<String> empUUID = queryRoot.
                get(TeamMember_.employeeUUID);

        return Optional.of(criteriaBuilder.or(
                criteriaBuilder.like(memberId, "%" + searchValue + "%"),
                criteriaBuilder.equal(empUUID, searchValue)));
    }

}
