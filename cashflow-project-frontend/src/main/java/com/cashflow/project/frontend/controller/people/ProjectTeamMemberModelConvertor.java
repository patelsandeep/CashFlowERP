package com.cashflow.project.frontend.controller.people;

import com.cashflow.accountpayables.domain.supplier.ContactPerson;
import com.cashflow.accountpayables.domain.supplier.Supplier;
import com.cashflow.accountpayables.service.api.supplier.SupplierService;
import com.cashflow.frontend.support.cacheing.sessioncached.SessionCached;
import com.cashflow.project.api.people.ProjectSubContractorService;
import com.cashflow.project.api.people.ProjectTeamMemberService;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.milestone.ProjectMilestone;
import com.cashflow.project.domain.project.personnel.OrganizationLevel;
import com.cashflow.project.domain.project.personnel.ProjectRole;
import com.cashflow.project.domain.project.personnel.SubContractor;
import com.cashflow.project.domain.project.personnel.TeamMember;
import com.cashflow.project.domain.project.phase.ProjectPhase;
import com.cashflow.project.domain.project.task.ProjectTask;
import com.cashflow.project.frontend.controller.model.ProjectRoleModel;
import com.cashflow.project.frontend.controller.model.ProjectTeamMemberModel;
import com.cashflow.project.frontend.controller.supervisor.SupervisorEntityModel;
import com.cashflow.project.frontend.controller.supervisor.SupervisorEntityType;
import com.cashflow.useraccount.domain.businessunit.Branch;
import com.cashflow.useraccount.domain.businessunit.BusinessUnit;
import com.cashflow.useraccount.domain.businessunit.CompanyAccount;
import com.cashflow.useraccount.domain.businessuser.AuthorizedUser;
import com.cashflow.useraccount.domain.businessuser.Employee;
import com.cashflow.useraccount.domain.businessuser.professional.Professional;
import com.cashflow.useraccount.service.api.search.AuthorizedUserSearchService;
import com.cashflow.useraccount.service.api.search.SearchContext;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 *
 * 
 * @since Nov 29, 2016, 4:26:37 PM
 */
@Dependent
public class ProjectTeamMemberModelConvertor implements Serializable {

    private static final long serialVersionUID = -8881475571939551470L;

    @Inject
    private AuthorizedUserSearchService authorizedUserSearchService;

    @Inject
    private ProjectTeamMemberService projectTeamMemberService;

    @Inject
    private SupplierService supplierService;

    @Inject
    private ProjectSubContractorService projectSubContractorService;

    @Nonnull
    @SessionCached
    public ProjectTeamMemberModel convertEmployeeToModel(
            @Nonnull final String teamMemberUUID) {
        final ProjectTeamMemberModel projectTeamMemberModel = new ProjectTeamMemberModel();
        final TeamMember teamMember = projectTeamMemberService
                .getTeamMember(teamMemberUUID);
        if (null == teamMember) {
            return projectTeamMemberModel;
        }
        this.setProjectLevelDetails(projectTeamMemberModel,
                                    teamMember.getProjectLevel());
        projectTeamMemberModel.setMemberID(teamMember.getMemberId());

        final AuthorizedUser user = getUser(teamMember.getEmployeeUUID());
        if (user == null) {
            return projectTeamMemberModel;
        }
        if (user instanceof Employee) {
            projectTeamMemberModel.setTeamMember(new SupervisorEntityModel(user.getUuid(),
                                                                           user.getName(),
                                                                           SupervisorEntityType.EMPLOYEE));
        } else if (user instanceof Professional) {
            projectTeamMemberModel.setTeamMember(new SupervisorEntityModel(user.getUuid(),
                                                                           user.getName(),
                                                                           SupervisorEntityType.PROFESSIONANL));
        }
        projectTeamMemberModel.setMemberName(user.getName());
        projectTeamMemberModel.setDepartmentUUID(user.getDepartmentUUID());
        projectTeamMemberModel.setOrganizationLevel(getLevel(user));
        projectTeamMemberModel.setPlatformRole(user.getFunctionalRoles()
                .stream()
                .findFirst()
                .orElse(null));

        setProjectRoles(projectTeamMemberModel, teamMember.getProjectRoles());

        return projectTeamMemberModel;
    }

