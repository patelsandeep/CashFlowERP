package com.cashflow.project.domain.facade.contextual;

import com.cashflow.entitydomains.facade.context.ContextFacade;
import com.cashflow.project.domain.project.Project;
import javax.ejb.Stateless;
import javax.enterprise.util.AnnotationLiteral;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.Getter;

import static com.cashflow.project.domain.facade.PersistenceUnitName.PERSISTENCE_NAME;

/**
 *
 * 
 * @since Nov 12, 2016, 1:41:13 PM
 */
@Stateless
public class ProjectContextFacade extends ContextFacade<ProjectContext, Project> {

    @Getter
    @PersistenceContext(unitName = PERSISTENCE_NAME)
    private EntityManager entityManager;

    public ProjectContextFacade() {
        super(Project.class, new ProjectContextQueryBuilderImpl());
    }

    @SuppressWarnings("AnnotationAsSuperInterface") //CDI
    private static final class ProjectContextQueryBuilderImpl extends AnnotationLiteral<ProjectContextQueryBuilder>
            implements ProjectContextQueryBuilder {

        private static final long serialVersionUID = -6568934324105642310L;
    }
}
