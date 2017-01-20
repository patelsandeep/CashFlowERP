package com.cashflow.project.domain.service.expenseinformation;

import com.cashflow.project.domain.facade.level.ProjectLevelFacade;
import com.cashflow.project.domain.project.level.ProjectLevel;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.EJB;
import javax.enterprise.context.Dependent;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 *
 * 
 * @since Dec 21, 2016, 3:29:19 PM
 */
@Dependent
public class ProjectLevelUUIDCollector implements Serializable {

    private static final long serialVersionUID = -5253837842157943837L;

    @EJB
    private ProjectLevelFacade projectLevelFacade;

    @Nonnull
    public List<String> collectLevelUUIDsForFreeText(@Nullable final String freeText,
                                                     @Nonnull final String businessAccountId) {
        if (isNullOrEmpty(freeText)) {
            return Collections.EMPTY_LIST;
        }

        final List<ProjectLevel<?>> levels = projectLevelFacade
                .findUUIDByNameorNumber(freeText, businessAccountId);
        final List<String> uuids = levels
                .stream()
                .map((level) -> level.getProjectLevelsUUIDs())
                .flatMap(List::stream)
                .collect(Collectors.toList());

        return uuids;

    }

}
