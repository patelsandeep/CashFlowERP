package com.cashflow.project.domain.facade.rentedequipment;

import com.cashflow.entitydomains.facade.context.ContextQueryBuilder;
import com.cashflow.project.domain.project.level.ProjectLevel_;
import com.cashflow.project.domain.project.rentedequipment.RentedEquipment;
import com.cashflow.project.domain.project.rentedequipment.RentedEquipmentExpense;
import com.cashflow.project.domain.project.rentedequipment.RentedEquipmentExpense_;
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
 * @since 16 Dec, 2016, 7:16:58 PM
 */
@ApplicationScoped
@RentedContextQueryBuilder
public class RentedEquipmentProjectLevelUUIDQueryBuilder implements ContextQueryBuilder<RentedEquipment, RentedEquipmentExpense> {

    @Override
    public Optional<Predicate> buildPredicate(@Nonnull final RentedEquipment context,
                                              @Nonnull final CriteriaBuilder criteriaBuilder,
                                              @Nonnull final Root<RentedEquipmentExpense> queryRoot) {
        checkNotNull(context, "The context must not be null");
        checkNotNull(criteriaBuilder, "The criteriaBuilder must not be null");
        checkNotNull(queryRoot, "The queryRoot must not be null");

        final String projectUUID = context.getProjectUUID();
        if (projectUUID == null) {
            return Optional.empty();
        }
        final Expression<List<String>> projectLevelUUIDPath = queryRoot
                .get(RentedEquipmentExpense_.projectLevel)
                .get(ProjectLevel_.projectLevelsUUIDs);
        return Optional.of(criteriaBuilder.isMember(projectUUID, projectLevelUUIDPath));

    }
}
