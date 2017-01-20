package com.cashflow.project.api.expenseinformation;

import com.cashflow.project.domain.project.personnel.TeamMemberCategory;
import com.cashflow.project.domain.project.timesheet.WorkTime;
import com.cashflow.useraccount.domain.businessuser.FunctionalRole;
import com.anosym.common.Amount;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
 * 
 * @since Dec 12, 2016, 11:37:56 AM
 */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LabourExpenseInformation {

    /**
     * making this @Nullbale because there may be the possibilities that member can have either expense report or
     * timesheet report
     */
    @Nullable
    private String reportUUID;

    @Nonnull
    private TeamMemberCategory teamMemberCategory;

    @Nullable
    private String timeSheetUUID;

    @Nonnull
    private String projectId;

    @Nonnull
    private String phaseId;

    @Nonnull
    private String milestoneId;

    @Nonnull
    private String taskId;

    @Nonnull
    private String memberName;

    @Nonnull
    private String memberId;

    @Nullable
    private FunctionalRole systemRole;

    @Nullable
    private String department;

    @Nullable
    private String projectRole;

    @Nullable
    private WorkTime regularHours;

    @Nullable
    private WorkTime ptoHours;

    @Nullable
    private WorkTime overTimeHours;

    @Nullable
    private WorkTime totalHours;

    @Nullable
    private Amount expenseReportAmount;
}
