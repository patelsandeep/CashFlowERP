package com.cashflow.project.domain.project.ownedequipment;

import com.cashflow.entitydomains.AbstractBusinessAccountDomain;
import com.cashflow.project.domain.project.level.ProjectLevel;
import javax.annotation.Nonnull;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static javax.persistence.EnumType.STRING;

/**
 *
 * 
 * @since 23 Dec, 2016, 11:49:32 AM
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class Equipment extends AbstractBusinessAccountDomain {

    private static final long serialVersionUID = -7692683489150394353L;

    @Nonnull
    @ManyToOne
    private ProjectLevel projectLevel;

    @Nonnull
    private String equipmentId;

    @Nonnull
    @Enumerated(STRING)
    private EquipmentStatus status;

}
