package com.cashflow.project.api.approval;

import com.cashflow.project.domain.approval.ProjectApproval;
import javax.annotation.Nonnull;

/**
 *
 * 
 * @since Nov 21, 2016, 5:56:03 PM
 */
public interface ProjectApprovalService {

    void save(@Nonnull final ProjectApproval projectApproval);

}
