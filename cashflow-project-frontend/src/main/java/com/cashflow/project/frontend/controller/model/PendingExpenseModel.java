package com.cashflow.project.frontend.controller.model;

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
 * @since 4 Jan, 2017, 2:26:25 PM
 */
@Getter
@Setter
public class PendingExpenseModel implements Serializable {

    private static final long serialVersionUID = 7115678137169182398L;

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
    private String expenseUUID;

    @Nullable
    private String projectUUID;

    @Nullable
    private String message;

    @Nullable
    private Calendar expenseDate;

    @Nullable
    private Calendar toExpenseDate;

    @Nullable
    private Amount billableExpense;

    @Nullable
    private Amount nonBillableExpense;

    @Nullable
    private Amount totalExpense;

    private boolean check;

    @Nullable
    private Currency currency;

}
