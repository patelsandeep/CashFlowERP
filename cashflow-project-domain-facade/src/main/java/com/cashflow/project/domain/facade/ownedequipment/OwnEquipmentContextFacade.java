package com.cashflow.project.domain.facade.ownedequipment;

import com.cashflow.entitydomains.facade.context.ContextFacade;
import com.cashflow.project.domain.project.ownedequipment.OwnEquipmentContext;
import com.cashflow.project.domain.project.ownedequipment.OwnedEquipmentExpense;
import javax.ejb.Stateless;
import javax.enterprise.util.AnnotationLiteral;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.Getter;

import static com.cashflow.project.domain.facade.PersistenceUnitName.PERSISTENCE_NAME;

/**
 *
 * 
 * @since 20 Dec, 2016, 1:10:52 PM
 */
@Stateless
public class OwnEquipmentContextFacade extends ContextFacade<OwnEquipmentContext, OwnedEquipmentExpense> {

    @Getter
    @PersistenceContext(unitName = PERSISTENCE_NAME)
    private EntityManager entityManager;

    public OwnEquipmentContextFacade() {
        super(OwnedEquipmentExpense.class, new OwnEquipmentContextQueryBuilderImpl());
    }

    @SuppressWarnings("AnnotationAsSuperInterface") //CDI
    private static final class OwnEquipmentContextQueryBuilderImpl extends AnnotationLiteral<OwnEquipmentContextQueryBuilder>
            implements OwnEquipmentContextQueryBuilder {

        private static final long serialVersionUID = -6434159689406971099L;

    }
}
