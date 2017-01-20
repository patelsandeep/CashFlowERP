package com.cashflow.project.frontend.controller.expenses;

import com.cashflow.frontend.support.httpparameter.HttpParameter;
import com.cashflow.project.api.progress.ProjectLevelProgressService;
import com.cashflow.project.domain.project.ProjectStatus;
import com.cashflow.project.domain.project.expense.LabourExpenseType;
import com.cashflow.project.domain.project.level.ProjectLevelProgress;
import com.cashflow.project.frontend.controller.model.ModelViewScopedController;
import com.cashflow.project.translation.expenses.ExpenseTranslationService;
import com.cashflow.project.translation.expenses.LabourTypeTranslationService;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.enterprise.inject.Instance;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;

import static com.cashflow.frontend.support.jsfutil.SelectItemHelper.getSelectItems;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_UUID;

/**
 *
 * 
 * @since 6 Dec, 2016, 12:01:26 PM
 */
@ModelViewScopedController
public class ExpenseLabourTypeController implements Serializable {

    private static final long serialVersionUID = 8907862417598528593L;

    @Inject
    private LabourTypeTranslationService labourTypeTranslationService;

    @Inject
    private ExpenseTranslationService expenseTranslationService;

    @Inject
    private ProjectLevelProgressService progressService;

    @Getter
    @Setter
    private LabourExpenseType labourExpenseType;

    @Inject
    @HttpParameter(SELECTED_PROJECT_UUID)
    private Instance<String> pUUID;

    @Nonnull
    public SelectItem[] getLabourExpenseTypes() {
        return getSelectItems(LabourExpenseType.values(),
                              true,
                              expenseTranslationService.getLaborExpenseTypeLabel(),
                              labourTypeTranslationService::getLabourExpenseTypeLabel);
    }

    public void checkApprovedProject() {
        if (null == pUUID.get()) {
            return;
        }
        final List<String> projectUUIDs = ImmutableList.of(pUUID.get());
        final List<ProjectLevelProgress> progress = progressService.getProjectLevelProgresss(projectUUIDs);
        final Optional<ProjectLevelProgress> projectProgress = progress.stream().findFirst();
        final boolean isActive = projectProgress.get().getProjectStatus() == ProjectStatus.APPROVED;
        if (!isActive) {
            RequestContext.getCurrentInstance().execute("PF('validation_message').show();");
        } else {
            // currently onsuccess is not working while we validate so for testing purpose we have added this here
            RequestContext.getCurrentInstance().execute("PF('expense_type').show();");
        }

    }
}
