package com.cashflow.project.frontend.controller.people;

import com.cashflow.accessroles.service.useraccessrole.AccessRoleRequest;
import com.cashflow.accessroles.service.useraccessrole.UserAccessRoleAssignmentService;
import com.cashflow.project.api.milestone.ProjectMilestoneService;
import com.cashflow.project.api.people.ProjectSubContractorService;
import com.cashflow.project.api.people.ProjectTeamMemberService;
import com.cashflow.project.api.phase.ProjectPhaseService;
import com.cashflow.project.api.task.ProjectTaskService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.personnel.MarkUpMethod;
import com.cashflow.project.domain.project.personnel.ProjectRole;
import com.cashflow.project.domain.project.personnel.SubContractor;
import com.cashflow.project.domain.project.personnel.TeamMember;
import com.cashflow.project.domain.project.personnel.TeamMemberCategory;
import com.cashflow.project.frontend.controller.model.ProjectRoleModel;
import com.cashflow.project.frontend.controller.model.ProjectTeamMemberModel;
import com.anosym.common.Amount;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 *
 * 
 * @since Nov 26, 2016, 7:16:48 PM
 */
@Dependent
public class ProjectTeamMemberProcessor implements Serializable {

    private static final long serialVersionUID = 3813273552502950250L;

    @Inject
    private ProjectTaskService projectTaskService;

    @Inject
    private ProjectMilestoneService projectMilestoneService;

    @Inject
    private ProjectPhaseService projectPhaseService;

    @Inject
    private UserAccessRoleAssignmentService userAccessRoleAssignmentService;

    @Inject
    private ProjectTeamMemberService projectTeamMemberService;

    @Inject
    private ProjectSubContractorService projectSubContractorService;

    public void saveMember(@Nonnull final ProjectTeamMemberModel projectTeamMemberModel,
                           @Nonnull final Project project) {

        if (projectTeamMemberModel.getTeamMemberCategory() == TeamMemberCategory.EMPLOYEE) {
            saveEmployee(projectTeamMemberModel, project);
        } else if (projectTeamMemberModel.getTeamMemberCategory() == TeamMemberCategory.CONSULTANT
                || projectTeamMemberModel.getTeamMemberCategory() == TeamMemberCategory.SUB_CONTRACTOR) {
            saveSubContractor(projectTeamMemberModel, project);
        }

    }

    private void saveEmployee(@Nonnull final ProjectTeamMemberModel projectTeamMemberModel,
                              @Nonnull final Project project) {

        TeamMember member;
        if (isNullOrEmpty(projectTeamMemberModel.getTeammemberUUID())) {
            member = new TeamMember(this.getProjectLevel(projectTeamMemberModel, project),
                                    projectTeamMemberModel.getTeamMember().getSupervisorUUID(),
                                    projectTeamMemberModel.getMemberID());
        } else {
            member = projectTeamMemberService.getTeamMember(projectTeamMemberModel.getTeammemberUUID());
            member.setProjectLevel(this.getProjectLevel(projectTeamMemberModel, project));
            member.setEmployeeUUID(projectTeamMemberModel.getTeamMember().getSupervisorUUID());
            member.setMemberId(projectTeamMemberModel.getMemberID());
        }

        member.setProjectRoles(prepareProjectRoles(projectTeamMemberModel));

        projectTeamMemberService
                .save(member);
        member.getProjectRoles()
                .stream()
                .forEach((role) -> assignRoles(role, member.getEmployeeUUID()));

    }

    private void assignRoles(@Nonnull final ProjectRole projectRole, @Nonnull final String userUUID) {
        final AccessRoleRequest request = AccessRoleRequest
                .builder()
                .accessRoleUUID(projectRole.getAccessScopeUUID())
                .businessReason("Project Role Assign")
                .userUUID(userUUID)
                .build();
        userAccessRoleAssignmentService.assignAccessRole(request);
    }

