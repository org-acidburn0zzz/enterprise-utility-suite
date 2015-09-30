/*******************************************************************************
 * Copyright (C) 2015 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version 2 only
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *******************************************************************************/
package com.blackducksoftware.tools.teamsync;

import java.util.List;

import com.blackducksoftware.sdk.codecenter.application.data.ApplicationNameVersionToken;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.sdk.codecenter.role.data.ApplicationRoleAssignment;
import com.blackducksoftware.tools.commonframework.standard.codecenter.CodeCenterServerWrapper;

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
     * @return
     */
    public static List<ApplicationRoleAssignment> getAppUserRoles(
	    CodeCenterServerWrapper ccServerWrapper, String appName,
	    String appVersion) throws SdkFault {
	ApplicationNameVersionToken appToken = new ApplicationNameVersionToken();
	appToken.setName(appName);
	appToken.setVersion(appVersion);
	List<ApplicationRoleAssignment> team = ccServerWrapper
		.getInternalApiWrapper().getProxy().getRoleApi()
		.getApplicationRoles(appToken);
	return team;
    }

}
