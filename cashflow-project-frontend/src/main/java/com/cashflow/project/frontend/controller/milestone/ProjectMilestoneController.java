package com.cashflow.project.frontend.controller.milestone;

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
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.ProjectStatus;
import com.cashflow.project.domain.project.ProjectType;
import com.cashflow.project.domain.project.level.PerformanceStatus;
import com.cashflow.project.domain.project.level.SafetyRating;
import com.cashflow.project.domain.project.milestone.ProjectMilestone;
import com.cashflow.project.domain.project.phase.PhaseContext;
import com.cashflow.project.domain.project.phase.ProjectPhase;
import com.cashflow.project.frontend.controller.model.ModelViewScopedController;
import com.cashflow.project.frontend.controller.model.ProjectMilestoneModel;
import com.cashflow.project.frontend.controller.model.SuccessMessageModel;
import com.cashflow.project.frontend.controller.supervisor.SupervisorEntityModel;
import com.cashflow.project.frontend.controller.supervisor.SupervisorEntityResolver;
import com.cashflow.project.frontend.controller.supervisor.SupervisorEntityType;
import com.cashflow.project.translation.milestone.ProjectMilestoneTranslationService;
import com.cashflow.project.translation.performancestatus.PerformanceStatusTranslationService;
import com.cashflow.project.translation.safetyrating.SafetyRatingTranslationService;
import com.cashflow.project.translation.status.StatusTranslationService;
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
 * @since 22 Nov, 2016, 11:01:45 AM
 */
@ModelViewScopedController
public class ProjectMilestoneController implements Serializable {

    private static final long serialVersionUID = -7236146253374863770L;

    @Inject
    private Logger logger;

    @Inject
    private ProjectMilestoneService projectMilestoneService;

    @Inject
    private ProjectService projectService;

    @Inject
    private CustomerService customerService;

    @Inject
    private ProjectPhaseService phaseService;

    @Inject
    private EmployeeService employeeService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private SupervisorEntityResolver milestoneSupervisorEntityResolver;

    @Inject
    private MilestoneProcessor milestoneProcessor;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private ProjectMilestoneTranslationService projectMilestoneTranslationService;

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
    @IdContext(forDomain = ProjectMilestone.class, prefix = "MN", length = 5)
    private UniqueIdGenerator uniqueIdGenerator;

    @Getter
    @Setter
    private Project project;

    @Getter
    @Setter
    private ProjectPhase projectPhase;

    @Getter
    @Setter
    private SuccessMessageModel successMessageModel;

    @Getter
    @Setter
    private ProjectMilestoneModel milestoneModel;

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

