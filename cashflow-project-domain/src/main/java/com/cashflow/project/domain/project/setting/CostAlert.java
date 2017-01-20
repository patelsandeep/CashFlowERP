package com.cashflow.project.domain.project.setting;

import com.cashflow.entitydomains.AbstractBusinessAccountDomain;
import com.cashflow.project.domain.project.level.ProjectLevel;
import java.math.BigDecimal;
import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
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
 * @since Nov 10, 2016, 8:13:23 PM
 */
@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CostAlert extends AbstractBusinessAccountDomain {

    private static final long serialVersionUID = 4024619837480268189L;

    @Nonnull
    @ManyToOne
    private ProjectLevel<?> projectLevel;

    @Min(1)
    @Setter
    @Nonnull
    @Max(100)
    private BigDecimal percentage;
}
