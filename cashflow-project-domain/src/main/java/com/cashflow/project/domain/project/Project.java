package com.cashflow.project.domain.project;

import com.cashflow.project.domain.project.level.ProjectLevel;
import com.anosym.common.Currency;
import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * 
 * @since Nov 10, 2016, 7:51:09 PM
 */
@Entity
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"businessAccountId", "id"})})
@NamedQueries({
    @NamedQuery(name = "Project.findByProjectIdOrUUID",
                query = "SELECT p FROM Project p WHERE (p.id = :id OR p.uuid = :id) and p.businessAccountId = :businessAccountId")
})
public class Project extends ProjectLevel {

    private static final long serialVersionUID = 2167149510293504460L;

    @Nonnull
    private String customerUUID;

    @Nonnull
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Nonnull
    @Enumerated(EnumType.STRING)
    private ProjectType projectType;

    @Nonnull
    @Enumerated(EnumType.STRING)
    private ContractType contractType;

    @Nonnull
    private String cityLocation;

    private boolean locationPermission;

    private boolean insuranceRequired;
}
