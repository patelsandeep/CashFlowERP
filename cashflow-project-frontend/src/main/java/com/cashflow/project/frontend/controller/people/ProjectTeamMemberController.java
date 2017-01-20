package com.cashflow.project.frontend.controller.people;

import com.cashflow.access.authorization.LoggedInAuthorizedUser;
import com.cashflow.access.authorization.LoggedInCompanyAccount;
import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.access.authorization.uniqueid.IdContext;
import com.cashflow.access.authorization.uniqueid.UniqueIdGenerator;
import com.cashflow.accessroles.domain.accessrole.AccessRole;
import com.cashflow.accessroles.domain.accessrole.AccessRoleType;
import com.cashflow.accessroles.domain.accessrole.AccessScopeDefinition;
import com.cashflow.accessroles.domain.accessscope.AccessScope;
import com.cashflow.accessroles.service.accessrole.AccessRoleRequestContext;
import com.cashflow.accessroles.service.accessrole.AccessRoleService;
import com.cashflow.accessroles.service.accessrole.NewAccessRole;
import com.cashflow.accessroles.service.accessscope.AccessScopeService;
import com.cashflow.accountpayables.domain.supplier.ContactPerson;
import com.cashflow.accountpayables.domain.supplier.Supplier;
import com.cashflow.accountpayables.domain.supplier.SupplierSearchContext;
import com.cashflow.accountpayables.service.api.supplier.SupplierService;
import com.cashflow.accountreceivables.domain.customer.Customer;
import com.cashflow.accountreceivables.service.api.customer.CustomerService;
import com.cashflow.frontend.support.cacheing.requestcached.RequestCached;
import com.cashflow.frontend.support.httpparameter.HttpParameter;
import com.cashflow.project.api.milestone.ProjectMilestoneService;
import com.cashflow.project.api.phase.ProjectPhaseService;
import com.cashflow.project.api.project.ProjectService;
import com.cashflow.project.api.task.ProjectTaskService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.ProjectType;
import com.cashflow.project.domain.project.level.ProjectLevelCategory;
import com.cashflow.project.domain.project.milestone.ProjectMilestone;
import com.cashflow.project.domain.project.personnel.CostRateSource;
import com.cashflow.project.domain.project.personnel.MarkUpMethod;
import com.cashflow.project.domain.project.personnel.OrganizationLevel;
import com.cashflow.project.domain.project.personnel.SubContractor;
import com.cashflow.project.domain.project.personnel.TeamMember;
import com.cashflow.project.domain.project.personnel.TeamMemberCategory;
import com.cashflow.project.domain.project.phase.PhaseContext;
import com.cashflow.project.domain.project.phase.ProjectPhase;
import com.cashflow.project.domain.project.task.ProjectTask;
import com.cashflow.project.frontend.controller.model.ModelViewScopedController;
import com.cashflow.project.frontend.controller.model.NewProjectRoleModel;
import com.cashflow.project.frontend.controller.model.ProjectRoleModel;
import com.cashflow.project.frontend.controller.model.ProjectTeamMemberModel;
import com.cashflow.project.frontend.controller.model.SuccessMessageModel;
import com.cashflow.project.frontend.controller.supervisor.SupervisorEntityModel;
import com.cashflow.project.translation.people.CostRateSourceTranslationService;
import com.cashflow.project.translation.people.MarkUpMethodTranslationService;
import com.cashflow.project.translation.people.OrganizationLevelTranslationService;
import com.cashflow.project.translation.people.ProjectTeamMemberTranslationService;
import com.cashflow.project.translation.people.TeamMemberCategoryTranslationService;
import com.cashflow.project.translation.project.ProjectLevelCategoryTranslationService;
import com.cashflow.useraccount.domain.businessunit.CompanyAccount;
import com.cashflow.useraccount.domain.businessunit.Department;
import com.cashflow.useraccount.domain.businessuser.AuthorizedUser;
import com.cashflow.useraccount.service.api.businessunit.department.DepartmentService;
import com.anosym.profiler.Profile;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;

