package com.cashflow.project.frontend.controller.subcontractor;

import com.cashflow.accountpayables.domain.supplier.ContactPerson;
import com.cashflow.accountpayables.domain.supplier.Supplier;
import com.cashflow.accountpayables.service.api.supplier.SupplierService;
import com.cashflow.entitydomains.Address;
import com.cashflow.entitydomains.PhoneNumber;
import com.cashflow.project.translation.subcontractor.SubContractorTranslationService;
import java.io.Serializable;
import javax.annotation.Nonnull;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import static com.cashflow.frontend.support.errormessage.ErrorMessagePreconditions.assertNotNull;

/**
 *
 * 
 * @since Dec 29, 2016, 7:10:00 AM
 */
@Dependent
public class SubContractorProcessor implements Serializable {

    private static final long serialVersionUID = 5444908882365221373L;

    @Inject
    private SupplierService supplierService;

    @Inject
    private SubContractorTranslationService subContractorTranslationService;

    @Nonnull
    public Supplier createSupplier(@Nonnull final SupplierModel supplierModel) {

        validateSupplier(supplierModel);

        final Address cAddress = supplierModel.getContactAddress();

        if (null != supplierModel.getMobileNumber()) {
            final PhoneNumber mn = new PhoneNumber(supplierModel.getCountryModel().getCountry(),
                                                   supplierModel.getMobileNumber());
            cAddress.setMobileNumber(mn);
        }

        if (null != supplierModel.getPhoneNumber()) {
            final PhoneNumber pn = new PhoneNumber(supplierModel.getCountryModel().getCountry(),
                                                   supplierModel.getPhoneNumber());
            cAddress.setPhoneNumber(pn);
        }

        final ContactPerson contactPerson = new ContactPerson(supplierModel.getContactName(),
                                                              cAddress);

        final Supplier supplier = new Supplier(supplierModel.getName(),
                                               supplierModel.getId(),
                                               contactPerson,
                                               Supplier.SupplierStatus.SAVED);

        final Address address = supplierModel.getAddress();

        address.setCountry(supplierModel.getCountryModel().getCountry());

        supplier.setAddress(address);

        supplier.setSubContractor(true);

        supplierService.addOrUpdateSupplier(supplier);

        return supplier;
    }

    private void validateSupplier(@Nonnull final SupplierModel supplierModel) {
        assertNotNull(supplierModel.getName(),
                      subContractorTranslationService.getSubContractorNameRequired());
        assertNotNull(supplierModel.getCountryModel(),
                      subContractorTranslationService.getCountryRequired());
        assertNotNull(supplierModel.getContactName(),
                      subContractorTranslationService.getContactNameRequired());
    }

}
