package com.cashflow.project.frontend.controller.people;

import com.cashflow.access.authorization.LoggedInCompanyAccount;
import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.accessroles.domain.accessrole.AccessRole;
import com.cashflow.accessroles.service.accessrole.AccessRoleRequestContext;
import com.cashflow.accessroles.service.accessrole.AccessRoleService;
import com.cashflow.accountpayables.domain.supplier.ContactPerson;
import com.cashflow.accountpayables.domain.supplier.Supplier;
import com.cashflow.accountpayables.domain.supplier.SupplierSearchContext;
import com.cashflow.accountpayables.service.api.supplier.SupplierService;
import com.cashflow.frontend.support.httpparameter.HttpParameter;
import com.cashflow.project.api.milestone.ProjectMilestoneService;
import com.cashflow.project.api.people.ProjectSubContractorService;
import com.cashflow.project.api.people.ProjectTeamMemberService;
import com.cashflow.project.api.phase.ProjectPhaseService;
import com.cashflow.project.api.project.ProjectService;
import com.cashflow.project.api.task.ProjectTaskService;
import com.cashflow.project.config.projectrole.ProjectProfessionalTypeMappingConfigurationService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.level.ProjectLevelCategory;
import com.cashflow.project.domain.project.milestone.MilestoneContext;
import com.cashflow.project.domain.project.milestone.ProjectMilestone;
import com.cashflow.project.domain.project.personnel.ProjectRole;
import com.cashflow.project.domain.project.personnel.SubContractor;
import com.cashflow.project.domain.project.personnel.SubcontractorContext;
import com.cashflow.project.domain.project.personnel.TeamMember;
import com.cashflow.project.domain.project.personnel.TeamMemberCategory;
import com.cashflow.project.domain.project.personnel.TeamMemberContext;
import com.cashflow.project.domain.project.phase.PhaseContext;
import com.cashflow.project.domain.project.phase.ProjectPhase;
import com.cashflow.project.domain.project.task.ProjectTask;
import com.cashflow.project.domain.project.task.TaskContext;
import com.cashflow.project.frontend.controller.model.ModelViewScopedController;
import com.cashflow.project.frontend.controller.pagination.PaginationModel;
import com.cashflow.project.frontend.service.urlbuilder.StaticLinkUrlBuilder;
import com.cashflow.project.frontend.service.urlbuilder.UrlContext;
import com.cashflow.useraccount.domain.businessunit.BusinessUnit;
import com.cashflow.useraccount.domain.businessunit.CompanyAccount;
import com.cashflow.useraccount.domain.businessunit.Department;
import com.cashflow.useraccount.domain.businessuser.Employee;
import com.cashflow.useraccount.domain.businessuser.EmployeeInformation;
import com.cashflow.useraccount.domain.businessuser.employee.EmployeeInformationSearchContext;
import com.cashflow.useraccount.domain.businessuser.professional.Professional;
import com.cashflow.useraccount.service.api.EmployeeService;
import com.cashflow.useraccount.service.api.businessaccount.employee.EmployeeInformationService;
import com.cashflow.useraccount.service.api.businessunit.BusinessUnitService;
import com.cashflow.useraccount.service.api.businessunit.department.DepartmentService;
import com.cashflow.useraccount.service.api.professional.ProfessionalService;
import com.anosym.profiler.Profile;
import com.google.common.base.Throwables;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;

import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_UUID;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since 29 Nov, 2016, 1:01:05 PM
 */
@ModelViewScopedController
public class ProjectTeamMemberListController extends PaginationModel implements Serializable {

    private static final long serialVersionUID = 5176871786299698491L;

    private static final String PROJECT_LIST_URL = "projects/projects.xhtml";

    @Inject
    private StaticLinkUrlBuilder staticLinkUrlBuilder;

    @Inject
    private ProjectProfessionalTypeMappingConfigurationService projectProfessionalTypeMappingConfigurationService;

    @Getter
    @Setter
    private Project project;

    @Getter
    @Setter
    private List<PeopleEntityModel> peopleEntityModels;

    @Getter
    @Setter
    private List<TeamMember> teamMembers = new ArrayList<>();

    @Getter
    @Setter
    private List<SubContractor> subContractors = new ArrayList<>();

    @Inject
    @HttpParameter(SELECTED_PROJECT_UUID)
    private Instance<String> pUUID;

    @Inject
    private ProjectTeamMemberService projectTeamMemberService;

    @Inject
    private AccessRoleService accessRoleService;

    @Inject
    private ProjectSubContractorService projectSubContractorService;

    @Getter
    @Setter
    private ProjectLevelCategory projectLevelCategory;

