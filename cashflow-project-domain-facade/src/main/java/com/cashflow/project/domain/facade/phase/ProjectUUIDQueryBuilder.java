package com.cashflow.project.domain.facade.phase;

import com.cashflow.entitydomains.facade.context.ContextQueryBuilder;
import com.cashflow.project.domain.project.phase.PhaseContext;
import com.cashflow.project.domain.project.phase.ProjectPhase;
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
@PhaseContextQueryBuilder
public class ProjectUUIDQueryBuilder implements ContextQueryBuilder<PhaseContext, ProjectPhase> {

    @Override
    public Optional<Predicate> buildPredicate(@Nonnull final PhaseContext context,
                                              @Nonnull final CriteriaBuilder criteriaBuilder,
                                              @Nonnull final Root<ProjectPhase> queryRoot) {
        checkNotNull(context, "The context must not be null");
        checkNotNull(criteriaBuilder, "The criteriaBuilder must not be null");
        checkNotNull(queryRoot, "The queryRoot must not be null");

        final String projectUUID = context.getProjectUUID();
        if (projectUUID == null) {
            return Optional.empty();
        }

        final Expression<List<String>> projectLevelUUIDsPath = queryRoot
                .get("projectLevelsUUIDs");
        return Optional.of(criteriaBuilder.isMember(projectUUID, projectLevelUUIDsPath));
    }

}
