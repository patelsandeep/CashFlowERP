package com.cashflow.project.frontend.controller.supervisor;

import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.frontend.support.cacheing.requestcached.RequestCached;
import com.cashflow.project.config.projectrole.ProjectManagerRolesMappingConfigurationService;
import com.cashflow.project.config.projectrole.ProjectProfessionalTypeMappingConfigurationService;
import com.cashflow.useraccount.domain.businessuser.EmployeeInformation;
import com.cashflow.useraccount.domain.businessuser.FunctionalRole;
import com.cashflow.useraccount.domain.businessuser.employee.EmployeeInformationSearchContext;
import com.cashflow.useraccount.domain.businessuser.employee.SearchResultOption;
import com.cashflow.useraccount.service.api.businessaccount.employee.EmployeeInformationService;
import com.cashflow.useraccount.service.api.professional.ProfessionalService;
import com.anosym.profiler.Profile;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 *
 * 
 * @since 21 Nov, 2016, 12:15:00 PM
 */
@Dependent
public class SupervisorEntityResolver implements Serializable {

    private static final long serialVersionUID = -3722631802422088488L;

    @Inject
    private ProjectManagerRolesMappingConfigurationService projectManagerRoleMappingConfigurationService;

    @Inject
    private ProjectProfessionalTypeMappingConfigurationService projectProfessionalTypeMappingConfigurationService;

    @Inject
    private EmployeeInformationService employeeInformationService;

    @Inject
    private ProfessionalService professionalService;

    @Inject
    private AsynchronousService asynchronousService;

    @Nonnull
    @Profile
    @RequestCached
    public List<SupervisorEntityModel> loadSupervisorValues(@Nullable final String searchExpression) {

        final Future<List<SupervisorEntityModel>> employees = asynchronousService.execute(() -> {
            final EmployeeInformationSearchContext emp = EmployeeInformationSearchContext
                    .builder()
                    .name(searchExpression)
                    .searchResultOption(SearchResultOption.RESULT)
                    .build();

            final List<EmployeeInformation> employeesInfo = employeeInformationService.getEmployeeInformations(emp)
                    .getEmployeeInformations();

            final FunctionalRole[] roles = projectManagerRoleMappingConfigurationService
                    .getRoleMappingsForProjectManagers();

            return employeesInfo
                    .stream()
                    .map((ei) -> ei.getEmployee())
                    .filter((e) -> Arrays.asList(roles)
                    .stream()
                    .anyMatch((fr) -> e.getFunctionalRoles()
                    .contains(fr)))
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

}
