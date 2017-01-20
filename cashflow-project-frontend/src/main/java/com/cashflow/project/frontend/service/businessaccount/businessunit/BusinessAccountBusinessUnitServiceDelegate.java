package com.cashflow.project.frontend.service.businessaccount.businessunit;

import com.cashflow.useraccount.domain.businessunit.BusinessUnit;
import com.cashflow.useraccount.domain.businessuser.Employee;
import com.cashflow.useraccount.service.api.businessunit.BusinessAccountBusinessUnitService;
import com.anosym.cache.Cacheable;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Priority;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.interceptor.Interceptor;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Session based business account business units informations retrieval. The business unit informations are cached for
 * the session.
 *
 * 
 * @since Nov 13, 2016, 12:48:44 PM
 */
@Decorator
@Dependent
@Priority(Interceptor.Priority.APPLICATION)
public abstract class BusinessAccountBusinessUnitServiceDelegate implements BusinessAccountBusinessUnitService, Serializable {

    private static final long serialVersionUID = -1156306378841312973L;

    @Any
    @Inject
    @Delegate
    private BusinessAccountBusinessUnitService businessAccountBusinessUnitService;

    @Override
    @Cacheable(providerId = "inMemoryCacheProvider",
               cacheKeyProviderId = "business-account-context-cachekey-provider",
               lifeTime = 10,
               strategy = Cacheable.CacheLifeTimeStrategy.AFTER_WRITE,
               unit = TimeUnit.MINUTES)
    public List<BusinessUnit<?>> getBusinessUnits() {
        return businessAccountBusinessUnitService.getBusinessUnits();
    }

    @Override
    @Cacheable(providerId = "inMemoryCacheProvider",
               cacheKeyProviderId = "business-account-context-cachekey-provider",
               lifeTime = 10,
               strategy = Cacheable.CacheLifeTimeStrategy.AFTER_WRITE,
               unit = TimeUnit.MINUTES)
    public BusinessUnit<?> getBusinessUnit(@Nonnull final String businessUnitUUID) {
        checkNotNull(businessUnitUUID, "The businessUnitUUID must not be null");

        return businessAccountBusinessUnitService.getBusinessUnit(businessUnitUUID);
    }

    @Override
    @Cacheable(providerId = "inMemoryCacheProvider",
               cacheKeyProviderId = "business-account-context-cachekey-provider",
               lifeTime = 10,
               strategy = Cacheable.CacheLifeTimeStrategy.AFTER_WRITE,
               unit = TimeUnit.MINUTES)
    public List<Employee> getBusinessUnitManagers(@Nonnull final String businessUnitUUID) {
        checkNotNull(businessUnitUUID, "The businessUnitUUID must not be null");

        return businessAccountBusinessUnitService.getBusinessUnitManagers(businessUnitUUID);
    }

}
