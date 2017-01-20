package com.cashflow.project.domain.facade.people;

import com.cashflow.entitydomains.facade.context.ContextFacade;
import com.cashflow.project.domain.project.personnel.TeamMember;
import com.cashflow.project.domain.project.personnel.TeamMemberContext;
import javax.annotation.Nonnull;
import javax.ejb.Stateless;
import javax.enterprise.util.AnnotationLiteral;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static com.cashflow.project.domain.facade.PersistenceUnitName.PERSISTENCE_NAME;

/**
 *
 * 
 * @since 1 Dec, 2016, 3:36:52 PM
 */
@Stateless
public class ProjectTeamMemberContextFacade extends ContextFacade<TeamMemberContext, TeamMember> {

    @PersistenceContext(unitName = PERSISTENCE_NAME)
    protected EntityManager entityManager;

    public ProjectTeamMemberContextFacade() {
        super(TeamMember.class, new TeamMemberContextBuilderImpl());
    }

    @Nonnull
    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @SuppressWarnings("AnnotationAsSuperInterface")
    private static final class TeamMemberContextBuilderImpl extends AnnotationLiteral<ProjectTeamMemberContextBuilder>
            implements ProjectTeamMemberContextBuilder {

        private static final long serialVersionUID = -6486727937322327808L;

    }

}
