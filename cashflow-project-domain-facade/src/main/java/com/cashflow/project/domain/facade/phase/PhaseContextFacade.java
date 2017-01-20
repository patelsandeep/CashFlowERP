package com.cashflow.project.domain.facade.phase;

import com.cashflow.entitydomains.facade.context.ContextFacade;
import com.cashflow.project.domain.project.phase.PhaseContext;
import com.cashflow.project.domain.project.phase.ProjectPhase;
import javax.ejb.Stateless;
import javax.enterprise.util.AnnotationLiteral;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.Getter;

import static com.cashflow.project.domain.facade.PersistenceUnitName.PERSISTENCE_NAME;

/**
 *
 * 
 * @since Nov 18, 2016, 09:50:25 AM
 */
@Stateless
public class PhaseContextFacade extends ContextFacade<PhaseContext, ProjectPhase> {

    @Getter
    @PersistenceContext(unitName = PERSISTENCE_NAME)
    private EntityManager entityManager;

    public PhaseContextFacade() {
        super(ProjectPhase.class, new PhaseContextQueryBuilderImpl());
    }

    @SuppressWarnings("AnnotationAsSuperInterface") //CDI
    private static final class PhaseContextQueryBuilderImpl extends AnnotationLiteral<PhaseContextQueryBuilder>
            implements PhaseContextQueryBuilder {

        private static final long serialVersionUID = 8808299907933421559L;

    }
}
