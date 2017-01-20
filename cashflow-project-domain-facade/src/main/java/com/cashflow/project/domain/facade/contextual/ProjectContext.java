package com.cashflow.project.domain.facade.contextual;

import com.cashflow.entitydomains.facade.context.Context;
import com.cashflow.project.domain.expenseinformation.FilterProperty;
import java.util.Calendar;
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
 * @since Nov 12, 2016, 1:41:27 PM
 */
@Getter
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectContext implements Context {

    @Nullable
    private String businessUnitUUID;

    @Nullable
    private String departmentUUID;

    @Nullable
    @FilterProperty
    private String projectManager;

    @Nullable
    @FilterProperty
    private Calendar createdFromDate;

    @Nullable
    @FilterProperty
    private Calendar createdToDate;

    @Nullable
    @FilterProperty
    private Calendar beginningFromDate;

    @Nullable
    @FilterProperty
    private Calendar beginningToDate;

    @Nullable
    @FilterProperty
    private Calendar endingFromDate;

    @Nullable
    @FilterProperty
    private Calendar endingToDate;

    @Nullable
    @FilterProperty
    private String customerUUID;

    @Nullable
    @FilterProperty
    private String cityLocation;

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
