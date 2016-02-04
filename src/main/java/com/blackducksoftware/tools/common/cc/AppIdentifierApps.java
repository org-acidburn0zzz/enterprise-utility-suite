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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *******************************************************************************/

package com.blackducksoftware.tools.common.cc;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.codecenter.role.data.ApplicationRoleAssignment;
import com.blackducksoftware.tools.common.EntAppName;
import com.blackducksoftware.tools.common.EntAppNameConfigurationManager;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterServerWrapper;
import com.blackducksoftware.tools.connector.codecenter.application.ApplicationPojo;
import com.blackducksoftware.tools.teamsync.AppTeam;
import com.blackducksoftware.tools.teamsync.CodeCenterUtils;
import com.blackducksoftware.tools.teamsync.DeriveAppIdentifierTeamAlgorithm;

/**
 * The set of applications associated with an appIdentifier.
 *
 * @author sbillings
 *
 */
public class AppIdentifierApps {
    private final Logger log = LoggerFactory.getLogger(this.getClass()
            .getName());

    private final List<AppTeam> appTeams; // The AppIdentifier's list of

    // existing apps

    public AppIdentifierApps(EntAppNameConfigurationManager config,
            CodeCenterServerWrapper ccServerWrapper, AppTeam newAppTeam)
            throws Exception {

        List<ApplicationPojo> apps = ccServerWrapper.getApplicationManager().getApplications(0, Integer.MAX_VALUE, newAppTeam.getAppIdentifier());

        appTeams = new ArrayList<>(apps.size());
        for (ApplicationPojo app : apps) {

            if (!app.getName()
                    .startsWith(
                            newAppTeam.getAppIdentifier()
                                    + config.getSeparatorString())) {
                log.info("AppIdentifier: " + newAppTeam.getAppIdentifier()
                        + ": skipping false match on app " + app.getName());
                continue;
            }
            log.info("AppIdentifier: " + newAppTeam.getAppIdentifier()
                    + ": matching app " + app.getName());
            EntAppName newAppNameObject = new EntAppName(config, app.getName());

            // Skip snapshots
            if (!newAppNameObject.isConformant()) {
                log.info("Skipping app "
                        + app.getName()
                        + " (the name does not conform to the specified pattern)");
                continue;
            }
            AppTeam appTeam = new AppTeam(CodeCenterUtils.getAppUserRoles(
                    ccServerWrapper, app.getName(), app.getVersion()),
                    newAppNameObject);
            appTeams.add(appTeam);
        }
    }

    /**
     * Get the team that every app in this AppIdentifier should have
     *
     * @return
     */
    public List<ApplicationRoleAssignment> getTeam() {
        return DeriveAppIdentifierTeamAlgorithm.deriveTeam(appTeams);
    }

    @Override
    public String toString() {
        return "AppIdentifierApps [appTeams=" + appTeams + "]";
    }

}
