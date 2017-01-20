package com.cashflow.project.domain.project.expense.subcontractor;

import com.cashflow.entitydomains.AbstractBusinessAccountDomain;
import com.cashflow.entitydomains.FileUrl;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.timesheet.ExpenseStatus;
import com.anosym.common.Amount;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since Dec 26, 2016, 3:30:21 PM
 */
@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class SubContractorExpense extends AbstractBusinessAccountDomain {

    private static final long serialVersionUID = -8676944578269345097L;

    @Nonnull
    @Temporal(TemporalType.DATE)
    private Calendar expenseDate;

    @Nonnull
    private String expenseScheduleId;

    @Nonnull
    @ManyToOne
    private ProjectLevel<?> projectLevel;

    @Nonnull
    private String supplierUUID;

    @Nonnull
    private String subContractorService;

    @Nullable
    private String description;

    @Nonnull
    private String invoiceNumber;

    @Nonnull
    private Calendar invoiceDueDate;

    @Nonnull
    private Amount invoiceAmount;

    @Nonnull
    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    private Categorization categorization;

    @Nullable
    private String taxId;

    @Nullable
    private String taxId2;

    @Nullable
    private String notes;

    @Nonnull
    @Enumerated(EnumType.STRING)
    private ExpenseStatus expenseStatus;

    @Nullable
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<FileUrl> expenseDocuments;

    @Nonnull
    public Set<FileUrl> getExpenseDocuments() {
        return expenseDocuments != null ? expenseDocuments : (expenseDocuments = new HashSet<>());
    }

    public void setExpenseDocuments(@Nonnull final Set<FileUrl> expenseDocuments) {
        checkNotNull(expenseDocuments, "The expenseDocuments must not be null");

        getExpenseDocuments().clear();
        getExpenseDocuments().addAll(expenseDocuments);
    }

}