    @Inject
    private ProjectService projectService;

    @Inject
    private ProjectPhaseService projectPhaseService;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private ProjectMilestoneService projectMilestoneService;

    @Inject
    private ProjectTaskService projectTaskService;

    @Inject
    private EmployeeService employeeService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private ProfessionalService professionalService;

    @Inject
    private SupplierService supplierService;

    @Inject
    private EmployeeInformationService employeeInformationService;

    @Getter
    @Setter
    @Nullable
    private String searchValue;

    @Getter
    @Setter
    @Nullable
    private List<Supplier> suppliers;

    @Getter
    @Setter
    @Nullable
    private Map<String, EmployeeInformation> users;

    @Getter
    @Setter
    @Nullable
    private String filterValue;

    @Getter
    @Setter
    @Nullable
    private String projectLevelUUID;

    @Getter
    @Setter
    @Nullable
    private String projectRole;

    @Getter
    @Setter
    @Nullable
    private String department;

    @Inject
    private BusinessUnitService businessUnitService;

    @Inject
    @LoggedInCompanyAccount
    private Instance<CompanyAccount> companyAccount;

    @PostConstruct
    public void initPeoples() {
        if (null != pUUID.get()) {
            final String projectUUID = checkNotNull(pUUID.get(), "Project is not selected");
            project = checkNotNull(projectService.getProject(projectUUID), "Project not found");
            projectLevelUUID = project.getUuid();
            loadPeoples();
            this.setCount(peopleEntityModels.size());
            peopleEntityModels = peopleEntityModels.subList(getPage() * getLimit(),
                                                            peopleEntityModels.size() > ((getPage() + 1) * getLimit()) ? (getPage() + 1) * getLimit() : peopleEntityModels
                                                                    .size());

        }
    }

    @Override
    public void loadData() {
        initPeoples();
    }

    @Nonnull
    public String redirectProjectSummary() {
        final UrlContext.Builder context = UrlContext.builder()
                .path(PROJECT_LIST_URL)
                .forceFacesRedirect(true);
        return staticLinkUrlBuilder.buildURL(context.build());
    }

    public void loadUsers(@Nonnull final List<TeamMember> members) {
        final List<String> employeeUUIDs = members
                .stream()
                .map((teammember) -> teammember.getEmployeeUUID())
                .collect(Collectors.toList());

        final EmployeeInformationSearchContext searchContext = EmployeeInformationSearchContext
                .builder()
                .employeeNumbersOrUuids(employeeUUIDs)
                .build();
        final List<EmployeeInformation> employees = employeeInformationService.getEmployeeInformations(searchContext)
                .getEmployeeInformations();
        users = new HashMap<>();
        for (EmployeeInformation ei : employees) {
            users.put(ei.getEmployee().getUuid(), ei);

        }

    }

