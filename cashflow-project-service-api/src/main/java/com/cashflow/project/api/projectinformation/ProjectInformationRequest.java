package com.cashflow.project.api.projectinformation;

import com.cashflow.indexing.service.search.QueryBuilder.SortOrder;
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
 * @since Nov 12, 2016, 10:00:08 AM
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true, builderClassName = "Builder")
public class ProjectInformationRequest {

    @Nonnull
    private String businessUnitUUID;

    @Nullable
    private String departmentUUID;

    @Nullable
    @Singular
    private Map<Filter, Object> filters;

    @Nullable
    @Singular
    private Map<Filter, SortOrder> sortOrders;

    @Nonnull
    public Map<Filter, Object> getFilters() {
        return firstNonNull(filters, ImmutableMap.<Filter, Object>of());
    }

    @Nonnull
    public Map<Filter, SortOrder> getSortOrders() {
        return firstNonNull(sortOrders, ImmutableMap.<Filter, SortOrder>of());
    }

}
