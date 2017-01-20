package com.cashflow.project.api.expenseinformation;

import com.cashflow.project.domain.project.filtering.Filter;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;

import static com.google.common.base.MoreObjects.firstNonNull;

/**
 *
 * 
 * @since Dec 12, 2016, 12:17:00 PM
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true, builderClassName = "Builder")
public class LabourExpenseInformationRequest {

    @Nullable
    @Singular
    private Map<Filter, Object> filters;

    @Nonnull
    public Map<Filter, Object> getFilters() {
        return firstNonNull(filters, ImmutableMap.<Filter, Object>of());
    }

}
