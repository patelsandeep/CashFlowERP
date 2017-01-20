package com.cashflow.project.domain.project.phase;

import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.level.ProjectLevel;
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
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NamedQueries({
    @NamedQuery(name = "ProjectPhase.findByPhaseIdOrUUID",
                query = "SELECT pp FROM ProjectPhase pp WHERE (pp.id = :id OR pp.uuid = :id) and pp.businessAccountId = :businessAccountId"),
    @NamedQuery(name = "ProjectPhase.findUUIDByPhaseNameOrNumber",
                query = "SELECT pp.uuid FROM ProjectPhase pp WHERE (pp.name LIKE :name OR pp.phaseNumber LIKE :name) and pp.businessAccountId = :businessAccountId")
})
public class ProjectPhase extends ProjectLevel<Project> {

    private static final long serialVersionUID = 2879445609820485332L;

    @Setter
    @Nonnull
    private String phaseNumber;

}
