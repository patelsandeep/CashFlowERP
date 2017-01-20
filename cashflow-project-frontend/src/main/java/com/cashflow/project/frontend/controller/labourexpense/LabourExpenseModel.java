package com.cashflow.project.frontend.controller.labourexpense;

import com.cashflow.entitydomains.FileUrl;
import com.cashflow.project.domain.project.expense.ExpenseReport;
import com.cashflow.project.domain.project.expense.LabourExpenseType;
import com.cashflow.project.domain.project.personnel.TeamMemberCategory;
import com.cashflow.project.domain.project.timesheet.TimeSheet;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * 
 * @since Dec 15, 2016, 12:21:16 PM
 */
@Getter
@Setter
public class LabourExpenseModel implements Serializable {

    private static final long serialVersionUID = 1340231314563227046L;

    @Nonnull
    private String memberUUID;

    @Nonnull
    private TeamMemberCategory teamMemberCategory;

    @Nullable
    private LabourExpenseType labourExpenseType;

    @Nonnull
    private List<TimeSheet> timsheets = new ArrayList<>();

    @Nonnull
    private List<ExpenseReport> expenseReports = new ArrayList<>();

    @Nonnull
    private List<FileUrl> fileUrls = new ArrayList<>();

}
