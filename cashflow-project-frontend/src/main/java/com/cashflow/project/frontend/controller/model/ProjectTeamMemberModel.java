package com.cashflow.project.frontend.controller.model;

import com.cashflow.accessroles.domain.accessrole.AccessRole;
import com.cashflow.accessroles.domain.accessrole.AccessScopeDefinition;
import com.cashflow.accessroles.domain.accessscope.AccessScope;
import com.cashflow.accountpayables.domain.supplier.ContactPerson;
import com.cashflow.project.domain.project.personnel.OrganizationLevel;
import com.cashflow.project.domain.project.personnel.TeamMemberCategory;
import com.cashflow.project.frontend.controller.supervisor.SupervisorEntityModel;
import com.cashflow.useraccount.domain.businessuser.FunctionalRole;
import com.anosym.common.Currency;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * 
 * @since Nov 26, 2016, 11:57:15 AM
 */
@Getter
@Setter
public class ProjectTeamMemberModel implements Serializable {

    private static final long serialVersionUID = -3429987944247305955L;

    @Nullable
    private String teammemberUUID;

    @Nonnull
    private TeamMemberCategory teamMemberCategory;

    @Nonnull
    private String projectName;

    @Nonnull
    private Currency currency;

    @Nonnull
    private String customerName;

    @Nullable
    private String memberName;

    @Nullable
    private String phaseUUID;

    @Nullable
    private String phaseName;

    @Nullable
    private String milestoneUUID;

    @Nullable
    private String mileStoneName;

    @Nullable
    private String taskUUID;

    @Nonnull
    private String taskID;

    @Nullable
    private String taskname;

    @Nullable
    private OrganizationLevel organizationLevel;

    @Nullable
    private String departmentUUID;

    @Nullable
    private String supplierUUID;

    @Nullable
    private String supplierName;

    @Nullable
    private String supplierId;

    @Nullable
    private SupervisorEntityModel teamMember;

    @Nonnull
    private String memberID;

    @Nullable
    private FunctionalRole platformRole;

    @Nonnull
    private List<ProjectRoleModel> projectRoleModels = new ArrayList<>();

    @Nonnull
    private List<ContactPerson> persons = new ArrayList<>();

    @Nonnull
    private List<AccessRole> accessRoles = new ArrayList<>();

    @Nonnull
    private List<AccessScopeDefinition> accessScopes = new ArrayList<>();

    @Nonnull
    private Map<AccessScope, Boolean> checked = new HashMap<>();

    private boolean advanceSettings;

}
