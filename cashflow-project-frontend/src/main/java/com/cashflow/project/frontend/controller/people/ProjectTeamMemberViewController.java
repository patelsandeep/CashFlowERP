package com.cashflow.project.frontend.controller.people;

import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.accessroles.domain.accessrole.AccessRole;
import com.cashflow.accessroles.domain.accessrole.AccessScopeDefinition;
import com.cashflow.accessroles.domain.accessscope.AccessScope;
import com.cashflow.accessroles.service.accessrole.AccessRoleRequestContext;
import com.cashflow.accessroles.service.accessrole.AccessRoleService;
import com.cashflow.accountreceivables.domain.customer.Customer;
import com.cashflow.accountreceivables.service.api.customer.CustomerService;
import com.cashflow.frontend.support.httpparameter.HttpParameter;
import com.cashflow.project.api.project.ProjectService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.ProjectType;
import com.cashflow.project.domain.project.personnel.TeamMemberCategory;
import com.cashflow.project.frontend.controller.model.ModelViewScopedController;
import com.cashflow.project.frontend.controller.model.ProjectTeamMemberModel;
import com.cashflow.useraccount.domain.businessunit.Department;
import com.cashflow.useraccount.service.api.businessunit.department.DepartmentService;
import com.anosym.profiler.Profile;
import com.google.common.base.Throwables;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 *
 * 
 * @since Nov 29, 2016, 4:25:34 PM
 */
@ModelViewScopedController
public class ProjectTeamMemberViewController implements Serializable {

    private static final long serialVersionUID = -2850495173124068127L;

    @Inject
    @HttpParameter(SELECTED_PROJECT_UUID)
    private Instance<String> pUUID;

    @Inject
    private ProjectService projectService;

    @Inject
    private ProjectTeamMemberModelConvertor projectTeamMemberModelConvertor;

    @Inject
    private CustomerService customerService;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private AccessRoleService accessRoleService;

    @Getter
    @Setter
    private Project project;

    @Getter
    @Setter
    private ProjectTeamMemberModel projectTeamMemberModel;

    @PostConstruct
    void initViewTeamMember() {
        projectTeamMemberModel = new ProjectTeamMemberModel();
        if (!isNullOrEmpty(pUUID.get())) {
            this.project = checkNotNull(projectService.getProject(pUUID.get()), "Failed to load project for uuid: %s",
                                        pUUID.get());
            setProjectValues(project);
        }
    }

    @Profile
    private void setProjectValues(@Nonnull final Project project) {

        projectTeamMemberModel.setProjectName(project.getName());
        projectTeamMemberModel.setCurrency(project.getCurrency());
        if (project.getProjectType() == ProjectType.CUSTOMER_PROJECT) {
            final Future<Customer> customerRequest = asynchronousService.execute(() -> customerService
                    .getCustomer(project.getCustomerUUID()));
            try {
                final Customer customer = customerRequest.get();
                projectTeamMemberModel.setCustomerName(customer.getName());
            } catch (InterruptedException | ExecutionException ex) {
                throw Throwables.propagate(ex);
            }

        } else if (project.getProjectType() == ProjectType.INTERNAL_PROJECT) {
            final Future<Department> departmentRequest = asynchronousService.execute(() -> departmentService
                    .findDepartment(project.getCustomerUUID()));
            try {
                final Department department = departmentRequest.get();
                projectTeamMemberModel.setCustomerName(department.getName());
            } catch (InterruptedException | ExecutionException ex) {
                throw Throwables.propagate(ex);
            }

        }
    }

    public void viewProjectTeamMember(@Nonnull final PeopleEntityModel model) {
        if (model.getCategory() == TeamMemberCategory.EMPLOYEE) {
            projectTeamMemberModel = projectTeamMemberModelConvertor
                    .convertEmployeeToModel(model.getMemberUUID());
        } else {
            projectTeamMemberModel = projectTeamMemberModelConvertor
                    .convertSubContractorToModel(model.getMemberUUID());
        }
        setProjectValues(project);
        projectTeamMemberModel.setTeammemberUUID(model.getMemberUUID());
        projectTeamMemberModel.setTeamMemberCategory(model.getCategory());
        updatePermissions(projectTeamMemberModel.getProjectRoleModels().get(0).getProjectRoleUUID());
        final Future<List<AccessRole>> accessRoles = asynchronousService.execute(()
                -> getAccessRoles()
        );
        try {
            projectTeamMemberModel.setAccessRoles(accessRoles.get());
        } catch (InterruptedException | ExecutionException ex) {
            throw Throwables.propagate(ex);
        }

    }

    public void updatePermissions(@Nullable final String accessRoleUUID) {
        projectTeamMemberModel.setChecked(new HashMap<>());
        if (null == accessRoleUUID) {
            return;
        }
        final Map<String, List<AccessScopeDefinition>> roles = getAccessRoles()
                .stream()
                .collect(Collectors.toMap((ar) -> ar.getUuid(), (ar) -> ar.getAccessScopeDefinitions()));

        final Map<AccessScope, Boolean> defsMap = new HashMap<>();

        // not able to use collect here as i need multiple if else here.
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

}
