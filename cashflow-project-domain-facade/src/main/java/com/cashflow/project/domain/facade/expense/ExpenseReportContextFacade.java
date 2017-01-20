package com.cashflow.project.domain.facade.expense;

import com.cashflow.entitydomains.facade.context.ContextFacade;
import com.cashflow.project.domain.expenseinformation.ExpenseReportContext;
import com.cashflow.project.domain.project.expense.ExpenseReport;
import javax.ejb.Stateless;
import javax.enterprise.util.AnnotationLiteral;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.Getter;

import static com.cashflow.project.domain.facade.PersistenceUnitName.PERSISTENCE_NAME;

/**
 *
 * 
 * @since Dec 12, 2016, 3:56:21 PM
 */
@Stateless
public class ExpenseReportContextFacade extends ContextFacade<ExpenseReportContext, ExpenseReport> {

    @Getter
    @PersistenceContext(unitName = PERSISTENCE_NAME)
    private EntityManager entityManager;

    public ExpenseReportContextFacade() {
        super(ExpenseReport.class, new ExpenseReportContextQueryBuilderImpl());
    }

    @SuppressWarnings("AnnotationAsSuperInterface") //CDI
    private static final class ExpenseReportContextQueryBuilderImpl extends AnnotationLiteral<ExpenseReportContextBuilder>
            implements ExpenseReportContextBuilder {

        private static final long serialVersionUID = -1460403281732682664L;

    }
}
