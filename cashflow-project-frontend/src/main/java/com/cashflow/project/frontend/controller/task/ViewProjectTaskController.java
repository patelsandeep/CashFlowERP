package com.cashflow.project.frontend.controller.task;

import com.cashflow.access.authorization.LoggedInAuthorizedUser;
import com.cashflow.access.authorization.LoggedInBusinessAccount;
import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.accountreceivables.domain.customer.Customer;
import com.cashflow.accountreceivables.service.api.customer.CustomerService;
import com.cashflow.frontend.support.httpparameter.HttpParameter;
import com.cashflow.project.api.project.ProjectService;
import com.cashflow.project.api.task.ProjectTaskService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.ProjectType;
import com.cashflow.project.domain.project.task.ProjectTask;
import com.cashflow.project.frontend.controller.model.ModelViewScopedController;
import com.cashflow.project.frontend.controller.model.ProjectTaskModel;
import com.cashflow.project.frontend.controller.model.TaskBudgetInformation;
import com.cashflow.project.frontend.controller.model.TaskDepositModel;
import com.cashflow.project.frontend.controller.model.TaskPenaltyInformation;
import com.cashflow.project.frontend.controller.model.TaskRevenueInformation;
import com.cashflow.project.translation.task.ProjectTaskTranslationService;
import com.cashflow.useraccount.domain.businessaccount.BusinessAccount;
import com.cashflow.useraccount.domain.businessunit.Department;
import com.cashflow.useraccount.domain.businessuser.AuthorizedUser;
import com.cashflow.useraccount.domain.businessuser.Employee;
import com.cashflow.useraccount.service.api.EmployeeService;
import com.cashflow.useraccount.service.api.businessunit.department.DepartmentService;
import com.anosym.profiler.Profile;
import com.google.common.base.Throwables;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
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
 * @since 25 Nov, 2016, 4:51:52 PM
 */
@ModelViewScopedController
public class ViewProjectTaskController implements Serializable {

    private static final long serialVersionUID = -4259292520899311518L;

    @Inject
    private Logger logger;

    @Inject
    private ProjectTaskTranslationService projectTaskTranslationService;

    @Inject
    private ProjectTaskService projectTaskService;

    @Inject
    private ProjectTaskModelConvertor projectTaskModelConvertor;

    @Inject
    private ProjectService projectService;

    @Inject
    private CustomerService customerService;

    @Inject
    private EmployeeService employeeService;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    @HttpParameter(SELECTED_PROJECT_UUID)
    private Instance<String> pUUID;

    @Getter
    @Setter
    private Project project;

    @Getter
    @Setter
    private ProjectTask projectTask;

    @Getter
    @Setter
    private ProjectTaskModel projectTaskModel;

    @Getter
    @Setter
    private TaskBudgetInformation budgetInformation;

    @Getter
    @Setter
    private TaskRevenueInformation revenueInformation;

    @Getter
    @Inject
    @LoggedInAuthorizedUser
    private Instance<AuthorizedUser> authorizedUser;

    @Getter
    @Inject
    @LoggedInBusinessAccount
    private Instance<BusinessAccount> businessAccount;

    @PostConstruct
    void initPhase() {

        projectTaskModel = new ProjectTaskModel();
        budgetInformation = new TaskBudgetInformation();
        revenueInformation = new TaskRevenueInformation();
        projectTaskModel.setTaskBudgetInformation(budgetInformation);
        projectTaskModel.setTaskRevenueInformation(revenueInformation);
        projectTaskModel.setTaskDepositModel(new TaskDepositModel());
        projectTaskModel.setTaskPenaltyInformation(new TaskPenaltyInformation());
        if (!isNullOrEmpty(pUUID.get())) {
            this.project = checkNotNull(projectService.getProject(pUUID.get()), "Failed to load project for uuid: %s",
                                        pUUID.get());

            setProjectValues(project);
        }
    }

    @Profile
    private void setProjectValues(@Nonnull final Project project) {

        projectTaskModel.setProjectUUID(project.getUuid());
        projectTaskModel.setProjectName(project.getName());
        projectTaskModel.setProjectId(project.getId());
        projectTaskModel.setCurrency(project.getCurrency());

        final Future<Employee> employeeRequest = asynchronousService.execute(() -> employeeService
                .findEmployee(project.getManager()));

        if (project.getProjectType() == ProjectType.CUSTOMER_PROJECT) {

            final Future<Customer> customerRequest = asynchronousService.execute(() -> customerService
                    .getCustomer(project.getCustomerUUID()));
            try {
                final Customer customer = customerRequest.get();
                projectTaskModel.setCustomerName(customer.getName());
                projectTaskModel.setCustomerId(customer.getCustomerNumber());
                final Employee employee = employeeRequest.get();
                projectTaskModel.setProjectManager(employee.getName());
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
                projectTaskModel.setCustomerName(department.getName());
                projectTaskModel.setCustomerId(department.getCode());
                final Employee employee = employeeRequest.get();
                projectTaskModel.setProjectManager(employee.getName());
            } catch (InterruptedException | ExecutionException ex) {
                throw Throwables.propagate(ex);
            }

        }

    }

    public void viewProjectTask(@Nonnull final String taskUUID) {

        if (!isNullOrEmpty(taskUUID)) {
            projectTask = projectTaskService.getTask(taskUUID);
            projectTaskModelConvertor.setProjectTaskValues(projectTaskModel, budgetInformation, revenueInformation,
                                                           taskUUID);
            projectTaskModelConvertor
                    .setProjectTaskBudgetValues(projectTaskModel, budgetInformation, revenueInformation, taskUUID);
        }
    }

}
