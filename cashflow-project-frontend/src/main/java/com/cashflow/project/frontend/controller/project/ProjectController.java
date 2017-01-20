package com.cashflow.project.frontend.controller.project;

import com.cashflow.access.authorization.LoggedInAuthorizedUser;
import com.cashflow.access.authorization.LoggedInBusinessAccount;
import com.cashflow.access.authorization.LoggedInCompanyAccount;
import com.cashflow.access.authorization.uniqueid.IdContext;
import com.cashflow.access.authorization.uniqueid.UniqueIdGenerator;
import com.cashflow.datarepository.service.DataRepository;
import com.cashflow.datarepository.service.DataRepositoryService;
import com.cashflow.entitydomains.FileUrl;
import com.cashflow.frontend.support.cacheing.requestcached.RequestCached;
import com.cashflow.frontend.support.httpparameter.HttpParameter;
import com.cashflow.project.config.projectrole.ProjectManagerRolesMappingConfigurationService;
import com.cashflow.project.domain.project.ContractType;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.ProjectStatus;
import com.cashflow.project.domain.project.ProjectType;
import com.cashflow.project.domain.project.RevenueRecordingMethod;
import com.cashflow.project.domain.project.level.ProjectLevelCategory;
import com.cashflow.project.frontend.controller.businessunit.BusinessUnitEvent;
import com.cashflow.project.frontend.controller.businessunit.OnBusinessUnitSelected;
import com.cashflow.project.frontend.controller.model.GeneralInfoModel;
import com.cashflow.project.frontend.controller.model.ModelViewScopedController;
import com.cashflow.project.frontend.controller.model.ProjectModel;
import com.cashflow.project.frontend.service.urlbuilder.StaticLinkUrlBuilder;
import com.cashflow.project.frontend.service.urlbuilder.UrlContext;
import com.cashflow.project.translation.project.ProjectGeneralInfoTranslationService;
import com.cashflow.project.translation.project.ProjectTranslationService;
import com.cashflow.useraccount.domain.businessaccount.BusinessAccount;
import com.cashflow.useraccount.domain.businessunit.CompanyAccount;
import com.cashflow.useraccount.domain.businessuser.AuthorizedUser;
import com.cashflow.useraccount.domain.businessuser.Employee;
import com.cashflow.useraccount.domain.businessuser.EmployeeInformation;
import com.cashflow.useraccount.domain.businessuser.FunctionalRole;
import com.cashflow.useraccount.domain.businessuser.employee.EmployeeInformationSearchContext;
import com.cashflow.useraccount.domain.businessuser.employee.SearchResultOption;
import com.cashflow.useraccount.service.api.businessaccount.employee.EmployeeInformationService;
import com.anosym.common.Currency;
import com.anosym.profiler.Profile;
import com.anosym.urlbuilder.QueryParameter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Instance;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
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
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 *
 * 
 */
@ModelViewScopedController
public class ProjectController implements Serializable {

    private static final long serialVersionUID = -1409837583705785615L;

    private static final String PROJECT_LIST_URL = "projects/projects.xhtml";

    private static final String PROJECT_URL = "projects/project.xhtml";

    @Inject
    private Logger logger;

    @Inject
    private ProjectModelConverter projectModelConverter;

    @Inject
    private ProjectProcessor projectProcessor;

    @Inject
    private ProjectEntityTypeResolver projectEntityTypeResolver;

    @Inject
    private ProjectManagerRolesMappingConfigurationService projectManagerRoleMappingConfigurationService;

    @Inject
    private StaticLinkUrlBuilder staticLinkUrlBuilder;

    @Inject
    private EmployeeInformationService employeeInformationService;

    @Inject
    private ProjectTranslationService projectTranslationService;

    @Inject
    private ProjectGeneralInfoTranslationService generalInfoTranslationService;

    @Getter
    @Setter
    private ProjectModel projectModel;

    @Inject
    @HttpParameter(SELECTED_PROJECT_UUID)
    private Instance<String> pUUID;

    @Getter
    @Setter
    private GeneralInfoModel generalInfoModel;

    @Getter
    @Inject
    @LoggedInCompanyAccount
    private Instance<CompanyAccount> companyAccount;

    @Getter
    @Inject
    @LoggedInBusinessAccount
    private Instance<BusinessAccount> businessAccount;

