package com.cashflow.project.frontend.controller.model;

import com.cashflow.entitydomains.FileUrl;
import com.cashflow.project.domain.project.timesheet.ExpenseStatus;
import com.anosym.common.Amount;
import com.anosym.common.Currency;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * 
 * @since Dec 27, 2016, 10:40:07 AM
 */
@Getter
@Setter
public class SubContractorExpenseModel implements Serializable {

    private static final long serialVersionUID = -8056298089206037844L;

    @Nonnull
    private String subContractorExpenseID;

    @Nonnull
    private Currency currency;

    @Nullable
    private String subContractorExpenseUUID;

    @Nonnull
    private String project;

    @Nonnull
    private String customer;

    @Nonnull
    private Calendar date;

    @Nullable
    private String phase;

    @Nullable
    private String phaseName;

    @Nullable
    private String milestone;

    @Nullable
    private String milestoneName;

    @Nullable
    private String task;

    @Nullable
    private String taskName;

    @Nullable
    private String taskID;

    @Nullable
    private String supplierUUID;

    @Nullable
    private String supplierID;

    @Nullable
    private String subContractorService;

    @Nullable
    private String description;

    @Nonnull
    private String invoiceNumber;

    @Nonnull
    private Calendar invoiceDueDate;

    @Nonnull
    private BigDecimal invoiceAmount = BigDecimal.ZERO;

    @Nullable
    private String taxId;

    @Nullable
    private String taxId2;

    @Nullable
    private BigDecimal taxValue;

    @Nullable
    private BigDecimal taxValue2;

    @Nullable
    private BigDecimal labourValue = BigDecimal.ZERO;

    @Nullable
    private BigDecimal materialValue = BigDecimal.ZERO;

    @Nullable
    private BigDecimal equipmentValue = BigDecimal.ZERO;

    @Nullable
    private Amount nonBillableAmount;

    @Nonnull
    private Amount invoiceTotal;

    @Nullable
    private BigDecimal labourMarkUpValue = BigDecimal.ZERO;

    @Nullable
    private Amount labourMarkUpAmount;

    @Nullable
    private BigDecimal laborNonBillableValue = BigDecimal.ZERO;

    @Nullable
    private Amount laborBillableAmount;

    @Nullable
    private BigDecimal materialMarkUpValue = BigDecimal.ZERO;

    @Nullable
    private Amount materialMarkUpAmount;

    @Nullable
    private BigDecimal materialNonBillableValue = BigDecimal.ZERO;

    @Nullable
    private Amount materialBillableAmount;

    @Nullable
    private BigDecimal equipmentMarkUpValue = BigDecimal.ZERO;

    @Nullable
    private Amount equipmentMarkUpAmount;

    @Nullable
    private BigDecimal equipmentNonBillableValue = BigDecimal.ZERO;

    @Nullable
    private Amount equipmentBillableAmount;

    @Nullable
    private Amount totalMarkUpAmount;

    @Nullable
    private Amount totalBillableAmount;

    @Nullable
    private ExpenseStatus expenseStatus;

    @Nullable
    private String notes;

    @Nullable
    private String mode;

    @Nullable
    private String action;

    @Nonnull
    private List<FileUrl> expenseDocs = new ArrayList<>();

    @Nullable
    private HashMap<String, InputStream> inputStreams = new HashMap<>();

}
