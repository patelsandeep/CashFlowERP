package com.cashflow.project.frontend.controller.model;

import com.cashflow.project.domain.project.expense.BillableType;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.timesheet.ExpenseStatus;
import com.anosym.common.Amount;
import com.anosym.common.Currency;
import java.io.Serializable;
import java.util.Calendar;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * 
 * @since 10 Jan, 2017, 11:13:42 AM
 */
@Getter
@Setter
public class ExpenseInfoModel implements Serializable {

    private static final long serialVersionUID = 3707158542760258304L;

    @Nullable
    private String projectUUID;

    @Nullable
    private String projectId;

    @Nullable
    private String projectName;

    @Nullable
    private Currency currency;

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
    private Amount billableAmount;

    @Nullable
    private Amount nonBillableAmount;

    @Nullable
    private Amount expenseAmount;

    @Nullable
    private String taskName;

    @Nullable
    private String memberName;

    @Nullable
    private String memberId;

    @Nullable
    private ProjectLevel projectLevel;

    @Nullable
    private Calendar expenseDetailDate;

    @Nullable
    private String pendingUUID;

    @Nullable
    private String expenseUUID;

    @Nullable
    private Calendar expenseDate;

    @Nullable
    private Calendar toExpenseDate;

    @Nullable
    private String customerName;

    @Nullable
    private ExpenseStatus expenseStatus;

}
