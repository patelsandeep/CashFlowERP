package com.cashflow.project.translation.timesheet;

import com.cashflow.project.domain.project.timesheet.PTOCategory;
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
 * @since 7 Dec, 2016, 12:19:41 PM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface PTOCategoryTranslationService {

    @Nonnull
    @Info("The translated label for PTO category")
    @DynamicDefault(PTOCategoryDefaultTranslator.class)
    String getPTOCategoryLabel(
            @ConfigParam(name = "ptoCategory") @Nonnull final PTOCategory pTOCategory);

    static final class PTOCategoryDefaultTranslator implements DefaultConfigurator {

        @Override
        public String getDefault(final Method configMethod, final Object[] args) {
            checkNotNull(configMethod, "The configMethod must not be null");
            checkNotNull(args, "The args must not be null");

            return ((PTOCategory) args[0]).toString();
        }

    }

}
