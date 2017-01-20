package com.cashflow.project.domain.project.timesheet;

import com.cashflow.entitydomains.AbstractBusinessAccountDomain;
import com.cashflow.project.domain.project.expense.BillableType;
import java.io.Serializable;
import java.util.Calendar;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.TemporalType.DATE;

/**
 *
 * 
 * @since Dec 9, 2016, 1:56:23 PM
 */
@Entity
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class TimeSheetInfo extends AbstractBusinessAccountDomain implements Serializable {

    private static final long serialVersionUID = 2303305168971009666L;

    @Nonnull
    @Temporal(DATE)
    private Calendar timeSheetDate;

    @Nonnull
    @Enumerated(STRING)
    private BillableType billableType;

    @Nullable
    @Enumerated(STRING)
    private PTOCategory ptoCategory;

    @Nullable
    private WorkTime regularTime;

    @Nullable
    private WorkTime overTime;

    @Nullable
    private WorkTime ptoTime;

}
