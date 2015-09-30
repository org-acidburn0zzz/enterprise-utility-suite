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

import java.util.List;
import java.util.Set;

import com.blackducksoftware.sdk.codecenter.application.data.Application;
import com.blackducksoftware.tools.addusers.lobuseradjust.UserStatus;

/**
 * A UserManager can create users, add users to an application, and delete users
 * from an application.
 *
 * @author sbillings
 *
 */
public interface UserManager {
    /**
     * Add the given users to the given app.
     *
     * @param app
     * @param usersToAdd
     * @return
     * @throws Exception
     */
    List<String> addUsers(Application app, Set<String> usersToAdd)
	    throws Exception;

    /**
     * Create user accounts for the given usernames.
     *
     * @param usersToCreate
     * @return
     * @throws Exception
     */
    List<String> createUsers(Set<String> usersToCreate) throws Exception;

    /**
     * Remove the given users from the given application.
     *
     * @param app
     * @param usersToDelete
     * @return
     * @throws Exception
     */
    List<UserStatus> deleteUsers(Application app, Set<String> usersToDelete)
	    throws Exception;
}
