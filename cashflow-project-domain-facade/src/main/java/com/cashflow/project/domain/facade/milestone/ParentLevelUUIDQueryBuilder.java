package com.cashflow.project.domain.facade.milestone;

import com.cashflow.entitydomains.facade.context.ContextQueryBuilder;
import com.cashflow.project.domain.project.milestone.MilestoneContext;
import com.cashflow.project.domain.project.milestone.ProjectMilestone;
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
 */
@ApplicationScoped
@MilestoneContextQueryBuilder
public class ParentLevelUUIDQueryBuilder implements ContextQueryBuilder<MilestoneContext, ProjectMilestone> {

    @Override
    public Optional<Predicate> buildPredicate(@Nonnull final MilestoneContext context,
                                              @Nonnull final CriteriaBuilder criteriaBuilder,
                                              @Nonnull final Root<ProjectMilestone> queryRoot) {
        checkNotNull(context, "The context must not be null");
        checkNotNull(criteriaBuilder, "The criteriaBuilder must not be null");
        checkNotNull(queryRoot, "The queryRoot must not be null");

        final String parentLevelUUID = context.getParentLevelUUID();
        if (parentLevelUUID == null) {
            return Optional.empty();
        }

        final Expression<List<String>> projectLevelUUIDs = queryRoot
                .get("projectLevelsUUIDs");

        return Optional.of(criteriaBuilder
                .isMember(parentLevelUUID, projectLevelUUIDs));
    }

}
