package com.cashflow.project.domain.facade.timesheet;

import com.cashflow.entitydomains.facade.context.ContextFacade;
import com.cashflow.project.domain.expenseinformation.TimesheetContext;
import com.cashflow.project.domain.project.timesheet.TimeSheet;
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
public class TimesheetContextFacade extends ContextFacade<TimesheetContext, TimeSheet> {

    @Getter
    @PersistenceContext(unitName = PERSISTENCE_NAME)
    private EntityManager entityManager;

    public TimesheetContextFacade() {
        super(TimeSheet.class, new TimesheetContextQueryBuilderImpl());
    }

    @SuppressWarnings("AnnotationAsSuperInterface") //CDI
    private static final class TimesheetContextQueryBuilderImpl extends AnnotationLiteral<TimesheetContextBuilder>
            implements TimesheetContextBuilder {

        private static final long serialVersionUID = 4348914636187828278L;

    }
}
