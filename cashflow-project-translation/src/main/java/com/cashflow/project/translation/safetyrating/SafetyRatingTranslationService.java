package com.cashflow.project.translation.safetyrating;

import com.cashflow.project.domain.project.level.SafetyRating;
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
 * @since 30 Nov, 2016, 12:45:17 PM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface SafetyRatingTranslationService {

    @Nonnull
    @Info("The translated label for SafetyRating")
    @DynamicDefault(SafetyRatingDefaultTranslator.class)
    String getSafetyRatingLabel(
            @ConfigParam(name = "safetyRating") @Nonnull final SafetyRating safetyRating);

    static final class SafetyRatingDefaultTranslator implements DefaultConfigurator {

        @Override
        public String getDefault(final Method configMethod, final Object[] args) {
            checkNotNull(configMethod, "The configMethod must not be null");
            checkNotNull(args, "The args must not be null");

            return ((SafetyRating) args[0]).toString();
        }

    }
}
