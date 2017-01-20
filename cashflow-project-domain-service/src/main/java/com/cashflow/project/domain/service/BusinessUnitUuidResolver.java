package com.cashflow.project.domain.service;

import com.cashflow.project.domain.service.projectinformation.AbstractProjectInformationService;
import com.cashflow.useraccount.domain.businessunit.BusinessUnit;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since Nov 16, 2016, 7:22:32 PM
 */
@ApplicationScoped
public class BusinessUnitUuidResolver extends AbstractProjectInformationService {

    @Nonnull
    public List<String> getBusinessUnitUuidHierarchy(@Nonnull final String businessUnitUUID) {
        checkNotNull(businessUnitUUID, "The businessUnitUUID must not be null");

        final BusinessUnit<?> businessUnit = Optional
                .ofNullable(getBusinessUnits().get(businessUnitUUID))
                .orElseThrow(() -> new IllegalArgumentException("No Business Unit Defined for UUID"));
        return getBusinessUnitsUUID(businessUnit);
    }

    @Nonnull
    private List<String> getBusinessUnitsUUID(@Nonnull final BusinessUnit<?> businessUnit) {
        final ImmutableList.Builder<String> builder = ImmutableList.builder();
        builder.add(businessUnit.getUuid());

        final BusinessUnit<?> parentBusinessUnit = businessUnit.getParentUnit();
        if (parentBusinessUnit != null) {
            builder.addAll(getBusinessUnitsUUID(parentBusinessUnit));
        }

        return builder.build();
    }
}