import static com.cashflow.frontend.support.errormessage.ErrorMessagePreconditions.assertNotNull;
import static com.cashflow.frontend.support.errormessage.ErrorMessagePreconditions.assertState;
import static com.cashflow.frontend.support.jsfutil.SelectItemHelper.getSelectItems;
import static com.cashflow.frontend.support.jsfutil.SuccessMessageHelper.addSuccessMessage;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_UUID;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 *
 * 
 * @since Nov 26, 2016, 11:06:43 AM
 */
@ModelViewScopedController
public class ProjectTeamMemberController implements Serializable {

    private static final long serialVersionUID = -39767445176484060L;

    @Inject
    @HttpParameter(SELECTED_PROJECT_UUID)
    private Instance<String> pUUID;

    @Inject
    @IdContext(forDomain = TeamMember.class, prefix = "E", length = 6)
    private UniqueIdGenerator memberUniqueId;

    @Inject
    @IdContext(forDomain = SubContractor.class, prefix = "SC", length = 5)
    private UniqueIdGenerator contractorUniqueId;

    @Inject
    @LoggedInCompanyAccount
    private Instance<CompanyAccount> companyAccount;

    @Inject
    @LoggedInAuthorizedUser
    private Instance<AuthorizedUser> authorizedUser;

    @Inject
    private ProjectService projectService;

    @Inject
    private AccessRoleService accessRoleService;

    @Inject
    private AccessScopeService accessScopeService;

    @Inject
    private ProjectTeamMemberProcessor projectTeamMemberProcessor;

    @Inject
    private CustomerService customerService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private ProjectTaskService projectTaskService;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private ProjectMilestoneService milestoneService;

    @Inject
    private ProjectPhaseService projectPhaseService;

    @Inject
    private SupplierService supplierService;

    @Inject
    private ProjectTeamMemberTranslationService projectTeamMemberTranslationService;

    @Inject
    private MarkUpMethodTranslationService markUpMethodTranslationService;

    @Inject
    private ProjectLevelCategoryTranslationService projectLevelCategoryTranslationService;

    @Inject
    private TeamMemberCategoryTranslationService teamMemberCategoryTranslationService;

    @Inject
    private OrganizationLevelTranslationService organizationLevelTranslationService;

    @Inject
    private CostRateSourceTranslationService costRateSourceTranslationService;

    @Inject
    private TeamMemberEntityResolver teamMemberEntityResolver;

    @Inject
    private ProjectTeamMemberModelConvertor projectTeamMemberModelConvertor;

    @Getter
    @Setter
    private Project project;

    @Getter
    @Setter
    private ProjectTeamMemberModel projectTeamMemberModel;

    @Getter
    @Setter
    private NewProjectRoleModel newProjectRoleModel;

    @Getter
    @Setter
    private SuccessMessageModel successMessageModel;

    @PostConstruct
    void initTeamMember() {
        projectTeamMemberModel = new ProjectTeamMemberModel();
        addProjectRole();
        if (!isNullOrEmpty(pUUID.get())) {
            this.project = checkNotNull(projectService.getProject(pUUID.get()), "Failed to load project for uuid: %s",
                                        pUUID.get());
            setProjectValues(project);
        }
    }

    @Nonnull
    public SelectItem[] getTeamMemberCategories() {
        return getSelectItems(TeamMemberCategory.values(),
                              true,
                              projectTeamMemberTranslationService.getSelectTeamMemberCategory(),
                              teamMemberCategoryTranslationService::getTeamMemberCategoryLabel);
    }

    @Nonnull
    public SelectItem[] getOrganizationLevels() {
        return getSelectItems(OrganizationLevel.values(),
                              true,
                              projectTeamMemberTranslationService.getSelectOrganizationLevel(),
                              organizationLevelTranslationService::getOrganizationLevelLabel);
    }

