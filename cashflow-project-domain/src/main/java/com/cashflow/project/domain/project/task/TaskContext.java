package com.cashflow.project.domain.project.task;

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
 * @since Dec 9, 2016, 4:01:00 PM
 */
@Getter
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskContext implements Context {

    @Nullable
    private String parentLevelUUID;

    @Nullable
    private Integer offset;

    @Nullable
    private Integer limit;

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
    public Map<String, Order> getOrderFields() {
        return firstNonNull(orderFields, ImmutableMap.of("updatedDate", Order.DESC));
    }
}
