package com.cashflow.project.domain.project.personnel;

import com.cashflow.entitydomains.AbstractBusinessAccountDomain;
import com.cashflow.project.domain.project.level.ProjectLevel;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since Nov 11, 2016, 7:28:11 PM
 */
@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NamedQueries({
    @NamedQuery(name = "TeamMember.findByMemberIdOrUUID",
                query = "SELECT tm FROM TeamMember tm WHERE (tm.memberId =:id OR tm.uuid = :id) and tm.businessAccountId = :businessAccountId")})
public class TeamMember extends AbstractBusinessAccountDomain {

    private static final long serialVersionUID = -7904107793104254369L;

    @Nonnull
    @ManyToOne
    private ProjectLevel<?> projectLevel;

    @Nonnull
    private String employeeUUID;

    @Nonnull
    private String memberId;

    @Nullable
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<ProjectRole> projectRoles;

    @Nonnull
    public Set<ProjectRole> getProjectRoles() {
        return projectRoles == null ? (projectRoles = new HashSet<>()) : projectRoles;
    }

    public void setProjectRoles(@Nonnull final Set<ProjectRole> projectRoles) {
        checkNotNull(projectRoles, "The ProjectRoles must not be null");

        final Set<ProjectRole> _projectRoles = getProjectRoles();
        _projectRoles.clear();
        _projectRoles.addAll(projectRoles);

    }
}
