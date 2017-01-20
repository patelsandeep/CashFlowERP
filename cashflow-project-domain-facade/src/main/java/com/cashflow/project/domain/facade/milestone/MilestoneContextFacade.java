package com.cashflow.project.domain.facade.milestone;

import com.cashflow.entitydomains.facade.context.ContextFacade;
import com.cashflow.project.domain.project.milestone.MilestoneContext;
import com.cashflow.project.domain.project.milestone.ProjectMilestone;
import javax.ejb.Stateless;
import javax.enterprise.util.AnnotationLiteral;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.Getter;

import static com.cashflow.project.domain.facade.PersistenceUnitName.PERSISTENCE_NAME;

/**
 *
 * 
 * @since Dec 9, 2016, 3:56:21 PM
 */
@Stateless
public class MilestoneContextFacade extends ContextFacade<MilestoneContext, ProjectMilestone> {

    @Getter
    @PersistenceContext(unitName = PERSISTENCE_NAME)
    private EntityManager entityManager;

    public MilestoneContextFacade() {
        super(ProjectMilestone.class, new MilestoneContextQueryBuilderImpl());
    }

    @SuppressWarnings("AnnotationAsSuperInterface") //CDI
    private static final class MilestoneContextQueryBuilderImpl extends AnnotationLiteral<MilestoneContextQueryBuilder>
            implements MilestoneContextQueryBuilder {

        private static final long serialVersionUID = 4659163864809789948L;

    }
}
