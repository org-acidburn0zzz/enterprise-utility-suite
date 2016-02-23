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

import com.blackducksoftware.tools.common.EntAppName;
import com.blackducksoftware.tools.connector.codecenter.application.ApplicationUserPojo;

/**
 * An application team (the list of ApplicationUserPojos for the
 * application).
 *
 * @author sbillings
 *
 */
public class AppTeam {
    private final String appName;

    private final String appIdentifier;

    private final List<ApplicationUserPojo> team;

    public AppTeam(List<ApplicationUserPojo> team,
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

    public List<ApplicationUserPojo> getTeam() {
        return team;
    }

    public int getTeamSize() {
        return team.size();
    }

    /**
     * Returns true if this team already contains the given
     * ApplicationUserPojo.
     *
     * @param newRoleAssignment
     * @return
     */
    public boolean containsRoleAssignment(
            ApplicationUserPojo newRoleAssignment) {
        for (ApplicationUserPojo existingRoleAssignment : team) {
            if (newRoleAssignment.getUserId()
                    .equals(existingRoleAssignment.getUserId())) {
                if (newRoleAssignment.getRoleId()
                        .equals(existingRoleAssignment.getRoleId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "AppTeam [appName=" + appName + ", appIdentifier=" + appIdentifier + ", team=" + team + "]";
    }

}
