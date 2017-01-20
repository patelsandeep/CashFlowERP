package com.cashflow.project.domain.facade.people;

import com.cashflow.project.domain.facade.ProjectAbstractFacade;
import com.cashflow.project.domain.project.personnel.TeamMember;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 *
 * 
 * @since Nov 26, 2016, 11:11:05 AM
 */
@Stateless
public class ProjectTeamMemberFacade extends ProjectAbstractFacade<TeamMember> {

    public ProjectTeamMemberFacade() {
        super(TeamMember.class);
    }

    @Nullable
    public TeamMember findByMemberIdOrUUID(@Nonnull final String memberIdOrUUID,
                                           @Nonnull final String businessAccountId) {
        checkNotNull(memberIdOrUUID, "The memberIdOrUUID must not be null");

        final List<TeamMember> projectMembers = getEntityManager()
                .createNamedQuery("TeamMember.findByMemberIdOrUUID", TeamMember.class)
                .setParameter("id", memberIdOrUUID)
                .setParameter("businessAccountId", businessAccountId)
                .getResultList();
        checkState(projectMembers.size() <= 1,
                   "Invalid lookup. Member ID/uuid{%s} returns multiple results",
                   memberIdOrUUID);

        return projectMembers
                .stream()
                .findAny()
                .orElse(null);
    }

}
