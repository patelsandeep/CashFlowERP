package com.cashflow.project.frontend.controller.menuoption;

import com.cashflow.accessroles.service.authorizationcontext.AccessScopeController;
import com.cashflow.project.config.menuoption.MenuOptionConfigurationService;
import com.cashflow.project.domain.util.menuoption.MenuOption;
import com.cashflow.project.translation.menuoption.MenuOptionTranslationService;
import com.anosym.urlbuilder.RelativeUrlBuilder;
import java.util.List;
import javax.enterprise.inject.Instance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 *
 * 
 * @since Nov 11, 2016, 11:59:05 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class MenuOptionControllerTest {

    private static final String API_KEY = "023fa1c4-a863-11e6-ba32-0ba6236672ac";

    private static final String JAZ_SESSION_ID = "023fa1c4-a863-11e6-ba32-0ba6236672ac";

    private static final String SELECTED_MENU_OPTION = "INVOICES";

    @Mock
    private AccessScopeController accessScopeController;

    @Mock
    private RelativeUrlBuilder relativeUrlBuilder;

    @Mock
    private MenuOptionTranslationService menuOptionTranslationService;

    @Mock
    private MenuOptionConfigurationService menuOptionConfigurationService;

    @Mock
    private Instance<String> selectedMenuOption;

    @Mock
    private Instance<String> apiKey;

    @Mock
    private Instance<String> jazSessionId;

    @InjectMocks
    private MenuOptionController menuOptionController;

    @Before
    public void setUp() {
        when(apiKey.get()).thenReturn(API_KEY);
        when(jazSessionId.get()).thenReturn(JAZ_SESSION_ID);
        when(selectedMenuOption.get()).thenReturn(SELECTED_MENU_OPTION);
        when(menuOptionTranslationService.getMenuOptionLabel(any(MenuOption.class))).then((invocation) -> {
            return invocation.getArgumentAt(0, MenuOption.class).getLabel();
        });
        when(menuOptionConfigurationService.getEnabledOrderedMenus()).thenReturn(MenuOption.values());
        when(relativeUrlBuilder.buildWithPath(anyString(), anyMapOf(String.class, String.class))).then((invocation) -> {
            return invocation.getArgumentAt(0, String.class);
        });
    }

    @Test
    public void verifyMenuModelsWithoutAccessScope() {
        menuOptionController.initMenuOptions();

        final List<MenuOptionModel> menuOptionModels = menuOptionController.getMenuOptions();
        assertThat(menuOptionModels, hasSize(greaterThanOrEqualTo(9)));

        assertThat("There must only be one selected menu option model",
                   menuOptionModels.stream().filter(MenuOptionModel::isSelected).count(), is(1l));
    }

    @Test
    public void verifyMenuModelsWithAccessScope() {
        when(accessScopeController.hasScope("project.reports.view")).thenReturn(true);
        menuOptionController.initMenuOptions();

        final List<MenuOptionModel> menuOptionModels = menuOptionController.getMenuOptions();
        assertThat("Expected menu model with project reports view scope",
                   menuOptionModels, hasSize(greaterThanOrEqualTo(10)));

        assertThat("There must only be one selected menu option model",
                   menuOptionModels.stream().filter(MenuOptionModel::isSelected).count(), is(1l));
    }

}
