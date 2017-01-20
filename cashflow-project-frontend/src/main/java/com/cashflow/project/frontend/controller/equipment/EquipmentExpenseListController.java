package com.cashflow.project.frontend.controller.equipment;

import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.frontend.support.httpparameter.HttpParameter;
import com.cashflow.project.api.equipmentinformation.EquipmentExpenseInformation;
import com.cashflow.project.api.equipmentinformation.EquipmentExpenseInformationRequest;
import com.cashflow.project.api.equipmentinformation.EquipmentExpenseInformationResult;
import com.cashflow.project.api.equipmentinformation.EquipmentExpenseInformationService;
import com.cashflow.project.api.level.ProjectLevelService;
import com.cashflow.project.api.project.ProjectService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.filtering.Filter;
import com.cashflow.project.domain.project.filtering.FilterDomain;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.ownedequipment.EquipmentType;
import com.cashflow.project.frontend.controller.model.ModelViewScopedController;
import com.cashflow.project.frontend.controller.pagination.PaginationModel;
import com.cashflow.project.frontend.service.urlbuilder.StaticLinkUrlBuilder;
import com.cashflow.project.frontend.service.urlbuilder.UrlContext;
import com.cashflow.project.translation.equipment.EquipmentTabTranslationService;
import com.cashflow.project.translation.equipment.owned.EquipmentTypeTranslationService;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;

import static com.cashflow.frontend.support.jsfutil.SelectItemHelper.getSelectItems;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_UUID;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 *
 * 
 * @since Dec 20, 2016, 4:19:37 PM
 */
@ModelViewScopedController
public class EquipmentExpenseListController extends PaginationModel implements Serializable {

    private static final long serialVersionUID = -7671859692965508364L;

    private static final String PROJECT_URL = "/projects/projects.xhtml";

    @Inject
    private StaticLinkUrlBuilder staticLinkUrlBuilder;

    @Inject
    @HttpParameter(SELECTED_PROJECT_UUID)
    private Instance<String> pUUID;

    @Inject
    private ProjectService projectService;

    @Inject
    private EquipmentTypeTranslationService equipmentTypeTranslationService;

    @Inject
    private ProjectLevelService projectLevelService;

    @Inject
    private EquipmentTabTranslationService equipmentTabTranslationService;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private EquipmentExpenseInformationService equipmentExpenseInformationService;

    private String projectUUID;

    @Getter
    @Setter
    private Project project;

    @Getter
    private List<EquipmentExpenseInformation> equipmentExpenseInformations;

    @Getter
    @Setter
    private String projectLevelUUID;

    @Getter
    @Setter
    private String freeSearch;

    @Getter
    private List<ProjectLevel> projectLevels;

    @Getter
    @Setter
    private EquipmentType equipmentType;

    @PostConstruct
    public void initEquipmentExpense() {
        projectUUID = checkNotNull(pUUID.get(), "Project UUID must not be null");
        final Future<Project> projectFuture = asynchronousService
                .execute(() -> checkNotNull(projectService.getProject(projectUUID),
                                            "Failed to load project for uuid: %s",
                                            projectUUID));
        final Future<List<ProjectLevel>> filterValuesRequest = asynchronousService
                .execute(() -> projectLevelService
                        .findLevelsByUUID(projectUUID));
        loadEquipmentExpense();
        try {
            this.project = projectFuture.get();
            projectLevels = filterValuesRequest.get();
        } catch (InterruptedException | ExecutionException ex) {
            throw Throwables.propagate(ex);
        }
    }

    public void loadEquipmentExpense() {
        final EquipmentExpenseInformationRequest request = buildEquipmentExpenseInformationRequest();
        // adding asynch here as its called from @PostConstruct also.
        final Future<EquipmentExpenseInformationResult> resultRequest = asynchronousService
                .execute(() -> equipmentExpenseInformationService.getEquipmentExpenseInformations(request));
        try {
            this.equipmentExpenseInformations = resultRequest.get().getEquipmentExpenseInformations();
            this.setCount(resultRequest.get().getCount());
        } catch (InterruptedException | ExecutionException ex) {
            throw Throwables.propagate(ex);
        }
    }

    @Override
    public void loadData() {
        loadEquipmentExpense();
    }

    @Nonnull
    private EquipmentExpenseInformationRequest buildEquipmentExpenseInformationRequest() {
        final Map<Filter, Object> filters = new HashMap<>();
        prepareFilters(filters);

        return EquipmentExpenseInformationRequest
                .builder()
                .filters(filters)
                .build();
    }

    private void prepareFilters(@Nonnull final Map<Filter, Object> filters) {
        filters.put(Filter.builder()
                .filterDomains(ImmutableList.of(FilterDomain.EQUIPMENT_EXPENSE_INFORMATION))
                .attribute("projectUUID")
                .name("projectUUID")
                .build(), null != projectLevelUUID ? projectLevelUUID : projectUUID);
        if (!isNullOrEmpty(freeSearch)) {
            filters.put(Filter.builder()
                    .filterDomains(ImmutableList.of(FilterDomain.EQUIPMENT_EXPENSE_INFORMATION))
                    .attribute("freeText")
                    .name("name")
                    .build(), freeSearch);
        }
        if (equipmentType != null) {
            filters.put(Filter.builder()
                    .filterDomains(ImmutableList.of(FilterDomain.EQUIPMENT_EXPENSE_INFORMATION))
                    .attribute("equipmentType")
                    .name("equipmentType")
                    .build(), equipmentType);
        }
    }

    @Nonnull
    public String redirectProjectSummary() {
        final UrlContext.Builder context = UrlContext
                .builder()
                .path(PROJECT_URL)
                .forceFacesRedirect(true);
        return staticLinkUrlBuilder.buildURL(context.build());
    }

    @Nonnull
    public SelectItem[] getEquipmentTypes() {
        return getSelectItems(EquipmentType.values(),
                              true,
                              equipmentTabTranslationService.getFilterByLabel(),
                              equipmentTypeTranslationService::getEquipmentTypeLabel);
    }

}
