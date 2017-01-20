package com.cashflow.project.frontend.controller.project;

import javax.annotation.Nonnull;
import lombok.Builder;
import lombok.Getter;

/**
 *
 * 
 */
@Getter
@Builder
public class ProjectTypeEntityModel {

    @Nonnull
    private String entityUUID;

    @Nonnull
    private String entityName;

    @Nonnull
    private ProjectEntityType entityType;
}
