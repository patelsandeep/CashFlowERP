package com.cashflow.project.domain.project.expense.subcontractor;

import com.cashflow.entitydomains.AbstractBusinessAccountDomain;
import com.anosym.common.Amount;
import java.math.BigDecimal;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 *
 * 
 * @since Dec 26, 2016, 5:01:28 PM
 */
@Entity
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Categorization extends AbstractBusinessAccountDomain {

    private static final long serialVersionUID = -6819255682765038510L;

    @Nonnull
    private Amount labourExpense;

    @Nullable
    private Amount labourNonBillable;

    @Nullable
    private BigDecimal labourMarkUp;

    @Nonnull
    private Amount materialExpense;

    @Nullable
    private Amount materialNonBillable;

    @Nullable
    private BigDecimal materialMarkUp;

    @Nonnull
    private Amount equipmentExpense;

    @Nullable
    private Amount equipmentNonBillable;

    @Nullable
    private BigDecimal equipmentMarkUp;

}
