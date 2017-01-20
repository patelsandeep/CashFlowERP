package com.cashflow.project.domain.facade.ownedequipment;

import com.cashflow.entitydomains.facade.context.ContextQueryBuilder;
import com.cashflow.project.domain.project.level.ProjectLevel_;
import com.cashflow.project.domain.project.ownedequipment.OwnEquipmentContext;
import com.cashflow.project.domain.project.ownedequipment.OwnedEquipmentExpense;
import com.cashflow.project.domain.project.ownedequipment.OwnedEquipmentExpense_;
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
 * @since 20 Dec, 2016, 1:16:58 PM
 */
@ApplicationScoped
@OwnEquipmentContextQueryBuilder
public class OwnEquipmentProjectLevelUUIDQueryBuilder implements ContextQueryBuilder<OwnEquipmentContext, OwnedEquipmentExpense> {

    @Override
    public Optional<Predicate> buildPredicate(@Nonnull final OwnEquipmentContext context,
                                              @Nonnull final CriteriaBuilder criteriaBuilder,
                                              @Nonnull final Root<OwnedEquipmentExpense> queryRoot) {
        checkNotNull(context, "The context must not be null");
        checkNotNull(criteriaBuilder, "The criteriaBuilder must not be null");
        checkNotNull(queryRoot, "The queryRoot must not be null");

        final String projectUUID = context.getProjectUUID();
        if (projectUUID == null) {
            return Optional.empty();
        }
        final Expression<List<String>> projectLevelUUIDPath = queryRoot
                .get(OwnedEquipmentExpense_.projectLevel)
                .get(ProjectLevel_.projectLevelsUUIDs);
        return Optional.of(criteriaBuilder.isMember(projectUUID, projectLevelUUIDPath));

    }
}