    @Getter
    @Inject
    @LoggedInAuthorizedUser
    private Instance<AuthorizedUser> authorizedUser;

    @Inject
    @IdContext(forDomain = Project.class, prefix = "P", length = 6)
    private UniqueIdGenerator uniqueIdGenerator;

    private static int count = 001;

    @Inject
    @DataRepository(REPOSITORY_NAME)
    private DataRepositoryService dataRepositoryService;

    private BusinessUnitEvent businessUnitEvent;

    @PostConstruct
    void initProject() {
        projectModel = new ProjectModel();
        generalInfoModel = new GeneralInfoModel();
        if (!isNullOrEmpty(pUUID.get())) {
            projectModelConverter.setProjectValues(projectModel, generalInfoModel, pUUID.get());
            calculateProjectGrossProfit();
        } else {
            projectModel.setProjectId(uniqueIdGenerator.nextId());
            projectModel.setProjectStatus(ProjectStatus.NEW);
            projectModel.setStartDate(new Date());
            projectModel.setProjectType(ProjectType.CUSTOMER_PROJECT);
            projectModel.setProjectManager(
                    authorizedUser.get() instanceof Employee ? (Employee) authorizedUser.get() : null);
            updateEndDate();

            generalInfoModel.setLocation(companyAccount.get().getAddress().getCity());
            generalInfoModel.setCustomerDepositReq("Yes");
            projectModel.setCurrency(Currency
                    .fromCountry(companyAccount.get().getAddress().getCountry())
                    .or(Currency.CAD));
        }
    }

    void onBusinessUnitEvent(
            @Observes(notifyObserver = Reception.IF_EXISTS) @OnBusinessUnitSelected @Nonnull final BusinessUnitEvent businessUnitEvent) {
        this.businessUnitEvent = businessUnitEvent;
    }

    @Nonnull
    @RequestCached
    public List<ProjectTypeEntityModel> loadProjectTypeValues(@Nullable final String searchExpression) {
        return projectEntityTypeResolver.loadProjectTypeValues(searchExpression);
    }

    public void updateEndDate() {
        final DateTime t1 = new DateTime(projectModel.getStartDate());
        projectModel.setEndDate(t1.plusDays(45).toDate());
    }

    @Nonnull
    public SelectItem[] getContractTypes() {
        return getSelectItems(ContractType.values(),
                              true,
                              projectTranslationService.getSelectContractTypeLabel(),
                              (contractType) -> contractType.toString());
    }

    public void updateRevenueMethod() {
        projectModel.setRevenueRecMethod(projectModel.getContractType().getRevenueRecordingMethod());
    }

    @Nonnull
    public SelectItem[] getRevenueRecodingMethods() {
        return getSelectItems(RevenueRecordingMethod.values(),
                              true,
                              projectTranslationService.getRevenueRecMethodLabel(),
                              (revenue) -> revenue.toString());
    }

    @Nonnull
    public SelectItem[] getProjectStatuses() {
        return getSelectItems(ProjectStatus.values(),
                              true,
                              projectTranslationService.getProjectStatusLabel(),
                              (projectStatus) -> projectStatus.toString());
    }

    @Nonnull
    public SelectItem[] getLevelCategories() {
        return getSelectItems(ProjectLevelCategory.values(),
                              true,
                              projectTranslationService.getSelectProjectLevelCategoryLabel(),
                              (level) -> level.toString());
    }

    @Nonnull
    public SelectItem[] getCurrencies() {
        return getSelectItems(Currency.values(),
                              true,
                              projectTranslationService.getSelectCurrencyLabel(),
                              (currency) -> currency.toString());
    }

    @Nonnull
    @RequestCached
    public List<Employee> getLoadProjectManagers() {
        final EmployeeInformationSearchContext emp = EmployeeInformationSearchContext
                .builder()
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
                .collect(Collectors.toList());
    }

    public void updateCustorDeptId() {
        projectEntityTypeResolver.updateCustorDeptId(projectModel);
    }

