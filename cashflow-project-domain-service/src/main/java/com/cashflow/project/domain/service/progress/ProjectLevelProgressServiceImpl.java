package com.cashflow.project.domain.service.progress;

import com.cashflow.backend.businessaccount.authorization.RequiresBusinessAccount;
import com.cashflow.project.api.progress.ProjectLevelProgressService;
import com.cashflow.project.domain.facade.level.ProjectLevelProgressFacade;
import com.cashflow.project.domain.project.level.ProjectLevelProgress;
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
 * @since Nov 18, 2016, 4:01:48 PM
 */
@Stateless
@Dependent
@RequiresBusinessAccount
@Local(ProjectLevelProgressService.class)
public class ProjectLevelProgressServiceImpl implements ProjectLevelProgressService {

    @EJB
    private ProjectLevelProgressFacade projectLevelProgressFacade;

    @Override
    public List<ProjectLevelProgress> getProjectLevelProgresss(@Nonnull final List<String> projectLevelUUIDs) {
        checkNotNull(projectLevelUUIDs, "The projectLevelUUIDs must not be null");

        return projectLevelProgressFacade
                .getProjectLevelProgresss(projectLevelUUIDs);
    }

}
