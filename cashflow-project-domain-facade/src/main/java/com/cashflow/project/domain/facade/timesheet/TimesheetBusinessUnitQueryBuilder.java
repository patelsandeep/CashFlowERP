package com.cashflow.project.domain.facade.timesheet;

import com.cashflow.entitydomains.facade.context.ContextQueryBuilder;
import com.cashflow.project.domain.expenseinformation.TimesheetContext;
import com.cashflow.project.domain.project.timesheet.TimeSheet;
import com.cashflow.project.domain.project.timesheet.TimeSheet_;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since Jan 19, 2017, 10:22:13 AM
 */
@ApplicationScoped
@TimesheetContextBuilder
public class TimesheetBusinessUnitQueryBuilder implements ContextQueryBuilder<TimesheetContext, TimeSheet> {

    @Override
    public Optional<Predicate> buildPredicate(@Nonnull final TimesheetContext context,
                                              @Nonnull final CriteriaBuilder criteriaBuilder,
                                              @Nonnull final Root<TimeSheet> queryRoot,
                                              @Nonnull final AbstractQuery<?> criteriaQuery) {
        checkNotNull(context, "The context must not be null");
        checkNotNull(criteriaBuilder, "The criteriaBuilder must not be null");
        checkNotNull(queryRoot, "The queryRoot must not be null");

        final String businessUnitsUUID = context.getBusinessUnitUUID();
        if (businessUnitsUUID == null) {
            return Optional.empty();
        }

        final Expression<String> businessUnitUUIDsPath = queryRoot.get(TimeSheet_.businessUnitUUID);
        return Optional.of(criteriaBuilder.equal(businessUnitUUIDsPath, businessUnitsUUID));
    }

}