    @Profile
    private void setProjectValues(@Nonnull final Project project) {

        projectTeamMemberModel.setProjectName(project.getName());
        projectTeamMemberModel.setCurrency(project.getCurrency());

        final Future<List<AccessRole>> accessRoles = asynchronousService.execute(()
                -> getAccessRoles());
        if (project.getProjectType() == ProjectType.CUSTOMER_PROJECT) {

            final Future<Customer> customerRequest = asynchronousService.execute(() -> customerService
                    .getCustomer(project.getCustomerUUID()));
            try {
                final Customer customer = customerRequest.get();
                projectTeamMemberModel.setCustomerName(customer.getName());
                projectTeamMemberModel.setAccessRoles(accessRoles.get());
            } catch (InterruptedException | ExecutionException ex) {
                throw Throwables.propagate(ex);
            }
        }
        if (project.getProjectType() == ProjectType.INTERNAL_PROJECT) {

            final Future<Department> departmentRequest = asynchronousService.execute(() -> departmentService
                    .findDepartment(
                            project.getCustomerUUID()));
            try {
                final Department department = departmentRequest.get();
                projectTeamMemberModel.setCustomerName(department.getName());
                projectTeamMemberModel.setAccessRoles(accessRoles.get());
            } catch (InterruptedException | ExecutionException ex) {
                throw Throwables.propagate(ex);
            }

        }
    }

    @Nonnull
    @RequestCached
    public List<ProjectPhase> getProjectPhases() {
        if (!isNullOrEmpty(pUUID.get())) {
            return projectPhaseService
                    .getProjectPhases(PhaseContext
                            .builder()
                            .projectUUID(pUUID.get())
                            .build());
        }
        return ImmutableList.of();
    }

    @Nonnull
    @RequestCached
    public List<ProjectMilestone> getMilestones() {
        if (!isNullOrEmpty(projectTeamMemberModel.getPhaseUUID())) {
            return milestoneService
                    .findByPhaseUUIDs(ImmutableList.of(projectTeamMemberModel.getPhaseUUID()));
        }
        return ImmutableList.of();
    }

    @Nonnull
    @RequestCached
    public List<ProjectTask> getProjectTasks() {
        if (!isNullOrEmpty(projectTeamMemberModel.getMilestoneUUID())) {
            return projectTaskService
                    .findByMilestoneOrProjectUUIDs(ImmutableList.of(projectTeamMemberModel.getMilestoneUUID()));
        } else if (!isNullOrEmpty(pUUID.get())) {
            return projectTaskService
                    .findByMilestoneOrProjectUUIDs(ImmutableList.of(pUUID.get()));
        }
        return ImmutableList.of();
    }

    @Nullable
    public String getTaskID() {
        if (isNullOrEmpty(projectTeamMemberModel.getTaskUUID())) {
            return null;
        }

        final ProjectTask task = projectTaskService.getTask(projectTeamMemberModel.getTaskUUID());
        if (null == task) {
            return null;
        }

        return task.getId();
    }

    @Nonnull
    @RequestCached
    public List<Department> getDepartments() {
        return departmentService
                .findDepartments(companyAccount.get().getUuid());
    }

    @Nonnull
    @RequestCached
    public SelectItem[] getCostRateSorces() {
        return getSelectItems(CostRateSource.values(),
                              true,
                              projectTeamMemberTranslationService.getSelectCostRateSource(),
                              costRateSourceTranslationService::getCostRateSourceLabel);
    }

    @Nonnull
    @RequestCached
    public SelectItem[] getMarkUpMethods() {
        return getSelectItems(MarkUpMethod.values(),
                              true,
                              projectTeamMemberTranslationService.getSelectMarkUpMethod(),
                              markUpMethodTranslationService::getMarkUpMethodLabel);
    }

    public void addProjectRole() {
        final ProjectRoleModel model = new ProjectRoleModel();
        projectTeamMemberModel.getProjectRoleModels().add(model);
    }

