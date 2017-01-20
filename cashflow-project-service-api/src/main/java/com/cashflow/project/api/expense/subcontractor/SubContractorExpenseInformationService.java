package com.cashflow.project.api.expense.subcontractor;

import javax.annotation.Nonnull;

/**
 *
 * 
 * @since Jan 3, 2017, 2:57:00 PM
 */
public interface SubContractorExpenseInformationService {

    @Nonnull
    SubContractorExpenseInformationResult getSubContractorExpenseInformations(
            @Nonnull final SubContractorExpenseInformationRequest informationRequest);

}
