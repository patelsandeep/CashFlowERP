package com.cashflow.project.frontend.controller.subcontractor;

import com.cashflow.entitydomains.Address;
import com.cashflow.frontend.support.countrymodel.CountryModel;
import java.io.Serializable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * 
 * @since Dec 29, 2016, 7:15:49 AM
 */
@Getter
@Setter
public class SupplierModel implements Serializable {

    private static final long serialVersionUID = 2809863113693527015L;

    @Nonnull
    private String name;

    @Nonnull
    private String id;

    @Nonnull
    private CountryModel countryModel;

    @Nonnull
    private Address address;

    @Nonnull
    private String contactName;

    @Nonnull
    private Address contactAddress;

    @Nullable
    private String mobileNumber;

    @Nullable
    private String phoneNumber;

}
