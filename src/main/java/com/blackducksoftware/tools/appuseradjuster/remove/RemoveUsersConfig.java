/*******************************************************************************
 * Copyright (C) 2016 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version 2 only
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *******************************************************************************/
package com.blackducksoftware.tools.appuseradjuster.remove;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import com.blackducksoftware.tools.appuseradjuster.AppUserAdjusterConfig;
import com.blackducksoftware.tools.commonframework.core.config.user.CommonUser;

public class RemoveUsersConfig extends AppUserAdjusterConfig {
    private static final String DEACTIVATE_USERS_REMOVED_FROM_ALL_PROPERTY = "deactivate.users.removed.from.all";

    private boolean deactivateUsersRemovedFromAll = false;

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
        String deactivateUsersRemovedFromAllString = super.getOptionalProperty(DEACTIVATE_USERS_REMOVED_FROM_ALL_PROPERTY);
        if ((deactivateUsersRemovedFromAllString != null) && (deactivateUsersRemovedFromAllString.equalsIgnoreCase("true"))) {
            deactivateUsersRemovedFromAll = true;
        }

    }

    public boolean isDeactivateUsersRemovedFromAll() {
        return deactivateUsersRemovedFromAll;
    }
}