    @Nonnull
    @SessionCached
    public ProjectTeamMemberModel convertSubContractorToModel(
            @Nonnull final String subContractorUUID) {
        final ProjectTeamMemberModel projectTeamMemberModel = new ProjectTeamMemberModel();
        final SubContractor subContractor = projectSubContractorService
                .getSubContractor(subContractorUUID);
        if (null == subContractor) {
            return projectTeamMemberModel;
        }
        this.setProjectLevelDetails(projectTeamMemberModel,
                                    subContractor.getProjectLevel());
        projectTeamMemberModel.setMemberID(subContractor.getMemberId());
        projectTeamMemberModel.setMemberName(subContractor.getMemberName());
        projectTeamMemberModel.setSupplierUUID(subContractor.getSubContractorUUID());
        final Supplier supplier = supplierService
                .findSupplier(subContractor.getSubContractorUUID());
        if (null == supplier) {
            return projectTeamMemberModel;
        }
        projectTeamMemberModel.setSupplierId(supplier.getSupplierNumber());
        projectTeamMemberModel.setSupplierName(supplier.getSupplierName());
        final List<ContactPerson> contactPersons = supplier.getOtherContactPersons();
        contactPersons.add(supplier.getContactPerson());
        projectTeamMemberModel.setPersons(contactPersons);

        setProjectRoles(projectTeamMemberModel, subContractor.getProjectRoles());
        return projectTeamMemberModel;
    }

    @Nullable
    private AuthorizedUser getUser(@Nonnull final String uuid) {
        final SearchContext context = SearchContext
                .builder()
                .userUUID(uuid)
                .offset(0)
                .limit(100)
                .build();
        return authorizedUserSearchService
                .search(context)
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Nullable
    private OrganizationLevel getLevel(@Nonnull final AuthorizedUser employee) {
        if (employee.getBusinessUnit() instanceof Branch) {
            return OrganizationLevel.BRACH_LEVEL;
        } else if (employee.getBusinessUnit() instanceof BusinessUnit) {
            return OrganizationLevel.BUSINESS_UNIT_LEVEL;
        } else if (employee.getBusinessUnit() instanceof CompanyAccount) {
            return OrganizationLevel.COMPANY_LEVEL;
        }
        return null;
    }

    private void setProjectLevelDetails(@Nonnull final ProjectTeamMemberModel projectTeamMemberModel,
                                        @Nullable final ProjectLevel<?> projectLevel) {
        if (projectLevel == null) {
            return;
        }
        if (projectLevel instanceof ProjectTask) {
            projectTeamMemberModel.setTaskUUID(projectLevel.getUuid());
            projectTeamMemberModel.setTaskID(projectLevel.getId());
            projectTeamMemberModel.setTaskname(projectLevel.getName());

            setProjectLevelDetails(projectTeamMemberModel, projectLevel.getParentLevel());

        } else if (projectLevel instanceof ProjectMilestone) {
            projectTeamMemberModel.setMilestoneUUID(projectLevel.getUuid());
            projectTeamMemberModel.setMileStoneName(projectLevel.getName());

            setProjectLevelDetails(projectTeamMemberModel, projectLevel.getParentLevel());

        } else if (projectLevel instanceof ProjectPhase) {
            projectTeamMemberModel.setPhaseUUID(projectLevel.getUuid());
            projectTeamMemberModel.setPhaseName(projectLevel.getName());
        }
    }

    private void setProjectRoles(@Nonnull final ProjectTeamMemberModel model,
                                 @Nonnull final Set<ProjectRole> projectRoles) {
        projectRoles
                .stream()
                .forEach((role) -> {
                    model.getProjectRoleModels().add(prepareModel(role));
                });
    }

    @Nonnull
    private ProjectRoleModel prepareModel(@Nonnull final ProjectRole projectRole) {
        final ProjectRoleModel model = new ProjectRoleModel();
        model.setBillRate(projectRole.getBillRate().getValue());
        model.setCostRate(projectRole.getCostRate().getValue());
        if (null != projectRole.getMarkUpMethod()) {
            model.setMarkUpMethod(projectRole.getMarkUpMethod());
        }
        if (null != projectRole.getMarkUp()) {
            model.setMarkUpValue(projectRole.getMarkUp());
        }
        model.setProjectRoleUUID(projectRole.getAccessScopeUUID());
        return model;
    }

}
