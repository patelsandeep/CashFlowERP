package com.cashflow.project.domain.project.ownedequipment;

import com.cashflow.entitydomains.facade.context.Context;
import com.cashflow.project.domain.expenseinformation.FilterProperty;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.google.common.base.MoreObjects.firstNonNull;

/**
 *
 * 
 * @since 20 Dec, 2016, 1:04:58 PM
 */
@Getter
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OwnEquipmentContext implements Context {

    @Nonnull
    @FilterProperty
    private String projectUUID;

    @Nullable
    @FilterProperty
    private String phase;

    @Nullable
    @FilterProperty
    private String milestone;

    @Nullable
    @FilterProperty
    private String task;

    @Nullable
    @FilterProperty("freeText")
    private String name;

    @Nullable
    @FilterProperty
    private Integer offset;

    @Nullable
    @FilterProperty
    private Integer limit;

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
}
