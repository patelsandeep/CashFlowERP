package com.cashflow.project.domain.project.personnel;

import com.cashflow.entitydomains.facade.context.Context;
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
 * @since 1 Dec, 2016, 11:39:13 AM
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubcontractorContext implements Context {

    @Nullable
    private Integer offset;

    @Nullable
    private Integer limit;

    @Nullable
    private String projectLevelUUID;

    @Nullable
    private String projectRole;

    @Nullable
    private String memberName;

    @Nullable
    private String search;

    @Nullable
    private String memberId;

    @Nullable
    private String businessUnitUUID;

    @Nullable
    private String departmentUUID;

    @Nullable
    @Singular
    private Map<String, Context.Order> orderFields;

    @Nonnull
    @Override
    public Integer getOffset() {
        return firstNonNull(offset, 0);
    }

    @Nonnull
    @Override
    public Integer getLimit() {
        return firstNonNull(limit, 100);
    }

    @Nonnull
    @Override
    public Map<String, Context.Order> getOrderFields() {
        return firstNonNull(orderFields, ImmutableMap.of("updatedDate", Context.Order.DESC));
    }
}
