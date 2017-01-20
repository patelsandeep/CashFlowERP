package com.cashflow.project.frontend.controller.menuoption;

import com.cashflow.accessroles.service.authorizationcontext.AccessScopeController;
import com.cashflow.frontend.support.httpparameter.HttpParameter;
import com.cashflow.project.config.menuoption.MenuOptionConfigurationService;
import com.cashflow.project.domain.util.menuoption.MenuOption;
import com.cashflow.project.translation.menuoption.MenuOptionTranslationService;
import com.anosym.urlbuilder.RelativeUrlBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;

/**
 *
 * 
 * @since Oct 3, 2016, 11:38:59 AM
 */
@Named
@RequestScoped
public class MenuOptionController implements Serializable {

    private static final long serialVersionUID = 6724800385315159201L;

    private static final String MENU_OPTION_QUERY_PARAMETER = "mo_qp";

    private static final String API_KEY_QUERY_PARAMETER = "api_key";

    private static final String JAZ_SESSIONID_QUERY_PARAMETER = "jazsessionid";

    @Inject
    private AccessScopeController accessScopeController;

    @Inject
    private RelativeUrlBuilder relativeUrlBuilder;

    @Inject
    private MenuOptionTranslationService menuOptionTranslationService;

    @Inject
    private MenuOptionConfigurationService menuOptionConfigurationService;

    @Inject
    @HttpParameter(MENU_OPTION_QUERY_PARAMETER)
    private Instance<String> selectedMenuOption;

    @Inject
    @HttpParameter(API_KEY_QUERY_PARAMETER)
    private Instance<String> apiKey;

    @Inject
    @HttpParameter(JAZ_SESSIONID_QUERY_PARAMETER)
    private Instance<String> jazSessionId;

    @Getter
    private List<MenuOptionModel> menuOptions;

    @Getter
    private MenuOption currentMenuOption;

    @PostConstruct
    void initMenuOptions() {
        currentMenuOption = Optional
                .ofNullable(this.selectedMenuOption.get())
                .map(MenuOption::valueOf)
                .orElse(MenuOption.PROJECTS);

        this.menuOptions = ImmutableList
                .copyOf(menuOptionConfigurationService.getEnabledOrderedMenus())
                .stream()
                .filter(this::hasAccess)
                .map((menuOption) -> buildMenuOptionModel(menuOption, currentMenuOption))
                .collect(Collectors.toList());
    }

    @Nonnull
    public MenuOptionModel buildMenuOptionModel(@Nonnull final MenuOption menuOption,
                                                @Nullable final MenuOption selectedMenuOption) {
        final Map<String, String> parameters = ImmutableMap.of(
                MENU_OPTION_QUERY_PARAMETER, menuOption.name(),
                API_KEY_QUERY_PARAMETER, apiKey.get(),
                JAZ_SESSIONID_QUERY_PARAMETER, jazSessionId.get());
        return MenuOptionModel
                .builder()
                .menuLink(relativeUrlBuilder.buildWithPath(menuOption.getTemplate(), parameters))
                .selected(menuOption == selectedMenuOption)
                .label(menuOptionTranslationService.getMenuOptionLabel(menuOption))
                .build();
    }

    private boolean hasAccess(@Nonnull final MenuOption menuOption) {
        if (menuOption.getRequiredAccessScopes().isEmpty()) {
            return true;
        }

        return menuOption
                .getRequiredAccessScopes()
                .stream()
                .anyMatch((accessScope) -> accessScopeController.hasScope(accessScope));
    }

}
