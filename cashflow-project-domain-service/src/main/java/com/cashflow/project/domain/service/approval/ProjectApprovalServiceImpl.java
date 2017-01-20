package com.cashflow.project.domain.service.approval;

import com.cashflow.project.api.approval.ProjectApprovalService;
import com.cashflow.project.domain.approval.ProjectApproval;
import com.cashflow.project.domain.facade.approval.ProjectApprovalFacade;
import javax.annotation.Nonnull;
import javax.ejb.EJB;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since Nov 21, 2016, 5:57:10 PM
 */
public class ProjectApprovalServiceImpl implements ProjectApprovalService {

    @EJB
    private ProjectApprovalFacade projectApprovalFacade;

    @Override
    public void save(@Nonnull final ProjectApproval projectApproval) {
        checkNotNull(projectApproval, "The projectApproval must not be null");

        projectApprovalFacade.edit(projectApproval);
    }

}
