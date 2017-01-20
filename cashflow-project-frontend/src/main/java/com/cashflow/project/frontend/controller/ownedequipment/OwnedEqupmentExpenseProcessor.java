package com.cashflow.project.frontend.controller.ownedequipment;

import com.cashflow.datarepository.service.DataRepository;
import com.cashflow.datarepository.service.DataRepositoryService;
import com.cashflow.project.api.milestone.ProjectMilestoneService;
import com.cashflow.project.api.ownedequipment.OwnedEquipmentExpenseService;
import com.cashflow.project.api.phase.ProjectPhaseService;
import com.cashflow.project.api.task.ProjectTaskService;
import com.cashflow.project.domain.project.Project;
import com.cashflow.project.domain.project.level.ProjectLevel;
import com.cashflow.project.domain.project.ownedequipment.EquipmentStatus;
import com.cashflow.project.domain.project.ownedequipment.OwnedEquipmentDetail;
import com.cashflow.project.domain.project.ownedequipment.OwnedEquipmentExpense;
import com.cashflow.project.frontend.controller.model.EquipmentDetailModel;
import com.cashflow.project.frontend.controller.model.OwnedEquipmentExpenseModel;
import com.anosym.common.Amount;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import static com.cashflow.project.frontend.controller.model.DataRepositoryName.REPOSITORY_NAME;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 *
 * 
 * @since 14 Dec, 2016, 6:25:20 PM
 */
@Dependent
public class OwnedEqupmentExpenseProcessor implements Serializable {

    private static final long serialVersionUID = -6787660616468189137L;

    @Inject
    private OwnedEquipmentExpenseService ownedEquipmentService;

    @Inject
    private ProjectTaskService projectTaskService;

    @Inject
    private ProjectMilestoneService projectMilestoneService;

    @Inject
    private ProjectPhaseService projectPhaseService;

    @Inject
    @DataRepository(REPOSITORY_NAME)
    private DataRepositoryService dataRepositoryService;

    public void saveOwnedEquipment(@Nonnull final OwnedEquipmentExpenseModel ownedEquipmentExpenseModel,
                                   @Nonnull final Project project,
                                   @Nonnull final String action) {

        checkNotNull(ownedEquipmentExpenseModel, "The ownedEquipmentExpenseModel must not be null");
        checkNotNull(project, "The project must not be null");
        OwnedEquipmentExpense ownedEquipment;
        if (isNullOrEmpty(ownedEquipmentExpenseModel.getOwnedEquipmentUUID())) {
            ownedEquipment = new OwnedEquipmentExpense();
        } else {
            ownedEquipment = ownedEquipmentService
                    .getExpenseReport(ownedEquipmentExpenseModel.getOwnedEquipmentUUID());
        }
        ownedEquipment.setEquipmentId(ownedEquipmentExpenseModel.getOwnedEquipmentId());
        ownedEquipment.setProjectLevel(getProjectLevel(ownedEquipmentExpenseModel, project));
        if (action.equals("approve")) {
            ownedEquipment.setStatus(EquipmentStatus.APPROVED);
        } else {
            ownedEquipment.setStatus(EquipmentStatus.SAVED);
        }

        final List<OwnedEquipmentDetail> details = new ArrayList<>();

        ownedEquipmentExpenseModel
                .getEquipmentDetailModels()
                .stream()
                .forEach(p -> details.add(toDomainEquipmentDetails(p, ownedEquipmentExpenseModel, ownedEquipment)));

        ownedEquipment.getOwnedEquipmentDetails().clear();
        ownedEquipment.getOwnedEquipmentDetails().addAll(details);

        ownedEquipmentService.save(ownedEquipment);

    }

    private ProjectLevel getProjectLevel(@Nonnull final OwnedEquipmentExpenseModel ownedEquipmentExpenseModel,
                                         @Nonnull final Project project) {
        if (!isNullOrEmpty(ownedEquipmentExpenseModel.getTask())) {
            return projectTaskService.getTask(ownedEquipmentExpenseModel.getTask());
        } else if (!isNullOrEmpty(ownedEquipmentExpenseModel.getMilestone())) {
            return projectMilestoneService.getMilestone(ownedEquipmentExpenseModel.getMilestone());
        } else if (!isNullOrEmpty(ownedEquipmentExpenseModel.getPhase())) {
            return projectPhaseService.getPhase(ownedEquipmentExpenseModel.getPhase());
        } else {
            return project;

        }

    }

    @Nonnull
    private OwnedEquipmentDetail toDomainEquipmentDetails(@Nonnull final EquipmentDetailModel detailModel,
                                                          @Nonnull final OwnedEquipmentExpenseModel ownedEquipmentExpenseModel,
                                                          @Nullable final OwnedEquipmentExpense equipment) {
        OwnedEquipmentDetail newEquipment = null;
        if (equipment != null) {
            newEquipment = equipment.getOwnedEquipmentDetails()
                    .stream()
                    .filter(
                            p -> p != null && p.getUuid().equals(detailModel.getUuid()))
                    .findFirst()
                    .orElse(new OwnedEquipmentDetail(detailModel.getEquipmentDate(),
                                                     detailModel.getOwnedEquipment().getUuid(),
                                                     new Amount(ownedEquipmentExpenseModel.getCurrency(), detailModel
                                                                .getBillRate()),
                                                     detailModel.getUnit(),
                                                     detailModel.getBillable(),
                                                     detailModel.getEquipmentDocs())
                    );

        }
        detailModel.getEquipmentDocs()
                .stream()
                .filter((url) -> (detailModel.getInputStreams().containsKey(url.getUrl())))
                .forEach((url) -> {
                    dataRepositoryService.set(detailModel.getInputStreams().get(url.getUrl()),
                                              url.getUrl(),
                                              url.getContentType(),
                                              url.getContentSize());
                });
        return newEquipment;
    }
}
