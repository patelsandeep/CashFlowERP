package com.cashflow.project.domain.project.level;

import com.cashflow.entitydomains.AbstractBusinessAccountDomain;
import com.cashflow.entitydomains.FileUrl;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.persistence.EnumType.STRING;

/**
 *
 * 
 * @since Nov 10, 2016, 11:17:23 PM
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NamedQueries({
    @NamedQuery(name = "ProjectLevel.findUUIDByNameOrId",
                query = "SELECT pl FROM ProjectLevel pl WHERE (pl.name LIKE :name OR pl.id LIKE :name) and pl.businessAccountId = :businessAccountId"),
    @NamedQuery(name = "ProjectLevel.findLevelsByUUID",
                query = "SELECT pl FROM ProjectLevel pl WHERE pl.parentLevel IS NOT EMPTY AND :uuid MEMBER OF pl.projectLevelsUUIDs")
})
public abstract class ProjectLevel<T extends ProjectLevel> extends AbstractBusinessAccountDomain {

    private static final long serialVersionUID = 6062351149714760842L;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> projectLevelsUUIDs;

    @Setter
    @Nonnull
    @Enumerated(STRING)
    private ProjectLevelCategory projectLevelCategory;

    @Setter
    @Nullable
    @ManyToOne(targetEntity = ProjectLevel.class)
    private T parentLevel;

    @Setter
    @Nonnull
    private String id;

    @Setter
    @Nonnull
    private String name;

    @Setter
    @Nullable
    @Column(length = 1024)
    private String summary;

    /**
     * Description of what is to be delivered.
     */
    @Setter
    @Nullable
    @Column(columnDefinition = "TEXT")
    private String deliverables;

    @Setter
    @Nonnull
    @Temporal(TemporalType.DATE)
    private Calendar startDate;

    @Setter
    @Nonnull
    @Temporal(TemporalType.DATE)
    private Calendar endDate;

    /**
     * Same as supervisor.
     */
    @Setter
    @Nonnull
    private String manager;

    @Setter
    @Nonnull
    @Enumerated(STRING)
    private SafetyRating safetyRating;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<FileUrl> documents;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> businessUnitUUIDs;

    protected ProjectLevel(@Nonnull final ProjectLevelCategory projectLevelCategory) {
        this.projectLevelCategory = checkNotNull(projectLevelCategory, "The projectLevelCategory must not be null");
    }

    protected ProjectLevel(@Nonnull final T parentLevel, @Nonnull final ProjectLevelCategory projectLevelCategory) {
        this.parentLevel = checkNotNull(parentLevel, "The parentLevel must not be null");
        this.projectLevelCategory = checkNotNull(projectLevelCategory, "The projectLevelCategory must not be null");
    }

    @Nonnull
    public List<FileUrl> getDocuments() {
        return documents != null ? documents : (documents = new ArrayList<>());
    }

    public void setDocuments(@Nonnull final List<FileUrl> documents) {
        checkNotNull(documents, "The documents must not be null");

        getDocuments().clear();
        getDocuments().addAll(documents);
    }

    @Override
    public void setBusinessUnitUUID(@Nullable final String businessUnitUUID) {
        super.setBusinessUnitUUID(businessUnitUUID);
    }

    @Override
    public void setDepartmentUUID(@Nullable final String departmentUUID) {
        super.setDepartmentUUID(departmentUUID);
    }

    @Override
    public void setAuthorizedUserUUID(@Nullable final String authorizedUserUUID) {
        super.setAuthorizedUserUUID(authorizedUserUUID);
    }

    @Nonnull
    public List<String> getBusinessUnitUUIDs() {
        return businessUnitUUIDs != null ? businessUnitUUIDs : (businessUnitUUIDs = new ArrayList<>());
    }

    public void setBusinessUnitUUIDs(@Nonnull final Collection<String> businessUnitUUIDs) {
        checkNotNull(businessUnitUUIDs, "The businessUnitUUIDs must not be null");

        getBusinessUnitUUIDs().clear();
        getBusinessUnitUUIDs().addAll(businessUnitUUIDs);
    }

    @Nonnull
    public List<String> getProjectLevelsUUIDs() {
        return projectLevelsUUIDs != null ? projectLevelsUUIDs : (projectLevelsUUIDs = new ArrayList<>());
    }

    public void setProjectLevelsUUIDs(@Nonnull final Collection<String> projectLevelsUUIDs) {
        checkNotNull(projectLevelsUUIDs, "The projectLevelsUUIDs must not be null");

        getProjectLevelsUUIDs().clear();
        getProjectLevelsUUIDs().addAll(projectLevelsUUIDs);
    }
}
