package com.cashflow.project.domain.project.level;

import com.cashflow.entitydomains.AbstractBusinessAccountDomain;
import com.cashflow.project.domain.project.ProjectStatus;
import java.math.BigDecimal;
import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * 
 * @since Nov 12, 2016, 8:08:14 AM
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NamedQueries({
    @NamedQuery(name = "ProjectLevelProgress.findProjectLevelProgress",
                query = "SELECT p FROM ProjectLevelProgress p WHERE p.projectLevel.uuid IN :projectLevelUUIDs")
})
public class ProjectLevelProgress extends AbstractBusinessAccountDomain {

    private static final long serialVersionUID = -9032223732994838299L;

    @Nonnull
    @OneToOne
    private ProjectLevel<?> projectLevel;

    @Min(0)
    @Nonnull
    @Max(100)
    private BigDecimal percentOfCompletion;

    @Min(0)
    @Nonnull
    @Max(100)
    private BigDecimal physicalPercentageOfCompletion;

    @Nonnull
    @Enumerated(EnumType.STRING)
    private PerformanceStatus performanceStatus;

    @Nonnull
    @Enumerated(EnumType.STRING)
    private LevelStatus levelStatus;
    
    @Nonnull
    @Enumerated(EnumType.STRING)
    private ProjectStatus projectStatus;

}
