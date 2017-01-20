package com.cashflow.project.domain.facade.people;

import com.cashflow.entitydomains.facade.context.ContextQueryBuilder;
import com.cashflow.project.domain.project.level.ProjectLevel_;
import com.cashflow.project.domain.project.personnel.SubContractor;
import com.cashflow.project.domain.project.personnel.SubContractor_;
import com.cashflow.project.domain.project.personnel.SubcontractorContext;
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
 * @since 1 Dec, 2016, 6:31:37 PM
 */
@ApplicationScoped
@ProjectSubContractorContextBuilder
public class ProjectSubContractorProjectLevelUUIDQueryBuilder implements ContextQueryBuilder<SubcontractorContext, SubContractor> {

    @Override
    public Optional<Predicate> buildPredicate(@Nonnull final SubcontractorContext context,
                                              @Nonnull final CriteriaBuilder criteriaBuilder,
                                              @Nonnull final Root<SubContractor> queryRoot) {
        checkNotNull(context, "The context must not be null");
        checkNotNull(criteriaBuilder, "The criteriaBuilder must not be null");
        checkNotNull(queryRoot, "The queryRoot must not be null");

        final String projectUUID = context.getProjectLevelUUID();
        if (projectUUID == null) {
            return Optional.empty();
        }
        final Expression<List<String>> projectLevelUUID = queryRoot
                .get(SubContractor_.projectLevel)
                .get(ProjectLevel_.projectLevelsUUIDs);
        return Optional.of(criteriaBuilder
                .isMember(projectUUID, projectLevelUUID));

    }

}
