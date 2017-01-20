package com.cashflow.project.frontend.controller.tabview;

import com.cashflow.frontend.support.httpparameter.HttpParameter;
import com.cashflow.project.domain.project.level.ProjectLevelCategory;
import com.cashflow.project.domain.project.level.ProjectLevelSetting;
import com.cashflow.project.frontend.service.urlbuilder.StaticLinkUrlBuilder;
import com.cashflow.project.frontend.service.urlbuilder.UrlContext;
import com.cashflow.project.translation.project.tabtitle.ProjectTabTitleTranslationService;
import com.cashflow.project.translation.project.tabtitle.TabTitle;
import com.anosym.urlbuilder.QueryParameter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_SELECTED_TAB;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_UUID;

/**
 *
 * 
 * @since Nov 23, 2016, 6:02:20 AM
 */
@ApplicationScoped
public class DefaultTabViewUrlModelBuilder implements TabViewUrlModelBuilder {

    private static final Map<TabTitle, ProjectLevelCategory> CONDITIONALLY_INCLUDED_CATEGORIES = ImmutableMap.of(
            TabTitle.PHASES, ProjectLevelCategory.PHASE, TabTitle.MILESTONES, ProjectLevelCategory.MILESTONE);

    /**
     * Maintaining current naming. Must be renamed to something meaningful.
     */
    private static final String PROJECT_URL = "projects/project.xhtml";

    @Inject
    private CurrentSelectedProjectSettingService currentSelectedProjectSettingService;

    @Inject
    private ProjectTabTitleTranslationService projectTabTitleTranslationService;

    @Inject
    private StaticLinkUrlBuilder staticLinkUrlBuilder;

    @Inject
    @HttpParameter(SELECTED_PROJECT_UUID)
    private Instance<String> pUUID;

    @Inject
    @HttpParameter(SELECTED_PROJECT_SELECTED_TAB)
    private Instance<String> currentTab;

    @Nonnull
    @Override
    public List<TabViewUrlModel> build() {
        final Optional<ProjectLevelSetting> projectLevelSetting = currentSelectedProjectSettingService
                .getProjectLevelSetting();
        final TabTitle selectedTabTitle = Optional
                .ofNullable(currentTab.get())
                .map(TabTitle::valueOf)
                .orElse(TabTitle.GENERAL_INFO);
        return ImmutableList
                .copyOf(TabTitle.values())
                .stream()
                .map((tabTitle) -> buildUrlModel(tabTitle, selectedTabTitle, projectLevelSetting))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Nullable
    private TabViewUrlModel buildUrlModel(@Nonnull final TabTitle tabTitle,
                                          @Nonnull final TabTitle selectedTabTitle,
                                          @Nonnull final Optional<ProjectLevelSetting> projectLevelSetting) {
        /**
         * Phase and Milestone tabs are conditionally included. If project is already created, the project setting
         * determines whether we display phases and milestones.
         */
        final ProjectLevelCategory projectLevelCategory = CONDITIONALLY_INCLUDED_CATEGORIES.get(tabTitle);
        final boolean visible = !tabTitle.isProjectRequired() || projectLevelCategory == null || pUUID.get() == null || projectLevelSetting
                .map(ProjectLevelSetting::getProjectLevelCategories)
                .filter((categories) -> categories.contains(projectLevelCategory))
                .isPresent();
        if (!visible) {
            return null;
        }

        final boolean selected = selectedTabTitle == tabTitle;
        final boolean isActive = !tabTitle.isProjectRequired() || pUUID.get() != null;
        final String templateUrl = isActive ? geTemplateUrl(tabTitle) : "#";

        return TabViewUrlModel
                .builder()
                .selected(selected)
                .name(projectTabTitleTranslationService.getTitle(tabTitle))
                .tabTitle(tabTitle)
                .templateUrl(templateUrl)
                .build();
    }

    @Nullable
    private String geTemplateUrl(@Nonnull final TabTitle tabTitle) {
        final String projectUUID = pUUID.get();
        if (projectUUID == null) {
            return null;
        }

        final List<QueryParameter<?>> parameters = ImmutableList
                .<QueryParameter<?>>builder()
                .add(buildQueryParameter(SELECTED_PROJECT_SELECTED_TAB, tabTitle.name()))
                .add(buildQueryParameter(SELECTED_PROJECT_UUID, projectUUID))
                .build();
        final UrlContext urlContext = UrlContext
                .builder()
                .path(PROJECT_URL)
                .forceFacesRedirect(true)
                .additionalParameters(parameters)
                .build();
        return staticLinkUrlBuilder.buildURL(urlContext);
    }

    @Nonnull
    private QueryParameter<String> buildQueryParameter(@Nonnull final String parameter, @Nonnull String parameterValue) {
        return QueryParameter
                .<String>builder()
                .parameter(parameter)
                .parameterValue(parameterValue)
                .build();
    }

}
