package com.cashflow.project.domain.facade.contextual;

import com.cashflow.entitydomains.facade.context.ContextQueryBuilder;
import com.cashflow.project.domain.project.Project;
import java.util.Calendar;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since Nov 12, 2016, 2:39:09 PM
 */
public abstract class ProjectDateQueryBuilder implements ContextQueryBuilder<ProjectContext, Project> {

    @Nonnull
    protected Optional<Predicate> doBuildPredicate(@Nonnull final ProjectContext context,
            @Nonnull final CriteriaBuilder criteriaBuilder,
            @Nonnull final Root<Project> queryRoot,
            @Nonnull final SingularAttribute<? super Project, Calendar> datePath,
            @Nullable final Calendar fromDate,
            @Nullable final Calendar toDate) {
        checkNotNull(context, "The context must not be null");
        checkNotNull(criteriaBuilder, "The criteriaBuilder must not be null");
        checkNotNull(queryRoot, "The queryRoot must not be null");
        checkNotNull(datePath, "The datePath must not be null");

        final Path<Calendar> datePathQuery = queryRoot.get(datePath);
        if (fromDate != null && toDate != null) {
            return Optional.of(criteriaBuilder.between(datePathQuery, fromDate, toDate));
        }

        if (fromDate != null) {
            return Optional.of(criteriaBuilder.greaterThanOrEqualTo(datePathQuery, fromDate));
        }

        if (toDate != null) {
            return Optional.of(criteriaBuilder.lessThanOrEqualTo(datePathQuery, toDate));
        }

        return Optional.empty();
    }

}
