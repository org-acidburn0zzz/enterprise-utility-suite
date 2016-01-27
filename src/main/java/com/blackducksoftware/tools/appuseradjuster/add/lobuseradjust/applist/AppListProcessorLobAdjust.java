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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *******************************************************************************/

package com.blackducksoftware.tools.appuseradjuster.add.lobuseradjust.applist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.codecenter.application.data.ApplicationNameVersionToken;
import com.blackducksoftware.sdk.codecenter.common.data.UserRolePageFilter;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.tools.appuseradjuster.UserAdjustmentReport;
import com.blackducksoftware.tools.appuseradjuster.add.AddUserConfig;
import com.blackducksoftware.tools.appuseradjuster.add.lobuseradjust.SimpleUserSet;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterServerWrapper;
import com.blackducksoftware.tools.connector.codecenter.application.ApplicationPojo;
import com.blackducksoftware.tools.connector.codecenter.application.ApplicationUserPojo;
import com.blackducksoftware.tools.connector.codecenter.user.CodeCenterUserPojo;
import com.blackducksoftware.tools.connector.codecenter.user.UserStatus;

/**
 * An AppListProcessor that implements the "LOB adjust" mode algorithm.
 *
 * @author sbillings
 *
 */
public class AppListProcessorLobAdjust implements AppListProcessor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass()
            .getName());

    private final CodeCenterServerWrapper codeCenterServerWrapper;

    private final AddUserConfig config;

    private String targetRoleId; // at some point we discover this Role

    // ID. Until then, it's null

    public AppListProcessorLobAdjust(
            CodeCenterServerWrapper codeCenterServerWrapper,
            AddUserConfig config) {
        this.codeCenterServerWrapper = codeCenterServerWrapper;
        this.config = config;
    }

    @Override
    public List<ApplicationPojo> loadApplications() throws CommonFrameworkException {
        List<ApplicationPojo> apps = codeCenterServerWrapper.getApplicationManager().getApplications(0, Integer.MAX_VALUE);
        logger.debug("Loaded " + apps.size() + " apps");
        return apps;
    }

    @Override
    public void processAppList(List<ApplicationPojo> apps, SimpleUserSet newUsers,
            UserAdjustmentReport report) throws Exception {
        int appIndex = 0;
        int appCount = apps.size();
        for (ApplicationPojo app : apps) {
            logger.info("Processing application " + app.getName() + " ("
                    + ++appIndex + " of " + appCount + ")");
            processApp(codeCenterServerWrapper, app, newUsers, report);
        }
    }

    private void processApp(CodeCenterServerWrapper codeCenterServerWrapper,
            ApplicationPojo app, SimpleUserSet newUsers, UserAdjustmentReport report)
            throws Exception {
        String attrValueString = app.getAttributeByName(config.getLobAttrName());
        if (attrValueString == null) {
            if (!config.isOmitMissingLobRecordsFromReport()) {
                logger.warn("Application " + app.getName()
                        + " has no LOB attribute value");
                report.addRecord(app.getName(), app.getVersion(), false, null,
                        null, null, "This application has no LOB value");
            }
            return; // This app does not belong to any LOB
        }
        if (!attrValueString.equals(config.getLob())) {
            return; // This app belongs to a different LOB
        }

        logger.debug("This application (" + app.getName()
                + ") belongs to the LOB we're processing");
        processLobApp(codeCenterServerWrapper, app, newUsers,
                report);
    }

    private void processLobApp(CodeCenterServerWrapper codeCenterServerWrapper,
            ApplicationPojo app, SimpleUserSet newUsers, UserAdjustmentReport report)
            throws Exception {
        logger.debug("processLobApp(): app: " + app.getName());
        ApplicationNameVersionToken appToken = new ApplicationNameVersionToken();
        appToken.setName(app.getName());
        appToken.setVersion(app.getVersion());

        UserRolePageFilter filter = new UserRolePageFilter();
        filter.setFirstRowIndex(0);
        filter.setLastRowIndex(Integer.MAX_VALUE);

        List<ApplicationUserPojo> roleAssignments = codeCenterServerWrapper.getApplicationManager().getAllUsersAssignedToApplication(app.getId());

        Set<String> oldUsers = new HashSet<String>();
        for (ApplicationUserPojo roleAssignment : roleAssignments) {
            logger.debug("Checking role assignment: User " + roleAssignment.getUserName() + ": Role: "
                    + roleAssignment.getRoleName());

            CodeCenterUserPojo user = codeCenterServerWrapper.getUserManager().getUserById(roleAssignment.getUserId());

            boolean thisUserIsRelevant = thisUserHasTheRelevantRoleOnThisApp(
                    codeCenterServerWrapper, app, user, roleAssignment
                            .getRoleId());

            if (thisUserIsRelevant) {
                logger.debug("User " + user.getUsername()
                        + " has role = target role; adding to oldUsers list.");
                oldUsers.add(user.getUsername());
            } else {
                logger.debug("User " + user.getUsername()
                        + " has some other role; not going to remove it");
            }
        }

        SimpleUserSet userSet = new SimpleUserSet(oldUsers);
        SimpleUserSet usersToAdd = userSet.getUsersToAdd(newUsers);
        SimpleUserSet usersToDelete = userSet.getUsersToDelete(newUsers);

        if (usersToAdd.getUserSet().size() > 0) {
            Set<String> roleNames = new HashSet<>(1);
            roleNames.add(config.getUserRole());
            logger.debug("Adding users: " + usersToAdd + " (role: " + config.getUserRole() + ")");
            codeCenterServerWrapper.getApplicationManager().addUsersByNameToApplicationTeam(app.getId(), usersToAdd.getUserSet(), roleNames,
                    config.isCircumventLocks());
        } else {
            logger.debug("There are no users to add");
        }

        List<UserStatus> usersRemoved = new ArrayList<UserStatus>(0);
        if (usersToDelete.getUserSet().size() > 0) {
            logger.debug("Removing users: " + usersToDelete);
            usersRemoved = codeCenterServerWrapper.getApplicationManager().removeUsersByNameFromApplicationAllRoles(app.getId(),
                    usersToDelete.getUserSet(), config.isCircumventLocks());
        } else {
            logger.debug("There are no users to remove");
        }
        logger.debug("Adding record to report for app " + app.getName());
        List<String> usersAdded = new ArrayList<>(usersToAdd.getUserSet());
        report.addRecord(app.getName(), app.getVersion(), true, null,
                usersAdded, usersRemoved, null);
        logger.debug("Done adding record to report for app " + app.getName());
    }

    private boolean thisUserHasTheRelevantRoleOnThisApp(
            CodeCenterServerWrapper codeCenterServerWrapper, ApplicationPojo app,
            CodeCenterUserPojo user, String usersRoleIdOnThisApp) throws CommonFrameworkException {

        if (targetRoleId != null) {
            // If we already know the target Role ID, this is easy
            if (targetRoleId.equals(usersRoleIdOnThisApp)) {
                return true;
            } else {
                return false;
            }
        } else {
            // If we don't, there's a lot more work involved
            return determineWhetherUserHasTheRelevantRoleOnThisApp(
                    codeCenterServerWrapper, app, user, usersRoleIdOnThisApp);
        }
    }

    /**
     * Dig in to find out if this user has the "target role" on this app. Also,
     * if you stumble across the role ID for the target role, remember it.
     *
     * Since we don't yet know the RoleID of the target Role, we have to compare
     * by role name, which is a pain to get. During that painful process we
     * might learn the Role ID of the target role. If we do, we record it in
     * this.targetRoleId so we don't have to go through this misery more than
     * once
     *
     * Loop through ALL role assignments for this app, and build 2 maps: 1.
     * ("userAssignmentMap") Map<username, ApplicationRoleAssignment> 2.
     * ("roleNameMap") Map<roleName, ApplicationRoleAssignment> With map #1, we
     * can easily determine if the given user has the targeted role on this app
     * With map #2, we might get lucky and learn the RoleID of the targeted Role
     * Name. If so: Remember it!
     *
     * @param app
     * @param user
     * @param usersRoleIdOnThisApp
     * @return
     * @throws CommonFrameworkException
     * @throws SdkFault
     */
    private boolean determineWhetherUserHasTheRelevantRoleOnThisApp(
            CodeCenterServerWrapper codeCenterServerWrapper, ApplicationPojo app,
            CodeCenterUserPojo user, String usersRoleIdOnThisApp) throws CommonFrameworkException {
        boolean userHasRelevantRole = false;

        Map<String, ApplicationUserPojo> userAssignmentMap = new HashMap<>();
        Map<String, ApplicationUserPojo> roleNameMap = new HashMap<>();

        List<ApplicationUserPojo> userRoles = codeCenterServerWrapper.getApplicationManager().getAllUsersAssignedToApplication(app.getId());
        for (ApplicationUserPojo userRole : userRoles) {
            logger.debug("Checking Role Assignment: " + userRole);

            logger.debug("Adding role assignment to userAssignmentMap");
            userAssignmentMap.put(userRole.getUserName(), userRole);
            logger.debug("Adding role assignment to roleNameMap");
            roleNameMap.put(userRole.getRoleName(), userRole);
        }

        // Does the given user have the targeted role?
        logger.debug("Checking to see if user " + user.getUsername()
                + " has the targeted role...");
        if (userAssignmentMap.containsKey(user.getUsername())) {
            ApplicationUserPojo a = userAssignmentMap.get(user.getUsername());
            if (a.getRoleName().equals(config.getUserRole())) {
                userHasRelevantRole = true;
                logger.debug("... it does.");
            }
        }

        // Did we stumble across the RoleID for the targeted role?
        logger.debug("Checking to see if we encountered the RoleID for the targeted role");
        if (roleNameMap.containsKey(config.getUserRole())) {
            ApplicationUserPojo targetRole = roleNameMap.get(config.getUserRole());
            targetRoleId = targetRole.getRoleId();
        }

        logger.debug("determineWhetherUserHasTheRelevantRoleOnThisApp(): Returning "
                + userHasRelevantRole + " for user " + user.getUsername() + " on app " + app.getName());
        return userHasRelevantRole;
    }

}
