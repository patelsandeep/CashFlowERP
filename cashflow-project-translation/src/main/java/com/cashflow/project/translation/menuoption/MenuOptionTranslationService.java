package com.cashflow.project.translation.menuoption;

import com.cashflow.project.domain.util.menuoption.MenuOption;
import com.cashflow.core.DefaultConfigurator;
import com.cashflow.core.annotations.Config;
import com.cashflow.core.annotations.ConfigParam;
import com.cashflow.core.annotations.ConfigProject;
import com.cashflow.core.annotations.ConfigRoot;
import com.cashflow.core.annotations.DynamicDefault;
import com.cashflow.core.annotations.Info;
import java.lang.reflect.Method;
import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since Oct 3, 2016, 11:59:20 AM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface MenuOptionTranslationService {

    @Nonnull
    @Info("The translated label for menu options")
    @DynamicDefault(MenuOptionDefaultTranslator.class)
    String getMenuOptionLabel(@ConfigParam(name = "menuOption") @Nonnull final MenuOption menuOption);

    static final class MenuOptionDefaultTranslator implements DefaultConfigurator {

        @Override
        public String getDefault(final Method configMethod, final Object[] args) {
            checkNotNull(configMethod, "The configMethod must not be null");
            checkNotNull(args, "The args must not be null");

            return ((MenuOption) args[0]).getLabel();
        }

    }
}
