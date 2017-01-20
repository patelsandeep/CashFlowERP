package com.cashflow.project.domain.project.milestone;

import com.cashflow.entitydomains.facade.context.Context;
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
 * @since Dec 9, 2016, 3:53:44 PM
 */
@Getter
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MilestoneContext implements Context {

    @Nullable
    private String parentLevelUUID;

    @Nullable
    private Integer offset;

    @Nullable
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
