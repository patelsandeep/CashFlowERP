package com.cashflow.project.domain.facade.timesheet;

import com.cashflow.entitydomains.facade.context.ContextQueryBuilder;
import com.cashflow.project.domain.expenseinformation.TimesheetContext;
import com.cashflow.project.domain.project.timesheet.TimeSheet;
import com.cashflow.project.domain.project.timesheet.TimeSheetInfo;
import com.cashflow.project.domain.project.timesheet.TimeSheetInfo_;
import com.cashflow.project.domain.project.timesheet.TimeSheet_;
import java.util.Calendar;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since Jan 19, 2017, 5:34:58 PM
 */
@ApplicationScoped
@TimesheetContextBuilder
public class TimesheetDateQueryBuilder implements ContextQueryBuilder<TimesheetContext, TimeSheet> {

    @Override
    public Optional<Predicate> buildPredicate(TimesheetContext context,
                                              CriteriaBuilder criteriaBuilder,
                                              Root<TimeSheet> queryRoot) {
        checkNotNull(context, "The context must not be null");
        checkNotNull(criteriaBuilder, "The criteriaBuilder must not be null");
        checkNotNull(queryRoot, "The queryRoot must not be null");

        final Calendar date = context.getTimesheetDate();
        if (date == null) {
            return Optional.empty();
        }

        final Join<TimeSheet, TimeSheetInfo> descriptionJoin = queryRoot.join(TimeSheet_.timeSheetInfos);

        final Path<Calendar> datePathQuery = descriptionJoin.get(TimeSheetInfo_.timeSheetDate);

        return Optional.of(criteriaBuilder.equal(datePathQuery, date));
    }
}
