package com.cashflow.project.frontend.controller.tabview;

import com.cashflow.frontend.support.httpparameter.HttpParameter;
import com.cashflow.project.translation.project.tabtitle.TabTitle;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;

import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_SELECTED_TAB;

/**
 *
 * 
 * @since Nov 17, 2016, 6:23:04 PM
 */
@Named
@RequestScoped
public class TabViewController implements Serializable {

    private static final long serialVersionUID = -3115463369826815738L;

    @Inject
    private TabViewUrlModelBuilder tabViewUrlModelBuilder;

    @Inject
    @HttpParameter(SELECTED_PROJECT_SELECTED_TAB)
    private Instance<String> currentTabParameter;

    @Setter
    @Getter
    private String url;

    @Setter
    @Getter
    private TabTitle currentTab;

    @PostConstruct
    void initCurrentTab() {
        this.currentTab = Optional
                .ofNullable(currentTabParameter.get())
                .map(TabTitle::valueOf)
                .orElse(TabTitle.GENERAL_INFO);
    }

    @Nonnull
    public List<TabViewUrlModel> getTabModels() {
        return tabViewUrlModelBuilder.build();
    }

}
