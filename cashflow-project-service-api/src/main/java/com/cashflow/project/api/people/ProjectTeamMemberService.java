package com.cashflow.project.api.people;

import com.cashflow.project.domain.project.personnel.TeamMember;
import com.cashflow.project.domain.project.personnel.TeamMemberContext;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * 
 * @since Nov 26, 2016, 11:08:42 AM
 */
public interface ProjectTeamMemberService {

    void save(@Nonnull final TeamMember teamMember);

    @Nullable
    TeamMember getTeamMember(@Nonnull final String uuid);

    @Nonnull
    List<TeamMember> teamMembers(@Nonnull final TeamMemberContext teamMemberContext);

}
