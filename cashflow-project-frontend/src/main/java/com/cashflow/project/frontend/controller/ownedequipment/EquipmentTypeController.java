package com.cashflow.project.frontend.controller.ownedequipment;

import com.cashflow.frontend.support.httpparameter.HttpParameter;
import com.cashflow.project.api.progress.ProjectLevelProgressService;
import com.cashflow.project.domain.project.ProjectStatus;
import com.cashflow.project.domain.project.level.ProjectLevelProgress;
import com.cashflow.project.domain.project.ownedequipment.EquipmentType;
import com.cashflow.project.frontend.controller.model.ModelViewScopedController;
import com.cashflow.project.translation.equipment.owned.EquipmentTypeTranslationService;
import com.cashflow.project.translation.ownedequipment.OwnedEquipmentExpenseTranslationService;
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

import static com.cashflow.frontend.support.errormessage.ErrorMessagePreconditions.assertState;
import static com.cashflow.frontend.support.jsfutil.SelectItemHelper.getSelectItems;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_UUID;

/**
 *
 * 
 * @since 14 Dec, 2016, 10:11:59 AM
 */
@ModelViewScopedController
public class EquipmentTypeController implements Serializable {

    private static final long serialVersionUID = -2588668406939321176L;

    @Inject
    private OwnedEquipmentExpenseTranslationService ownedEquipmentExpenseTranslationService;

    @Inject
    private EquipmentTypeTranslationService equipmentTypeTranslationService;

    @Inject
    private ProjectLevelProgressService progressService;

    @Getter
    @Setter
    private EquipmentType equipmentType;

    @Inject
    @HttpParameter(SELECTED_PROJECT_UUID)
    private Instance<String> pUUID;

    @Nonnull
    public SelectItem[] getEquipmentTypes() {
        return getSelectItems(EquipmentType.values(),
                              true,
                              ownedEquipmentExpenseTranslationService.getEquipmentTypeLabel(),
                              equipmentTypeTranslationService::getEquipmentTypeLabel);
    }

    public void checkApprovedProject() {
        if (null == pUUID.get()) {
            return;
        }
        final List<String> projectUUIDs = ImmutableList.of(pUUID.get());
        final List<ProjectLevelProgress> progress = progressService.getProjectLevelProgresss(projectUUIDs);
        final Optional<ProjectLevelProgress> projectProgress = progress.stream().findFirst();
        final boolean isActive = projectProgress.get().getProjectStatus() == ProjectStatus.APPROVED;
        // currently onsuccess is not working while we validate so for testing purpose we have added this here
        if (!isActive) {
            RequestContext.getCurrentInstance().execute("PF('owned_equip_validate_message').show();");
        }
        assertState(isActive, ownedEquipmentExpenseTranslationService.getProjectMustApprovedLabel());

        RequestContext.getCurrentInstance().execute("PF('equipment_type').show();");

    }
}
