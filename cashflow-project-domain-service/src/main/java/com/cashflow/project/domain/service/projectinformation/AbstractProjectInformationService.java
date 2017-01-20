package com.cashflow.project.domain.service.projectinformation;

import com.cashflow.useraccount.domain.businessunit.BusinessUnit;
import com.cashflow.useraccount.service.api.businessunit.BusinessAccountBusinessUnitService;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 *
 * 
 * @since Nov 12, 2016, 1:30:56 PM
 */
public abstract class AbstractProjectInformationService {

    @Inject
    private BusinessAccountBusinessUnitService businessAccountBusinessUnitService;

    @Nonnull
    protected Map<String, BusinessUnit<?>> getBusinessUnits() {
        return businessAccountBusinessUnitService
                .getBusinessUnits()
                .stream()
                .collect(Collectors.toMap(BusinessUnit::getUuid, Function.identity()));
    }
}
