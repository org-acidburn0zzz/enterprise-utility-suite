package com.blackducksoftware.tools.appuseradjuster.remove;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import com.blackducksoftware.tools.appuseradjuster.AppUserAdjusterConfig;
import com.blackducksoftware.tools.commonframework.core.config.user.CommonUser;

public class RemoveUsersConfig extends AppUserAdjusterConfig {

    public RemoveUsersConfig(CommonUser user) {
        super(user, APPLICATION.CODECENTER);
        loadAdditionalProperties();
    }

    public RemoveUsersConfig(InputStream is) {
        super(is, APPLICATION.CODECENTER);
        loadAdditionalProperties();
    }

    public RemoveUsersConfig(Properties props) {
        super(props, APPLICATION.CODECENTER);
        loadAdditionalProperties();
    }

    public RemoveUsersConfig(String configFileLocation) {
        super(configFileLocation, APPLICATION.CODECENTER);
        loadAdditionalProperties();
    }

    public RemoveUsersConfig(File configFile) {
        super(configFile.toString(), APPLICATION.CODECENTER);
        loadAdditionalProperties();
    }

    private void loadAdditionalProperties() {

    }
}
