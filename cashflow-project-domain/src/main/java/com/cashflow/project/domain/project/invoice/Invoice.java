package com.cashflow.project.domain.project.invoice;

import com.anosym.common.Amount;
import com.cashflow.entitydomains.AbstractBusinessAccountDomain;
import com.cashflow.project.domain.project.level.ProjectLevel;
import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
 *
 * @since Nov 12, 2016, 9:31:14 AM
 */
@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NamedQueries({
    @NamedQuery(name = "Invoice.findInvoices",
                query = "SELECT i FROM Invoice i WHERE i.projectLevel.uuid IN :projectLevelUUIDs")
})
public class Invoice extends AbstractBusinessAccountDomain {

    private static final long serialVersionUID = 4230678077285686754L;

    @Nonnull
    @ManyToOne
    private ProjectLevel<?> projectLevel;

    @Nonnull
    private Amount amount;

}
