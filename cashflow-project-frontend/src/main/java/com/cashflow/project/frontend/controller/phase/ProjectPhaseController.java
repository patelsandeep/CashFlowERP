package com.cashflow.project.frontend.controller.phase;

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
import com.cashflow.project.api.phase.ProjectPhaseService;
import com.cashflow.project.api.project.ProjectService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.ProjectStatus;
import com.cashflow.project.domain.project.ProjectType;
import com.cashflow.project.domain.project.level.PerformanceStatus;
import com.cashflow.project.domain.project.level.SafetyRating;
import com.cashflow.project.domain.project.phase.PhaseContext;
import com.cashflow.project.domain.project.phase.ProjectPhase;
import com.cashflow.project.frontend.controller.model.ModelViewScopedController;
import com.cashflow.project.frontend.controller.model.ProjectPhaseModel;
import com.cashflow.project.frontend.controller.model.SuccessMessageModel;
import com.cashflow.project.frontend.controller.supervisor.SupervisorEntityModel;
import com.cashflow.project.frontend.controller.supervisor.SupervisorEntityResolver;
import com.cashflow.project.frontend.controller.supervisor.SupervisorEntityType;
import com.cashflow.project.translation.performancestatus.PerformanceStatusTranslationService;
import com.cashflow.project.translation.phase.ProjectPhaseTranslationService;
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
import static java.lang.String.format;

/**
 *
 * 
 */
@ModelViewScopedController
public class ProjectPhaseController implements Serializable {

    private static final long serialVersionUID = -8326378113413291575L;

    @Inject
    private Logger logger;

    @Inject
    private PhaseProcessor phaseProcessor;

    @Inject
    private SupervisorEntityResolver supervisorEntityResolver;

    @Inject
    private ProjectService projectService;

    @Inject
    private CustomerService customerService;

    @Inject
    private ProjectPhaseService phaseService;

    @Inject
    private EmployeeService employeeService;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private ProjectPhaseTranslationService projectPhaseTranslationService;

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
    @IdContext(forDomain = ProjectPhase.class, prefix = "PP", length = 5)
    private UniqueIdGenerator uniqueIdGenerator;

    @Getter
    @Setter
    private ProjectPhaseModel phaseModel;

    @Getter
    @Setter
    private SuccessMessageModel successMessageModel;

    @Getter
    @Setter
    private Project project;

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
        phaseModel = new ProjectPhaseModel();
        phaseModel.setPhaseId(uniqueIdGenerator.nextId());
        phaseModel.setSafetyRating(SafetyRating.SAFE);
        phaseModel.setPerformanceStatus(PerformanceStatus.ON_TIME);
        phaseModel.setPhaseStatus(ProjectStatus.NEW);
        phaseModel.setStartDate(Calendar.getInstance());
        updateEndDate();
        defaultPhaseSupervisor();

