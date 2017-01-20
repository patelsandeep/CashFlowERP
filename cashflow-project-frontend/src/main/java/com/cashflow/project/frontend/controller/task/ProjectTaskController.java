package com.cashflow.project.frontend.controller.task;

import com.cashflow.access.authorization.LoggedInAuthorizedUser;
import com.cashflow.access.authorization.LoggedInBusinessAccount;
import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.access.authorization.uniqueid.IdContext;
import com.cashflow.access.authorization.uniqueid.UniqueIdGenerator;
import com.cashflow.accountreceivables.domain.customer.Customer;
import com.cashflow.accountreceivables.service.api.customer.CustomerService;
import com.cashflow.datarepository.service.DataRepository;
import com.cashflow.datarepository.service.DataRepositoryService;
import com.cashflow.entitydomains.FileUrl;
import com.cashflow.frontend.support.cacheing.requestcached.RequestCached;
import com.cashflow.frontend.support.httpparameter.HttpParameter;
import com.cashflow.project.api.milestone.ProjectMilestoneService;
import com.cashflow.project.api.phase.ProjectPhaseService;
import com.cashflow.project.api.project.ProjectService;
import com.cashflow.project.api.task.ProjectTaskService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.ProjectStatus;
import com.cashflow.project.domain.project.ProjectType;
import com.cashflow.project.domain.project.level.PerformanceStatus;
import com.cashflow.project.domain.project.level.ProjectLevelCategory;
import com.cashflow.project.domain.project.level.SafetyRating;
import com.cashflow.project.domain.project.milestone.ProjectMilestone;
import com.cashflow.project.domain.project.phase.PhaseContext;
import com.cashflow.project.domain.project.phase.ProjectPhase;
import com.cashflow.project.domain.project.task.ProjectTask;
import com.cashflow.project.frontend.controller.model.ModelViewScopedController;
import com.cashflow.project.frontend.controller.model.ProjectTaskModel;
import com.cashflow.project.frontend.controller.model.SuccessMessageModel;
import com.cashflow.project.frontend.controller.model.TaskBudgetInformation;
import com.cashflow.project.frontend.controller.model.TaskDepositModel;
import com.cashflow.project.frontend.controller.model.TaskPenaltyInformation;
import com.cashflow.project.frontend.controller.model.TaskRevenueInformation;
import com.cashflow.project.frontend.controller.supervisor.SupervisorEntityModel;
import com.cashflow.project.frontend.controller.supervisor.SupervisorEntityResolver;
import com.cashflow.project.frontend.controller.supervisor.SupervisorEntityType;
import com.cashflow.project.translation.performancestatus.PerformanceStatusTranslationService;
import com.cashflow.project.translation.safetyrating.SafetyRatingTranslationService;
import com.cashflow.project.translation.status.StatusTranslationService;
import com.cashflow.project.translation.task.ProjectTaskTranslationService;
import com.cashflow.useraccount.domain.businessaccount.BusinessAccount;
import com.cashflow.useraccount.domain.businessunit.Department;
import com.cashflow.useraccount.domain.businessuser.AuthorizedUser;
import com.cashflow.useraccount.domain.businessuser.Employee;
import com.cashflow.useraccount.domain.businessuser.professional.Professional;
import com.cashflow.useraccount.service.api.EmployeeService;
import com.cashflow.useraccount.service.api.businessunit.department.DepartmentService;
import com.anosym.profiler.Profile;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import org.primefaces.event.FileUploadEvent;

import static com.cashflow.frontend.support.errormessage.ErrorMessagePreconditions.assertNotNull;
import static com.cashflow.frontend.support.errormessage.ErrorMessagePreconditions.assertState;
import static com.cashflow.frontend.support.jsfutil.SelectItemHelper.getSelectItems;
import static com.cashflow.frontend.support.jsfutil.SuccessMessageHelper.addSuccessMessage;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_UUID;
import static com.cashflow.project.frontend.controller.model.DataRepositoryName.REPOSITORY_NAME;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;

