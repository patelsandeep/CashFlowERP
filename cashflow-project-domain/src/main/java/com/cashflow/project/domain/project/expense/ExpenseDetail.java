package com.cashflow.project.domain.project.expense;

import com.cashflow.entitydomains.AbstractDomain;
import com.cashflow.entitydomains.FileUrl;
import com.anosym.common.Amount;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
 * @since 5 Dec, 2016, 2:21:41 PM
 */
@Entity
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpenseDetail extends AbstractDomain {

    private static final long serialVersionUID = -2461782402968126335L;

    @Nonnull
    @Enumerated(STRING)
    private ExpenseType expenseType;

    @Nonnull
    private Amount expenseAmount;

    @Setter
    @Nonnull
    @Temporal(TemporalType.DATE)
    private Calendar expenseDate;

    @Nullable
    private String taxId;

    @Nullable
    private String taxId2;

    @Nonnull
    private Amount totalExpense;

    @Nonnull
    private BillableType billable;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<FileUrl> expenseDocuments;

    @Nonnull
    public List<FileUrl> getExpenseDocuments() {
        return expenseDocuments != null ? expenseDocuments : (expenseDocuments = new ArrayList<>());
    }

    public void setDocuments(@Nonnull final List<FileUrl> expenseDocuments) {
        checkNotNull(expenseDocuments, "The expenseDocuments must not be null");

        getExpenseDocuments().clear();
        getExpenseDocuments().addAll(expenseDocuments);
    }

}
