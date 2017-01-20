package com.cashflow.project.frontend.controller.labourexpense;

import com.cashflow.accessroles.domain.accessrole.AccessRole;
import com.cashflow.project.domain.project.expense.LabourExpenseType;
import com.cashflow.project.domain.project.level.ProjectLevel;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * 
 * @since Dec 22, 2016, 12:40:19 PM
 */
@Getter
@Setter
public class FilterModel {

    @Nullable
    private String searchValue;

    @Nullable
    private String accessRoleUUID;

    @Nullable
    private LabourExpenseType labourExpenseType;

    @Nullable
    private String projectLevelUUID;

    @Nullable
    private String filterType = "Project role";

    @Nonnull
    private List<AccessRole> accessRoles = new ArrayList<>();

    @Nonnull
    private List<ProjectLevel> projectLevels = new ArrayList<>();

}
