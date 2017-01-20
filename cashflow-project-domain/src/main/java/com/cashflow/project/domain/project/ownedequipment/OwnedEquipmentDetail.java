package com.cashflow.project.domain.project.ownedequipment;

import com.cashflow.entitydomains.AbstractBusinessAccountDomain;
import com.cashflow.entitydomains.FileUrl;
import com.cashflow.project.domain.project.expense.BillableType;
import com.anosym.common.Amount;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.Nonnull;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.persistence.EnumType.STRING;

/**
 *
 * 
 * @since 12 Dec, 2016, 6:05:24 PM
 */
@Entity
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OwnedEquipmentDetail extends AbstractBusinessAccountDomain implements Serializable {

    private static final long serialVersionUID = -8540979007820895965L;

    @Nonnull
    @Temporal(TemporalType.DATE)
    private Calendar equipmentDate;

    @Nonnull
    private String fixedAssetUUID;

    @Nonnull
    private Amount billRate;

    @Nonnull
    private Integer unit;

    @Nonnull
    @Enumerated(STRING)
    private BillableType billable;

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
