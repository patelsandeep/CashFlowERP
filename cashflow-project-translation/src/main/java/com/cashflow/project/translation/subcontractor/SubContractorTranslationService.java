package com.cashflow.project.translation.subcontractor;

import com.cashflow.core.annotations.Config;
import com.cashflow.core.annotations.ConfigProject;
import com.cashflow.core.annotations.ConfigRoot;
import com.cashflow.core.annotations.Default;
import com.cashflow.core.annotations.Info;
import javax.annotation.Nonnull;

/**
 *
 * 
 * @since Dec 29, 2016, 5:34:35 AM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "translation-service.properties")
public interface SubContractorTranslationService {

    @Nonnull
    @Default("Add New Sub-Contractor")
    @Info("Translated Header Label for add new Sub-contractor")
    String getAddNewSubContractor();

    @Nonnull
    @Default("Sub-Contractor ID")
    @Info("Translated Label for Sub-Contractor ID")
    String getSubContractorID();

    @Nonnull
    @Default("Sub-Contractor Name")
    @Info("Translated Label for Sub-Contractor Name")
    String getSubContractorName();

    @Nonnull
    @Default("Please enter Sub-Contractor Name")
    @Info("Translated Label for Please enter Sub-Contractor Name")
    String getSubContractorNameRequired();

    @Nonnull
    @Default("Country")
    @Info("Translated Label for Country")
    String getCountry();

    @Nonnull
    @Default("Please select Country")
    @Info("Translated Label for Please select Country")
    String getCountryRequired();

    @Nonnull
    @Default("State/Province")
    @Info("Translated Label for State/Province")
    String getStateOrProvince();

    @Nonnull
    @Default("Zip/Postal Code")
    @Info("Translated Label for Zip/Postal Code")
    String getZipOrPostalCode();

    @Nonnull
    @Default("City")
    @Info("Translated Label for City")
    String getCity();

    @Nonnull
    @Default("Address")
    @Info("Translated Label for Address")
    String getAddress();

    @Nonnull
    @Default("Contact Name")
    @Info("Translated Label for Contact Name")
    String getContactName();

    @Nonnull
    @Default("Please enter Contact Name")
    @Info("Translated Label for Contact Name required")
    String getContactNameRequired();

    @Nonnull
    @Default("Email")
    @Info("Translated Label for Email")
    String getEmail();

    @Nonnull
    @Default("Phone")
    @Info("Translated Label for Phone")
    String getPhone();

    @Nonnull
    @Default("Mobile")
    @Info("Translated Label for Mobile")
    String getMobile();

    @Nonnull
    @Default("Cancel")
    @Info("Translated Label for Cancel")
    String getCancel();

    @Nonnull
    @Default("Save")
    @Info("Translated Label for Save")
    String getSave();

}
