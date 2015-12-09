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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.codecenter.application.data.Application;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationNameVersionToken;
import com.blackducksoftware.sdk.codecenter.common.data.UserRolePageFilter;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.sdk.codecenter.role.data.ApplicationRoleAssignment;
import com.blackducksoftware.sdk.codecenter.role.data.RoleNameOrIdToken;
import com.blackducksoftware.sdk.codecenter.role.data.RoleNameToken;
import com.blackducksoftware.sdk.codecenter.user.data.User;
import com.blackducksoftware.sdk.codecenter.user.data.UserCreate;
import com.blackducksoftware.sdk.codecenter.user.data.UserIdToken;
import com.blackducksoftware.sdk.codecenter.user.data.UserNameOrIdToken;
import com.blackducksoftware.sdk.codecenter.user.data.UserNameToken;
import com.blackducksoftware.sdk.codecenter.user.data.UserUpdate;
import com.blackducksoftware.tools.addusers.UserCreatorConfig;
import com.blackducksoftware.tools.addusers.lobuseradjust.UserStatus;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterServerWrapper;

/**
 * The default UserManager. If it encounters a locked application that it has
 * been asked to change, it throws an exeption.
 *
 * @author sbillings
 *
 */
public class UserManagerImpl implements UserManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass()
	    .getName());
    private final UserCreatorConfig config;
    private final CodeCenterServerWrapper ccServerWrapper;
    private final Map<String, String> userNameIdMap = new HashMap<String, String>(
	    100);

    public UserManagerImpl(UserCreatorConfig config,
	    CodeCenterServerWrapper ccServerWrapper) {
	this.config = config;
	this.ccServerWrapper = ccServerWrapper;
    }

    @Override
    public List<String> addUsers(Application app, Set<String> usersToAdd)
	    throws Exception {
	logger.debug("addUsers()");
	List<String> usersActuallyAdded = new ArrayList<String>(
		usersToAdd.size());

	if (usersToAdd.size() == 0) {
	    return usersActuallyAdded;
	}
	List<UserNameOrIdToken> userNameTokens = new ArrayList<UserNameOrIdToken>();
	List<RoleNameOrIdToken> applicationRoleIds = new ArrayList<RoleNameOrIdToken>();
	RoleNameToken roleNameToken = new RoleNameToken();
	roleNameToken.setName(config.getUserRole());
	applicationRoleIds.add(roleNameToken);
	for (String username : usersToAdd) {
	    logger.info("Adding user: " + username + " to app " + app.getName());
	    UserNameToken userNameToken = new UserNameToken();
	    userNameToken.setName(username);
	    userNameTokens.add(userNameToken);

	    usersActuallyAdded.add(username);
	}
	addUsers(app, userNameTokens, applicationRoleIds);
	return usersActuallyAdded;
    }

    protected void addUsers(Application app,
	    List<UserNameOrIdToken> userNameTokens,
	    List<RoleNameOrIdToken> applicationRoleIds) throws SdkFault {
	ccServerWrapper
		.getInternalApiWrapper()
		.getApplicationApi()
		.addUserToApplicationTeam(app.getNameVersion(), userNameTokens,
			applicationRoleIds);
    }

    @Override
    public List<String> createUsers(Set<String> usersToCreate) throws Exception {
	logger.debug("createUsers()");

	List<String> usersActuallyCreated = new ArrayList<String>(
		usersToCreate.size());
	if (usersToCreate.size() == 0) {
	    return usersActuallyCreated;
	}

	List<UserNameOrIdToken> userNameTokens = new ArrayList<UserNameOrIdToken>();
	List<RoleNameOrIdToken> applicationRoleIds = new ArrayList<RoleNameOrIdToken>();
	RoleNameToken roleNameToken = new RoleNameToken();
	roleNameToken.setName(config.getUserRole());
	applicationRoleIds.add(roleNameToken);
	for (String username : usersToCreate) {
	    UserNameToken userNameToken = new UserNameToken();
	    userNameToken.setName(username);
	    userNameTokens.add(userNameToken);

	    logger.debug("Checking to see if user " + username + " exists");
	    boolean userExists = false;
	    try {
		User u = ccServerWrapper.getInternalApiWrapper().getUserApi()
			.getUser(userNameToken);
		if (u != null) {
		    userExists = true;
		    userNameIdMap.put(username, u.getId().getId());
		    makeSureUserIsActive(u);
		}
	    } catch (SdkFault e) {
		userExists = false;
	    }

	    if (!userExists) {
		logger.info("Creating new Code Center user: " + username);
		UserCreate uc = new UserCreate();
		uc.setName(username);
		uc.setActive(true);
		uc.setPassword(config.getNewUserPassword());
		UserIdToken userIdToken = ccServerWrapper
			.getInternalApiWrapper().getUserApi().createUser(uc);
		userNameIdMap.put(username, userIdToken.getId());
		usersActuallyCreated.add(username);
	    }
	}
	dumpUserNameIdMap();
	return usersActuallyCreated;
    }

    private void makeSureUserIsActive(User u) throws SdkFault {
	if (!u.isActive()) {
	    logger.info("Making user " + u.getId().getId() + " active");
	    UserUpdate userUpdate = new UserUpdate();
	    userUpdate.setId(u.getId());
	    userUpdate.setActive(true);
	    ccServerWrapper.getInternalApiWrapper().getUserApi()
		    .updateUser(userUpdate);
	}
    }

    private void dumpUserNameIdMap() {
	logger.debug("userNameIdMap:");
	for (String username : userNameIdMap.keySet()) {
	    logger.debug("\tUsername: " + username + "; User ID: "
		    + userNameIdMap.get(username));
	}
    }

    @Override
    public List<UserStatus> deleteUsers(Application app,
	    Set<String> usersToDelete) throws Exception {
	logger.debug("deleteUsers()");
	dumpUserNameIdMap();
	List<UserStatus> userDeletionStatus = new ArrayList<UserStatus>(
		usersToDelete.size());
	if (usersToDelete.size() == 0) {
	    return userDeletionStatus;
	}
	for (String username : usersToDelete) {
	    logger.info("Removing user: " + username + " from app "
		    + app.getName());
	    updateUserNameIdMap(username);
	    logger.debug("User ID pulled from userNameIdMap: "
		    + userNameIdMap.get(username));

	    ApplicationNameVersionToken appToken = new ApplicationNameVersionToken();
	    appToken.setName(app.getName());
	    appToken.setVersion(app.getVersion());

	    UserNameToken userToken = new UserNameToken();
	    userToken.setName(username);

	    // Get username's roles on this application
	    UserRolePageFilter filter = new UserRolePageFilter();
	    filter.setFirstRowIndex(0);
	    filter.setLastRowIndex(Integer.MAX_VALUE);
	    logger.debug("Getting role assignments for user " + username
		    + " in application " + app.getName());
	    List<ApplicationRoleAssignment> roleAssignments = ccServerWrapper
		    .getInternalApiWrapper().getApplicationApi()
		    .searchUserInApplicationTeam(app.getId(), username, filter);
	    logger.debug("Found " + roleAssignments.size()
		    + " role assignments for user " + username);

	    for (ApplicationRoleAssignment roleToRemove : roleAssignments) {
		try {
		    logger.debug("Removing user " + username + " ("
			    + roleToRemove.getUserIdToken().getId() + ") "
			    + " role " + roleToRemove.getRoleIdToken().getId()
			    + " from app " + app.getName());

		    // searchUserInApplicationTeam() returns a superset of
		    // users, such as
		    // user1, user10, and user100 when we search for user1.
		    if (!roleToRemove.getUserIdToken().getId()
			    .equals(userNameIdMap.get(username))) {
			logger.debug("This is not the user we're looking for... skipping it");
			continue;
		    }

		    ccServerWrapper
			    .getInternalApiWrapper()
			    .getApplicationApi()
			    .removeUserInApplicationTeam(appToken, userToken,
				    roleToRemove.getRoleIdToken());
		    logger.debug("Removal of user " + username
			    + " was successful");
		    userDeletionStatus
			    .add(new UserStatus(username, true, null));
		} catch (SdkFault e) {
		    String msg = "Error removing user " + username
			    + " with role "
			    + roleToRemove.getRoleIdToken().getId()
			    + " from application " + app.getName()
			    + " version " + app.getVersion() + ": "
			    + e.getMessage();
		    logger.error(msg);
		    userDeletionStatus.add(new UserStatus(username, false, e
			    .getMessage()));
		    // throw new Exception(msg);
		}
	    }
	}
	return userDeletionStatus;
    }

    private void updateUserNameIdMap(String username) throws SdkFault {
	if (userNameIdMap.containsKey(username)) {
	    return; // user is already in our name/id map
	}

	UserNameToken userNameToken = new UserNameToken();
	userNameToken.setName(username);
	User user = ccServerWrapper.getInternalApiWrapper().getUserApi()
		.getUser(userNameToken);
	userNameIdMap.put(username, user.getId().getId());
	logger.debug("Added to userNameIdMap: Username: " + username
		+ "; User ID: " + user.getId().getId());
    }

    protected CodeCenterServerWrapper getCcServerWrapper() {
	return ccServerWrapper;
    }

}
