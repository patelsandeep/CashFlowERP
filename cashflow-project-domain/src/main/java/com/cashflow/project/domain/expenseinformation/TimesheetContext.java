package com.cashflow.project.domain.expenseinformation;

import com.cashflow.entitydomains.facade.context.Context;
import com.cashflow.project.domain.project.timesheet.ExpenseStatus;
import java.util.Calendar;
import java.util.List;
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
 * @since Dec 12, 2016, 1:41:27 PM
 */
@Getter
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TimesheetContext implements Context {

    @Nullable
    private String businessUnitUUID;

    @Nullable
    private String departmentUUID;

    @Nullable
    private Calendar timesheetDate;

    @Nullable
    @FilterProperty
    private String projectUUID;

    @Nullable
    @FilterProperty
    private String teamMemberUUID;

    @Nullable
    @FilterProperty
    private String subContractorUUID;

    @Nullable
    @FilterProperty
    private String accessRoleUUID;

    @Nullable
    @FilterProperty
    private Calendar timesheetFromDate;

    @Nullable
    @FilterProperty
    private Calendar timesheetToDate;

    @Nullable
    @FilterProperty
    private ExpenseStatus status;

    @Nullable
    @FilterProperty("freeText")
    private String name;

    @Nullable
    @FilterProperty
    private List<String> projectLevelUUIDs;

    @Nullable
    @FilterProperty
    private List<String> peopleUUIDs;

    @Nullable
    @FilterProperty
    private List<String> timesheetUUIDs;

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
