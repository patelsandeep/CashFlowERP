package com.cashflow.project.translation.expenses;

import com.cashflow.project.domain.project.expense.ExpenseType;
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
 * @since 6 Dec, 2016, 10:39:25 AM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface ExpenseTypeTranslationService {

    @Nonnull
    @Info("The translated label for Expense Type")
    @DynamicDefault(ExpenseTypeDefaultTranslator.class)
    String getExpenseTypeLabel(
            @ConfigParam(name = "expenseType") @Nonnull final ExpenseType expenseType);

    static final class ExpenseTypeDefaultTranslator implements DefaultConfigurator {

        @Override
        public String getDefault(final Method configMethod, final Object[] args) {
            checkNotNull(configMethod, "The configMethod must not be null");
            checkNotNull(args, "The args must not be null");

            return ((ExpenseType) args[0]).toString();
        }

    }
}
