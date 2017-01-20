package com.cashflow.project.domain.facade.rentedequipment;

import com.cashflow.entitydomains.facade.context.ContextQueryBuilder;
import com.cashflow.project.domain.project.rentedequipment.RentedEquipment;
import com.cashflow.project.domain.project.rentedequipment.RentedEquipmentExpense;
import com.cashflow.project.domain.project.rentedequipment.RentedEquipmentExpense_;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 *
 * 
 * @since Dec 26, 2016, 2:39:09 PM
 */
@ApplicationScoped
@RentedContextQueryBuilder
public class NameQueryBuilder implements ContextQueryBuilder<RentedEquipment, RentedEquipmentExpense> {

    @Override
    public Optional<Predicate> buildPredicate(RentedEquipment context,
                                              CriteriaBuilder criteriaBuilder,
                                              Root<RentedEquipmentExpense> queryRoot) {
        checkNotNull(context, "The context must not be null");
        checkNotNull(criteriaBuilder, "The criteriaBuilder must not be null");
        checkNotNull(queryRoot, "The queryRoot must not be null");

        final String name = context.getName();
        if (name == null) {
            return Optional.empty();
        }

        final Path<String> namePath = queryRoot.get(RentedEquipmentExpense_.equipmentName);
        final Path<String> idPath = queryRoot.get(RentedEquipmentExpense_.equipmentId);
        final String nameSearch = format("%%%s%%", name);
        final String idSearch = format("%%%s%%", idPath);
        return Optional.of(criteriaBuilder.or(
                criteriaBuilder.like(namePath, nameSearch),
                criteriaBuilder.like(idPath, idSearch)));
    }

}
