package com.cashflow.project.translation.equipment.rented;

import com.cashflow.project.domain.project.rentedequipment.BillUnit;
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
 * @since 19 Dec, 2016, 4:22:31 PM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface BillUnitTranslationService {

    @Nonnull
    @Info("The translated label for Bill Unit")
    @DynamicDefault(BillUnitDefaultTranslator.class)
    String getBillUnitLabel(
            @ConfigParam(name = "billUnit") @Nonnull final BillUnit billUnit);

    static final class BillUnitDefaultTranslator implements DefaultConfigurator {

        @Override
        public String getDefault(final Method configMethod, final Object[] args) {
            checkNotNull(configMethod, "The configMethod must not be null");
            checkNotNull(args, "The args must not be null");

            return ((BillUnit) args[0]).toString();
        }

    }
}
