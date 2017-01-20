package com.cashflow.project.domain.facade.timesheet;

import com.cashflow.entitydomains.facade.context.ContextQueryBuilder;
import com.cashflow.project.domain.expenseinformation.TimesheetContext;
import com.cashflow.project.domain.project.timesheet.TimeSheet;
import com.cashflow.project.domain.project.timesheet.TimeSheet_;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since 26 Dec, 2016, 6:36:04 PM
 */
@ApplicationScoped
@TimesheetContextBuilder
public class TimesheetUUIDsQueryBuilder implements ContextQueryBuilder<TimesheetContext, TimeSheet> {

    @Override
    public Optional<Predicate> buildPredicate(@Nonnull final TimesheetContext context,
                                              @Nonnull final CriteriaBuilder criteriaBuilder,
                                              @Nonnull final Root<TimeSheet> queryRoot) {
        checkNotNull(context, "The context must not be null");
        checkNotNull(criteriaBuilder, "The criteriaBuilder must not be null");
        checkNotNull(queryRoot, "The queryRoot must not be null");

        final List<String> timesheetUUIDs = context.getTimesheetUUIDs();
        if (null == timesheetUUIDs || timesheetUUIDs.isEmpty()) {
            return Optional.empty();
        }
        final Expression<String> timesheetUUIDPath = queryRoot
                .get(TimeSheet_.uuid);

        return Optional.of((timesheetUUIDPath.in(timesheetUUIDs)));

    }

}
