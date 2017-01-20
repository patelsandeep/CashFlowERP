package com.cashflow.project.translation.project.tabtitle;

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
import static com.google.common.base.Preconditions.checkState;

/**
 *
 * 
 * @since Nov 23, 2016, 7:22:58 AM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface ProjectTabTitleTranslationService {

    @Nonnull
    @DynamicDefault(TabTitleDefaultTranslator.class)
    @Info("Translated text for project view/edit tab title")
    String getTitle(@ConfigParam(name = "tabTitle") @Nonnull final TabTitle tabTitle);

    class TabTitleDefaultTranslator implements DefaultConfigurator {

        @Override
        public String getDefault(@Nonnull final Method configMethod, @Nonnull final Object[] args) {
            checkNotNull(configMethod, "The configMethod must not be null");
            checkNotNull(args, "The args must not be null");
            checkState(args.length == 1 && args[0] instanceof TabTitle, "Expected single argument of type TabTitle");

            return ((TabTitle) args[0]).getName();
        }

    }

}
