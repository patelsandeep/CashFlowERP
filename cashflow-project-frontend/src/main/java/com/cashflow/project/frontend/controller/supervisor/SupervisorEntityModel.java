package com.cashflow.project.frontend.controller.supervisor;

import javax.annotation.Nonnull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 *
 * 
 * @since 21 Nov, 2016, 12:11:42 PM
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class SupervisorEntityModel {

    @Nonnull
    private String supervisorUUID;

    @Nonnull
    private String supervisorName;

    @Nonnull
    private SupervisorEntityType phaseSupervisorEntityType;
}
