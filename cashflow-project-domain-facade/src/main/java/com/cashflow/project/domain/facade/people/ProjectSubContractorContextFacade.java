package com.cashflow.project.domain.facade.people;

import com.cashflow.entitydomains.facade.context.ContextFacade;
import com.cashflow.project.domain.project.personnel.SubContractor;
import com.cashflow.project.domain.project.personnel.SubcontractorContext;
import javax.annotation.Nonnull;
import javax.ejb.Stateless;
import javax.enterprise.util.AnnotationLiteral;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static com.cashflow.project.domain.facade.PersistenceUnitName.PERSISTENCE_NAME;

/**
 *
 * 
 * @since 1 Dec, 2016, 12:01:01 PM
 */
@Stateless
public class ProjectSubContractorContextFacade extends ContextFacade<SubcontractorContext, SubContractor> {

    @PersistenceContext(unitName = PERSISTENCE_NAME)
    protected EntityManager entityManager;

    public ProjectSubContractorContextFacade() {
        super(SubContractor.class, new SubContractorContextBuilderImpl());
    }

    @Nonnull
    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @SuppressWarnings("AnnotationAsSuperInterface")
    private static final class SubContractorContextBuilderImpl extends AnnotationLiteral<ProjectSubContractorContextBuilder>
            implements ProjectSubContractorContextBuilder {

        private static final long serialVersionUID = 5305705331468842545L;

    }

}