        final Future<Project> projectRequest = asynchronousService.execute(() -> checkNotNull(projectService
                .getProject(projectUUID), "Failed to load project for uuid: %s", projectUUID));
        updatePhaseNumber();
        try {
            project = projectRequest.get();
            setProjectValues(projectRequest.get());
        } catch (InterruptedException | ExecutionException ex) {
            throw Throwables.propagate(ex);
        }
    }

    private void updatePhaseNumber() {

        final List<ProjectPhase> projectPhases = phaseService
                .getProjectPhases(PhaseContext
                        .builder()
                        .projectUUID(pUUID.get())
                        .build());
        if (projectPhases.isEmpty()) {
            phaseModel.setPhaseNumber("Phase 1");
        } else {
            phaseModel.setPhaseNumber(format("Phase %s", (projectPhases.size() + 1)));
        }
    }

    @Nonnull
    @RequestCached
    public List<SupervisorEntityModel> loadSupervisors(@Nullable final String searchExpression) {
        return supervisorEntityResolver.loadSupervisorValues(searchExpression);
    }

    @Nonnull
    public SelectItem[] getSafetyRatings() {
        return getSelectItems(SafetyRating.values(),
                              true,
                              projectPhaseTranslationService.getSelectSafetyRatingLabel(),
                              safetyRatingTranslationService::getSafetyRatingLabel);
    }

    @Nonnull
    public SelectItem[] getPerformanceStatuses() {
        return getSelectItems(PerformanceStatus.values(),
                              true,
                              projectPhaseTranslationService.getSelectPerformanceStatusLabel(),
                              performanceStatusTranslationService::getPerformanceStatusLabel);
    }

    public void calculatePhaseGrossProfit() {
        phaseModel.setPhaseBudgetGrossProfit(phaseModel.getPhaseBudgetRevenue()
                .subtract(phaseModel.getPhaseBudgetCost()));
    }

    @Nonnull
    public SelectItem[] getPhaseStatuses() {
        return getSelectItems(ProjectStatus.values(),
                              true,
                              projectPhaseTranslationService.getSelectPhaseStatusLabel(),
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
                        .append(phaseModel.getPhaseId())
                        .append("/")
                        .append(UUID.randomUUID().toString()).toString();
                phaseModel.getPhaseFileUrls().add(new FileUrl(itemUrl, event.getFile().getContentType(),
                                                              event.getFile().getSize(),
                                                              event.getFile().getFileName(),
                                                              projectPhaseTranslationService.getPhaseAttachmentsLabel() + count));
                phaseModel.getInputStreams().put(itemUrl, itemImageInputStream);
                count++;
                addSuccessMessage(projectPhaseTranslationService.getUploadSuccessMessage());

            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

    }

    @Profile
    private void setProjectValues(@Nonnull final Project project) {

        phaseModel.setProjectUUID(project.getUuid());
        phaseModel.setProjectName(project.getName());
        phaseModel.setProjectId(project.getId());
        phaseModel.setCurrency(project.getCurrency());

        final Future<Employee> employeeRequest = asynchronousService.execute(() -> employeeService
                .findEmployee(project.getManager()));
        if (project.getProjectType() == ProjectType.CUSTOMER_PROJECT) {

            final Future<Customer> customerRequest = asynchronousService.execute(() -> customerService
                    .getCustomer(project.getCustomerUUID()));
            try {
                final Customer customer = customerRequest.get();
                phaseModel.setCustomerName(customer.getName());
                phaseModel.setCustomerId(customer.getCustomerNumber());
                final Employee employee = employeeRequest.get();
                phaseModel.setProjectManager(employee.getName());
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
                phaseModel.setCustomerName(department.getName());
                phaseModel.setCustomerId(department.getCode());
                final Employee employee = employeeRequest.get();
                phaseModel.setProjectManager(employee.getName());
            } catch (InterruptedException | ExecutionException ex) {
                throw Throwables.propagate(ex);
            }

        }

    }

    private void updateEndDate() {
        final DateTime t1 = new DateTime(phaseModel.getStartDate());
        phaseModel.setEndDate(t1.plusDays(30).toGregorianCalendar());
    }

    public void saveProjectPhase() {
        validate();
        phaseProcessor.process(phaseModel, project);
        successMessageModel = new SuccessMessageModel();
        phaseModel.getPhaseFileUrls()
                .stream()
                .filter((url) -> (phaseModel.getInputStreams().containsKey(url.getUrl())))
                .forEach((url) -> {
                    dataRepositoryService.set(phaseModel.getInputStreams().get(url.getUrl()),
                                              url.getUrl(), url
                                              .getContentType(),
                                              url.getContentSize());
                });
        successMessageModel.setSuccessMessage(projectPhaseTranslationService.getProjectPhaseSuccessMessage());
        phaseModel = new ProjectPhaseModel();
        phaseModel.setSafetyRating(SafetyRating.SAFE);
        phaseModel.setPerformanceStatus(PerformanceStatus.ON_TIME);
        phaseModel.setPhaseStatus(ProjectStatus.NEW);
        phaseModel.setPhaseId(uniqueIdGenerator.nextId());
        phaseModel.setStartDate(Calendar.getInstance());
        updateEndDate();
        defaultPhaseSupervisor();
        updatePhaseNumber();
        setProjectValues(project);
        addSuccessMessage(projectPhaseTranslationService.getProjectPhaseSuccessMessage());
    }

    private void validate() {
        phaseModel.setPhaseName(emptyToNull(phaseModel.getPhaseName()));
        phaseModel.setPhaseNumber(emptyToNull(phaseModel.getPhaseNumber()));
        phaseModel.setPhaseId(emptyToNull(phaseModel.getPhaseId()));

        assertNotNull(phaseModel.getPhaseName(), () -> projectPhaseTranslationService.getPhaseNameRequiredLabel());
        assertNotNull(phaseModel.getPhaseNumber(), () -> projectPhaseTranslationService.getPhaseNumberRequiredLabel());
        assertNotNull(phaseModel.getPhaseId(), () -> projectPhaseTranslationService.getPhaseIdRequiredLabel());
        assertNotNull(phaseModel.getPhaseSupervisor(), () -> projectPhaseTranslationService
                      .getPhaseSupervisorRequiredLabel());
        assertNotNull(phaseModel.getPerformanceStatus(), () -> projectPhaseTranslationService
                      .getPerformanceStatusRequiredLabel());
        assertNotNull(phaseModel.getPhaseStatus(), () -> projectPhaseTranslationService
                      .getPhaseStatusRequiredLabel());
        checkDepositValue();
    }

    private void defaultPhaseSupervisor() {

        if (authorizedUser.get() instanceof Employee) {
            final SupervisorEntityModel entityModel = new SupervisorEntityModel(authorizedUser.get().getUuid(),
                                                                                authorizedUser.get().getName(),
                                                                                SupervisorEntityType.EMPLOYEE);
            phaseModel.setPhaseSupervisor(entityModel);
        } else if (authorizedUser.get() instanceof Professional) {
            final SupervisorEntityModel entityModel = new SupervisorEntityModel(authorizedUser.get().getUuid(),
                                                                                authorizedUser.get().getName(),
                                                                                SupervisorEntityType.PROFESSIONANL);
            phaseModel.setPhaseSupervisor(entityModel);
        }
    }

    public void checkDepositValue() {
        assertState(
                phaseModel.getPhaseBudgetCost().compareTo(phaseModel.getDepositOrRetainers()) > 0 || phaseModel
                .getPhaseBudgetCost().compareTo(phaseModel.getDepositOrRetainers()) == 0,
                () -> projectPhaseTranslationService.getCheckDepositAmount());
    }
}
