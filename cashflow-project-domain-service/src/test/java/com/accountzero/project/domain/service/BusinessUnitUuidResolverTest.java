package com.cashflow.project.domain.service;

import com.cashflow.useraccount.domain.businessunit.BusinessUnit;
import com.cashflow.useraccount.service.api.businessunit.BusinessAccountBusinessUnitService;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * 
 * @since Nov 16, 2016, 7:27:12 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class BusinessUnitUuidResolverTest {

    @Mock
    private BusinessAccountBusinessUnitService businessAccountBusinessUnitService;

    @InjectMocks
    private BusinessUnitUuidResolver businessUnitUuidResolver;

    @Test
    public void verifyBusinessUnitIds() {
        final BusinessUnit businessUnit = mock(BusinessUnit.class);
        when(businessUnit.getUuid()).thenReturn("uuid1");

        final BusinessUnit parentBusinessUnit = mock(BusinessUnit.class);
        when(parentBusinessUnit.getUuid()).thenReturn("uuid2");
        when(businessUnit.getParentUnit()).thenReturn(parentBusinessUnit);

        final BusinessUnit parentParentBusinessUnit = mock(BusinessUnit.class);
        when(parentParentBusinessUnit.getUuid()).thenReturn("uuid3");
        when(parentBusinessUnit.getParentUnit()).thenReturn(parentParentBusinessUnit);

        final List<BusinessUnit<?>> businessUnits
                = ImmutableList.of(businessUnit, parentBusinessUnit, parentParentBusinessUnit);
        when(businessAccountBusinessUnitService.getBusinessUnits()).thenReturn(businessUnits);

        final List<String> businessUnitUUIDs = businessUnitUuidResolver.getBusinessUnitUuidHierarchy("uuid1");
        assertThat(businessUnitUUIDs, hasSize(3));
        assertThat(businessUnitUUIDs, hasItems("uuid1", "uuid2", "uuid3"));
    }

}
