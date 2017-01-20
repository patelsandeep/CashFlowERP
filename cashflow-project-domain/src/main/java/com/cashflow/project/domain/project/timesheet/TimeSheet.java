package com.cashflow.project.domain.project.timesheet;

import com.cashflow.entitydomains.AbstractBusinessAccountDomain;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.personnel.SubContractor;
import com.cashflow.project.domain.project.personnel.TeamMember;
import java.io.Serializable;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.TemporalType.DATE;

/**
 *
 * 
 * @since Dec 5, 2016, 3:46:28 PM
 */
@Entity
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NamedQueries({
    @NamedQuery(name = "TimeSheet.findByIdOrUUID",
                query = "SELECT ts FROM TimeSheet ts WHERE (ts.timeSheetID = :id OR ts.uuid = :id) and ts.businessAccountId = :businessAccountId")
})
public class TimeSheet extends AbstractBusinessAccountDomain implements Serializable {

    private static final long serialVersionUID = -2785409084730150067L;

    @Nonnull
    @ManyToOne
    private ProjectLevel projectLevel;

    @Nonnull
    private String timeSheetID;

    @Nullable
    @ManyToOne
    private TeamMember teamMember;

    @Nullable
    @ManyToOne
    private SubContractor subContractor;

    @Nonnull
    @Enumerated(STRING)
    private ExpenseStatus status;

    @Nullable
    @Temporal(DATE)
    private Calendar approvalDate;

    @Nullable
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<TimeSheetInfo> timeSheetInfos;

    @Nonnull
    public Set<TimeSheetInfo> getTimeSheetInfos() {
        return timeSheetInfos == null ? (timeSheetInfos = new HashSet<>()) : timeSheetInfos;
    }

    public void setTimeSheetInfos(@Nonnull final Set<TimeSheetInfo> timeSheetInfos) {
        checkNotNull(timeSheetInfos, "The timeSheetInfos must not be null");

        getTimeSheetInfos().clear();
        getTimeSheetInfos().addAll(timeSheetInfos);
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
