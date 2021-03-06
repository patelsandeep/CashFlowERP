package com.cashflow.project.domain.project.costing;

import com.cashflow.project.domain.project.level.ProjectLevel;
import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * 
 * @since Nov 10, 2016, 9:39:57 PM
 */
@Entity
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NamedQueries({
    @NamedQuery(name = "IndirectProjectCosting.findIndirectProjectCostings",
                query = "SELECT idpc FROM IndirectProjectCosting idpc WHERE idpc.projectLevel.uuid IN :projectLevelUUIDs")
})
public class IndirectProjectCosting extends ProjectCosting {

    private static final long serialVersionUID = -5567321664243114256L;

    @Nonnull
    @Enumerated(EnumType.STRING)
    private CostClassification costClassification;

    @Nonnull
    @OneToOne
    private ProjectLevel<?> projectLevel;

}
