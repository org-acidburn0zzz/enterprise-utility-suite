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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.codecenter.application.data.ApplicationNameVersionToken;
import com.blackducksoftware.sdk.codecenter.role.data.ApplicationRoleAssignment;
import com.blackducksoftware.sdk.codecenter.role.data.RoleNameOrIdToken;
import com.blackducksoftware.sdk.codecenter.user.data.UserNameOrIdToken;
import com.blackducksoftware.tools.common.EntAppName;
import com.blackducksoftware.tools.common.cc.AppIdentifierApps;
import com.blackducksoftware.tools.common.cc.AppList;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterServerWrapper;

/**
 * For each application in the given list, figure out what team it should have
 * by aggregating the teams of other apps with the same appIdentifier, and then
 * update the application's team to be that aggregated team.
 *
 * @author sbillings
 *
 */
public class TeamSyncProcessor {
    private final Logger log = LoggerFactory.getLogger(this.getClass()
            .getName());

    private final CodeCenterServerWrapper ccServerWrapper;

    private final TeamSyncConfig config;

    private final AppList newAppNames;

    private final Map<String, AppIdentifierApps> teamCache = new HashMap<>();

    public TeamSyncProcessor(CodeCenterServerWrapper ccServerWrapper,
            TeamSyncConfig config) {
        this.ccServerWrapper = ccServerWrapper;
        this.config = config;
        newAppNames = config.getNewAppList();
    }

    /**
     * For each application in the given list, figure out what team it should
     * have by aggregating the teams of other apps with the same appIdentifier,
     * and then update the application's team to be that aggregated team.
     *
     * @throws Exception
     */
    public void execute() throws Exception {

        for (String newAppName : newAppNames) {
            log.info("Looking at app " + newAppName);

            // Create an AppName object for the new app
            EntAppName newAppNameObject = new EntAppName(config, newAppName);
            // Skip snapshots
            if (!newAppNameObject.isConformant()) {
                log.info("Skipping app "
                        + newAppName
                        + " (the name does not conform to the specified pattern)");
                continue;
            }

            // Create a team object for the new app
            AppTeam newAppTeam = new AppTeam(CodeCenterUtils.getAppUserRoles(
                    ccServerWrapper, newAppNameObject.getAppName(),
                    config.getAppVersion()), newAppNameObject);

            String appIdentifier = newAppNameObject.getAppIdentifier();
            log.info("AppIdentifier: " + appIdentifier);
            AppIdentifierApps appIdentifierApps;
            if (!teamCache.containsKey(appIdentifier)) {
                // Create a AppIdentifierApps object for the AppIdentifier to which
                // the new app
                // belongs
                appIdentifierApps = new AppIdentifierApps(config,
                        ccServerWrapper, newAppTeam);
                teamCache.put(appIdentifier, appIdentifierApps);
            } else {
                log.info("Pulling team from cache for appIdentifier " + appIdentifier);
                appIdentifierApps = teamCache.get(appIdentifier);
            }

            // Assign the AppIdentifier team to the new app
            updateTeam(newAppTeam, appIdentifierApps.getTeam());
        }
    }

    /**
     * Add all the user/roles in the given team to this application.
     *
     * @param newTeam
     * @throws Exception
     */
    private void updateTeam(AppTeam appTeam,
            List<ApplicationRoleAssignment> newTeam) throws Exception {
        for (ApplicationRoleAssignment roleAssignment : newTeam) {

            if (appTeam.containsRoleAssignment(roleAssignment)) {
                log.info("Skipping assignment of "
                        + roleAssignment.getUserNameToken().getName()
                        + "; this user/role is already assigned.");
                continue;
            }

            log.info("Adding user "
                    + roleAssignment.getUserNameToken().getName() + " / role "
                    + roleAssignment.getRoleNameToken().getName() + " to app "
                    + appTeam.getAppName());
            List<UserNameOrIdToken> userTokens = new ArrayList<UserNameOrIdToken>(
                    1);
            userTokens.add(roleAssignment.getUserIdToken());
            List<RoleNameOrIdToken> roleTokens = new ArrayList<RoleNameOrIdToken>(
                    1);
            roleTokens.add(roleAssignment.getRoleIdToken());
            ApplicationNameVersionToken appToken = new ApplicationNameVersionToken();
            appToken.setName(appTeam.getAppName());
            appToken.setVersion(config.getAppVersion());
            ccServerWrapper.getInternalApiWrapper().getApplicationApi()
                    .addUserToApplicationTeam(appToken, userTokens, roleTokens);
        }
    }
}
