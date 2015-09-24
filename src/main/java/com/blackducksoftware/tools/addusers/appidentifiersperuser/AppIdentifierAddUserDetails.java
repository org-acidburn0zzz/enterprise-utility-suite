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

package com.blackducksoftware.tools.addusers.appidentifiersperuser;

import java.util.List;

import com.blackducksoftware.sdk.codecenter.application.data.Application;

/**
 * This class manages, for one AppIdentifier, the list of applications found
 * with that AppIdentifier, and the list of users that want access to them.
 *
 * @author Steve Billings
 * @date Dec 5, 2014
 *
 */
public class AppIdentifierAddUserDetails {
    private final List<String> usernames;
    private List<Application> applications;

    public AppIdentifierAddUserDetails(List<String> usernames) {

	this.usernames = usernames;
    }

    /**
     * Getter for the username list.
     *
     * @return
     */
    public List<String> getUsernames() {
	return usernames;
    }

    /**
     * Getter for the application list.
     *
     * @return
     */
    public List<Application> getApplications() {
	return applications;
    }

    /**
     * Setter for the application list.
     *
     * @param applications
     */
    public void setApplications(List<Application> applications) {
	this.applications = applications;
    }

}
