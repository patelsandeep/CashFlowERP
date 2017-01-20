package com.cashflow.project.domain.facade.expense.subcontractor;

import com.cashflow.entitydomains.facade.context.ContextFacade;
import com.cashflow.project.domain.project.expense.subcontractor.SubContractorExpense;
import com.cashflow.project.domain.project.expense.subcontractor.SubContractorExpenseContext;
import javax.ejb.Stateless;
import javax.enterprise.util.AnnotationLiteral;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.Getter;

import static com.cashflow.project.domain.facade.PersistenceUnitName.PERSISTENCE_NAME;

/**
 *
 * 
 * @since Jan 3, 2017, 12:23:47 PM
 */
@Stateless
public class SubContractorExpenseContextFacade extends ContextFacade<SubContractorExpenseContext, SubContractorExpense> {

    @Getter
    @PersistenceContext(unitName = PERSISTENCE_NAME)
    private EntityManager entityManager;

    public SubContractorExpenseContextFacade() {
        super(SubContractorExpense.class, new SubContractorExpenseContextQueryBuilderImpl());
    }

    @SuppressWarnings("AnnotationAsSuperInterface") //CDI
    private static final class SubContractorExpenseContextQueryBuilderImpl extends AnnotationLiteral<SubContractorExpenseContextBuilder>
            implements SubContractorExpenseContextBuilder {

        private static final long serialVersionUID = -7033222810173781989L;

    }
}
