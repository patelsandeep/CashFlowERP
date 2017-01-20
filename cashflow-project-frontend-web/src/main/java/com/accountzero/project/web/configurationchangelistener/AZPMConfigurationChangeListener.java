package com.cashflow.project.web.configurationchangelistener;

import com.cashflow.core.annotations.ConfigProject;
import com.cashflow.core.internal.service.changelistener.AbstractConfigurationChangeListener;
import com.cashflow.core.internal.service.changelistener.ConfigurationChangeListener;
import javax.ejb.Stateless;
import javax.jws.WebService;

@Stateless
@ConfigProject("AZPM")
@WebService(portName = "ConfigurationChangeListenerProvider",
            serviceName = "ConfigurationChangeListenerService",
            targetNamespace = "http://cashflow.com/config/service/changelisteners",
            endpointInterface = "com.cashflow.core.internal.service.changelistener.ConfigurationChangeListener")
public class AZPMConfigurationChangeListener extends AbstractConfigurationChangeListener implements ConfigurationChangeListener {

    private static final long serialVersionUID = -6714920728096329737L;
}
