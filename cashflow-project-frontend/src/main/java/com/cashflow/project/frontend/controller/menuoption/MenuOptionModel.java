package com.cashflow.project.frontend.controller.menuoption;

import javax.annotation.Nonnull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 *
 * 
 * @since Oct 3, 2016, 11:50:08 AM
 */
@Getter
@ToString
@Builder(toBuilder = true)
public class MenuOptionModel {

    @Nonnull
    private String menuLink;

    @Nonnull
    private String label;

    @Nonnull
    private boolean selected;
}
