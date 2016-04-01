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

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.tools.common.EntAppName;
import com.blackducksoftware.tools.common.cc.AppIdentifierApps;
import com.blackducksoftware.tools.common.cc.AppList;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.ICodeCenterServerWrapper;
import com.blackducksoftware.tools.connector.codecenter.application.ApplicationPojo;
import com.blackducksoftware.tools.connector.codecenter.application.ApplicationUserPojo;

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

    private final ICodeCenterServerWrapper ccServerWrapper;

    private final TeamSyncConfig config;

    private final Map<String, AppIdentifierApps> teamCache = new HashMap<>();

    public TeamSyncProcessor(ICodeCenterServerWrapper ccServerWrapper,
            TeamSyncConfig config) {
        this.ccServerWrapper = ccServerWrapper;
        this.config = config;
    }

    /**
     * For each application in the "new app" list, figure out what team it should
     * have by aggregating the teams of other apps with the same appIdentifier,
     * and then update the application's team to be that aggregated team.
     *
     * @throws Exception
     */
    public void updateNewAppsTeams() throws Exception {
        AppList newAppNames = config.getNewAppList();
        for (String newAppName : newAppNames) {
            log.info("Looking at app " + newAppName);

            // Create an AppName object for the new app
            EntAppName newAppNameObject = new EntAppName(config, newAppName);

            // Skip non-conforming apps
            if (!newAppNameObject.isConformant()) {
                log.info("Skipping app " + newAppName
                        + " (the name does not conform to the specified pattern)");
                continue;
            }

            AppIdentifierApps fullTeam = getFullTeamForAppIdentifier(newAppNameObject.getAppIdentifier());

            // Assign the AppIdentifier team to the new app
            updateAppTeam(newAppNameObject, fullTeam.getTeam());
        }
    }

    /**
     * Derive a map of <username>:<appIdentifier list>
     *
     * @throws Exception
     */
    public Map<String, Set<String>> generateUserMembershipDirectory() throws Exception {
        Map<String, Set<String>> userMembershipDirectory = new HashMap<>();
        AppList allAppNames = getAllApplications();
        for (String appName : allAppNames) {
            log.info("Looking at app " + appName);

            // Create an AppName object for the new app
            EntAppName newAppNameObject = new EntAppName(config, appName);

            // Skip non-conforming apps
            if (!newAppNameObject.isConformant()) {
                log.info("Skipping app " + appName
                        + " (the name does not conform to the specified pattern)");
                continue;
            }

            AppIdentifierApps fullTeam = getFullTeamForAppIdentifier(newAppNameObject.getAppIdentifier());

            // Incorporate this fullTeam into the <username>:<appIdentifier list> map
            addTeamToUserMembershipDirectory(userMembershipDirectory, fullTeam);
        }
        return userMembershipDirectory;
    }

    private AppList getAllApplications() throws IOException, CommonFrameworkException {
        AppList allAppNames = new AppList();

        List<ApplicationPojo> apps = ccServerWrapper.getApplicationManager().getAllApplications(config.getAppFetchChunkSize());
        for (ApplicationPojo app : apps) {
            allAppNames.add(app.getName());
        }
        return allAppNames;
    }

    private void addTeamToUserMembershipDirectory(Map<String, Set<String>> usernameToAppIdListMap, AppIdentifierApps team) {
        for (ApplicationUserPojo user : team.getTeam()) {
            String username = user.getUserName();
            Set<String> appIdentifiers;
            if (usernameToAppIdListMap.containsKey(username)) {
                appIdentifiers = usernameToAppIdListMap.get(username);
            } else {
                appIdentifiers = new HashSet<>();
            }
            appIdentifiers.add(team.getAppIdentifier()); // Add this AppId to this user's set of appIds
            usernameToAppIdListMap.put(username, appIdentifiers); // update user's entry in the directory w/ updated set
                                                                  // of appIds
        }
    }

    private AppIdentifierApps getFullTeamForAppIdentifier(String appIdentifier) throws Exception {

        log.info("AppIdentifier: " + appIdentifier);
        AppIdentifierApps fullTeam;
        if (!teamCache.containsKey(appIdentifier)) {
            // Create a AppIdentifierApps object (includes teams) for the AppIdentifier to which
            // the new app
            // belongs
            fullTeam = new AppIdentifierApps(config,
                    ccServerWrapper, appIdentifier);
            teamCache.put(appIdentifier, fullTeam);
        } else {
            log.info("Pulling team from cache for appIdentifier " + appIdentifier);
            fullTeam = teamCache.get(appIdentifier);
        }
        return fullTeam;
    }

    /**
     * Add all the user/roles in the given team to this application.
     *
     * @param newTeam
     * @throws Exception
     */
    private void updateAppTeam(EntAppName appNameObject,
            List<ApplicationUserPojo> newTeam) throws Exception {

        // Create a team object for the new app
        AppTeam newAppTeam = new AppTeam(CodeCenterUtils.getAppUserRoles(
                ccServerWrapper, appNameObject.getAppName(),
                config.getAppVersion()), appNameObject);

        for (ApplicationUserPojo roleAssignment : newTeam) {

            if (newAppTeam.containsRoleAssignment(roleAssignment)) {
                log.info("Skipping assignment of user "
                        + roleAssignment.getUserName() + " to app " + newAppTeam.getAppName()
                        + "; this user/role is already assigned.");
                continue;
            }

            log.info("Adding user "
                    + roleAssignment.getUserName() + " / role "
                    + roleAssignment.getRoleName() + " to app "
                    + newAppTeam.getAppName());

            // TODO remove:
            // List<UserNameOrIdToken> userTokens = new ArrayList<UserNameOrIdToken>(
            // 1);
            // userTokens.add(roleAssignment.getUserIdToken());
            // List<RoleNameOrIdToken> roleTokens = new ArrayList<RoleNameOrIdToken>(
            // 1);
            // roleTokens.add(roleAssignment.getRoleIdToken());
            // ApplicationNameVersionToken appToken = new ApplicationNameVersionToken();
            // appToken.setName(appTeam.getAppName());
            // appToken.setVersion(config.getAppVersion());
            // ccServerWrapper.getInternalApiWrapper().getApplicationApi()
            // .addUserToApplicationTeam(appToken, userTokens, roleTokens);
            ApplicationPojo app = ccServerWrapper.getApplicationManager().getApplicationByNameVersion(newAppTeam.getAppName(), config.getAppVersion());
            Set<String> userNames = new HashSet<>();
            userNames.add(roleAssignment.getUserName());
            Set<String> roleNames = new HashSet<>();
            roleNames.add(roleAssignment.getRoleName());
            ccServerWrapper.getApplicationManager().addUsersByNameToApplicationTeam(app.getId(), userNames, roleNames, false);
            // TODO: Would be More efficient to collect them and make one call to manager
        }
    }
}
