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
package com.blackducksoftware.tools.teamsync;

import java.util.List;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.ICodeCenterServerWrapper;
import com.blackducksoftware.tools.connector.codecenter.application.ApplicationPojo;
import com.blackducksoftware.tools.connector.codecenter.application.ApplicationUserPojo;

/**
 * Code Center utility methods.
 *
 * @author sbillings
 *
 */
public class CodeCenterUtils {
    /**
     * Get the team for this one app
     *
     * TODO: At first glance, it looks like what ApplicationManager.getAllUsersAssignedToApplication() is equivalent,
     * via
     * a diff api. TODO: Would be good to have a test to verify this
     *
     * @return
     */
    public static List<ApplicationUserPojo> getAppUserRoles(
            ICodeCenterServerWrapper ccServerWrapper, String appName,
            String appVersion) throws CommonFrameworkException {

        ApplicationPojo app = ccServerWrapper.getApplicationManager().getApplicationByNameVersion(appName, appVersion);
        List<ApplicationUserPojo> team = ccServerWrapper.getApplicationManager().getAllUsersAssignedToApplication(app.getId());
        return team;
    }

}
