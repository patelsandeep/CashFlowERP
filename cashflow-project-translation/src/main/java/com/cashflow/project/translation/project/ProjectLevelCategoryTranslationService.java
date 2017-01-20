package com.cashflow.project.translation.project;

import com.cashflow.project.domain.project.level.ProjectLevelCategory;
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
 * @since Jan 13, 2017, 10:24:45 AM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface ProjectLevelCategoryTranslationService {

    @Nonnull
    @Info("The translated label for markUpMethod")
    @DynamicDefault(ProjectLevelCategoryDefaultTranslator.class)
    String getProjectLevelCategoryLabel(
            @ConfigParam(name = "markUpMethod") @Nonnull final ProjectLevelCategory projectLevelCategory);

    static final class ProjectLevelCategoryDefaultTranslator implements DefaultConfigurator {

        @Override
        public String getDefault(final Method configMethod, final Object[] args) {
            checkNotNull(configMethod, "The configMethod must not be null");
            checkNotNull(args, "The args must not be null");

            return ((ProjectLevelCategory) args[0]).toString();
        }

    }

}