    @Nonnull
    @RequestCached
    public List<Supplier> getSuppliers() {
        final SupplierSearchContext context = SupplierSearchContext
                .builder()
                .build();
        return supplierService.findSuppliers(context)
                .stream()
                .filter((sup) -> sup.isSubContractor() || sup.isConsultant())
                .collect((Collectors.toList()));
    }

    public void suppllierDtl() {
        if (isNullOrEmpty(projectTeamMemberModel.getSupplierUUID())) {
            return;
        }
        final Supplier supplier = supplierService
                .findSupplier(projectTeamMemberModel.getSupplierUUID());
        if (null == supplier) {
            return;
        }
        projectTeamMemberModel.setSupplierId(supplier.getSupplierNumber());
        final List<ContactPerson> contactPersons = new ArrayList<>();
        if (null != supplier.getContactPerson()) {
            contactPersons.add(supplier.getContactPerson());
        }
        contactPersons.addAll(supplier.getOtherContactPersons());
        projectTeamMemberModel.setPersons(contactPersons);
    }

    public void updateMemberId() {
        final TeamMemberCategory cat = projectTeamMemberModel.getTeamMemberCategory();
        initTeamMember();
        projectTeamMemberModel.setTeamMemberCategory(cat);
        if (cat == TeamMemberCategory.EMPLOYEE) {
            projectTeamMemberModel.setMemberID(memberUniqueId.nextId());
        } else if (cat == TeamMemberCategory.CONSULTANT
                || cat == TeamMemberCategory.SUB_CONTRACTOR) {
            projectTeamMemberModel.setMemberID(contractorUniqueId.nextId());
        }
    }

    @Nonnull
    @RequestCached
    public List<SupervisorEntityModel> loadTeamMembers(@Nullable final String searchExpression) {
        return teamMemberEntityResolver
                .loadTeamMembersValues(searchExpression,
                                       projectTeamMemberModel.getDepartmentUUID(),
                                       projectTeamMemberModel.getOrganizationLevel());
    }

    public void saveProjectTeamMember() {
        validate();
        successMessageModel = new SuccessMessageModel();
        projectTeamMemberProcessor.saveMember(projectTeamMemberModel, project);

        if (isNullOrEmpty(projectTeamMemberModel.getTeammemberUUID())) {
            successMessageModel.setSuccessMessage(
                    projectTeamMemberTranslationService.getProjectTeamMemberSuccessMessage());
            addSuccessMessage(projectTeamMemberTranslationService
                    .getProjectTeamMemberSuccessMessage());
        } else {
            successMessageModel.setSuccessMessage(
                    projectTeamMemberTranslationService.getProjectTeamMemberUpdateSuccessMessage());
            addSuccessMessage(projectTeamMemberTranslationService
                    .getProjectTeamMemberUpdateSuccessMessage());
        }

        initTeamMember();
    }

    private void validate() {
        assertNotNull(projectTeamMemberModel.getTeamMemberCategory(), () -> projectTeamMemberTranslationService
                      .getSelectTeamMemberCategoryRquiredMessage());
        assertNotNull(project, () -> projectTeamMemberTranslationService
                      .getSelectProjectRequiredMessage());
        assertNotNull(projectTeamMemberModel.getMemberID(), () -> projectTeamMemberTranslationService
                      .getMemberIDRequiredMessage());
        if (projectTeamMemberModel.getTeamMemberCategory() == TeamMemberCategory.EMPLOYEE) {
            assertNotNull(projectTeamMemberModel.getTeamMember(), () -> projectTeamMemberTranslationService
                          .getSelectEmployeeRequiredMessage());
        } else {
            assertNotNull(projectTeamMemberModel.getSupplierUUID(), () -> projectTeamMemberTranslationService
                          .getSupplierRequiredMessage());
            assertNotNull(projectTeamMemberModel.getMemberName(), () -> projectTeamMemberTranslationService
                          .getSelectMemberRequiredMessage());
        }
        assertState(projectTeamMemberModel.getProjectRoleModels().size() > 0,
                    () -> projectTeamMemberTranslationService.getAddAtleastOneProjectRoleMessage());
    }

