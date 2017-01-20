package com.cashflow.project.domain.facade.timesheet;

import com.cashflow.project.domain.facade.ProjectAbstractFacade;
import com.cashflow.project.domain.project.timesheet.TimeSheet;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 *
 * 
 * @since Dec 6, 2016, 10:11:34 AM
 */
@Stateless
public class TimeSheetFacade extends ProjectAbstractFacade<TimeSheet> {

    public TimeSheetFacade() {
        super(TimeSheet.class);
    }

    @Nullable
    public TimeSheet findByIdOrUUID(@Nonnull final String idOrUUID,
                                    @Nonnull final String businessAccountId) {
        checkNotNull(idOrUUID, "The idOrUUID must not be null");

        final List<TimeSheet> timeSheets = getEntityManager()
                .createNamedQuery("TimeSheet.findByIdOrUUID", TimeSheet.class)
                .setParameter("id", idOrUUID)
                .setParameter("businessAccountId", businessAccountId)
                .getResultList();
        checkState(timeSheets.size() <= 1,
                   "Invalid lookup. TimeSheet ID/uuid{%s} returns multiple results",
                   idOrUUID);

        return timeSheets
                .stream()
                .findAny()
                .orElse(null);
    }

}
