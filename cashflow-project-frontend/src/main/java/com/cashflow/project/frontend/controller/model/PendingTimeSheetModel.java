package com.cashflow.project.frontend.controller.model;

import com.cashflow.project.domain.project.timesheet.TimeSheetInfo;
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
 * @since 21 Dec, 2016, 2:52:01 PM
 */
@Getter
@Setter
public class PendingTimeSheetModel implements Serializable {

    @Nullable
    private String project;

    @Nullable
    private String member;

    @Nullable
    private String name;

    @Nullable
    private String memberUUID;

    @Nullable
    private String uuid;

    @Nullable
    private String timeSheetUUID;

    @Nullable
    private String projectUUID;

    @Nullable
    private String message;

    @Nullable
    private Calendar timesheetDate;

    @Nullable
    private Calendar toTimesheetDate;

    @Nullable
    private WorkTime billableHours = new WorkTime();

    @Nullable
    private WorkTime nonBillableHours = new WorkTime();

    @Nullable
    private WorkTime totalHours = new WorkTime();

    private boolean check;

    @Nonnull
    private List<TimeSheetInfo> timesheetInfos = new ArrayList<>();

    @Nonnull
    private List<TimeSheetInfoModel> timeSheetInfoModels = new ArrayList<>();
}
