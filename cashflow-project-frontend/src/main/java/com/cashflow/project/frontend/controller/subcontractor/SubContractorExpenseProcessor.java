package com.cashflow.project.frontend.controller.subcontractor;

import com.cashflow.datarepository.service.DataRepository;
import com.cashflow.datarepository.service.DataRepositoryService;
import com.cashflow.project.api.expense.subcontractor.SubContractorExpenseService;
import com.cashflow.project.api.milestone.ProjectMilestoneService;
import com.cashflow.project.api.phase.ProjectPhaseService;
import com.cashflow.project.api.task.ProjectTaskService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.expense.subcontractor.Categorization;
import com.cashflow.project.domain.project.expense.subcontractor.SubContractorExpense;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.timesheet.ExpenseStatus;
import com.cashflow.project.frontend.controller.model.SubContractorExpenseModel;
import com.anosym.common.Amount;
import com.anosym.common.Currency;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import static com.cashflow.project.frontend.controller.model.DataRepositoryName.REPOSITORY_NAME;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 *
 * 
 * @since Dec 29, 2016, 4:14:53 AM
 */
@Dependent
public class SubContractorExpenseProcessor implements Serializable {

    private static final long serialVersionUID = 7326479632542812761L;

    @Inject
    private SubContractorExpenseService subContractorExpenseService;

    @Inject
    private ProjectTaskService projectTaskService;

    @Inject
    private ProjectMilestoneService projectMilestoneService;

    @Inject
    private ProjectPhaseService projectPhaseService;

    @Inject
    @DataRepository(REPOSITORY_NAME)
    private DataRepositoryService dataRepositoryService;

    public void process(@Nonnull final SubContractorExpenseModel subContractorExpenseModel,
                        @Nonnull final Project project) {
        SubContractorExpense expense;
        Categorization categorization;

        if (isNullOrEmpty(subContractorExpenseModel.getSubContractorExpenseUUID())) {
            expense = new SubContractorExpense();
            categorization = new Categorization();
        } else {
            expense = subContractorExpenseService.get(subContractorExpenseModel.getSubContractorExpenseUUID());
            categorization = expense.getCategorization();
        }

        expense.setExpenseDate(subContractorExpenseModel.getDate());
        expense.setExpenseScheduleId(subContractorExpenseModel.getSubContractorExpenseID());
        expense.setProjectLevel(getProjectLevel(subContractorExpenseModel, project));

        expense.setSupplierUUID(subContractorExpenseModel.getSupplierUUID());
        expense.setSubContractorService(subContractorExpenseModel.getSubContractorService());
        expense.setDescription(subContractorExpenseModel.getDescription());
        expense.setInvoiceNumber(subContractorExpenseModel.getInvoiceNumber());
        expense.setInvoiceDueDate(subContractorExpenseModel.getInvoiceDueDate());
        expense.setInvoiceAmount(toAmount(subContractorExpenseModel.getCurrency(),
                                          subContractorExpenseModel.getInvoiceAmount()));
        expense.setTaxId(subContractorExpenseModel.getTaxId());
        expense.setTaxId2(subContractorExpenseModel.getTaxId2());

        categorization.setEquipmentExpense(toAmount(subContractorExpenseModel.getCurrency(),
                                                    subContractorExpenseModel.getEquipmentValue()));
        categorization.setEquipmentMarkUp(subContractorExpenseModel.getEquipmentMarkUpValue());
        categorization.setEquipmentNonBillable(toAmount(subContractorExpenseModel.getCurrency(),
                                                        subContractorExpenseModel.getEquipmentNonBillableValue()));

        categorization.setLabourExpense(toAmount(subContractorExpenseModel.getCurrency(),
                                                 subContractorExpenseModel.getLabourValue()));
        categorization.setLabourMarkUp(subContractorExpenseModel.getLabourMarkUpValue());
        categorization.setLabourNonBillable(toAmount(subContractorExpenseModel.getCurrency(),
                                                     subContractorExpenseModel.getLaborNonBillableValue()));

        categorization.setMaterialExpense(toAmount(subContractorExpenseModel.getCurrency(),
                                                   subContractorExpenseModel.getMaterialValue()));
        categorization.setMaterialMarkUp(subContractorExpenseModel.getMaterialMarkUpValue());
        categorization.setMaterialNonBillable(toAmount(subContractorExpenseModel.getCurrency(),
                                                       subContractorExpenseModel.getMaterialNonBillableValue()));

        expense.setCategorization(categorization);

        expense.setNotes(subContractorExpenseModel.getNotes());
        expense.setExpenseDocuments(new HashSet<>(subContractorExpenseModel.getExpenseDocs()));
        if (subContractorExpenseModel.getAction().equals("save")) {
            expense.setExpenseStatus(ExpenseStatus.SAVED);
        } else if (subContractorExpenseModel.getAction().equals("approve")) {
            expense.setExpenseStatus(ExpenseStatus.APPROVED);
        }
        subContractorExpenseService.create(expense);

        uploadToAWS(subContractorExpenseModel);

    }

    private ProjectLevel getProjectLevel(@Nonnull final SubContractorExpenseModel expenseReportModel,
                                         @Nonnull final Project project) {
        if (!isNullOrEmpty(expenseReportModel.getTask())) {
            return projectTaskService.getTask(expenseReportModel.getTask());
        } else if (!isNullOrEmpty(expenseReportModel.getMilestone())) {
            return projectMilestoneService.getMilestone(expenseReportModel.getMilestone());
        } else if (!isNullOrEmpty(expenseReportModel.getPhase())) {
            return projectPhaseService.getPhase(expenseReportModel.getPhase());
        } else {
            return project;

        }

    }

    @Nonnull
    private Amount toAmount(@Nonnull final Currency currency, @Nullable final BigDecimal value) {
        BigDecimal zeroValue = BigDecimal.ZERO;
        if (null == value) {
            return new Amount(currency, zeroValue);
        } else {
            return new Amount(currency, value);
        }
    }

    private void uploadToAWS(@Nonnull final SubContractorExpenseModel expenseModel) {
        expenseModel.getExpenseDocs()
                .stream()
                .filter((url) -> (expenseModel.getInputStreams().containsKey(url.getUrl())))
                .forEach((url) -> {
                    dataRepositoryService.set(expenseModel.getInputStreams().get(url.getUrl()),
                                              url.getUrl(),
                                              url.getContentType(),
                                              url.getContentSize());
                });
    }

}
