package com.cashflow.project.domain.facade.contextual;

import com.cashflow.entitydomains.facade.context.ContextQueryBuilder;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.Project_;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since Nov 12, 2016, 2:39:09 PM
 */
@ApplicationScoped
@ProjectContextQueryBuilder
public class CustomerQueryBuilder implements ContextQueryBuilder<ProjectContext, Project> {

    @Override
    public Optional<Predicate> buildPredicate(ProjectContext context,
                                              CriteriaBuilder criteriaBuilder,
                                              Root<Project> queryRoot) {
        checkNotNull(context, "The context must not be null");
        checkNotNull(criteriaBuilder, "The criteriaBuilder must not be null");
        checkNotNull(queryRoot, "The queryRoot must not be null");

        final String customerUUID = context.getCustomerUUID();
        if (customerUUID == null) {
            return Optional.empty();
        }

        final Path<String> customerUUIDPath = queryRoot.get(Project_.customerUUID);
        return Optional.of(criteriaBuilder.equal(customerUUIDPath, customerUUID));
    }

}
