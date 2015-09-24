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

package com.blackducksoftware.tools.addusers.lobuseradjust;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.blackducksoftware.sdk.codecenter.application.data.Application;
import com.blackducksoftware.tools.common.cc.UserManager;

public class MockUserManager implements UserManager {

    @Override
    public List<String> addUsers(Application app, Set<String> usersToAdd)
	    throws Exception {
	List<String> list = new ArrayList<String>(usersToAdd.size());
	list.addAll(usersToAdd);
	return list;
    }

    @Override
    public List<String> createUsers(Set<String> usersToCreate) throws Exception {
	List<String> list = new ArrayList<String>(usersToCreate.size());
	list.addAll(usersToCreate);
	return list;
    }

    @Override
    public List<UserStatus> deleteUsers(Application app,
	    Set<String> usersToDelete) throws Exception {
	List<UserStatus> list = new ArrayList<UserStatus>(usersToDelete.size());
	for (String user : usersToDelete) {
	    list.add(new UserStatus(user, true, null));
	}
	return list;
    }

}
