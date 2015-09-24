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

import com.blackducksoftware.sdk.codecenter.role.data.ApplicationRoleAssignment;
import com.blackducksoftware.tools.common.EntAppName;

public class AppTeam {
    private final String appName;
    private final String appIdentifier;
    private final List<ApplicationRoleAssignment> team;

    public AppTeam(List<ApplicationRoleAssignment> team,
	    EntAppName newAppNameObject) throws Exception {

	appName = newAppNameObject.getAppName();
	appIdentifier = newAppNameObject.getAppIdentifier();

	this.team = team;
    }

    public String getAppName() {
	return appName;
    }

    public String getAppIdentifier() {
	return appIdentifier;
    }

    public List<ApplicationRoleAssignment> getTeam() {
	return team;
    }

    public int getTeamSize() {
	return team.size();
    }

    public boolean containsRoleAssignment(
	    ApplicationRoleAssignment newRoleAssignment) {
	for (ApplicationRoleAssignment existingRoleAssignment : team) {
	    if (newRoleAssignment.getUserIdToken().getId()
		    .equals(existingRoleAssignment.getUserIdToken().getId())) {
		if (newRoleAssignment
			.getRoleIdToken()
			.getId()
			.equals(existingRoleAssignment.getRoleIdToken().getId())) {
		    return true;
		}
	    }
	}
	return false;
    }

    @Override
    public String toString() {
	return "AppTeam [appName=" + appName + ", appIdentifier="
		+ appIdentifier + ", team size=" + team.size() + "]";
    }

}