    @Nonnull
    public List<AccessRole> getAccessRoles() {
        final AccessRoleRequestContext context = AccessRoleRequestContext
                .builder()
                .applicationCode("AZPM")
                .build();
        return accessRoleService.getAccessRoles(context);
    }

    @Nonnull
    public String getOnButtonDivCss() {
        return projectTeamMemberModel.isAdvanceSettings() ? "on-off-btn-ctn" : "off-btn-ctn";
    }

    @Nonnull
    public String getOnButtonCss() {
        return projectTeamMemberModel.isAdvanceSettings() ? "on-btn" : "off-btn";
    }

    @Nonnull
    public String getOffButtonCss() {
        return !projectTeamMemberModel.isAdvanceSettings() ? "on-btn" : "off-btn";
    }

    public void updateOrganization() {
        if (projectTeamMemberModel.getDepartmentUUID() != null) {
            projectTeamMemberModel.setOrganizationLevel(OrganizationLevel.COMPANY_LEVEL);
        }
    }

    public void updateBillRate(@Nonnull final ProjectRoleModel projectRoleModel) {
        if (projectRoleModel.getMarkUpMethod() == MarkUpMethod.ABSOLUTE) {
            projectRoleModel.setBillRate(projectRoleModel.getCostRate()
                    .add(projectRoleModel.getMarkUpValue()));
        } else if (projectRoleModel.getMarkUpMethod() == MarkUpMethod.PERCENTAGE) {
            projectRoleModel.setBillRate(projectRoleModel.getCostRate()
                    .add(projectRoleModel.getCostRate()
                            .multiply(projectRoleModel.getMarkUpValue())
                            .multiply(new BigDecimal(0.01))));
        }
    }

    public void updatePlatformRole() {
        if (null != projectTeamMemberModel.getTeamMember()
                && projectTeamMemberModel.getTeamMember().getSupervisorUUID() != null) {
            final AuthorizedUser user = teamMemberEntityResolver.getUser(projectTeamMemberModel
                    .getTeamMember().getSupervisorUUID());
            if (user != null) {
                projectTeamMemberModel.setPlatformRole(user.getFunctionalRoles()
                        .stream()
                        .findFirst()
                        .orElse(null));
            }
        }
    }

    public void updatePermissions(@Nullable final String accessRoleUUID) {
        projectTeamMemberModel.setChecked(new HashMap<>());
        if (null == accessRoleUUID) {
            return;
        }
        if ("Add New Project Role".equals(accessRoleUUID)) {
            newProjectRoleModel = new NewProjectRoleModel();
            final Map<AccessScope, Boolean> scopes = accessScopeService.getAccessScopes()
                    .stream()
                    .filter((scope) -> scope.getId().startsWith("projects.") && scope.getScopeOrder() != null)
                    .collect(Collectors.toMap(Function.identity(), (role) -> false));
            final Map<AccessScope, Boolean> orderedScopeMap = new TreeMap<>(
                    (Comparator<AccessScope>) (o1, o2) -> o2.getScopeOrder().compareTo(o1.getScopeOrder())
            );
            orderedScopeMap.putAll(scopes);
            newProjectRoleModel.setAccessScopes(orderedScopeMap);
            // adding this here because need to open dialog box based on conditions
            RequestContext.getCurrentInstance().execute("PF('add_project_role').show();");
            return;
        }
        final Map<String, List<AccessScopeDefinition>> roles = getAccessRoles()
                .stream()
                .collect(Collectors.toMap((ar) -> ar.getUuid(),
                                          (ar) -> ar.getAccessScopeDefinitions()));

        final Map<AccessScope, Boolean> defsMap = new HashMap<>();

        roles.entrySet()
                .stream()
                .forEach((net) -> {
                    if (!net.getKey().equals(accessRoleUUID)) {
                        net.getValue()
                                .stream()
                                .forEach((def) -> {
                                    if (!defsMap.containsKey(def.getAccessScope())) {
                                        defsMap.put(def.getAccessScope(), Boolean.FALSE);
                                    }
                                });
                    } else {
                        net.getValue()
                                .stream()
                                .forEach((def) -> defsMap.put(def.getAccessScope(), Boolean.TRUE));
                    }
                });

        final Map<AccessScope, Boolean> orderedMap = new TreeMap<>(
                (Comparator<AccessScope>) (o1, o2) -> o2.getScopeOrder().compareTo(o1.getScopeOrder())
        );
        orderedMap.putAll(defsMap);
        projectTeamMemberModel.setChecked(orderedMap);
    }

