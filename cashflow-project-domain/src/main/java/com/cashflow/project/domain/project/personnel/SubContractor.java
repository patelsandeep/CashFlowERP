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
 * @since Nov 11, 2016, 7:30:03 PM
 */
@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NamedQueries({
    @NamedQuery(name = "SubContractor.findBySubContractorIdOrUUID",
                query = "SELECT sc FROM SubContractor sc WHERE (sc.memberId =:id OR sc.uuid = :id) and sc.businessAccountId = :businessAccountId")
})
public class SubContractor extends AbstractBusinessAccountDomain {

    private static final long serialVersionUID = -779533627145120137L;

    @Nonnull
    @ManyToOne
    private ProjectLevel<?> projectLevel;

    @Nonnull
    private String subContractorUUID;

    @Nonnull
    private String memberName;

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
