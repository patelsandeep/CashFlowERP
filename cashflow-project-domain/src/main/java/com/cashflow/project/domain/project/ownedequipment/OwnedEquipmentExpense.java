package com.cashflow.project.domain.project.ownedequipment;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since 12 Dec, 2016, 5:56:27 PM
 */
@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NamedQueries({
    @NamedQuery(name = "OwnedEquipmentExpense.findByOwnedEquipmentIdOrUUID",
                query = "SELECT oe FROM OwnedEquipmentExpense oe WHERE (oe.equipmentId =:id OR oe.uuid = :id) and oe.businessAccountId = :businessAccountId")})
public class OwnedEquipmentExpense extends Equipment {

    private static final long serialVersionUID = -1552054509250937860L;

    @Nullable
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<OwnedEquipmentDetail> ownedEquipmentDetails;

    @Nonnull
    public Set<OwnedEquipmentDetail> getOwnedEquipmentDetails() {
        return ownedEquipmentDetails == null ? (ownedEquipmentDetails = new HashSet<>()) : ownedEquipmentDetails;
    }

    public void setOwnedEquipmentDetails(@Nonnull final Set<OwnedEquipmentDetail> ownedEquipmentDetails) {
        checkNotNull(ownedEquipmentDetails, "The ownedEquipmentDetails must not be null");

        final Set<OwnedEquipmentDetail> _ownedEquipmentDetails = getOwnedEquipmentDetails();
        _ownedEquipmentDetails.clear();
        _ownedEquipmentDetails.addAll(ownedEquipmentDetails);

    }

}
