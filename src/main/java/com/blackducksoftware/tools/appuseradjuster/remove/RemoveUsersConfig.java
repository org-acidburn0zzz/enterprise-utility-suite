package com.blackducksoftware.tools.appuseradjuster.remove;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import com.blackducksoftware.tools.appuseradjuster.AppUserAdjusterConfig;
import com.blackducksoftware.tools.commonframework.core.config.user.CommonUser;

public class RemoveUsersConfig extends AppUserAdjusterConfig {

    public RemoveUsersConfig(CommonUser user, APPLICATION applicationName) {
        super(user, applicationName);
        loadAdditionalProperties();
    }

    public RemoveUsersConfig(InputStream is, APPLICATION applicationName) {
        super(is, applicationName);
        loadAdditionalProperties();
    }

    public RemoveUsersConfig(Properties props, APPLICATION applicationName) {
        super(props, applicationName);
        loadAdditionalProperties();
    }

    public RemoveUsersConfig(String configFileLocation, APPLICATION applicationName) {
        super(configFileLocation, applicationName);
        loadAdditionalProperties();
    }

    public RemoveUsersConfig(File configFile) {
        super(configFile.toString(), APPLICATION.CODECENTER);
        loadAdditionalProperties();
    }

    private void loadAdditionalProperties() {

    }
}
