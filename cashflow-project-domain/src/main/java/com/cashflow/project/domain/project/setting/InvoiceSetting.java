package com.cashflow.project.domain.project.setting;

import com.cashflow.entitydomains.AbstractBusinessAccountDomain;
import com.cashflow.project.domain.project.level.ProjectLevel;
import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * 
 * @since Nov 10, 2016, 8:09:32 PM
 */
@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InvoiceSetting extends AbstractBusinessAccountDomain {

    private static final long serialVersionUID = -8737767636077799001L;

    @Nonnull
    @ManyToOne
    private ProjectLevel<?> projectLevel;

    @Setter
    private boolean enabled;
}
