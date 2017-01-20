package com.cashflow.project.domain.facade.task;

import com.cashflow.entitydomains.facade.context.ContextQueryBuilder;
import com.cashflow.project.domain.project.task.ProjectTask;
import com.cashflow.project.domain.project.task.TaskContext;
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
 * @since Dec 9, 2016, 4:05:18 PM
 */
@ApplicationScoped
@TaskContextQueryBuilder
public class ParentLevelUUIDQueryBuilder implements ContextQueryBuilder<TaskContext, ProjectTask> {

    @Override
    public Optional<Predicate> buildPredicate(@Nonnull final TaskContext context,
                                              @Nonnull final CriteriaBuilder criteriaBuilder,
                                              @Nonnull final Root<ProjectTask> queryRoot) {
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
