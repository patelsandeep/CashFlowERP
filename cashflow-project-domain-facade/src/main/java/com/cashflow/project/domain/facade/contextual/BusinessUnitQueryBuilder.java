package com.cashflow.project.domain.facade.contextual;

import com.cashflow.entitydomains.facade.context.ContextQueryBuilder;
import com.cashflow.project.domain.project.Project;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 *
 * @since Nov 12, 2016, 2:39:09 PM
 */
@ApplicationScoped
@ProjectContextQueryBuilder
public class BusinessUnitQueryBuilder implements ContextQueryBuilder<ProjectContext, Project> {

    @Override
    public Optional<Predicate> buildPredicate(@Nonnull final ProjectContext context,
                                              @Nonnull final CriteriaBuilder criteriaBuilder,
                                              @Nonnull final Root<Project> queryRoot,
                                              @Nonnull final AbstractQuery<?> criteriaQuery) {
        checkNotNull(context, "The context must not be null");
        checkNotNull(criteriaBuilder, "The criteriaBuilder must not be null");
        checkNotNull(queryRoot, "The queryRoot must not be null");

        final String businessUnitsUUID = context.getBusinessUnitUUID();
        if (businessUnitsUUID == null) {
            return Optional.empty();
        }

        final Expression<List<String>> businessUnitUUIDsPath = queryRoot.get("businessUnitUUIDs");
        return Optional.of(criteriaBuilder.isMember(businessUnitsUUID, businessUnitUUIDsPath));
    }

}
