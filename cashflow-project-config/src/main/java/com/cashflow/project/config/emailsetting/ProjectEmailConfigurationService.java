package com.cashflow.project.config.emailsetting;

import com.cashflow.core.annotations.AppContext;
import com.cashflow.core.annotations.Config;
import com.cashflow.core.annotations.ConfigProject;
import com.cashflow.core.annotations.ConfigRoot;
import com.cashflow.core.annotations.Default;
import com.cashflow.core.annotations.Info;

/**
 *
 * 
 */
@ConfigRoot
@ConfigProject("AZPM")
@Config(propertyFile = "config-service.properties")
@AppContext(applicationModes = {LOCAL, DEVELOPMENT, TEST, LIVE})
public interface ProjectEmailConfigurationService extends EmailConfigurationService {

    @Override
    @Default("true")
    @Info("Enables TLS for the underlying smtp protocol")
    public boolean isEnableTls();

    @Override
    @Default("true")
    @Info("Enables smtp authentication")
    public boolean isEnableAuthentication();

    @Override
    @Default("587")
    @Info("The smtp port for the email server")
    public int getPort();

    @Override
    @Default("smtp.gmail.com")
    @Info("The smtp server address")
    public String getServer();

    @Override
    @Default("Please change this ;-) ")
    @Info("The password to use, if smtp authentication is enabled")
    public String getPassword();

    @Override
    @Default("info@cashflow.com")
    @Info("The source address for the email message. The sender address")
    public String getSourceAddress();

}
