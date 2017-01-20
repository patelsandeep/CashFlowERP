package com.cashflow.project.domain.project.expense;

import com.cashflow.entitydomains.AbstractBusinessAccountDomain;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.personnel.SubContractor;
import com.cashflow.project.domain.project.personnel.TeamMember;
import com.cashflow.project.domain.project.timesheet.ExpenseStatus;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
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
 * @since 5 Dec, 2016, 2:14:09 PM
 */
@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@NamedQueries({
    @NamedQuery(name = "ExpenseReport.findByExpenseReportIdOrUUID",
                query = "SELECT er FROM ExpenseReport er WHERE (er.expenseReportId =:id OR er.uuid = :id) and er.businessAccountId = :businessAccountId")})
public class ExpenseReport extends AbstractBusinessAccountDomain {

    private static final long serialVersionUID = -2032147517674547888L;

    @Nonnull
    @ManyToOne
    private ProjectLevel projectLevel;

    @Nullable
    @ManyToOne
    private TeamMember teamMember;

    @Nullable
    @ManyToOne
    private SubContractor subContractor;

    @Nonnull
    private String expenseReportId;

    @Setter
    @Nonnull
    @Temporal(TemporalType.DATE)
    private Calendar weekStartDate;

    @Setter
    @Nonnull
    @Temporal(TemporalType.DATE)
    private Calendar weekEndDate;

    @Setter
    @Nullable
    @Temporal(TemporalType.DATE)
    private Calendar approvalDate;

    @Nonnull
    @Enumerated(STRING)
    private ExpenseStatus status;

    @Nullable
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<ExpenseDetail> expenseDetails;

    @Nonnull
    public Set<ExpenseDetail> getExpenseDetails() {
        return expenseDetails == null ? (expenseDetails = new HashSet<>()) : expenseDetails;
    }

    public void setExpenseDetails(@Nonnull final Set<ExpenseDetail> expenseDetails) {
        checkNotNull(expenseDetails, "The expenseDetails must not be null");

        final Set<ExpenseDetail> _expenseDetails = getExpenseDetails();
        _expenseDetails.clear();
        _expenseDetails.addAll(expenseDetails);

    }

    @Override
    public void setBusinessUnitUUID(@Nullable final String businessUnitUUID) {
        super.setBusinessUnitUUID(businessUnitUUID);
    }

    @Override
    public void setDepartmentUUID(@Nullable final String departmentUUID) {
        super.setDepartmentUUID(departmentUUID);
    }

    @Override
    public void setAuthorizedUserUUID(@Nullable final String authorizedUserUUID) {
        super.setAuthorizedUserUUID(authorizedUserUUID);
    }

}
