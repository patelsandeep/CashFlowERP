package com.cashflow.project.domain.project.task;

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
 * @since Nov 10, 2016, 8:04:34 PM
 */
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NamedQueries({
    @NamedQuery(name = "ProjectTask.findByTaskIdOrUUID",
                query = "SELECT pt FROM ProjectTask pt WHERE (pt.id = :id OR pt.uuid = :id) and pt.businessAccountId = :businessAccountId"),
    @NamedQuery(name = "ProjectTask.findByMilestoneOrProjectUUIDs",
                query = "SELECT pt FROM ProjectTask pt WHERE pt.parentLevel.uuid IN :parentLevelUUIDs"),
    @NamedQuery(name = "ProjectTask.findUUIDByNameOrNumber",
                query = "SELECT pt.uuid FROM ProjectTask pt WHERE (pt.name LIKE :name OR pt.number LIKE :name) and pt.businessAccountId = :businessAccountId")
})
public class ProjectTask extends ProjectLevel<ProjectLevel> {

    private static final long serialVersionUID = 5064909488783652273L;

    @Nonnull
    private String number;

}
