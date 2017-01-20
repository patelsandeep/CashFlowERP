package com.cashflow.project.frontend.controller.rentedequipment;

import com.cashflow.project.api.milestone.ProjectMilestoneService;
import com.cashflow.project.api.phase.ProjectPhaseService;
import com.cashflow.project.api.rentedequipment.RentedEquipmentExpenseService;
import com.cashflow.project.api.task.ProjectTaskService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.ownedequipment.EquipmentStatus;
import com.cashflow.project.domain.project.rentedequipment.RentedEquipmentExpense;
import com.cashflow.project.frontend.controller.model.RentedEquipmentExpenseModel;
import com.anosym.common.Amount;
import java.io.Serializable;
import javax.annotation.Nonnull;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 *
 * 
 * @since 19 Dec, 2016, 6:02:10 PM
 */
@Dependent
public class RentedEquipmentExpenseProcessor implements Serializable {

    private static final long serialVersionUID = 2894995602896601699L;

    @Inject
    private RentedEquipmentExpenseService rentedEquipmentService;

    @Inject
    private ProjectTaskService projectTaskService;

    @Inject
    private ProjectMilestoneService projectMilestoneService;

    @Inject
    private ProjectPhaseService projectPhaseService;

    public void saveRentedEquipment(@Nonnull final RentedEquipmentExpenseModel rentedEquipmentExpenseModel,
                                    @Nonnull final Project project,
                                    @Nonnull final String action) {

        checkNotNull(rentedEquipmentExpenseModel, "The rentedEquipmentExpenseModel must not be null");
        checkNotNull(project, "The project must not be null");

        RentedEquipmentExpense rentedEquipment;
        if (isNullOrEmpty(rentedEquipmentExpenseModel.getRentedEquipmentUUID())) {
            rentedEquipment = new RentedEquipmentExpense();
        } else {
            rentedEquipment = rentedEquipmentService
                    .getRentedEquipmentExpense(rentedEquipmentExpenseModel.getRentedEquipmentUUID());
        }
        rentedEquipment.setProjectLevel(getProjectLevel(rentedEquipmentExpenseModel, project));
        rentedEquipment.setEquipmentDate(rentedEquipmentExpenseModel.getDate());
        rentedEquipment.setEquipmentId(rentedEquipmentExpenseModel.getRentedEquipmentId());
        rentedEquipment.setSupplierUUID(rentedEquipmentExpenseModel.getSupplier());
        rentedEquipment.setEquipmentName(rentedEquipmentExpenseModel.getEquipmentName());
        rentedEquipment.setSerialNumber(rentedEquipmentExpenseModel.getSerialNumber());
        rentedEquipment.setInvoiceNumber(rentedEquipmentExpenseModel.getInvoiceNumber());
        rentedEquipment.setInvoiceDueDate(rentedEquipmentExpenseModel.getInvocieDueDate());
        rentedEquipment.setSupplierInvoiceAmount(new Amount(rentedEquipmentExpenseModel.getCurrency(),
                                                            rentedEquipmentExpenseModel
                                                                    .getSupplierInvoiceAmount()));
        rentedEquipment.setTaxId(rentedEquipmentExpenseModel.getTaxId());
        rentedEquipment.setTaxId2(rentedEquipmentExpenseModel.getTaxId2());
        rentedEquipment.setBillUnit(rentedEquipmentExpenseModel.getBillingUnit());
        rentedEquipment.setCostRate(new Amount(rentedEquipmentExpenseModel.getCurrency(), rentedEquipmentExpenseModel
                                               .getCostRate()));
        rentedEquipment.setBillableQty(rentedEquipmentExpenseModel.getBillableQty());
        rentedEquipment.setNonBillableQty(rentedEquipmentExpenseModel.getNonBillableQty());
        rentedEquipment.setMarkUp(rentedEquipmentExpenseModel.getMarkUpValue());
        rentedEquipment.setMarkUpMethod(rentedEquipmentExpenseModel.getMarkUpMethod());
        rentedEquipment.setNotes(rentedEquipmentExpenseModel.getNotes());
        rentedEquipment.setEquipmentDocuments(rentedEquipmentExpenseModel.getRentedEquipmentDocs());
        if (action.equals("approve")) {
            rentedEquipment.setStatus(EquipmentStatus.APPROVED);
        } else {
            rentedEquipment.setStatus(EquipmentStatus.SAVED);
        }

        rentedEquipmentService.save(rentedEquipment);
    }

    private ProjectLevel getProjectLevel(@Nonnull final RentedEquipmentExpenseModel rentedEquipmentExpenseModel,
                                         @Nonnull final Project project) {
        if (!isNullOrEmpty(rentedEquipmentExpenseModel.getTask())) {
            return projectTaskService.getTask(rentedEquipmentExpenseModel.getTask());
        } else if (!isNullOrEmpty(rentedEquipmentExpenseModel.getMilestone())) {
            return projectMilestoneService.getMilestone(rentedEquipmentExpenseModel.getMilestone());
        } else if (!isNullOrEmpty(rentedEquipmentExpenseModel.getPhase())) {
            return projectPhaseService.getPhase(rentedEquipmentExpenseModel.getPhase());
        } else {
            return project;

        }

    }

}
