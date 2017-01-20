package com.cashflow.project.domain.facade.timesheet;

import com.cashflow.entitydomains.facade.context.ContextQueryBuilder;
import com.cashflow.project.domain.expenseinformation.TimesheetContext;
import com.cashflow.project.domain.project.timesheet.TimeSheet;
import com.cashflow.project.domain.project.timesheet.TimeSheetInfo;
import com.cashflow.project.domain.project.timesheet.TimeSheetInfo_;
import com.cashflow.project.domain.project.timesheet.TimeSheet_;
import java.util.Calendar;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SetAttribute;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since Jan 18, 2017, 4:58:31 PM
 */
@ApplicationScoped
@TimesheetContextBuilder
public class TimesheetBetweenDateQueryBuilder implements ContextQueryBuilder<TimesheetContext, TimeSheet> {

    @Override
    public Optional<Predicate> buildPredicate(TimesheetContext context,
                                              CriteriaBuilder criteriaBuilder,
                                              Root<TimeSheet> queryRoot) {
        checkNotNull(context, "The context must not be null");
        checkNotNull(criteriaBuilder, "The criteriaBuilder must not be null");
        checkNotNull(queryRoot, "The queryRoot must not be null");

        final Calendar fromDate = context.getTimesheetFromDate();
        final Calendar toDate = context.getTimesheetToDate();
        return doBuildPredicate(context, criteriaBuilder, queryRoot, TimeSheet_.timeSheetInfos, fromDate,
                                toDate);
    }

    @Nonnull
    private Optional<Predicate> doBuildPredicate(@Nonnull final TimesheetContext context,
                                                 @Nonnull final CriteriaBuilder criteriaBuilder,
                                                 @Nonnull final Root<TimeSheet> queryRoot,
                                                 @Nonnull final SetAttribute<TimeSheet, TimeSheetInfo> datePath,
                                                 @Nullable final Calendar fromDate,
                                                 @Nullable final Calendar toDate) {
        checkNotNull(context, "The context must not be null");
        checkNotNull(criteriaBuilder, "The criteriaBuilder must not be null");
        checkNotNull(queryRoot, "The queryRoot must not be null");
        checkNotNull(datePath, "The datePath must not be null");

        final Join<TimeSheet, TimeSheetInfo> descriptionJoin = queryRoot.join(TimeSheet_.timeSheetInfos);

        final Path<Calendar> datePathQuery = descriptionJoin.get(TimeSheetInfo_.timeSheetDate);

        if (fromDate != null && toDate != null) {
            return Optional.of(criteriaBuilder.between(datePathQuery, fromDate, toDate));
        }

        if (fromDate != null) {
            return Optional.of(criteriaBuilder.greaterThanOrEqualTo(datePathQuery, fromDate));
        }

        if (toDate != null) {
            return Optional.of(criteriaBuilder.lessThanOrEqualTo(datePathQuery, toDate));
        }

        return Optional.empty();
    }

}
