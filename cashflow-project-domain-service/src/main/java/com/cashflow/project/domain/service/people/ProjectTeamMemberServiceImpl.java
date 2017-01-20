package com.cashflow.project.domain.service.people;

import com.cashflow.access.authorization.LoggedInBusinessAccount;
import com.cashflow.backend.businessaccount.authorization.RequiresBusinessAccount;
import com.cashflow.project.api.people.ProjectTeamMemberService;
import com.cashflow.project.domain.facade.people.ProjectTeamMemberContextFacade;
import com.cashflow.project.domain.facade.people.ProjectTeamMemberFacade;
import com.cashflow.project.domain.project.personnel.TeamMember;
import com.cashflow.project.domain.project.personnel.TeamMemberContext;
import com.cashflow.useraccount.domain.businessaccount.BusinessAccount;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since Nov 26, 2016, 11:26:58 AM
 */
@Stateless
@Dependent
@RequiresBusinessAccount
@Local(ProjectTeamMemberService.class)
public class ProjectTeamMemberServiceImpl implements ProjectTeamMemberService {

    @EJB
    private ProjectTeamMemberFacade projectTeamMemberFacade;

    @EJB
    private ProjectTeamMemberContextFacade projectTeamMemberContextFacade;

    @Inject
    @LoggedInBusinessAccount
    private Instance<BusinessAccount> businessAccount;

    @Override
    public void save(@Nonnull final TeamMember teamMember) {
        checkNotNull(teamMember, "The teamMember must not be null");

        projectTeamMemberFacade.edit(teamMember);
    }

    @Override
    @Nullable
    public TeamMember getTeamMember(@Nonnull final String uuid) {
        checkNotNull(uuid, "The uuid must not be null");

        return projectTeamMemberFacade.findByMemberIdOrUUID(uuid,
                                                            businessAccount.get().getAccountId());
    }

    @Override
    @Nonnull
    public List<TeamMember> teamMembers(@Nonnull final TeamMemberContext teamMemberContext) {
        checkNotNull(teamMemberContext, "The teamMemberContext must not be null");

        return projectTeamMemberContextFacade
                .find(teamMemberContext);
    }

}
