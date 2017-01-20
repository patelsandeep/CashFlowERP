package com.cashflow.project.domain.facade.people;

import com.cashflow.entitydomains.facade.context.ContextQueryBuilder;
import com.cashflow.project.domain.project.level.ProjectLevel_;
import com.cashflow.project.domain.project.personnel.TeamMember;
import com.cashflow.project.domain.project.personnel.TeamMemberContext;
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
 * @since 1 Dec, 2016, 6:42:51 PM
 */
@ApplicationScoped
@ProjectTeamMemberContextBuilder
public class ProjectTeamMemberProjectLevelUUIDQueryBuilder implements ContextQueryBuilder<TeamMemberContext, TeamMember> {

    @Override
    public Optional<Predicate> buildPredicate(@Nonnull final TeamMemberContext context,
                                              @Nonnull final CriteriaBuilder criteriaBuilder,
                                              @Nonnull final Root<TeamMember> queryRoot) {
        checkNotNull(context, "The context must not be null");
        checkNotNull(criteriaBuilder, "The criteriaBuilder must not be null");
        checkNotNull(queryRoot, "The queryRoot must not be null");

        final String projectUUID = context.getProjectLevelUUID();
        if (projectUUID == null) {
            return Optional.empty();
        }
        final Expression<List<String>> projectLevelUUID = queryRoot.
                get(TeamMember_.projectLevel)
                .get(ProjectLevel_.projectLevelsUUIDs);

        return Optional.of(criteriaBuilder
                .isMember(projectUUID, projectLevelUUID));
    }

}
