package com.cashflow.project.config.menuoption;

import com.cashflow.project.domain.util.menuoption.MenuOption;
import com.cashflow.core.DefaultConfigurator;
import com.cashflow.core.annotations.Config;
import com.cashflow.core.annotations.ConfigProject;
import com.cashflow.core.annotations.ConfigRoot;
import com.cashflow.core.annotations.DynamicDefault;
import com.cashflow.core.annotations.Info;
import java.lang.reflect.Method;
import java.util.Arrays;
import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since Jan 4, 2017, 5:31:15 AM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "config-service.properties")
public interface MenuOptionConfigurationService {

    @Nonnull
    @DynamicDefault(MenuOptionDefaultConfigurator.class)
    @Info("Returns the sorted list of enabled menus. The order of definition in the config-service determines the order")
    MenuOption[] getEnabledOrderedMenus();

    class MenuOptionDefaultConfigurator implements DefaultConfigurator {

        @Override
        public String getDefault(@Nonnull final Method configMethod, @Nonnull final Object[] args) {
            checkNotNull(configMethod, "The configMethod must not be null");
            checkNotNull(args, "The args must not be null");

            return Arrays.toString(MenuOption.values());
        }

    }
}