    public void saveNewRole() {
        final List<String> accessScopeIds = newProjectRoleModel.getAccessScopes()
                .entrySet()
                .stream()
                .filter((scope) -> scope.getValue())
                .map((scope) -> scope.getKey().getId())
                .collect(Collectors.toList());
        final NewAccessRole newAccessRole = NewAccessRole.builder()
                .accessRoleApprover(authorizedUser.get().getUuid())
                .accessRoleName(newProjectRoleModel.getRoleName())
                .accessRoleType(AccessRoleType.APPLICATION_ROLE)
                .applicationCode("AZPM")
                .accessScopeIds(accessScopeIds)
                .build();
        accessRoleService.createAccessRole(newAccessRole);
        projectTeamMemberModel.setAccessRoles(getAccessRoles());
        successMessageModel = new SuccessMessageModel();
        successMessageModel.setSuccessMessage(
                projectTeamMemberTranslationService.getProjectRoleSuccessMessage());
        addSuccessMessage(projectTeamMemberTranslationService
                .getProjectRoleSuccessMessage());
    }

    public void editProjectTeamMember(@Nonnull final PeopleEntityModel peopleEntityModel) {
        if (peopleEntityModel.getCategory() == TeamMemberCategory.EMPLOYEE) {
            projectTeamMemberModel = projectTeamMemberModelConvertor
                    .convertEmployeeToModel(peopleEntityModel.getMemberUUID());
        } else {
            projectTeamMemberModel = projectTeamMemberModelConvertor
                    .convertSubContractorToModel(peopleEntityModel.getMemberUUID());
        }
        updatePermissions(projectTeamMemberModel.getProjectRoleModels().get(0).getProjectRoleUUID());
        setProjectValues(project);
        projectTeamMemberModel.setTeamMemberCategory(peopleEntityModel.getCategory());
        projectTeamMemberModel.setTeammemberUUID(peopleEntityModel.getMemberUUID());

    }

    public void editProjectTeamMemberFromView(@Nonnull final ProjectTeamMemberModel teamMemberModel) {
        final TeamMemberCategory cat = teamMemberModel.getTeamMemberCategory();
        final String memberUUID = teamMemberModel.getTeammemberUUID();
        if (cat == TeamMemberCategory.EMPLOYEE) {
            projectTeamMemberModel = projectTeamMemberModelConvertor
                    .convertEmployeeToModel(memberUUID);
        } else {
            projectTeamMemberModel = projectTeamMemberModelConvertor
                    .convertSubContractorToModel(memberUUID);
        }
        setProjectValues(project);
        updatePermissions(projectTeamMemberModel.getProjectRoleModels().get(0).getProjectRoleUUID());
        projectTeamMemberModel.setTeamMemberCategory(cat);
        projectTeamMemberModel.setTeammemberUUID(memberUUID);

    }

    @Nonnull
    public SelectItem[] getLevelCategories() {
        return getSelectItems(ProjectLevelCategory.values(),
                              true,
                              projectTeamMemberTranslationService.getSelectProjectRoleLevel(),
                              projectLevelCategoryTranslationService::getProjectLevelCategoryLabel);
    }
}
