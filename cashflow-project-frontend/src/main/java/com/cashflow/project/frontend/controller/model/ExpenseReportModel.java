package com.cashflow.project.frontend.controller.model;

import com.cashflow.project.domain.project.personnel.TeamMemberCategory;
import com.cashflow.project.domain.project.timesheet.ExpenseStatus;
import com.anosym.common.Amount;
import com.anosym.common.Currency;
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
 * @since 5 Dec, 2016, 12:52:53 PM
 */
@Getter
@Setter
public class ExpenseReportModel implements Serializable {

    private static final long serialVersionUID = -6976107294829103309L;

    @Nonnull
    private String expenseReportId;

    @Nonnull
    private String expenseReportUUID;

    @Nonnull
    private String customer;

    @Nonnull
    private String project;

    @Nonnull
    private String projectUUID;

    @Nullable
    private String phase;

    @Nullable
    private String milestone;

    @Nullable
    private String task;

    @Nullable
    private String phaseName;

    @Nullable
    private String milestoneName;

    @Nullable
    private String taskName;

    @Nullable
    private String taskId;

    @Nullable
    private String member;

    @Nullable
    private String memberName;

    @Nullable
    private TeamMemberCategory memberCategory;

    @Nullable
    private String memberId;

    @Nullable
    private String projectRole;

    @Nullable
    private Calendar weekStartDate;

    @Nullable
    private Calendar weekEndingDate;

    @Nonnull
    private List<ExpenseReportDataModel> reportDataModels = new ArrayList<>();

    @Nullable
    private Currency expenseCurrency;

    @Nullable
    private Amount taxTotal;

    @Nullable
    private Amount expenseTotal;

    @Nullable
    private Amount tax2Total;

    @Nullable
    private Amount totalExpenseAmount;

    @Nullable
    private ExpenseStatus expenseReportStatus;

}
