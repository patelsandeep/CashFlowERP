package com.cashflow.project.translation.expenses;

import com.cashflow.project.domain.project.timesheet.ExpenseStatus;
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
 * @since Jan 16, 2017, 2:39:34 PM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface ExpenseStatusTranslationService {

    @Nonnull
    @Info("The translated label for Expense status")
    @DynamicDefault(ExpenseStatusDefaultTranslator.class)
    String getExpenseStatusLabel(
            @ConfigParam(name = "billableType") @Nonnull final ExpenseStatus expenseStatus);

    static final class ExpenseStatusDefaultTranslator implements DefaultConfigurator {

        @Override
        public String getDefault(final Method configMethod, final Object[] args) {
            checkNotNull(configMethod, "The configMethod must not be null");
            checkNotNull(args, "The args must not be null");

            return ((ExpenseStatus) args[0]).toString();
        }

    }

}