/**
 *
 * 
 * @since 23 Nov, 2016, 1:52:28 PM
 */
@ModelViewScopedController
public class ProjectTaskController implements Serializable {

    private static final long serialVersionUID = 4013776646439818122L;

    @Inject
    private Logger logger;

    @Inject
    private ProjectTaskService projectTaskService;

    @Inject
    private ProjectTaskModelConvertor projectTaskModelConvertor;

    @Inject
    private TaskProcessor taskProcessor;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private ProjectService projectService;

    @Inject
    private CustomerService customerService;

    @Inject
    private ProjectMilestoneService milestoneService;

    @Inject
    private ProjectPhaseService projectPhaseService;

    @Inject
    private EmployeeService employeeService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private SupervisorEntityResolver supervisorEntityResolver;

    @Inject
    private ProjectTaskTranslationService projectTaskTranslationService;

    @Inject
    private PerformanceStatusTranslationService performanceStatusTranslationService;

    @Inject
    private SafetyRatingTranslationService safetyRatingTranslationService;

    @Inject
    private StatusTranslationService statusTranslationService;

    @Inject
    @HttpParameter(SELECTED_PROJECT_UUID)
    private Instance<String> pUUID;

    @Inject
    @IdContext(forDomain = ProjectTask.class, prefix = "T", length = 6)
    private UniqueIdGenerator uniqueIdGenerator;

    @Getter
    @Setter
    private Project project;

    @Getter
    @Setter
    private SuccessMessageModel successMessageModel;

    @Getter
    @Setter
    private ProjectTask projectTask;

    @Getter
    @Setter
    private ProjectMilestone projectMilestone;

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

    @Inject
    @DataRepository(REPOSITORY_NAME)
    private DataRepositoryService dataRepositoryService;

    private static int count = 001;

    @PostConstruct
    void initPhase() {
        final String projectUUID = checkNotNull(pUUID.get(), "Project not selected for phase");
        final Future<Project> projectRequest = asynchronousService
                .execute(() -> checkNotNull(projectService.getProject(projectUUID),
                                            "Failed to load project for uuid: %s", projectUUID));
        projectTaskModel = new ProjectTaskModel();
        budgetInformation = new TaskBudgetInformation();
        revenueInformation = new TaskRevenueInformation();
        projectTaskModel.setTaskBudgetInformation(budgetInformation);
        projectTaskModel.setTaskRevenueInformation(revenueInformation);
        projectTaskModel.setTaskDepositModel(new TaskDepositModel());
        projectTaskModel.setTaskPenaltyInformation(new TaskPenaltyInformation());

        projectTaskModel.setTaskId(uniqueIdGenerator.nextId());
        projectTaskModel.setSafetyRating(SafetyRating.SAFE);
        projectTaskModel.setPerformanceStatus(PerformanceStatus.ON_TIME);
        projectTaskModel.setTaskStatus(ProjectStatus.NEW);
        projectTaskModel.setStartDate(Calendar.getInstance());
        updateEndDate();
        defaultMilestoneSupervisor();

        try {
            project = projectRequest.get();
            setProjectValues(projectRequest.get());
        } catch (InterruptedException | ExecutionException ex) {
            throw Throwables.propagate(ex);
        }

        if (project.getProjectLevelCategory() == ProjectLevelCategory.TASK) {
            updateTaskNumber(projectUUID);
        }

    }

    private void updateTaskNumber(@Nonnull final String milestoneOrProjectUUID) {

        final List<ProjectTask> projectTasks = projectTaskService
                .findByMilestoneOrProjectUUIDs(ImmutableList.of(milestoneOrProjectUUID));
        if (projectTasks.isEmpty()) {
            projectTaskModel.setTaskNumber("Task 1");
        } else {
            projectTaskModel.setTaskNumber(format("Task %s", (projectTasks.size() + 1)));
        }
    }

