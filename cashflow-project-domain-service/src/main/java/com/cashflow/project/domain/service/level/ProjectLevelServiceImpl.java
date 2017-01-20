package com.cashflow.project.domain.service.level;

import com.cashflow.backend.businessaccount.authorization.RequiresBusinessAccount;
import com.cashflow.project.api.level.ProjectLevelService;
import com.cashflow.project.domain.facade.level.ProjectLevelFacade;
import com.cashflow.project.domain.project.level.ProjectLevel;
import java.util.List;
import javax.annotation.Nonnull;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since Dec 23, 2016, 5:59:31 PM
 */
@Stateless
@Dependent
@RequiresBusinessAccount
@Local(ProjectLevelService.class)
public class ProjectLevelServiceImpl implements ProjectLevelService {

    @EJB
    private ProjectLevelFacade projectLevelFacade;

    @Override
    @Nonnull
    public List<ProjectLevel> findLevelsByUUID(@Nonnull final String uuid) {
        checkNotNull(uuid, "The uuid must not be null");

        return projectLevelFacade.findLevelsByUUID(uuid);
    }

}
