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

import java.util.List;

import com.blackducksoftware.sdk.codecenter.application.data.Application;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.tools.addusers.UserAdjustmentReport;
import com.blackducksoftware.tools.addusers.lobuseradjust.SimpleUserSet;

/**
 * Mark each app as "locked". Test will use this to see if all apps were
 * "processed".
 *
 * @author Steve Billings
 * @date Sep 2, 2014
 *
 */
public class MockAppListProcessor implements AppListProcessor {
    List<Application> apps;

    /**
     * Constructor provides app list (not loaded from Code Center).
     *
     * @param apps
     */
    public MockAppListProcessor(List<Application> apps) {
	this.apps = apps;
    }

    public void setOmitMissingLobRecordsFromReport(boolean value) {
    }

    /**
     * Constructor provides app list (not loaded from Code Center).
     */
    @Override
    public List<Application> loadApplications() throws SdkFault {
	return apps;
    }

    public List<Application> getApplications() {
	return apps;
    }

    @Override
    public void processAppList(List<Application> appList,
	    SimpleUserSet newUsers, UserAdjustmentReport report)
	    throws Exception {

	// Process this app list passed in via the constructor
	for (Application app : appList) {
	    System.out.println("(Mock) processing app: " + app.getName());
	    app.setLocked(true);
	}

    }
}
