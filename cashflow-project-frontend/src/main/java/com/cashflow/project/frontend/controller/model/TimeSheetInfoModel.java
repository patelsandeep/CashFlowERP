package com.cashflow.project.frontend.controller.model;

import com.cashflow.project.domain.project.expense.BillableType;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.timesheet.ExpenseStatus;
import com.cashflow.project.domain.project.timesheet.WorkTime;
import java.io.Serializable;
import java.util.Calendar;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * 
 * @since 27 Dec, 2016, 12:26:04 PM
 */
@Getter
@Setter
public class TimeSheetInfoModel implements Serializable {

    private static final long serialVersionUID = -5537783430002318473L;

    @Nullable
    private String projectUUID;

    @Nullable
    private String projectId;

    @Nullable
    private String projectName;

    @Nullable
    private String pendingUUID;

    @Nullable
    private String timeSheetUUID;

    @Nullable
    private String phaseId;

    @Nullable
    private String timesheetId;

    @Nullable
    private String phaseName;

    @Nullable
    private String milestoneId;

    @Nullable
    private String milestoneName;

    @Nullable
    private String taskId;

    @Nullable
    private BillableType billableType;

    @Nullable
    private WorkTime regularTime;

    @Nullable
    private WorkTime overTime;

    @Nullable
    private WorkTime ptoTime;

    @Nullable
    private String taskName;

    @Nullable
    private String memberName;

    @Nullable
    private String memberId;

    @Nullable
    private ProjectLevel projectLevel;

    @Nullable
    private Calendar timeSheetInfoDate;

    @Nullable
    private Calendar timesheetDate;

    @Nullable
    private Calendar toTimesheetDate;

    @Nullable
    private String customerName;

    @Nullable
    private ExpenseStatus timesheetStatus;

}
