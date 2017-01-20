package com.cashflow.project.api.expenseinformation;

import javax.annotation.Nonnull;

/**
 *
 * 
 * @since Dec 12, 2016, 12:19:09 PM
 */
public interface LabourExpenseInformationService {

    @Nonnull
    LabourExpenseInformationResult getLabourExpenseInformation(
            @Nonnull final LabourExpenseInformationRequest informationRequest);

}
