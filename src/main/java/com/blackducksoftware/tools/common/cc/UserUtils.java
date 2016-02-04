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
import java.util.Set;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.ICodeCenterServerWrapper;
import com.blackducksoftware.tools.connector.codecenter.user.CodeCenterUserPojo;

public class UserUtils {
    public static List<String> createOrActivateUsers(ICodeCenterServerWrapper codeCenterServerWrapper, Set<String> usernames, String password)
            throws CommonFrameworkException {
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
                userId = codeCenterServerWrapper.getUserManager().createUser(username, password, "", "", "", true);
                usersCreated.add(username);
            }
        }
        return usersCreated;
    }
}
