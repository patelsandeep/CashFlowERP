package com.cashflow.project.frontend.controller.project;

import com.cashflow.access.authorization.LoggedInAuthorizedUser;
import com.cashflow.access.authorization.LoggedInCompanyAccount;
import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.accountreceivables.domain.customer.Customer;
import com.cashflow.accountreceivables.domain.customer.CustomerSearchContext;
import com.cashflow.accountreceivables.service.api.customer.CustomerService;
import com.cashflow.frontend.support.cacheing.requestcached.RequestCached;
import com.cashflow.project.domain.project.ProjectType;
import com.cashflow.project.frontend.controller.model.ProjectModel;
import com.cashflow.useraccount.domain.businessunit.CompanyAccount;
import com.cashflow.useraccount.domain.businessunit.Department;
import com.cashflow.useraccount.domain.businessuser.AuthorizedUser;
import com.cashflow.useraccount.service.api.businessunit.BusinessUnitService;
import com.cashflow.useraccount.service.api.businessunit.department.DepartmentService;
import com.anosym.profiler.Profile;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import lombok.Getter;

/**
 *
 * 
 */
@Dependent
public class ProjectEntityTypeResolver implements Serializable {

    private static final long serialVersionUID = -2898482665607158887L;

    @Inject
    private Logger logger;

    @Inject
    private CustomerService customerService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private BusinessUnitService businessUnitService;

    @Inject
    private AsynchronousService asynchronousService;

    @Getter
    @Inject
    @LoggedInCompanyAccount
    private Instance<CompanyAccount> companyAccount;

    @Getter
    @Inject
    @LoggedInAuthorizedUser
    private Instance<AuthorizedUser> authorizedUser;

    @Nonnull
    @Profile
    @RequestCached
    public List<ProjectTypeEntityModel> loadProjectTypeValues(@Nullable final String searchExpression) {
        final Future<List<ProjectTypeEntityModel>> customers = asynchronousService.execute(() -> {
            final CustomerSearchContext customerSearchContext = CustomerSearchContext
                    .builder()
                    .offset(0)
                    .limit(100)
                    .name(searchExpression)
                    .build();
            return customerService
                    .getCustomers(customerSearchContext)
                    .stream()
                    .map((cu) -> new ProjectTypeEntityModel(cu.getUuid(), cu.getName(),
                                                            ProjectEntityType.CUSTOMER))
                    .collect(Collectors.toList());
        });
        final Future<List<ProjectTypeEntityModel>> departments = asynchronousService.execute(() -> {
            return businessUnitService
                    .findBusinessUnits(companyAccount.get().getUuid())
                    .stream()
                    .filter((bu) -> bu instanceof Department
                            && bu.getName().toLowerCase().contains(searchExpression.toLowerCase()))
                    .map((depart) -> new ProjectTypeEntityModel(depart.getUuid(), depart.getName(),
                                                                ProjectEntityType.DEPARTMENT))
                    .collect(Collectors.toList());
        });

        try {
            return ImmutableList
                    .<ProjectTypeEntityModel>builder()
                    .addAll(customers.get())
                    .addAll(departments.get())
                    .build();
        } catch (final InterruptedException | ExecutionException e) {
            throw Throwables.propagate(e);
        }
    }

    public void updateCustorDeptId(@Nonnull final ProjectModel projectModel) {
        if (projectModel.getProjectTypeEntityModel().getEntityType() == ProjectEntityType.CUSTOMER) {
            projectModel.setProjectType(ProjectType.CUSTOMER_PROJECT);
            final Customer customer = customerService
                    .getCustomer(projectModel.getProjectTypeEntityModel()
                            .getEntityUUID());
            projectModel.setCustomerOrDeptUUID(customer.getUuid());
            if (null != customer.getCreditLimit()) {
                projectModel.setCurrency(customer.getCreditLimit().getCurrency());
            }
            projectModel.setCustomerId(customer.getCustomerNumber());
        } else if (projectModel.getProjectTypeEntityModel().getEntityType() == ProjectEntityType.DEPARTMENT) {
            final String departUUID = projectModel.getProjectTypeEntityModel().getEntityUUID();
            projectModel.setProjectType(ProjectType.INTERNAL_PROJECT);
            final Department department = departmentService
                    .findDepartment(departUUID);
            projectModel.setCustomerOrDeptUUID(departUUID);
            projectModel.setDepartmentId(null != department ? department.getCode() : "");
        }
    }

}