    @Profile
    public void saveProject() throws IOException {
        final Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap();
        final String action = params.get("action");
        validate();

        generalInfoModel
                .getProjectFileUrls()
                .stream()
                .filter((url) -> (generalInfoModel.getInputStreams().containsKey(url.getUrl())))
                .forEach((url) -> {
                    dataRepositoryService.set(generalInfoModel.getInputStreams().get(url.getUrl()),
                                              url.getUrl(),
                                              url.getContentType(),
                                              url.getContentSize());
                });
        final String projectUUID = projectProcessor.process(projectModel, generalInfoModel, businessUnitEvent, action);
        addSuccessMessage(projectTranslationService.getProjectGeneralInfoSavedSuccessfully());

        /**
         * At this point, we dont have projectUUID, we use projectId.
         */
        final QueryParameter<String> projectUUIDParam = QueryParameter
                .<String>builder()
                .parameter(SELECTED_PROJECT_UUID)
                .parameterValue(projectUUID)
                .build();
        final UrlContext urlContext = UrlContext
                .builder()
                .additionalParameter(projectUUIDParam)
                .forceFacesRedirect(true)
                .path(PROJECT_URL)
                .build();
        final String redirectUrl = staticLinkUrlBuilder.buildURL(urlContext);
        /**
         * Since this is a redirect, we are going to lose flash messages, keep them.
         */
        final FacesContext facesContext = FacesContext.getCurrentInstance();
        final ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.getFlash().setKeepMessages(true);
        externalContext.redirect(redirectUrl);

    }

    public void fileUpload(@Nonnull
            final FileUploadEvent event) {
        InputStream itemImageInputStream = null;
        try {
            if (null != event.getFile() && null != event.getFile().getInputstream()) {
                itemImageInputStream = event.getFile().getInputstream();

                final String itemUrl = new StringBuilder(businessAccount.get().getUuid())
                        .append("/")
                        .append("projects")
                        .append("/")
                        .append(projectModel.getProjectId())
                        .append("/")
                        .append(UUID.randomUUID().toString()).toString();
                generalInfoModel.getProjectFileUrls().add(new FileUrl(itemUrl, event.getFile().getContentType(),
                                                                      event.getFile().getSize(),
                                                                      event.getFile().getFileName(),
                                                                      projectTranslationService
                                                                              .getProjectAttachmentsLabel() + count));
                generalInfoModel.getInputStreams().put(itemUrl, itemImageInputStream);
                count++;
                addSuccessMessage(projectTranslationService.getUploadSuccessMessage());

            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

    }

    @Nonnull
    public String redirectProjectSummary() {
        final UrlContext.Builder context = UrlContext.builder()
                .path(PROJECT_LIST_URL)
                .forceFacesRedirect(true);
        return staticLinkUrlBuilder.buildURL(context.build());
    }

    public void updateCustomerDepositCheckbox() {
        generalInfoModel.setIncludeCustomerDeposit(Objects.equals("Yes", generalInfoModel.getCustomerDepositReq()));
    }

    private void validate() {
        assertNotNull(projectModel.getProjectName(), () -> projectTranslationService.getProjectNameRequiredMessage());
        assertNotNull(projectModel.getProjectId(), () -> projectTranslationService.getProjectIdRequiredMessage());
        assertNotNull(projectModel.getProjectManager(), () -> projectTranslationService.getProjectManagerRequiredLabel());
        assertNotNull(projectModel.getCustomerOrDeptUUID(), () -> projectTranslationService.getCustomerRequiredLabel());
        assertNotNull(projectModel.getContractType(), () -> projectTranslationService.getContractTypeRequiredLabel());
        assertNotNull(projectModel.getProjectBudgedCost(),
                      () -> projectTranslationService.getProjectBudgetedCostRequiredLabel());
        assertState(!generalInfoModel.isIncludeCustomerDeposit() || generalInfoModel.getLevelCategory() != null,
                    () -> generalInfoTranslationService.getCustomerDepositLevelrequiredLabel());

        checkDepositValue();

    }

    public void checkDepositValue() {
        if (generalInfoModel.getCustomerDepositReq().equals("Yes")) {
            assertState(
                    projectModel.getProjectBudgedCost().compareTo(generalInfoModel.getCustomerDeposit()) > 0 || projectModel
                    .getProjectBudgedCost().compareTo(generalInfoModel.getCustomerDeposit()) == 0,
                    () -> projectTranslationService.getCheckDepositAmount());
        }
    }

    public void calculateProjectGrossProfit() {
        BigDecimal profit = BigDecimal.ZERO;
        profit = projectModel.getProjectRevenue().subtract(projectModel.getProjectBudgedCost());
        projectModel.setProjectGrossProfit(profit);
    }
}
