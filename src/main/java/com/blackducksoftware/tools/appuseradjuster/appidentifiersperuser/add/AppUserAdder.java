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
package com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser.add;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.tools.appuseradjuster.AppUserAdjusterType;
import com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser.AppUserAdjuster;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.ICodeCenterServerWrapper;
import com.blackducksoftware.tools.connector.codecenter.user.CodeCenterUserPojo;
import com.blackducksoftware.tools.connector.codecenter.user.UserStatus;

public class AppUserAdder implements AppUserAdjuster {
    private final Logger logger = LoggerFactory.getLogger(this.getClass()
            .getName());

    private ICodeCenterServerWrapper codeCenterServerWrapper;

    private final String newUserPassword;

    private final Set<String> roleNames;

    public AppUserAdder(ICodeCenterServerWrapper codeCenterServerWrapper, String newUserPassword,
            String newUserRole) {
        this.codeCenterServerWrapper = codeCenterServerWrapper;
        this.newUserPassword = newUserPassword;

        roleNames = new HashSet<>(1);
        roleNames.add(newUserRole);
    }

    @Override
    public AppUserAdjusterType getType() {
        return AppUserAdjusterType.ADD;
    }

    @Override
    public List<String> preProcessUsers(Set<String> usernames) throws CommonFrameworkException {
        logger.info("Creating any users that don't already exist.");
        List<String> usersCreated = new ArrayList<>(usernames.size());
        String userId = null;
        for (String username : usernames) {
            try {
                CodeCenterUserPojo existingUser = codeCenterServerWrapper.getUserManager().getUserByName(username);
                userId = existingUser.getId();
                // user exists; make sure it's active
                if (!existingUser.isActive()) {
                    codeCenterServerWrapper.getUserManager().setUserActiveStatus(userId, true);
                }
            } catch (CommonFrameworkException e) {
                // user does not exist; create it
                userId = codeCenterServerWrapper.getUserManager().createUser(username, newUserPassword, "", "", "", true);
                usersCreated.add(username);
            }
        }
        return usersCreated;
    }

    @Override
    public List<UserStatus> adjustAppUsers(String appId, Set<String> userSet, boolean circumventLocks) throws CommonFrameworkException {
        logger.info("Adding to appId " + appId + " users: " + userSet);
        codeCenterServerWrapper.getApplicationManager().addUsersByNameToApplicationTeam(appId,
                userSet, roleNames, circumventLocks);
        List<UserStatus> userStatusList = new ArrayList<>(userSet.size());
        for (String username : userSet) {
            UserStatus userStatus = new UserStatus(username, true, null);
            userStatusList.add(userStatus);
        }
        return userStatusList;
    }

}
