package com.cashflow.project.translation.equipment.owned;

import com.cashflow.fixedasset.domain.FixedAssetCategories;
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
 * @since 14 Dec, 2016, 11:36:20 AM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface EquipmentCategoryTranslationService {

    @Nonnull
    @Info("The translated label for Equipment Category")
    @DynamicDefault(EquipmentCategoryDefaultTranslator.class)
    String getEquipmentCategoryLabel(
            @ConfigParam(name = "categories") @Nonnull final FixedAssetCategories categories);

    static final class EquipmentCategoryDefaultTranslator implements DefaultConfigurator {

        @Override
        public String getDefault(final Method configMethod, final Object[] args) {
            checkNotNull(configMethod, "The configMethod must not be null");
            checkNotNull(args, "The args must not be null");

            return ((FixedAssetCategories) args[0]).getDisplayCatName();
        }

    }
}
