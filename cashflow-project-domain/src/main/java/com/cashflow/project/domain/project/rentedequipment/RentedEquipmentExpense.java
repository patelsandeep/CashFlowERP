package com.cashflow.project.domain.project.rentedequipment;

import com.cashflow.entitydomains.FileUrl;
import com.cashflow.project.domain.project.ownedequipment.Equipment;
import com.cashflow.project.domain.project.personnel.MarkUpMethod;
import com.anosym.common.Amount;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.persistence.EnumType.STRING;

/**
 *
 * 
 * @since 16 Dec, 2016, 6:24:15 PM
 */
@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@NamedQueries({
    @NamedQuery(name = "RentedEquipmentExpense.findByRentedEquipmentIdOrUUID",
                query = "SELECT re FROM RentedEquipmentExpense re WHERE (re.equipmentId =:id OR re.uuid = :id) and re.businessAccountId = :businessAccountId")})
public class RentedEquipmentExpense extends Equipment {

    private static final long serialVersionUID = -5246475485392495611L;

    @Nonnull
    @Temporal(TemporalType.DATE)
    private Calendar equipmentDate;

    @Nonnull
    private String supplierUUID;

    @Nonnull
    private String equipmentName;

    @Nonnull
    private String serialNumber;

    @Nonnull
    private String invoiceNumber;

    @Nonnull
    @Temporal(TemporalType.DATE)
    private Calendar invoiceDueDate;

    @Nonnull
    private Amount supplierInvoiceAmount;

    @Nullable
    private String taxId;

    @Nullable
    private String taxId2;

    @Nullable
    private Integer billableQty;

    @Nullable
    private Integer nonBillableQty;

    @Nonnull
    private Amount costRate;

    @Nullable
    @Enumerated(STRING)
    private BillUnit billUnit;

    @Nullable
    @Enumerated(STRING)
    private MarkUpMethod markUpMethod;

    @Nullable
    private BigDecimal markUp;

    @Nullable
    private String notes;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<FileUrl> equipmentDocuments;

    @Nonnull
    public List<FileUrl> getEquipmentDocuments() {
        return equipmentDocuments != null ? equipmentDocuments : (equipmentDocuments = new ArrayList<>());
    }

    public void setEquipmentDocuments(@Nonnull final List<FileUrl> equipmentDocuments) {
        checkNotNull(equipmentDocuments, "The equipmentDocuments must not be null");

        getEquipmentDocuments().clear();
        getEquipmentDocuments().addAll(equipmentDocuments);
    }
}
