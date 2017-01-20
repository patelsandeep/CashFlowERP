package com.cashflow.project.frontend.controller.tabview;

import com.cashflow.project.translation.project.tabtitle.TabTitle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;

/**
 *
 * 
 * @since Nov 23, 2016, 5:51:43 AM
 */
@Getter
@Builder
public class TabViewUrlModel {

    @Nonnull
    private final TabTitle tabTitle;

    private final boolean selected;

    @Nonnull
    private final String name;

    @Nullable
    private final String templateUrl;
}
