package com.cashflow.project.frontend.controller.accessrole;

import com.cashflow.accessroles.service.authorizationcontext.AccessScopeController;
import com.cashflow.project.config.accessrole.ProjectAccessRoleConfigurationService;
import javax.annotation.Nonnull;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since Nov 12, 2016, 6:53:44 AM
 */
@Specializes
@RequestScoped
public class AccessScopeActivationController extends AccessScopeController {

    @Inject
    private ProjectAccessRoleConfigurationService projectAccessRoleConfigurationService;

    @Override
    public boolean hasScope(@Nonnull final String scopeId) {
        checkNotNull(scopeId, "The scopeId must not be null");

        if (!projectAccessRoleConfigurationService.isAccessRoleCheckEnabled()) {
            return true;
        }

        return super.hasScope(scopeId);
    }

}