    private void saveSubContractor(@Nonnull final ProjectTeamMemberModel projectTeamMemberModel,
                                   @Nonnull final Project project) {

        SubContractor subContactor;
        if (isNullOrEmpty(projectTeamMemberModel.getTeammemberUUID())) {
            subContactor = new SubContractor(this.getProjectLevel(projectTeamMemberModel, project),
                                             projectTeamMemberModel.getSupplierUUID(),
                                             projectTeamMemberModel.getMemberName(),
                                             projectTeamMemberModel.getMemberID());
        } else {
            subContactor = projectSubContractorService
                    .getSubContractor(projectTeamMemberModel.getTeammemberUUID());
            subContactor.setProjectLevel(this.getProjectLevel(projectTeamMemberModel, project));
            subContactor.setSubContractorUUID(projectTeamMemberModel.getSupplierUUID());
            subContactor.setMemberName(projectTeamMemberModel.getMemberName());
            subContactor.setMemberId(projectTeamMemberModel.getMemberID());
        }
        subContactor.setProjectRoles(prepareProjectRoles(projectTeamMemberModel));

        projectSubContractorService
                .save(subContactor);
//        subContactor.getProjectRoles()
//                .stream()
//                .forEach((role) -> assignRoles(role, subContactor.getSubContractorUUID()));
    }

    @Nonnull
    private ProjectLevel getProjectLevel(@Nonnull final ProjectTeamMemberModel projectTeamMemberModel,
                                         @Nonnull final Project project) {
        if (!isNullOrEmpty(projectTeamMemberModel.getTaskUUID())) {
            return projectTaskService.getTask(projectTeamMemberModel.getTaskUUID());
        } else if (!isNullOrEmpty(projectTeamMemberModel.getMilestoneUUID())) {
            return projectMilestoneService.getMilestone(projectTeamMemberModel.getMilestoneUUID());
        } else if (!isNullOrEmpty(projectTeamMemberModel.getPhaseUUID())) {
            return projectPhaseService.getPhase(projectTeamMemberModel.getPhaseUUID());
        } else {
            return project;
        }
    }

    @Nonnull
    private Set<ProjectRole> prepareProjectRoles(@Nonnull final ProjectTeamMemberModel projectTeamMemberModel) {
        return projectTeamMemberModel.getProjectRoleModels()
                .stream()
                .map(model -> prepareRoleEntity(projectTeamMemberModel, model))
                .collect(Collectors.toSet());
    }

    @Nonnull
    private ProjectRole prepareRoleEntity(@Nonnull final ProjectTeamMemberModel projectTeamMemberModel,
                                          @Nonnull final ProjectRoleModel projectRoleModel) {
        checkNotNull(projectRoleModel.getProjectRoleUUID(), "The projectRoleUUID must not be null");
        updateBillRate(projectRoleModel);
        return new ProjectRole(projectRoleModel.getCostRateSource(),
                               projectRoleModel.getProjectRoleUUID(),
                               new Amount(projectTeamMemberModel.getCurrency(), projectRoleModel.getCostRate()),
                               new Amount(projectTeamMemberModel.getCurrency(), projectRoleModel.getBillRate()),
                               projectRoleModel.getMarkUpMethod(),
                               projectRoleModel.getMarkUpValue());
    }

    private void updateBillRate(@Nonnull final ProjectRoleModel projectRoleModel) {
        if (projectRoleModel.getMarkUpMethod() == MarkUpMethod.ABSOLUTE) {
            projectRoleModel.setBillRate(projectRoleModel.getCostRate()
                    .add(projectRoleModel.getMarkUpValue()));
        } else if (projectRoleModel.getMarkUpMethod() == MarkUpMethod.PERCENTAGE) {
            projectRoleModel.setBillRate(projectRoleModel.getCostRate()
                    .add(projectRoleModel.getCostRate()
                            .multiply(projectRoleModel.getMarkUpValue())
                            .multiply(new BigDecimal(0.01))).setScale(2, RoundingMode.HALF_EVEN));
        }
    }

}
