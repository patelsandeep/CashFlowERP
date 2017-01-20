package com.cashflow.project.frontend.controller.model;

import com.cashflow.entitydomains.FileUrl;
import com.cashflow.project.domain.project.personnel.MarkUpMethod;
import com.cashflow.project.domain.project.rentedequipment.BillUnit;
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
 * @since 19 Dec, 2016, 10:59:06 AM
 */
@Getter
@Setter
public class RentedEquipmentExpenseModel implements Serializable {

    private static final long serialVersionUID = -5220673437434863745L;

    @Nullable
    private String rentedEquipmentId;

    @Nullable
    private String rentedEquipmentUUID;

    @Nonnull
    private String customer;

    @Nonnull
    private String project;

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

    @Nonnull
    private Calendar date = Calendar.getInstance();

    @Nonnull
    private Calendar invocieDueDate = Calendar.getInstance();

    @Nullable
    private String supplier;

    @Nullable
    private String supplierName;

    @Nullable
    private String supplierId;

    @Nullable
    private String equipmentName;

    @Nullable
    private String serialNumber;

    @Nullable
    private String invoiceNumber;

    @Nullable
    private Currency currency;

    @Nonnull
    private BigDecimal supplierInvoiceAmount = BigDecimal.ZERO;

    @Nullable
    private String taxId;

    @Nullable
    private Amount taxAmount;

    @Nullable
    private String taxId2;

    @Nullable
    private Amount taxAmount2;

    @Nonnull
    private Amount supplierInvoiceTotal;

    @Nonnull
    private BillUnit billingUnit = BillUnit.PER_HOUR;

    @Nonnull
    private BigDecimal costRate = BigDecimal.ZERO;

    @Nonnull
    private BigDecimal billRate = BigDecimal.ZERO;

    private int qty = 0;

    private int billableQty = 0;

    private int nonBillableQty = 0;

    @Nonnull
    private Amount billableAmount;

    @Nonnull
    private Amount nonBillableAmount;

    @Nonnull
    private MarkUpMethod markUpMethod = MarkUpMethod.PERCENTAGE;

    @Nonnull
    private BigDecimal markUpValue = BigDecimal.ZERO;

    @Nullable
    private String notes;

    @Nonnull
    private List<FileUrl> rentedEquipmentDocs = new ArrayList<>();

    @Nullable
    private HashMap<String, InputStream> inputStreams = new HashMap<>();
}
