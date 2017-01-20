package com.cashflow.project.api.expenseinformation;

import java.util.HashMap;
import java.util.Map;
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
public class LabourExpenseInformationResult {

    @Nonnull
    private Integer count;

    @Nonnull
    private Map<String, LabourExpenseInformation> labourExpenseInformations = new HashMap<>();

}
