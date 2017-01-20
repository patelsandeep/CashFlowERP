package com.cashflow.project.translation.equipment.owned;

import com.cashflow.project.domain.project.ownedequipment.EquipmentType;
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
 * @since 14 Dec, 2016, 10:19:13 AM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface EquipmentTypeTranslationService {

    @Nonnull
    @Info("The translated label for Equipment Type")
    @DynamicDefault(EquipmentTypeDefaultTranslator.class)
    String getEquipmentTypeLabel(
            @ConfigParam(name = "equipmentType") @Nonnull final EquipmentType equipmentType);

    static final class EquipmentTypeDefaultTranslator implements DefaultConfigurator {

        @Override
        public String getDefault(final Method configMethod, final Object[] args) {
            checkNotNull(configMethod, "The configMethod must not be null");
            checkNotNull(args, "The args must not be null");

            return ((EquipmentType) args[0]).toString();
        }

    }
}