    private void loadPeopleEntityValues() {
        loadUsers(teamMembers);
        for (TeamMember teamMember : teamMembers) {

            final PeopleEntityModel peopleEntityModel = new PeopleEntityModel();
            switch (teamMember.getProjectLevel().getProjectLevelCategory()) {
                case TASK:
                    peopleEntityModel.setTaskId(teamMember.getProjectLevel().getId());
                    if (null == teamMember.getProjectLevel().getParentLevel()) {
                        break;
                    }
                    peopleEntityModel.setMilestoneId(teamMember.getProjectLevel().getParentLevel().getId());
                    if (null == teamMember.getProjectLevel().getParentLevel().getParentLevel()) {
                        break;
                    }
                    peopleEntityModel.setPhaseId(teamMember.getProjectLevel().getParentLevel().getParentLevel()
                            .getId());
                    break;
                case MILESTONE:
                    peopleEntityModel.setTaskId("All");
                    peopleEntityModel.setMilestoneId(teamMember.getProjectLevel().getId());
                    peopleEntityModel.setPhaseId(teamMember.getProjectLevel().getParentLevel().getId());
                    break;
                case PHASE:
                    peopleEntityModel.setTaskId("All");
                    peopleEntityModel.setMilestoneId("All");
                    if (null != teamMember.getProjectLevel().getParentLevel()) {
                        peopleEntityModel.setPhaseId(teamMember.getProjectLevel().getId());
                    } else {
                        peopleEntityModel.setPhaseId("All");
                    }
                    break;
                default:
                    break;
            }
            peopleEntityModel.setMemberUUID(teamMember.getUuid());
            peopleEntityModel.setCategory(TeamMemberCategory.EMPLOYEE);
            peopleEntityModel.setProjectId(project.getId());
            peopleEntityModel.setMemberId(teamMember.getMemberId());
            setTeamMemberName(peopleEntityModel, teamMember);

            final EmployeeInformation ei = users.get(teamMember.getEmployeeUUID());
            peopleEntityModel.setPlatformRole(ei.getEmployee().getFunctionalRoles()
                    .stream()
                    .findFirst()
                    .orElse(null));
            if (null != ei.getDepartment()) {
                peopleEntityModel.setDepartment(ei.getDepartment());
            }

            final ProjectRole projectRole = getProjectRole(teamMember
                    .getProjectRoles());
            if (null != projectRole) {
                final Future<List<AccessRole>> accessRoles = asynchronousService.execute(()
                        -> getAccessRoles()
                );

                try {
                    for (AccessRole accessRole : accessRoles.get()) {
                        if (accessRole.getUuid().equals(projectRole.getAccessScopeUUID())) {
                            peopleEntityModel.setProjectRole(accessRole.getRoleName());
                        }
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    throw Throwables.propagate(ex);
                }

                peopleEntityModel.setCostRateSource(projectRole.getCostRateSource());
                peopleEntityModel.setCostRate(projectRole.getCostRate().scale(2));
                peopleEntityModel.setBillingRate(projectRole.getBillRate().scale(2));
                peopleEntityModel.setMarkupMethod(projectRole.getMarkUpMethod());
                peopleEntityModel.setMarkup(projectRole.getMarkUp());
            }

            peopleEntityModels.add(peopleEntityModel);

        }

    }

    private void setTeamMemberName(@Nonnull final PeopleEntityModel peopleEntityModel,
                                   @Nonnull final TeamMember teamMember) {
        final Employee employee = employeeService.findEmployee(teamMember.getEmployeeUUID());
        if (null != employee) {
            peopleEntityModel.setTeamMember(employee.getName());
        } else {
            final List<Professional> professionals = professionalService.findProfessionals(
                    projectProfessionalTypeMappingConfigurationService
                    .getTypeMappingsForProfessionals());

            professionals.stream()
                    .filter((professional) -> (professional.getUuid().equals(teamMember.getEmployeeUUID())))
                    .forEachOrdered((professional) -> {
                        peopleEntityModel.setTeamMember(professional.getName());
                    });
        }
    }

    private void loadSubcontractorEntityValues() {

        final List<String> supplierUUIDs = subContractors
                .stream()
                .map((sc) -> sc.getSubContractorUUID())
                .collect(Collectors.toList());
        final SupplierSearchContext searchContext = SupplierSearchContext.builder()
                .supplierUUIDs(supplierUUIDs)
                .offset(0)
                .limit(100)
                .build();

        suppliers = supplierService.findSuppliers(searchContext);
        for (SubContractor subContractor : subContractors) {

            final PeopleEntityModel peopleEntityModel = new PeopleEntityModel();
            switch (subContractor.getProjectLevel().getProjectLevelCategory()) {
                case TASK:
                    peopleEntityModel.setTaskId(subContractor.getProjectLevel().getId());
                    peopleEntityModel.setMilestoneId(subContractor.getProjectLevel().getParentLevel().getId());
                    peopleEntityModel.setPhaseId(subContractor.getProjectLevel().getParentLevel().getParentLevel()
                            .getId());
                    break;
                case MILESTONE:
                    peopleEntityModel.setTaskId("All");
                    peopleEntityModel.setMilestoneId(subContractor.getProjectLevel().getId());
                    peopleEntityModel.setPhaseId(subContractor.getProjectLevel().getParentLevel().getId());
                    break;
                case PHASE:
                    peopleEntityModel.setTaskId("All");
                    peopleEntityModel.setMilestoneId("All");
                    if (null != subContractor.getProjectLevel().getParentLevel()) {
                        peopleEntityModel.setPhaseId(subContractor.getProjectLevel().getId());
                    } else {
                        peopleEntityModel.setPhaseId("All");
                    }
                    break;
                default:
                    break;
            }
            peopleEntityModel.setMemberUUID(subContractor.getUuid());
            peopleEntityModel.setCategory(TeamMemberCategory.SUB_CONTRACTOR);
            peopleEntityModel.setProjectId(project.getId());
            peopleEntityModel.setMemberId(subContractor.getMemberId());
            for (Supplier supplier : suppliers) {
                if (supplier.getUuid().equals(subContractor.getSubContractorUUID())) {
                    final List<ContactPerson> contactPersons = supplier.getOtherContactPersons();
                    contactPersons.add(supplier.getContactPerson());
                    contactPersons.stream()
                            .filter((cp) -> (cp.getUuid().equals(subContractor.getMemberName())))
                            .forEachOrdered((cp) -> {
                                peopleEntityModel.setTeamMember(cp.getName());
                            });
                }
            }
            final ProjectRole role = getProjectRole(subContractor
                    .getProjectRoles());

            if (null != role) {
                getAccessRoles().stream()
                        .filter((accessRole) -> (accessRole.getUuid().equals(role.getAccessScopeUUID())))
                        .forEach((accessRole) -> {
                            peopleEntityModel.setProjectRole(accessRole.getRoleName());
                        });

                peopleEntityModel.setCostRateSource(role.getCostRateSource());
                peopleEntityModel.setCostRate(role.getCostRate().scale(2));
                peopleEntityModel.setBillingRate(role.getBillRate().scale(2));
                peopleEntityModel.setMarkup(role.getMarkUp());
                peopleEntityModel.setMarkupMethod(role.getMarkUpMethod());
            }
            peopleEntityModels.add(peopleEntityModel);
        }

    }

    @Nullable
    public ProjectRole getProjectRole(@Nonnull final Set<ProjectRole> projectRoles) {
        final ProjectRole projectRole = projectRoles
                .stream()
                .findFirst()
                .orElse(null);

        return projectRole;
    }

    @Profile
    public void loadPeoples() {
        peopleEntityModels = new ArrayList<>();
        prepareTeamMemberValues();
        prepareSubcontractorValues();
        loadPeopleEntityValues();
        loadSubcontractorEntityValues();

    }

    private void prepareTeamMemberValues() {
        teamMembers = new ArrayList<>();
        final TeamMemberContext memberContext = TeamMemberContext.builder()
                .search(searchValue)
                .projectLevelUUID(projectLevelUUID)
                .build();

        final List<TeamMember> members = projectTeamMemberService.teamMembers(memberContext);
        loadUsers(members);

        for (TeamMember teamMember : members) {
            if (null != department) {
                final EmployeeInformation ei = users.get(teamMember.getEmployeeUUID());
                if (ei.getDepartment() != null && ei.getDepartment().getUuid().equals(department)) {
                    teamMembers.add(teamMember);
                }
            } else if (null != projectRole) {
                teamMember.getProjectRoles().stream()
                        .filter((pr) -> (pr.getAccessScopeUUID().equals(projectRole)))
                        .forEach((member) -> {
                            teamMembers.add(teamMember);
                        });
            }
        }
        if (null == projectRole && null == department) {
            teamMembers.addAll(members);
        }

    }

    private void prepareSubcontractorValues() {
        subContractors = new ArrayList<>();
        final SubcontractorContext subcontractorContext = SubcontractorContext.builder()
                .search(searchValue)
                .projectLevelUUID(projectLevelUUID)
                .build();
        final List<SubContractor> contractors = projectSubContractorService.subContractors(subcontractorContext);
        if (null != projectRole) {

            contractors.forEach((contractor) -> {
                contractor.getProjectRoles().stream()
                        .filter((pr) -> (pr.getAccessScopeUUID().equals(projectRole)))
                        .forEach((role) -> {
                            subContractors.add(contractor);
                        });
            });
        } else {
            subContractors.addAll(contractors);
        }
    }

    public List<ProjectPhase> getLoadProjectPhases() {
        return projectPhaseService
                .getProjectPhases(PhaseContext
                        .builder()
                        .projectUUID(project.getUuid())
                        .build());
    }

    public List<ProjectMilestone> getLoadProjectMilestones() {
        return projectMilestoneService
                .getMilestones(MilestoneContext.builder()
                        .parentLevelUUID(project.getUuid())
                        .build());
    }

    public List<ProjectTask> getLoadProjectTasks() {
        return projectTaskService
                .getTasks(TaskContext.builder()
                        .parentLevelUUID(project.getUuid())
                        .build());
    }

    @Nonnull
    public List<AccessRole> getAccessRoles() {
        final AccessRoleRequestContext requestContext = AccessRoleRequestContext
                .builder()
                .applicationCode("AZPM")
                .build();
        return accessRoleService.getAccessRoles(requestContext);
    }

    @Nonnull
    public List<BusinessUnit<?>> getDepartments() {
        final List<BusinessUnit<?>> departments = businessUnitService
                .findBusinessUnits(companyAccount.get().getUuid())
                .stream()
                .filter((bu) -> bu instanceof Department)
                .collect(Collectors.toList());

        return departments;
    }

    public void loadDefaultValues() {
        projectLevelUUID = project.getUuid();
        department = null;
        projectRole = null;

    }
}
