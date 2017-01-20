package com.cashflow.project.domain.facade.timesheet;

import com.cashflow.entitydomains.facade.context.ContextQueryBuilder;
import com.cashflow.project.domain.expenseinformation.TimesheetContext;
import com.cashflow.project.domain.project.timesheet.TimeSheet;
import com.cashflow.project.domain.project.timesheet.TimeSheet_;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since Jan 19, 2017, 10:39:09 AM
 */
@ApplicationScoped
@TimesheetContextBuilder
public class TimesheetDepartmentQueryBuilder implements ContextQueryBuilder<TimesheetContext, TimeSheet> {

    @Override
    public Optional<Predicate> buildPredicate(TimesheetContext context,
                                              CriteriaBuilder criteriaBuilder,
                                              Root<TimeSheet> queryRoot) {
        checkNotNull(context, "The context must not be null");
        checkNotNull(criteriaBuilder, "The criteriaBuilder must not be null");
        checkNotNull(queryRoot, "The queryRoot must not be null");

        final String departmentUUID = context.getDepartmentUUID();
        if (departmentUUID == null) {
            return Optional.empty();
        }

        final Path<String> departmentUUIDPath = queryRoot.get(TimeSheet_.departmentUUID);
        return Optional.of(criteriaBuilder.equal(departmentUUIDPath, departmentUUID));
    }

}
