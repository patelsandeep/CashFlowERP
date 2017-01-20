package com.cashflow.project.frontend.controller.model;

import com.cashflow.project.domain.project.personnel.TeamMemberCategory;
import com.cashflow.project.domain.project.timesheet.ExpenseStatus;
import com.cashflow.project.domain.project.timesheet.WorkTime;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * 
 * @since Dec 6, 2016, 12:03:10 PM
 */
@Getter
@Setter
public class TimeSheetEntryModel implements Serializable {

    private static final long serialVersionUID = 4401954970951546157L;

    @Nonnull
    private String timeSheetID;

    @Nullable
    private String timeSheetUUID;

    @Nonnull
    private String project;

    @Nonnull
    private String customer;

    @Nullable
    private String phaseUUID;

    @Nullable
    private String phaseName;

    @Nullable
    private String milestoneUUID;

    @Nullable
    private Calendar approvalDate;

    @Nullable
    private String mileStoneName;

    @Nullable
    private String taskUUID;

    @Nonnull
    private String taskID;

    @Nullable
    private String taskname;

    @Nullable
    private String member;

    @Nullable
    private String memberName;

    @Nonnull
    private String memberID;

    @Nullable
    private String projectRole;

    @Nonnull
    private String projectRoleUUID;

    @Nonnull
    private Calendar weekStartDate;

    @Nonnull
    private Calendar weekEndDate;

    @Nullable
    private WorkTime totalRegularTime;

    @Nullable
    private WorkTime totalOverTime;

    @Nullable
    private WorkTime totalPtoTime;

    @Nullable
    private WorkTime finalTotal;

    @Nonnull
    private List<TimeSheetWeeklyEntryModel> weeklyEntries = new ArrayList<>();

    @Nullable
    private String supplierName;

    @Nullable
    private String supplierType;

    @Nullable
    private ExpenseStatus timesheetStatus;

    @Nullable
    private TeamMemberCategory teamMemberCategory;

}
