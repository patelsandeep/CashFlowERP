package com.cashflow.project.translation.people;

import com.cashflow.project.domain.project.personnel.TeamMemberCategory;
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
public interface TeamMemberCategoryTranslationService {

    @Nonnull
    @Info("The translated label for team member category")
    @DynamicDefault(TeamMemberCategoryDefaultTranslator.class)
    String getTeamMemberCategoryLabel(
            @ConfigParam(name = "teamMemberCategory") @Nonnull final TeamMemberCategory teamMemberCategory);

    static final class TeamMemberCategoryDefaultTranslator implements DefaultConfigurator {

        @Override
        public String getDefault(final Method configMethod, final Object[] args) {
            checkNotNull(configMethod, "The configMethod must not be null");
            checkNotNull(args, "The args must not be null");

            return ((TeamMemberCategory) args[0]).toString();
        }

    }
}
