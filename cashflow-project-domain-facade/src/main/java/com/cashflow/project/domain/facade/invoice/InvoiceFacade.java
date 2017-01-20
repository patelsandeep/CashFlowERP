package com.cashflow.project.domain.facade.invoice;

import com.cashflow.project.domain.facade.ProjectAbstractFacade;
import com.cashflow.project.domain.project.invoice.Invoice;
import java.util.List;
import javax.annotation.Nonnull;
import javax.ejb.Stateless;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;

/**
 *
 * 
 * @since Nov 12, 2016, 4:05:09 PM
 */
@Stateless
public class InvoiceFacade extends ProjectAbstractFacade<Invoice> {

    public InvoiceFacade() {
        super(Invoice.class);
    }

    @Nonnull
    public List<Invoice> getInvoices(@Nonnull final List<String> projectLevelUUIDs) {
        checkNotNull(projectLevelUUIDs, "The projectLevelUUIDs must not be null");

        if (projectLevelUUIDs.isEmpty()) {
            return ImmutableList.of();
        }

        return entityManager
                .createNamedQuery("Invoice.findInvoices")
                .setParameter("projectLevelUUIDs", projectLevelUUIDs)
                .getResultList();
    }
}
