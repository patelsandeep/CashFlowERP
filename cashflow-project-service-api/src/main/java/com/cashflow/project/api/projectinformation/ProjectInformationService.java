package com.cashflow.project.api.projectinformation;

import javax.annotation.Nonnull;

/**
 *
 * 
 * @since Nov 12, 2016, 9:59:49 AM
 */
public interface ProjectInformationService {

    @Nonnull
    ProjectInformationResult getProjectInformations(
            @Nonnull final ProjectInformationRequest projectInformationRequestContext);
}
