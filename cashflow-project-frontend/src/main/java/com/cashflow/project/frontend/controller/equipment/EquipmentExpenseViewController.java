package com.cashflow.project.frontend.controller.equipment;

import com.cashflow.access.authorization.LoggedInCompanyAccount;
import com.cashflow.access.authorization.service.asynchronous.AsynchronousService;
import com.cashflow.fixedasset.domain.FixedAssetCategories;
import com.cashflow.frontend.support.cacheing.requestcached.RequestCached;
import com.cashflow.frontend.support.httpparameter.HttpParameter;
import com.cashflow.project.api.ownedequipment.OwnedEquipmentExpenseService;
import com.cashflow.project.api.rentedequipment.RentedEquipmentExpenseService;
import com.cashflow.project.domain.project.ownedequipment.EquipmentStatus;
import com.cashflow.project.domain.project.ownedequipment.EquipmentType;
import com.cashflow.project.domain.project.ownedequipment.OwnedEquipmentExpense;
import com.cashflow.project.domain.project.rentedequipment.RentedEquipmentExpense;
import com.cashflow.project.frontend.controller.model.ModelViewScopedController;
import com.cashflow.project.frontend.controller.model.OwnedEquipmentExpenseModel;
import com.cashflow.project.frontend.controller.model.RentedEquipmentExpenseModel;
import com.cashflow.project.frontend.controller.model.SuccessMessageModel;
import com.cashflow.project.frontend.controller.ownedequipment.OwnedEquipmentExpenseModelConverter;
import com.cashflow.project.frontend.controller.rentedequipment.RentedEquipmentExpenseModelConverter;
import com.cashflow.project.translation.ownedequipment.OwnedEquipmentExpenseTranslationService;
import com.cashflow.project.translation.rentedequipment.RentedEquipmentExpenseTranslationService;
import com.cashflow.salestax.api.taxrate.TaxRateRequestContext;
import com.cashflow.salestax.api.taxrate.TaxRateService;
import com.cashflow.salestax.domain.taxrate.TaxRate;
import com.cashflow.useraccount.domain.businessunit.CompanyAccount;
import com.anosym.profiler.Profile;
import com.google.common.base.Throwables;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;

import static com.cashflow.frontend.support.jsfutil.SuccessMessageHelper.addSuccessMessage;
import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_UUID;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since Dec 24, 2016, 10:52:39 AM
 */
@ModelViewScopedController
public class EquipmentExpenseViewController implements Serializable {

    private static final long serialVersionUID = 5446131017693393576L;

    @Inject
    @HttpParameter(SELECTED_PROJECT_UUID)
    private Instance<String> pUUID;

    @Inject
    @LoggedInCompanyAccount
    private Instance<CompanyAccount> companyAccount;

    @Inject
    private TaxRateService taxRateService;

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private OwnedEquipmentExpenseTranslationService ownedEquipmentExpenseTranslationService;

    @Inject
    private RentedEquipmentExpenseTranslationService rentedEquipmentExpenseTranslationService;

    @Inject
    private OwnedEquipmentExpenseService ownedEquipmentExpenseService;

    @Inject
    private RentedEquipmentExpenseService rentedEquipmentExpenseService;

    @Inject
    private OwnedEquipmentExpenseModelConverter ownedEquipmentExpenseModelConverter;

    @Inject
    private RentedEquipmentExpenseModelConverter rentedEquipmentExpenseModelConverter;

    @Getter
    @Setter
    private OwnedEquipmentExpenseModel ownedEquipmentExpenseModel;

    @Getter
    @Setter
    private List<TaxRate> taxRates;

    @Getter
    @Setter
    private String uuid;

    @Getter
    @Setter
    @Nullable
    private EquipmentStatus status;

    @Getter
    @Setter
    private SuccessMessageModel successMessageModel;

    @Getter
    @Setter
    private EquipmentType equipType;

    @Getter
    @Setter
    private RentedEquipmentExpenseModel rentedEquipmentExpenseModel;

    @PostConstruct
    void initViewEquipExpense() {
        checkNotNull(pUUID.get(), "Project is not selected");
    }

    @Profile
    public void viewExpense(@Nonnull final String equipmentUUID,
                            @Nonnull final EquipmentType equipmentType) {

        checkNotNull(equipmentUUID, "equipmentUUID must not be null");
        checkNotNull(equipmentType, "equipmentType must not be null");
        this.setUuid(equipmentUUID);
        this.setEquipType(equipmentType);

        if (equipmentType == EquipmentType.OWNED) {

            final OwnedEquipmentExpense ownedEquipmentExpense = ownedEquipmentExpenseService
                    .getExpenseReport(equipmentUUID);

            this.setStatus(ownedEquipmentExpense.getStatus());
            ownedEquipmentExpenseModel = ownedEquipmentExpenseModelConverter.convertToModel(ownedEquipmentExpense);

            // adding this here because need to open dialog box based on conditions
            RequestContext.getCurrentInstance().execute("PF('view_owned_equipment_expense').show();");
        } else if (equipmentType == EquipmentType.RENTED) {

            final TaxRateRequestContext context = TaxRateRequestContext.builder()
                    .country(companyAccount.get().getAddress().getCountry())
                    .build();

            final Future<RentedEquipmentExpense> rentedEquipmentExpense = asynchronousService
                    .execute(() -> rentedEquipmentExpenseService
                    .getRentedEquipmentExpense(equipmentUUID));

            final Future<List<TaxRate>> taxRequest = asynchronousService
                    .execute(() -> taxRateService.findTaxRates(context));

            try {
                taxRates = taxRequest.get();
                rentedEquipmentExpenseModel = rentedEquipmentExpenseModelConverter
                        .convertToModel(rentedEquipmentExpense.get(), taxRates);

                this.setStatus(rentedEquipmentExpense.get().getStatus());
                // adding this here because need to open dialog box based on conditions
                RequestContext.getCurrentInstance().execute("PF('view_rented_equipment').show();");
            } catch (InterruptedException | ExecutionException ex) {
                throw Throwables.propagate(ex);
            }

        }
    }

    @Nonnull
    @RequestCached
    public FixedAssetCategories[] getAllFaParentCategories() {
        return FixedAssetCategories.values();
    }

    public void approveEquipment() {
        successMessageModel = new SuccessMessageModel();
        if (equipType == EquipmentType.OWNED) {
            final OwnedEquipmentExpense ownedEquipmentExpense = ownedEquipmentExpenseService
                    .getExpenseReport(uuid);
            ownedEquipmentExpense.setStatus(EquipmentStatus.APPROVED);
            ownedEquipmentExpenseService.save(ownedEquipmentExpense);
            successMessageModel.setSuccessMessage(ownedEquipmentExpenseTranslationService
                    .getOwnedEquipmentApprovedSuccessMessage());
            addSuccessMessage(ownedEquipmentExpenseTranslationService.getOwnedEquipmentApprovedSuccessMessage());
        } else if (equipType == EquipmentType.RENTED) {
            final RentedEquipmentExpense rentedEquipmentExpense = rentedEquipmentExpenseService
                    .getRentedEquipmentExpense(uuid);
            rentedEquipmentExpense.setStatus(EquipmentStatus.APPROVED);
            rentedEquipmentExpenseService.save(rentedEquipmentExpense);
            successMessageModel.setSuccessMessage(rentedEquipmentExpenseTranslationService
                    .getRentedEquipApprovedSuccessfully());
            addSuccessMessage(rentedEquipmentExpenseTranslationService.getRentedEquipApprovedSuccessfully());
        }

    }

}
