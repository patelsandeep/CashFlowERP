package com.cashflow.project.config.projectrole;

import com.cashflow.useraccount.domain.businessuser.professional.ProfessionalType;
import com.cashflow.core.annotations.AppContext;
import com.cashflow.core.annotations.Config;
import com.cashflow.core.annotations.ConfigProject;
import com.cashflow.core.annotations.ConfigRoot;
import com.cashflow.core.annotations.Default;
import javax.annotation.Nonnull;

import static com.cashflow.core.ApplicationMode.DEVELOPMENT;
import static com.cashflow.core.ApplicationMode.LIVE;
import static com.cashflow.core.ApplicationMode.LOCAL;
import static com.cashflow.core.ApplicationMode.TEST;

/**
 *
 * 
 * @since Nov 21, 2016, 6:55:06 PM
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "config-service.properties")
@AppContext(applicationModes = {LOCAL, DEVELOPMENT, TEST, LIVE})
public interface ProjectProfessionalTypeMappingConfigurationService {

    @Nonnull
    @Default("[ACCOUNTANT, BOOKKEEPER, CONSULTANT, SUB_CONTRACTOR]")
    ProfessionalType[] getTypeMappingsForProfessionals();

}
