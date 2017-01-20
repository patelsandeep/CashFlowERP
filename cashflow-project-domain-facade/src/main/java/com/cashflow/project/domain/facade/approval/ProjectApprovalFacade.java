package com.cashflow.project.domain.facade.approval;

import com.cashflow.project.domain.approval.ProjectApproval;
import com.cashflow.project.domain.facade.ProjectAbstractFacade;
import javax.ejb.Stateless;

/**
 *
 * 
 * @since Nov 21, 2016, 5:54:34 PM
 */
@Stateless
public class ProjectApprovalFacade extends ProjectAbstractFacade<ProjectApproval> {

    public ProjectApprovalFacade() {
        super(ProjectApproval.class);
    }

}
