package com.cashflow.project.domain.facade.rentedequipment;

import com.cashflow.entitydomains.facade.context.ContextFacade;
import com.cashflow.project.domain.project.rentedequipment.RentedEquipment;
import com.cashflow.project.domain.project.rentedequipment.RentedEquipmentExpense;
import javax.ejb.Stateless;
import javax.enterprise.util.AnnotationLiteral;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.Getter;

import static com.cashflow.project.domain.facade.PersistenceUnitName.PERSISTENCE_NAME;

/**
 *
 * 
 * @since 16 Dec, 2016, 7:10:52 PM
 */
@Stateless
public class RentedEquipmentExpenseContextFacade extends ContextFacade<RentedEquipment, RentedEquipmentExpense> {

    @Getter
    @PersistenceContext(unitName = PERSISTENCE_NAME)
    private EntityManager entityManager;

    public RentedEquipmentExpenseContextFacade() {
        super(RentedEquipmentExpense.class, new RentedEquipmentExpenseContextQueryBuilderImpl());
    }

    @SuppressWarnings("AnnotationAsSuperInterface") //CDI
    private static final class RentedEquipmentExpenseContextQueryBuilderImpl extends AnnotationLiteral<RentedContextQueryBuilder>
            implements RentedContextQueryBuilder {

        private static final long serialVersionUID = 7876502532859909091L;

    }
}