        milestoneModel = new ProjectMilestoneModel();
        milestoneModel.setMilestoneId(uniqueIdGenerator.nextId());
        milestoneModel.setSafetyRating(SafetyRating.SAFE);
        milestoneModel.setPerformanceStatus(PerformanceStatus.ON_TIME);
        milestoneModel.setMilestoneStatus(ProjectStatus.NEW);
        milestoneModel.setStartDate(Calendar.getInstance());
        updateEndDate();
        defaultMilestoneSupervisor();
        if (!isNullOrEmpty(pUUID.get())) {
            this.project = checkNotNull(projectService.getProject(pUUID.get()), "Failed to load project for uuid: %s",
                                        pUUID.get());
            setProjectValues(project);
        }
    }

    private void updateMilestonesNumber(@Nonnull final String phaseUUID) {

        final List<ProjectMilestone> projectMilestones = projectMilestoneService
                .findByPhaseUUIDs(ImmutableList.of(phaseUUID));
        if (projectMilestones.isEmpty()) {
            milestoneModel.setMilestoneNumber("Milestone 1");
        } else {
            milestoneModel.setMilestoneNumber(format("Milestone %s", (projectMilestones.size() + 1)));
        }
    }

    @Nonnull
    @Profile
    @RequestCached
    public List<SupervisorEntityModel> loadSupervisors(@Nullable final String searchExpression) {
        return milestoneSupervisorEntityResolver.loadSupervisorValues(searchExpression);
    }

    @Nonnull
    @RequestCached
    public List<ProjectPhase> loadPhases() {
        if (!isNullOrEmpty(pUUID.get())) {
            final List<ProjectPhase> projectPhases = phaseService
                    .getProjectPhases(PhaseContext
                            .builder()
                            .projectUUID(pUUID.get())
                            .build());
            return projectPhases;
        }
        return ImmutableList.of();
    }

    @Nonnull
    public SelectItem[] getSafetyRatings() {
        return getSelectItems(SafetyRating.values(),
                              true,
                              projectMilestoneTranslationService.getSelectSafetyRatingLabel(),
                              safetyRatingTranslationService::getSafetyRatingLabel);
    }

    @Nonnull
    public SelectItem[] getPerformanceStatuses() {
        return getSelectItems(PerformanceStatus.values(),
                              true,
                              projectMilestoneTranslationService.getSelectPerformanceStatusLabel(),
                              performanceStatusTranslationService::getPerformanceStatusLabel);
    }

    public void calculateMilestoneGrossProfit() {
        milestoneModel.setMilestoneBudgetGrossProfit(milestoneModel.getMilestoneBudgetRevenue()
                .subtract(milestoneModel.getMilestoneBudgetCost()));
    }

    @Nonnull
    public SelectItem[] getMilestoneStatuses() {
        return getSelectItems(ProjectStatus.values(),
                              true,
                              projectMilestoneTranslationService.getSelectMilestoneStatusLabel(),
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
                        .append(milestoneModel.getPhaseId())
                        .append("/")
                        .append(milestoneModel.getMilestoneId())
                        .append("/")
                        .append(UUID.randomUUID().toString()).toString();
                milestoneModel.getMilestoneFileUrls().add(new FileUrl(itemUrl, event.getFile().getContentType(),
                                                                      event.getFile().getSize(),
                                                                      event.getFile().getFileName(),
                                                                      projectMilestoneTranslationService
                                                                      .getMilestoneAttachmentsLabel() + count));
                milestoneModel
                        .getInputStreams().put(itemUrl, itemImageInputStream);
                count++;
                addSuccessMessage(projectMilestoneTranslationService.getUploadSuccessMessage());

            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

    }

    @Profile
    private void setProjectValues(@Nonnull final Project project) {

        milestoneModel.setProjectUUID(project.getUuid());
        milestoneModel.setProjectName(project.getName());
        milestoneModel.setProjectId(project.getId());
        milestoneModel.setCurrency(project.getCurrency());

        final Future<Employee> employeeRequest = asynchronousService.execute(() -> employeeService
                .findEmployee(project.getManager()));
        if (project.getProjectType() == ProjectType.CUSTOMER_PROJECT) {

            final Future<Customer> customerRequest = asynchronousService.execute(() -> customerService
                    .getCustomer(project.getCustomerUUID()));
            try {
                final Customer customer = customerRequest.get();
                milestoneModel.setCustomerName(customer.getName());
                milestoneModel.setCustomerId(customer.getCustomerNumber());
                final Employee employee = employeeRequest.get();
                milestoneModel.setProjectManager(employee.getName());
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
                milestoneModel.setCustomerName(department.getName());
                milestoneModel.setCustomerId(department.getCode());
                final Employee employee = employeeRequest.get();
                milestoneModel.setProjectManager(employee.getName());
            } catch (InterruptedException | ExecutionException ex) {
                throw Throwables.propagate(ex);
            }
        }

    }

    private void updateEndDate() {
        final DateTime t1 = new DateTime(milestoneModel.getStartDate());
        milestoneModel.setEndDate(t1.plusDays(30).toGregorianCalendar());
    }

    private void defaultMilestoneSupervisor() {

        if (authorizedUser.get() instanceof Employee) {
            final SupervisorEntityModel entityModel
                    = new SupervisorEntityModel(authorizedUser.get().getUuid(),
                                                authorizedUser.get()
                                                .getName(),
                                                SupervisorEntityType.EMPLOYEE);
            milestoneModel
                    .setMilestoneSupervisor(entityModel);
        } else if (authorizedUser.get() instanceof Professional) {
            final SupervisorEntityModel entityModel
                    = new SupervisorEntityModel(authorizedUser.get().getUuid(),
                                                authorizedUser.get()
                                                .getName(),
                                                SupervisorEntityType.PROFESSIONANL);
            milestoneModel
                    .setMilestoneSupervisor(entityModel);
        }

    }

    public void updatePhaseId() {
        final String phaseUUID = checkNotNull(milestoneModel.getProjectPhase(),
                                              "Project Phase not selected for milestone");
        final ProjectPhase selectedProjectPhase = phaseService.getPhase(phaseUUID);
        projectPhase = selectedProjectPhase;
        milestoneModel.setPhaseId(selectedProjectPhase.getId());
        updateMilestonesNumber(selectedProjectPhase.getUuid());
    }

    public void saveProjectMilestone() {
        validate();
        milestoneProcessor.process(milestoneModel, projectPhase);
        successMessageModel = new SuccessMessageModel();
        milestoneModel.getMilestoneFileUrls()
                .stream()
                .filter((url) -> (milestoneModel.getInputStreams().containsKey(url.getUrl())))
                .forEach((url) -> {
                    dataRepositoryService.set(milestoneModel.getInputStreams().get(url.getUrl()),
                                              url.getUrl(), url
                                              .getContentType(),
                                              url.getContentSize());
                });
        successMessageModel.setSuccessMessage(projectMilestoneTranslationService.getProjectMilestoneSuccessMessage());
        milestoneModel = new ProjectMilestoneModel();
        milestoneModel.setMilestoneId(uniqueIdGenerator.nextId());
        milestoneModel.setSafetyRating(SafetyRating.SAFE);
        milestoneModel.setPerformanceStatus(PerformanceStatus.ON_TIME);
        milestoneModel.setMilestoneStatus(ProjectStatus.NEW);
        milestoneModel.setStartDate(Calendar.getInstance());
        updateEndDate();
        defaultMilestoneSupervisor();
        setProjectValues(project);
        addSuccessMessage(projectMilestoneTranslationService.getProjectMilestoneSuccessMessage());
    }

    private void validate() {
        milestoneModel.setMilestoneName(emptyToNull(milestoneModel.getMilestoneName()));
        milestoneModel.setMilestoneNumber(emptyToNull(milestoneModel.getMilestoneNumber()));
        milestoneModel.setMilestoneId(emptyToNull(milestoneModel.getMilestoneId()));
        milestoneModel.setProjectPhase(emptyToNull(milestoneModel.getProjectPhase()));

        assertNotNull(milestoneModel.getProjectPhase(), () -> projectMilestoneTranslationService
                      .getPhaseRequiredLabel());
        assertNotNull(milestoneModel.getMilestoneName(), () -> projectMilestoneTranslationService
                      .getMilestoneNameRequiredLabel());
        assertNotNull(milestoneModel.getMilestoneNumber(), () -> projectMilestoneTranslationService
                      .getMilestoneNumberRequiredLabel());
        assertNotNull(milestoneModel.getMilestoneId(), () -> projectMilestoneTranslationService
                      .getMilestoneIdRequiredLabel());
        assertNotNull(milestoneModel.getMilestoneSupervisor(), () -> projectMilestoneTranslationService
                      .getMilestoneSupervisorRequiredLabel());
        assertNotNull(milestoneModel.getPerformanceStatus(), () -> projectMilestoneTranslationService
                      .getPerformanceStatusRequiredLabel());
        assertNotNull(milestoneModel.getMilestoneStatus(), () -> projectMilestoneTranslationService
                      .getMilestoneStatusRequiredLabel());

        checkDepositValue();

    }

    public void checkDepositValue() {
        assertState(
                milestoneModel.getMilestoneBudgetCost().compareTo(milestoneModel.getDepositOrRetainers()) > 0
                || milestoneModel.getMilestoneBudgetCost().compareTo(milestoneModel.getDepositOrRetainers()) == 0,
                () -> projectMilestoneTranslationService.getCheckDepositAmount());
    }
}
