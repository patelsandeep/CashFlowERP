package com.cashflow.project.frontend.controller.model;

import com.cashflow.entitydomains.FileUrl;
import com.cashflow.project.domain.project.expense.BillableType;
import com.cashflow.project.domain.project.expense.ExpenseType;
import com.anosym.common.Amount;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * 
 * @since 6 Dec, 2016, 10:04:04 AM
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseReportDataModel implements Serializable {

    private static final long serialVersionUID = -505927818148872852L;

    @Nullable
    private Calendar expenseDate = Calendar.getInstance();

    @Nullable
    private ExpenseType expenseType = ExpenseType.GENERAL_SUPPLIES;

    @Nonnull
    private BigDecimal expenseAmount = BigDecimal.ZERO;

    @Nullable
    private String taxId;

    @Nullable
    private String taxId2;

    @Nullable
    private BillableType billable = BillableType.YES;

    @Nullable
    private Amount totalAmount;

    @Nullable
    private String uuid;

    @Nonnull
    private List<FileUrl> projectFileUrls = new ArrayList<>();

    @Nullable
    private HashMap<String, InputStream> inputStreams = new HashMap<>();

}
