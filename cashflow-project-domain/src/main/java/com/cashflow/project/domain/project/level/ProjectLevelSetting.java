package com.cashflow.project.domain.project.level;

import com.cashflow.entitydomains.AbstractBusinessAccountDomain;
import com.cashflow.project.domain.project.Project;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * 
 * @since Nov 10, 2016, 11:44:19 PM
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NamedQueries({
    @NamedQuery(name = "ProjectLevelSetting.findProjectLevelSetting",
                query = "SELECT p FROM ProjectLevelSetting p WHERE p.project.uuid = :projectUUID")
})
public class ProjectLevelSetting extends AbstractBusinessAccountDomain {

    private static final long serialVersionUID = 2244608408100731656L;

    @Nonnull
    @OneToOne
    private Project project;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<ProjectLevelCategory> projectLevelCategories;

    @Nullable
    private ProjectLevelCategory customerDepositLevel;

    // Needs more thinking, does it really need to be here?
    private boolean includeSubContractors;

    // I guess this means that deposit is required before work begins.
    // If this is specified, then an amount specified will be created within the the "ProjectDeposit" domain object.
    private boolean customerDepositRequired;

    @Nonnull
    public List<ProjectLevelCategory> getProjectLevelCategories() {
        return projectLevelCategories == null ? (projectLevelCategories = new ArrayList<>()) : projectLevelCategories;
    }
}
