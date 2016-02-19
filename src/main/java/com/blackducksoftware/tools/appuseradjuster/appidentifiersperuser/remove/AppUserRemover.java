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
package com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser.remove;

import java.util.ArrayList;
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

public class AppUserRemover implements AppUserAdjuster {
    private final Logger logger = LoggerFactory.getLogger(this.getClass()
            .getName());

    private ICodeCenterServerWrapper codeCenterServerWrapper;

    public AppUserRemover(ICodeCenterServerWrapper codeCenterServerWrapper) {
        this.codeCenterServerWrapper = codeCenterServerWrapper;
    }

    @Override
    public AppUserAdjusterType getType() {
        return AppUserAdjusterType.REMOVE;
    }

    @Override
    public List<String> preProcessUsers(Set<String> usernames) throws CommonFrameworkException {
        return new ArrayList<String>(0);
    }

    @Override
    public List<UserStatus> adjustAppUsers(String appId, Set<String> userNames, boolean circumventLocks) throws CommonFrameworkException {
        logger.info("Removing from appId " + appId + " users: " + userNames);
        List<UserStatus> results = codeCenterServerWrapper.getApplicationManager().removeUsersByNameFromApplicationAllRoles(appId, userNames, circumventLocks);
        return results;
    }

    @Override
    public List<UserStatus> deActivateUsers(Set<String> usernames) throws CommonFrameworkException {
        logger.info("DeActivating users: " + usernames);
        List<UserStatus> deActivatedUsers = new ArrayList<>(usernames.size());
        for (String username : usernames) {
            // TODO: If we can use usermanager earlier to get UserID, then the user will be cached
            // and this lookup will be fast
            try {
                logger.debug("Looking up userId for username: " + username);
                CodeCenterUserPojo user = codeCenterServerWrapper.getUserManager().getUserByName(username);
                logger.debug("Looking up userId for username: " + username + " found userId: " + user.getId() + "; DeActivating this account");
                codeCenterServerWrapper.getUserManager().setUserActiveStatus(user.getId(), false);
                logger.debug("DeActivation complete");
                deActivatedUsers.add(new UserStatus(username, true, ""));
            } catch (CommonFrameworkException e) {
                String msg = "Error de-activating user: " + username + ": " + e.getMessage();
                logger.error(msg);
                deActivatedUsers.add(new UserStatus(username, false, msg));
            }
        }
        return deActivatedUsers;
    }
}
