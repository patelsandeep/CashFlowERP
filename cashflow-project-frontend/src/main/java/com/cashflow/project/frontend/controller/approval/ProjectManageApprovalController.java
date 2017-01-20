package com.cashflow.project.frontend.controller.approval;

import com.cashflow.frontend.support.httpparameter.HttpParameter;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import static com.cashflow.project.frontend.controller.accessrole.HttpParameterKeys.SELECTED_PROJECT_UUID;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 *
 * 
 * @since Nov 21, 2016, 7:28:46 PM
 */
@Named
@ViewScoped
public class ProjectManageApprovalController implements Serializable {

    private static final long serialVersionUID = 2712229392059839327L;

    @Inject
    @HttpParameter(SELECTED_PROJECT_UUID)
    private Instance<String> pUUID;

    @PostConstruct
    void initApproval() {
        if (!isNullOrEmpty(pUUID.get())) {

        }
    }

}
