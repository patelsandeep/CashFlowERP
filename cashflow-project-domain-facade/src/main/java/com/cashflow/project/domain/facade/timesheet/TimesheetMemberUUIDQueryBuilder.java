package com.cashflow.project.domain.facade.timesheet;

import com.cashflow.entitydomains.facade.context.ContextQueryBuilder;
import com.cashflow.project.domain.expenseinformation.TimesheetContext;
import com.cashflow.project.domain.project.personnel.TeamMember_;
import com.cashflow.project.domain.project.timesheet.TimeSheet;
import com.cashflow.project.domain.project.timesheet.TimeSheet_;
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
 * @since 15 Dec, 2016, 1:33:37 PM
 */
@ApplicationScoped
@TimesheetContextBuilder
public class TimesheetMemberUUIDQueryBuilder implements ContextQueryBuilder<TimesheetContext, TimeSheet> {

    @Override
    public Optional<Predicate> buildPredicate(@Nonnull final TimesheetContext context,
                                              @Nonnull final CriteriaBuilder criteriaBuilder,
                                              @Nonnull final Root<TimeSheet> queryRoot) {
        checkNotNull(context, "The context must not be null");
        checkNotNull(criteriaBuilder, "The criteriaBuilder must not be null");
        checkNotNull(queryRoot, "The queryRoot must not be null");

        final String teamMemberUUID = context.getTeamMemberUUID();
        if (teamMemberUUID == null) {
            return Optional.empty();
        }

        final Expression<String> teamMemberUUIDPath = queryRoot
                .get(TimeSheet_.teamMember)
                .get(TeamMember_.uuid);
        return Optional.of(criteriaBuilder
                .equal(teamMemberUUIDPath, teamMemberUUID));

    }

}
