package com.cashflow.project.domain.service.accessrole;

import com.cashflow.access.authorization.LoggedInBusinessAccount;
import com.cashflow.accessroles.domain.accessrole.AccessRole;
import com.cashflow.accessroles.service.accessrole.AccessRoleRequestContext;
import com.cashflow.accessroles.service.accessrole.AccessRoleService;
import com.cashflow.accessroles.service.accessrole.NewAccessRole;
import com.cashflow.useraccount.domain.businessaccount.BusinessAccount;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Priority;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.interceptor.Interceptor;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since 7 Dec, 2016, 2:42:07 PM
 */
@Dependent
@Decorator
@Priority(Interceptor.Priority.APPLICATION)
public class AccessRoleServiceDelegate implements AccessRoleService {

    private static final int CONCURRENCY_LEVEL = Runtime.getRuntime().availableProcessors() * 10;

    private static final Cache<AccessRoleKey, List<AccessRole>> ACCESS_ROLES_CACHE = CacheBuilder
            .<AccessRoleKey, List<AccessRole>>newBuilder()
            .concurrencyLevel(CONCURRENCY_LEVEL)
            .maximumSize(1000)
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build();

    @Inject
    @Delegate
    private AccessRoleService accessRoleService;

    @Inject
    @LoggedInBusinessAccount
    private Instance<BusinessAccount> businessAccount;

    @Override
    public List<AccessRole> getAccessRoles(@Nonnull final AccessRoleRequestContext accessRoleRequestContext) {
        checkNotNull(accessRoleRequestContext, "The accessRoleRequestContext must not be null");

        final AccessRoleKey accessRoleKey = new AccessRoleKey(businessAccount.get().getAccountId(),
                                                              accessRoleRequestContext);
        final List<AccessRole> cachedAccessRoles = ACCESS_ROLES_CACHE.getIfPresent(accessRoleKey);
        if (cachedAccessRoles != null) {
            return cachedAccessRoles;
        }

        final List<AccessRole> accessRoles = accessRoleService.getAccessRoles(accessRoleRequestContext);
        ACCESS_ROLES_CACHE.put(accessRoleKey, accessRoles);

        return accessRoles;
    }

    @Override
    public void updateAccessRole(@Nonnull final AccessRole accessRole) {

        accessRoleService.updateAccessRole(accessRole);

        final AccessRoleRequestContext accessRoleRequestContext = AccessRoleRequestContext
                .builder()
                .applicationCode("AZPM")
                .build();

        final AccessRoleKey accessRoleKey = new AccessRoleKey(businessAccount.get().getAccountId(),
                                                              accessRoleRequestContext);

        final List<AccessRole> accessRoles = accessRoleService.getAccessRoles(accessRoleRequestContext);
        ACCESS_ROLES_CACHE.put(accessRoleKey, accessRoles);
    }

    @Override
    public void createAccessRole(@Nonnull final NewAccessRole newAccessRole) {

        accessRoleService.createAccessRole(newAccessRole);
        final AccessRoleRequestContext accessRoleRequestContext = AccessRoleRequestContext
                .builder()
                .applicationCode("AZPM")
                .build();

        final AccessRoleKey accessRoleKey = new AccessRoleKey(businessAccount.get().getAccountId(),
                                                              accessRoleRequestContext);

        final List<AccessRole> accessRoles = accessRoleService.getAccessRoles(accessRoleRequestContext);
        ACCESS_ROLES_CACHE.put(accessRoleKey, accessRoles);
    }

    @EqualsAndHashCode
    @RequiredArgsConstructor
    private static class AccessRoleKey {

        private final String businessAccountId;

        private final AccessRoleRequestContext accessRoleRequestContext;

    }
}
