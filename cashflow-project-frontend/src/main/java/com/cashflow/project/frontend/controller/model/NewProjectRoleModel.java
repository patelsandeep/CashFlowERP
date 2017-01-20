package com.cashflow.project.frontend.controller.model;

import com.cashflow.accessroles.domain.accessscope.AccessScope;
import com.cashflow.project.domain.project.level.ProjectLevelCategory;
import java.io.Serializable;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * 
 * @since Jan 12, 2017, 6:26:00 PM
 */
@Getter
@Setter
public class NewProjectRoleModel implements Serializable {

    private static final long serialVersionUID = -8730803813841627331L;

    @Nonnull
    private String roleName;

    @Nonnull
    private ProjectLevelCategory projectLevelCategory;

    @Nullable
    private Map<AccessScope, Boolean> accessScopes;

}
