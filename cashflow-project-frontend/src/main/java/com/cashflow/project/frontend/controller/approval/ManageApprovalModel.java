package com.cashflow.project.frontend.controller.approval;

import java.io.Serializable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * 
 * @since Nov 21, 2016, 7:47:10 PM
 */
@Getter
@Setter
public class ManageApprovalModel implements Serializable {

    private static final long serialVersionUID = -8196159102704135637L;

    @Nonnull
    private String projectUUID;

    @Nonnull
    private String customerUUID;

    @Nonnull
    private String phaseUUID;

    @Nullable
    private String approverUUID;

    @Nullable
    private String projectRoleUUID;

}
