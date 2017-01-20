package com.cashflow.project.domain.project.milestone;

import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.phase.*;
import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * 
 * @since Nov 10, 2016, 11:23:25 PM
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NamedQueries({
    @NamedQuery(name = "ProjectMilestone.findByMilestoneIdOrUUID",
                query = "SELECT pm FROM ProjectMilestone pm WHERE (pm.id = :id OR pm.uuid = :id) and pm.businessAccountId = :businessAccountId"),
    @NamedQuery(name = "ProjectMilestone.findByPhaseUUIDs",
                query = "SELECT pm FROM ProjectMilestone pm WHERE pm.parentLevel.uuid IN :projectLevelUUIDs"),
    @NamedQuery(name = "ProjectMilestone.findUUIDByNameOrNumber",
                query = "SELECT pm.uuid FROM ProjectMilestone pm WHERE (pm.name LIKE :name OR pm.milestoneNumber LIKE :name) and pm.businessAccountId = :businessAccountId")
})
public class ProjectMilestone extends ProjectLevel<ProjectPhase> {

    private static final long serialVersionUID = 2879445609820485332L;

    @Setter
    @Nonnull
    private String milestoneNumber;

}
