package com.cashflow.project.api.expense.subcontractor;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
 * 
 * @since Dec 12, 2016, 12:15:20 PM
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubContractorExpenseInformationResult {

    @Nonnull
    private Integer count;

    @Nonnull
    private List<SubContractorExpenseInformation> subContractorExpenseInformations = new ArrayList<>();

}
