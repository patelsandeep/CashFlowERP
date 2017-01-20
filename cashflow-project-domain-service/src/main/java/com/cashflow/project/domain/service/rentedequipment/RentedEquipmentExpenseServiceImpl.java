package com.cashflow.project.domain.service.rentedequipment;

import com.cashflow.access.authorization.LoggedInBusinessAccount;
import com.cashflow.backend.businessaccount.authorization.RequiresBusinessAccount;
import com.cashflow.project.api.rentedequipment.RentedEquipmentExpenseService;
import com.cashflow.project.domain.facade.rentedequipment.RentedEquipmentExpenseContextFacade;
import com.cashflow.project.domain.facade.rentedequipment.RentedEquipmentExpenseFacade;
import com.cashflow.project.domain.project.rentedequipment.RentedEquipment;
import com.cashflow.project.domain.project.rentedequipment.RentedEquipmentExpense;
import com.cashflow.useraccount.domain.businessaccount.BusinessAccount;
import java.util.List;
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
 * @since 16 Dec, 2016, 7:23:15 PM
 */
@Stateless
@Dependent
@RequiresBusinessAccount
@Local(RentedEquipmentExpenseService.class)
public class RentedEquipmentExpenseServiceImpl implements RentedEquipmentExpenseService {

    @EJB
    private RentedEquipmentExpenseFacade equipmentFacade;

    @EJB
    private RentedEquipmentExpenseContextFacade equipmentContextFacade;

    @Inject
    @LoggedInBusinessAccount
    private Instance<BusinessAccount> businessAccount;

    @Override
    public void save(@Nonnull final RentedEquipmentExpense rentedEquipment) {
        checkNotNull(rentedEquipment, "The rentedEquipment must not be null");
        equipmentFacade.edit(rentedEquipment);
    }

    @Override
    public RentedEquipmentExpense getRentedEquipmentExpense(@Nonnull final String rentedEquipmentIdOrUUID) {
        checkNotNull(rentedEquipmentIdOrUUID, "The phaseIdOrUUID must not be null");
        return equipmentFacade
                .findByRentedEquipmentIdOrUUID(rentedEquipmentIdOrUUID,
                                               businessAccount.get().getAccountId());
    }

    @Override
    public List<RentedEquipmentExpense> getRentedEquipmentExpenses(@Nonnull final RentedEquipment rentedEquipmentContext) {
        checkNotNull(rentedEquipmentContext, "The rentedEquipmentContext must not be null");
        return equipmentContextFacade.find(businessAccount.get().getAccountId(),
                                           rentedEquipmentContext);
    }

}
