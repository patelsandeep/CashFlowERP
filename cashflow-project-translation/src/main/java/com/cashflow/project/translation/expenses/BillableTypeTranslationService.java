package com.cashflow.project.translation.expenses;

import com.cashflow.project.domain.project.expense.BillableType;
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
 * @since 6 Dec, 2016, 3:00:41 PM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface BillableTypeTranslationService {

    @Nonnull
    @Info("The translated label for Billable Type")
    @DynamicDefault(BillableTypeDefaultTranslator.class)
    String getBillableTypeLabel(
            @ConfigParam(name = "billableType") @Nonnull final BillableType billableType);

    static final class BillableTypeDefaultTranslator implements DefaultConfigurator {

        @Override
        public String getDefault(final Method configMethod, final Object[] args) {
            checkNotNull(configMethod, "The configMethod must not be null");
            checkNotNull(args, "The args must not be null");

            return ((BillableType) args[0]).toString();
        }

    }

}