    @Nonnull
    @Profile
    @RequestCached
    public List<SupervisorEntityModel> loadSupervisors(@Nullable final String searchExpression) {
        return supervisorEntityResolver.loadSupervisorValues(searchExpression);
    }

    @Nonnull
    @RequestCached
    public List<ProjectPhase> loadPhases() {
        if (!isNullOrEmpty(pUUID.get())) {
            final List<ProjectPhase> projectPhases = projectPhaseService
                    .getProjectPhases(PhaseContext
                            .builder()
                            .projectUUID(pUUID.get())
                            .build());
            return projectPhases;
        }
        return ImmutableList.of();
    }

    private void loadMilestone(@Nonnull final String phaseUUID) {
        final List<ProjectMilestone> projectMilestones = milestoneService
                .findByPhaseUUIDs(ImmutableList.of(phaseUUID));
        projectTaskModel.setProjectMilestones(projectMilestones);
    }

    @Nonnull
    public SelectItem[] getSafetyRatings() {
        return getSelectItems(SafetyRating.values(),
                              true,
                              projectTaskTranslationService.getSelectSafetyRatingLabel(),
                              safetyRatingTranslationService::getSafetyRatingLabel);
    }

    @Nonnull
    public SelectItem[] getPerformanceStatuses() {
        return getSelectItems(PerformanceStatus.values(),
                              true,
                              projectTaskTranslationService.getSelectPerformanceStatusLabel(),
                              performanceStatusTranslationService::getPerformanceStatusLabel);
    }

    public void calculateTaskGrossProfit() {
        projectTaskModel.getTaskBudgetInformation().setTaskBudgetGrossProfit(projectTaskModel
                .getTaskRevenueInformation().getTaskBudgetRevenue()
                .subtract(projectTaskModel.getTaskBudgetInformation().getTaskBudgetCost()));
    }

    public void calculateTaskCost() {
        projectTaskModel.getTaskBudgetInformation().setTaskBudgetCost(projectTaskModel.getTaskBudgetInformation()
                .getBudgetEquipmentCost()
                .add(projectTaskModel.getTaskBudgetInformation().getBudgetLabourCost())
                .add(projectTaskModel.getTaskBudgetInformation().getBudgetMaterialCost())
                .add(projectTaskModel.getTaskBudgetInformation().getBudgetSubContractorCost())
                .add(projectTaskModel.getTaskBudgetInformation().getCostAllocation())
                .add(projectTaskModel.getTaskBudgetInformation().getOtherIndirectCost()));

        calculateTaskGrossProfit();
    }

    public void calculateTaskRevenue() {
        projectTaskModel.getTaskRevenueInformation().setTaskBudgetRevenue(projectTaskModel.getTaskRevenueInformation()
                .getBudgetEquipmentRevenue()
                .add(projectTaskModel.getTaskRevenueInformation().getBudgetLabourRevenue())
                .add(projectTaskModel.getTaskRevenueInformation().getBudgetMaterialRevenue())
                .add(projectTaskModel.getTaskRevenueInformation().getBudgetSubContractorRevenue())
                .add(projectTaskModel.getTaskRevenueInformation().getRevenueAllocation())
                .add(projectTaskModel.getTaskRevenueInformation().getOtherIndirectRevenue()));

        calculateTaskGrossProfit();
    }

    @Nonnull
    public SelectItem[] getTaskStatuses() {
        return getSelectItems(ProjectStatus.values(),
                              true,
                              projectTaskTranslationService.getSelectTaskStatusLabel(),
                              statusTranslationService::getStatusLabel);
    }

