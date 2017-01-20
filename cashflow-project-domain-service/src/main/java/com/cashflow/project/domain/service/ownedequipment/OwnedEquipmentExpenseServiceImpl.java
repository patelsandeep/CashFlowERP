package com.cashflow.project.domain.service.ownedequipment;

import com.cashflow.access.authorization.LoggedInBusinessAccount;
import com.cashflow.backend.businessaccount.authorization.RequiresBusinessAccount;
import com.cashflow.project.api.ownedequipment.OwnedEquipmentExpenseService;
import com.cashflow.project.domain.facade.ownedequipment.OwnedEquipmentExpenseFacade;
import com.cashflow.project.domain.project.ownedequipment.OwnedEquipmentExpense;
import com.cashflow.useraccount.domain.businessaccount.BusinessAccount;
import javax.annotation.Nonnull;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since 12 Dec, 2016, 6:38:46 PM
 */
@Stateless
@Dependent
@RequiresBusinessAccount
@Local(OwnedEquipmentExpenseService.class)
public class OwnedEquipmentExpenseServiceImpl implements OwnedEquipmentExpenseService {

    @EJB
    private OwnedEquipmentExpenseFacade equipmentFacade;

    @Inject
    @LoggedInBusinessAccount
    private Instance<BusinessAccount> businessAccount;

    @Override
    public void save(@Nonnull final OwnedEquipmentExpense ownedEquipment) {
        checkNotNull(ownedEquipment, "The ownedEquipment must not be null");
        equipmentFacade.edit(ownedEquipment);
    }

    @Override
    public OwnedEquipmentExpense getExpenseReport(@Nonnull final String ownedEquipmentIdOrUUID) {
        checkNotNull(ownedEquipmentIdOrUUID, "The ownedEquipmentIdOrUUID must not be null");

        return equipmentFacade.findByOwnedEquipmentIdOrUUID(ownedEquipmentIdOrUUID,
                                                            businessAccount.get().getAccountId());
    }

}
