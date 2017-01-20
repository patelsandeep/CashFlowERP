package com.cashflow.project.frontend.controller.people;

import com.cashflow.project.domain.project.personnel.OrganizationLevel;
import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.frontend.support.cacheing.requestcached.RequestCached;
import com.cashflow.project.config.projectrole.ProjectProfessionalTypeMappingConfigurationService;
import com.cashflow.project.frontend.controller.supervisor.SupervisorEntityModel;
import com.cashflow.project.frontend.controller.supervisor.SupervisorEntityType;
import com.cashflow.useraccount.domain.businessunit.Branch;
import com.cashflow.useraccount.domain.businessunit.BusinessUnit;
import com.cashflow.useraccount.domain.businessunit.CompanyAccount;
import com.cashflow.useraccount.domain.businessuser.AuthorizedUser;
import com.cashflow.useraccount.domain.businessuser.Employee;
import com.cashflow.useraccount.domain.businessuser.EmployeeInformation;
import com.cashflow.useraccount.domain.businessuser.employee.EmployeeInformationSearchContext;
import com.cashflow.useraccount.domain.businessuser.employee.SearchResultOption;
import com.cashflow.useraccount.service.api.businessaccount.employee.EmployeeInformationService;
import com.cashflow.useraccount.service.api.professional.ProfessionalService;
import com.cashflow.useraccount.service.api.search.AuthorizedUserSearchService;
import com.cashflow.useraccount.service.api.search.SearchContext;
import com.anosym.profiler.Profile;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 *
 * 
 * @since 28 Nov, 2016, 10:17:05 AM
 */
@Dependent
public class TeamMemberEntityResolver implements Serializable {

    private static final long serialVersionUID = -3722631802422088488L;

    @Inject
    private ProjectProfessionalTypeMappingConfigurationService projectProfessionalTypeMappingConfigurationService;

    @Inject
    private EmployeeInformationService employeeInformationService;

    @Inject
    private ProfessionalService professionalService;

    @Inject
    private AuthorizedUserSearchService authorizedUserSearchService;

    @Inject
    private AsynchronousService asynchronousService;

    @Nonnull
    @Profile
    @RequestCached
    public List<SupervisorEntityModel> loadTeamMembersValues(@Nullable final String searchExpression,
                                                             @Nullable final String departmentUUID,
                                                             @Nullable final OrganizationLevel organizationLevel) {

        final Future<List<SupervisorEntityModel>> employees = asynchronousService.execute(() -> {
            final EmployeeInformationSearchContext.EmployeeInformationSearchContextBuilder emp = EmployeeInformationSearchContext
                    .builder()
                    .name(searchExpression)
                    .searchResultOption(SearchResultOption.RESULT);
            if (!isNullOrEmpty(departmentUUID)) {
                emp.departmentUUID(departmentUUID);
            }

            final List<EmployeeInformation> employeesInfo = employeeInformationService
                    .getEmployeeInformations(emp.build())
                    .getEmployeeInformations();
            return employeesInfo
                    .stream()
                    .map((ei) -> ei.getEmployee())
                    .filter((e) -> checkOrganizationLevel(e, organizationLevel))
                    .map((ph) -> new SupervisorEntityModel(ph.getUuid(), ph.getName(),
                                                           SupervisorEntityType.EMPLOYEE))
                    .collect(Collectors.toList());

        });
        final Future<List<SupervisorEntityModel>> professionals = asynchronousService.execute(() -> {
            return professionalService
                    .findProfessionals(projectProfessionalTypeMappingConfigurationService
                            .getTypeMappingsForProfessionals())
                    .stream()
                    .filter((pt) -> pt.getName().toLowerCase().contains(searchExpression.toLowerCase()))
                    .map((ph) -> new SupervisorEntityModel(ph.getUuid(), ph.getName(),
                                                           SupervisorEntityType.PROFESSIONANL))
                    .collect(Collectors.toList());
        });

        try {
            return ImmutableList
                    .<SupervisorEntityModel>builder()
                    .addAll(employees.get())
                    .addAll(professionals.get())
                    .build();
        } catch (final InterruptedException | ExecutionException e) {
            throw Throwables.propagate(e);
        }
    }

    private boolean checkOrganizationLevel(@Nonnull final Employee employee,
                                           @Nullable final OrganizationLevel organizationLevel) {
        if (organizationLevel == null) {
            return true;
        } else {
            return this.checkLevel(employee, organizationLevel);
        }
    }

    private boolean checkLevel(@Nonnull final Employee employee,
                               @Nullable final OrganizationLevel organizationLevel) {
        if (organizationLevel == OrganizationLevel.BRACH_LEVEL
                && employee.getBusinessUnit() instanceof Branch) {
            return true;
        } else if (organizationLevel == OrganizationLevel.BUSINESS_UNIT_LEVEL
                && employee.getBusinessUnit() instanceof BusinessUnit) {
            return true;
        } else if (organizationLevel == OrganizationLevel.COMPANY_LEVEL
                && employee.getBusinessUnit() instanceof CompanyAccount) {
            return true;
        }
        return false;
    }

    @Nullable
    public AuthorizedUser getUser(@Nonnull final String uuid) {
        final AuthorizedUser user = authorizedUserSearchService.search(SearchContext.builder()
                .userUUID(uuid)
                .offset(0)
                .limit(10)
                .build())
                .stream()
                .findFirst()
                .orElse(null);

        return user;
    }

}
