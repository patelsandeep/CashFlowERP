package com.cashflow.project.translation.expenses;

import com.cashflow.project.domain.project.expense.LabourExpenseType;
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
 * @since 5 Dec, 2016, 10:58:23 AM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface LabourTypeTranslationService {

    @Nonnull
    @Info("The translated label for Labour Expense Type")
    @DynamicDefault(LabourExpenseTypeDefaultTranslator.class)
    String getLabourExpenseTypeLabel(
            @ConfigParam(name = "labourExpenseType") @Nonnull final LabourExpenseType labourExpenseType);

    static final class LabourExpenseTypeDefaultTranslator implements DefaultConfigurator {

        @Override
        public String getDefault(final Method configMethod, final Object[] args) {
            checkNotNull(configMethod, "The configMethod must not be null");
            checkNotNull(args, "The args must not be null");

            return ((LabourExpenseType) args[0]).toString();
        }

    }
}
