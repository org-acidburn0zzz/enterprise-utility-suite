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

package com.blackducksoftware.tools.addusers.lobuseradjust.applist;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.codecenter.application.data.Application;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationNameVersionToken;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationPageFilter;
import com.blackducksoftware.sdk.codecenter.attribute.data.AbstractAttribute;
import com.blackducksoftware.sdk.codecenter.attribute.data.AttributeIdToken;
import com.blackducksoftware.sdk.codecenter.common.data.AttributeValue;
import com.blackducksoftware.sdk.codecenter.common.data.UserRolePageFilter;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.sdk.codecenter.role.data.ApplicationRoleAssignment;
import com.blackducksoftware.sdk.codecenter.user.data.User;
import com.blackducksoftware.tools.addusers.UserAdjustmentReport;
import com.blackducksoftware.tools.addusers.UserCreatorConfig;
import com.blackducksoftware.tools.addusers.lobuseradjust.SimpleUserSet;
import com.blackducksoftware.tools.addusers.lobuseradjust.UserStatus;
import com.blackducksoftware.tools.common.cc.UserManager;
import com.blackducksoftware.tools.commonframework.standard.codecenter.CodeCenterServerWrapper;

public class AppListProcessorLobAdjust implements AppListProcessor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass()
	    .getName());
    private final CodeCenterServerWrapper codeCenterServerWrapper;
    private final UserCreatorConfig config;
    private final UserManager userManager;
    private String targetRoleId; // at some point we discover this Role
				 // ID. Until then, it's null

    public AppListProcessorLobAdjust(
	    CodeCenterServerWrapper codeCenterServerWrapper,
	    UserCreatorConfig config, UserManager userManager) {
	this.codeCenterServerWrapper = codeCenterServerWrapper;
	this.config = config;
	this.userManager = userManager;
    }

    @Override
    public List<Application> loadApplications() throws SdkFault {
	ApplicationPageFilter filter = new ApplicationPageFilter();
	filter.setFirstRowIndex(0);
	filter.setLastRowIndex(Integer.MAX_VALUE);
	List<Application> apps = codeCenterServerWrapper
		.getInternalApiWrapper().getApplicationApi()
		.searchApplications(null, filter);
	logger.debug("Loaded " + apps.size() + " apps");
	return apps;
    }

    @Override
    public void processAppList(List<Application> apps, SimpleUserSet newUsers,
	    UserAdjustmentReport report) throws Exception {
	int appIndex = 0;
	int appCount = apps.size();
	for (Application app : apps) {
	    logger.info("Processing application " + app.getName() + " ("
		    + ++appIndex + " of " + appCount + ")");
	    processApp(codeCenterServerWrapper, app, newUsers, report);
	}
    }

    private void processApp(CodeCenterServerWrapper codeCenterServerWrapper,
	    Application app, SimpleUserSet newUsers, UserAdjustmentReport report)
	    throws Exception {
	List<AttributeValue> attrs = app.getAttributeValues();
	logger.debug("This app has " + attrs.size() + " attributes");

	boolean lobAttrFound = false;
	for (AttributeValue attr : attrs) {
	    AttributeIdToken attrIdToken = (AttributeIdToken) attr
		    .getAttributeId();
	    logger.debug("Processing attr " + attrIdToken.getId()
		    + " with value count: " + attr.getValues().size());

	    AbstractAttribute attrObject = codeCenterServerWrapper
		    .getInternalApiWrapper().getAttributeApi()
		    .getAttribute(attrIdToken);
	    String attrName = attrObject.getName();

	    if (attrName == null) {
		continue;
	    }

	    logger.debug("Found attr " + attrName + "; we're looking for attr "
		    + config.getLobAttrName());
	    if (!attrName.equals(config.getLobAttrName())) {
		continue;
	    }
	    logger.debug("Found the LOB attr");
	    lobAttrFound = true;
	    for (String attrValue : attr.getValues()) {
		logger.debug("\tValue: " + attrValue);

		if (attrValue == null) {
		    continue;
		}

		if (attrValue.equals(config.getLob())) {
		    logger.debug("This application (" + app.getName()
			    + ") belongs to the LOB we're processing");
		    processLobApp(codeCenterServerWrapper, app, newUsers,
			    report);
		}
	    }
	}
	if (!lobAttrFound && !config.isOmitMissingLobRecordsFromReport()) {
	    logger.warn("Application " + app.getName()
		    + " has no LOB attribute value");
	    report.addRecord(app.getName(), app.getVersion(), false, null,
		    null, null, "This application has no LOB value");
	}
    }

    private void processLobApp(CodeCenterServerWrapper codeCenterServerWrapper,
	    Application app, SimpleUserSet newUsers, UserAdjustmentReport report)
	    throws Exception {

	ApplicationNameVersionToken appToken = new ApplicationNameVersionToken();
	appToken.setName(app.getName());
	appToken.setVersion(app.getVersion());

	UserRolePageFilter filter = new UserRolePageFilter();
	filter.setFirstRowIndex(0);
	filter.setLastRowIndex(Integer.MAX_VALUE);

	List<ApplicationRoleAssignment> roleAssignments = codeCenterServerWrapper
		.getInternalApiWrapper().getApplicationApi()
		.searchUserInApplicationTeam(appToken, null, filter);

	Set<String> oldUsers = new HashSet<String>();
	for (ApplicationRoleAssignment roleAssignment : roleAssignments) {
	    logger.debug("Getting user for roleAssignment "
		    + roleAssignment.getRoleIdToken().getId());
	    logger.debug("roleAssignment: User id is "
		    + roleAssignment.getUserIdToken().getId());
	    User user = codeCenterServerWrapper.getInternalApiWrapper()
		    .getUserApi().getUser(roleAssignment.getUserIdToken());
	    logger.debug("roleAssignment: App " + app.getName()
		    + " user username: " + user.getName().getName()
		    + " roleId " + roleAssignment.getRoleIdToken().getId());

	    boolean thisUserIsRelevant = thisUserHasTheRelevantRoleOnThisApp(
		    codeCenterServerWrapper, app, user, roleAssignment
			    .getRoleIdToken().getId());

	    if (thisUserIsRelevant) {
		logger.debug("User " + user.getName().getName()
			+ " has role = target role; adding to oldUsers list.");
		oldUsers.add(user.getName().getName());
	    } else {
		logger.debug("User " + user.getName().getName()
			+ " has some other role; not going to remove it");
	    }
	}
	logger.debug("Creating userSet from oldUsers");
	SimpleUserSet userSet = new SimpleUserSet(oldUsers);
	logger.debug("Getting users to add");
	SimpleUserSet usersToAdd = userSet.getUsersToAdd(newUsers);
	logger.debug("Getting users to delete");
	SimpleUserSet usersToDelete = userSet.getUsersToDelete(newUsers);

	List<String> usersAdded = userManager.addUsers(app,
		usersToAdd.getUserSet());
	List<UserStatus> usersRemoved = userManager.deleteUsers(app,
		usersToDelete.getUserSet());

	logger.debug("Adding record to report for app " + app.getName());
	report.addRecord(app.getName(), app.getVersion(), true, null,
		usersAdded, usersRemoved, null);
	logger.debug("Done adding record to report for app " + app.getName());
    }

    private boolean thisUserHasTheRelevantRoleOnThisApp(
	    CodeCenterServerWrapper codeCenterServerWrapper, Application app,
	    User user, String usersRoleIdOnThisApp) throws SdkFault {

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
     * @throws SdkFault
     */
    private boolean determineWhetherUserHasTheRelevantRoleOnThisApp(
	    CodeCenterServerWrapper codeCenterServerWrapper, Application app,
	    User user, String usersRoleIdOnThisApp) throws SdkFault {
	boolean userHasRelevantRole = false;

	Map<String, ApplicationRoleAssignment> userAssignmentMap = new HashMap<String, ApplicationRoleAssignment>();
	Map<String, ApplicationRoleAssignment> roleNameMap = new HashMap<String, ApplicationRoleAssignment>();

	List<ApplicationRoleAssignment> applicationRoleAssignments = codeCenterServerWrapper
		.getInternalApiWrapper().getProxy().getRoleApi()
		.getApplicationRoles(app.getId());
	for (ApplicationRoleAssignment applicationRoleAssignment : applicationRoleAssignments) {
	    logger.debug("Role ID: "
		    + applicationRoleAssignment.getRoleIdToken().getId()
		    + " = Role Name: "
		    + applicationRoleAssignment.getRoleNameToken().getName()
		    + "; User: "
		    + applicationRoleAssignment.getUserNameToken().getName());

	    logger.debug("Adding role assignment to userAssignmentMap");
	    userAssignmentMap.put(applicationRoleAssignment.getUserNameToken()
		    .getName(), applicationRoleAssignment);
	    logger.debug("Adding role assignment to roleNameMap");
	    roleNameMap.put(applicationRoleAssignment.getRoleNameToken()
		    .getName(), applicationRoleAssignment);
	}

	// Does the given user have the targeted role?
	logger.debug("Checking to see if user " + user.getName()
		+ " has the targeted role");
	if (userAssignmentMap.containsKey(user.getName().getName())) {
	    ApplicationRoleAssignment a = userAssignmentMap.get(user.getName()
		    .getName());
	    if (a.getRoleNameToken().getName().equals(config.getUserRole())) {
		userHasRelevantRole = true;
	    }
	}

	// Did we stumble across the RoleID for the targeted role?
	logger.debug("Checking to see if we encountered the RoleID for the targeted role");
	if (roleNameMap.containsKey(config.getUserRole())) {
	    ApplicationRoleAssignment a = roleNameMap.get(config.getUserRole());
	    targetRoleId = a.getRoleIdToken().getId();
	}

	logger.debug("determineWhetherUserHasTheRelevantRoleOnThisApp(): Returning "
		+ userHasRelevantRole);
	return userHasRelevantRole;
    }

}