    public void fileUpload(@Nonnull final FileUploadEvent event) {
        InputStream itemImageInputStream = null;
        try {
            if (null != event.getFile() && null != event.getFile().getInputstream()) {
                itemImageInputStream = event.getFile().getInputstream();

                final String itemUrl = new StringBuilder(businessAccount.get().getUuid())
                        .append("/")
                        .append("projects")
                        .append("/")
                        .append(projectTaskModel.getPhaseId())
                        .append("/")
                        .append(projectTaskModel.getMilestoneId())
                        .append("/")
                        .append(UUID.randomUUID().toString()).toString();
                projectTaskModel.getTaskFileUrls().add(new FileUrl(itemUrl, event.getFile().getContentType(),
                                                                   event.getFile().getSize(),
                                                                   event.getFile().getFileName(),
                                                                   projectTaskTranslationService
                                                                   .getTaskAttachmentsLabel() + count));
                projectTaskModel.getInputStreams().put(itemUrl, itemImageInputStream);
                count++;
                addSuccessMessage(projectTaskTranslationService.getUploadSuccessMessage());

            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
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

    private void updateEndDate() {
        final DateTime t1 = new DateTime(projectTaskModel.getStartDate());
        projectTaskModel.setEndDate(t1.plusDays(30).toGregorianCalendar());
    }

    private void defaultMilestoneSupervisor() {

        if (authorizedUser.get() instanceof Employee) {
            final SupervisorEntityModel entityModel = new SupervisorEntityModel(authorizedUser.get().getUuid(),
                                                                                authorizedUser.get().getName(),
                                                                                SupervisorEntityType.EMPLOYEE);
            projectTaskModel.setTaskSupervisor(entityModel);
        } else if (authorizedUser.get() instanceof Professional) {
            final SupervisorEntityModel entityModel = new SupervisorEntityModel(authorizedUser.get().getUuid(),
                                                                                authorizedUser.get().getName(),
                                                                                SupervisorEntityType.PROFESSIONANL);
            projectTaskModel.setTaskSupervisor(entityModel);
        }

    }

    public void updatePhaseId() {
        final String phaseUUID = checkNotNull(projectTaskModel.getProjectPhase(),
                                              "Project Phase not selected for task");
        final ProjectPhase selectedProjectPhase = projectPhaseService.getPhase(phaseUUID);
        projectTaskModel.setPhaseId(selectedProjectPhase.getId());
        loadMilestone(selectedProjectPhase.getUuid());
    }

    public void updateMilestoneId() {
        final String milestoneUUID = checkNotNull(projectTaskModel.getProjectMilestone(),
                                                  "Project Milestone not selected for task");
        final ProjectMilestone selectedMilestone = milestoneService.getMilestone(milestoneUUID);
        projectMilestone = selectedMilestone;
        projectTaskModel.setMilestoneId(projectMilestone.getId());
        updateTaskNumber(milestoneUUID);
    }

    public void saveProjectTask() {
        validate();
        taskProcessor.process(projectTaskModel, projectMilestone, project);
        successMessageModel = new SuccessMessageModel();
        projectTaskModel.getTaskFileUrls()
                .stream()
                .filter((url) -> (projectTaskModel.getInputStreams().containsKey(url.getUrl())))
                .forEach((url) -> {
                    dataRepositoryService.set(projectTaskModel.getInputStreams().get(url.getUrl()),
                                              url.getUrl(), url
                                              .getContentType(),
                                              url.getContentSize());
                });

        if (null != projectTaskModel.getTaskUUID()) {
            successMessageModel.setSuccessMessage(projectTaskTranslationService.getProjectTaskUpdatedSuccessMessage());
            addSuccessMessage(projectTaskTranslationService.getProjectTaskUpdatedSuccessMessage());
        } else {
            successMessageModel.setSuccessMessage(projectTaskTranslationService.getProjectTaskSuccessMessage());
            addSuccessMessage(projectTaskTranslationService.getProjectTaskSuccessMessage());
        }

        projectTaskModel = new ProjectTaskModel();
        projectTaskModel.setTaskId(uniqueIdGenerator.nextId());
        projectTaskModel.setSafetyRating(SafetyRating.SAFE);
        projectTaskModel.setPerformanceStatus(PerformanceStatus.ON_TIME);
        projectTaskModel.setTaskStatus(ProjectStatus.NEW);
        projectTaskModel.setStartDate(Calendar.getInstance());
        updateEndDate();
        defaultMilestoneSupervisor();
        setProjectValues(project);
        if (project.getProjectLevelCategory() == ProjectLevelCategory.TASK) {
            updateTaskNumber(pUUID.get());
        }
    }

    private void validate() {
        projectTaskModel.setTaskName(emptyToNull(projectTaskModel.getTaskName()));
        projectTaskModel.setTaskNumber(emptyToNull(projectTaskModel.getTaskNumber()));
        projectTaskModel.setTaskId(emptyToNull(projectTaskModel.getTaskId()));

        if (project.getProjectLevelCategory() != ProjectLevelCategory.TASK) {
            projectTaskModel.setProjectPhase(emptyToNull(projectTaskModel.getProjectPhase()));
            projectTaskModel.setProjectMilestone(emptyToNull(projectTaskModel.getProjectMilestone()));

            assertNotNull(projectTaskModel.getProjectPhase(), () -> projectTaskTranslationService
                          .getPhaseRequiredLabel());
            assertNotNull(projectTaskModel.getProjectMilestone(), () -> projectTaskTranslationService
                          .getMilestoneRequiredLabel());
        }

        assertNotNull(projectTaskModel.getTaskName(), () -> projectTaskTranslationService
                      .getTaskNameRequiredLabel());
        assertNotNull(projectTaskModel.getTaskNumber(), () -> projectTaskTranslationService
                      .getTaskNumberRequiredLabel());
        assertNotNull(projectTaskModel.getTaskId(), () -> projectTaskTranslationService
                      .getTaskIdRequiredLabel());
        assertNotNull(projectTaskModel.getTaskSupervisor(), () -> projectTaskTranslationService
                      .getTaskSupervisorRequiredLabel());
        assertNotNull(projectTaskModel.getPerformanceStatus(), () -> projectTaskTranslationService
                      .getPerformanceStatusRequiredLabel());
        assertNotNull(projectTaskModel.getTaskStatus(), () -> projectTaskTranslationService
                      .getTaskStatusRequiredLabel());
        checkDepositValue();

    }

    public void saveTaskBudgetDetails() {
        successMessageModel = new SuccessMessageModel();
        if (null != projectTaskModel.getTaskUUID()) {
            successMessageModel.setSuccessMessage(projectTaskTranslationService
                    .getProjectTaskBudgetDetailsUpdateSuccessMessage());
            addSuccessMessage(projectTaskTranslationService.getProjectTaskBudgetDetailsUpdateSuccessMessage());
        } else {
            successMessageModel.setSuccessMessage(projectTaskTranslationService
                    .getProjectTaskBudgetDetailsSuccessMessage());
            addSuccessMessage(projectTaskTranslationService.getProjectTaskBudgetDetailsSuccessMessage());
        }

    }

    public void editProjectTask(@Nonnull final String taskUUID) {

        if (!isNullOrEmpty(taskUUID)) {
            projectTask = projectTaskService.getTask(taskUUID);
            projectTaskModelConvertor.setProjectTaskValues(projectTaskModel, budgetInformation, revenueInformation,
                                                           taskUUID);
            projectTaskModelConvertor
                    .setProjectTaskBudgetValues(projectTaskModel, budgetInformation, revenueInformation, taskUUID);
        }
    }

    public void checkDepositValue() {
        assertState(
                projectTaskModel.getTaskBudgetInformation().getTaskBudgetCost().compareTo(projectTaskModel
                        .getTaskDepositModel().getDepositOrRetainers()) > 0 || projectTaskModel
                .getTaskBudgetInformation().getTaskBudgetCost().compareTo(projectTaskModel.getTaskDepositModel()
                        .getDepositOrRetainers()) == 0,
                () -> projectTaskTranslationService.getCheckDepositAmount());
    }

}
