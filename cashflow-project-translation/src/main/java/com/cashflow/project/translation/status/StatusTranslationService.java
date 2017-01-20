package com.cashflow.project.translation.status;

import com.cashflow.project.domain.project.ProjectStatus;
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
 * @since 30 Nov, 2016, 12:48:34 PM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface StatusTranslationService {

    @Nonnull
    @Info("The translated label for Status")
    @DynamicDefault(StatusDefaultTranslator.class)
    String getStatusLabel(
            @ConfigParam(name = "Status") @Nonnull final ProjectStatus status);

    static final class StatusDefaultTranslator implements DefaultConfigurator {

        @Override
        public String getDefault(final Method configMethod, final Object[] args) {
            checkNotNull(configMethod, "The configMethod must not be null");
            checkNotNull(args, "The args must not be null");

            return ((ProjectStatus) args[0]).toString();
        }

    }

}
