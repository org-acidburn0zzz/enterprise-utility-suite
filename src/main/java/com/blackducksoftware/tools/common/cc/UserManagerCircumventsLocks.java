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

package com.blackducksoftware.tools.common.cc;

import java.util.List;

import com.blackducksoftware.sdk.codecenter.application.ApplicationApi;
import com.blackducksoftware.sdk.codecenter.application.data.Application;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationIdToken;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.sdk.codecenter.role.data.RoleNameOrIdToken;
import com.blackducksoftware.sdk.codecenter.user.data.UserNameOrIdToken;
import com.blackducksoftware.tools.addusers.UserCreatorConfig;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterServerWrapper;

/**
 * A UserManager that circumvents application locks if encountered while adding
 * users to an app. again.
 *
 * @author sbillings
 *
 */
public class UserManagerCircumventsLocks extends UserManagerImpl {
    public UserManagerCircumventsLocks(UserCreatorConfig config,
	    CodeCenterServerWrapper ccServerWrapper) {
	super(config, ccServerWrapper);
    }

    /**
     * Add users to an application, even if the application is locked. If it
     * encounters a locked application, it unlocks the application, adds the
     * users, and locks it again.
     */
    @Override
    protected void addUsers(Application app,
	    List<UserNameOrIdToken> userNameTokens,
	    List<RoleNameOrIdToken> applicationRoleIds) throws SdkFault {

	boolean origLockValue = app.isLocked();
	if (origLockValue) {
	    lock(app, false); // unlock
	}
	getCcServerWrapper()
		.getInternalApiWrapper()
		.getApplicationApi()
		.addUserToApplicationTeam(app.getNameVersion(), userNameTokens,
			applicationRoleIds);
	if (origLockValue) {
	    lock(app, true); // lock
	}
    }

    private void lock(Application app, boolean lockValue) throws SdkFault {
	ApplicationApi applicationApi = getCcServerWrapper()
		.getInternalApiWrapper().getApplicationApi();
	ApplicationIdToken appToken = new ApplicationIdToken();
	appToken.setId(app.getId().getId());

	applicationApi.lockApplication(appToken, lockValue);
    }
}
