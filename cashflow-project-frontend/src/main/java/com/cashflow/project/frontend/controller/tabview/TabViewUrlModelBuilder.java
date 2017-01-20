package com.cashflow.project.frontend.controller.tabview;

import java.util.List;
import javax.annotation.Nonnull;

/**
 *
 * 
 * @since Nov 23, 2016, 5:57:08 AM
 */
public interface TabViewUrlModelBuilder {

    @Nonnull
    List<TabViewUrlModel> build();
}
