package com.cashflow.project.domain.project.budget;

import com.cashflow.entitydomains.AbstractBusinessAccountDomain;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.anosym.common.Amount;
import javax.annotation.Nonnull;
import javax.persistence.Entity;
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
 * @since Nov 10, 2016, 11:19:37 PM
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NamedQueries({
    @NamedQuery(name = "Deposit.findDeposits",
                query = "SELECT d FROM Deposit d WHERE d.projectLevel.uuid IN :projectLevelUUIDs")
})
public class Deposit extends AbstractBusinessAccountDomain {

    private static final long serialVersionUID = 8933001756110482248L;

    @Nonnull
    @OneToOne
    private ProjectLevel<?> projectLevel;

    @Nonnull
    private Amount amount;

}
