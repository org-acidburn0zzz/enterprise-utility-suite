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

import com.blackducksoftware.tools.commonframework.standard.codecenter.CodeCenterServerWrapper;

import com.blackducksoftware.sdk.codecenter.application.data.Application;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.tools.addusers.UserAdjustmentReport;
import com.blackducksoftware.tools.addusers.lobuseradjust.SimpleUserSet;

public class SuicidalAppListProcessor implements AppListProcessor {
    List<Application> apps;

    /**
     * Constructor provides app list (not loaded from Code Center).
     * 
     * @param apps
     */
    public SuicidalAppListProcessor(List<Application> apps) {
	this.apps = apps;
    }

    public void setCodeCenterServerWrapper(
	    CodeCenterServerWrapper codeCenterServerWrapper) {
    }

    public void setOmitMissingLobRecordsFromReport(boolean value) {
    }

    /**
     * Constructor provides app list (not loaded from Code Center).
     */
    public List<Application> loadApplications() throws SdkFault {
	return apps;
    }

    public List<Application> getApplications() {
	return apps;
    }

    public void processAppList(List<Application> appList,
	    SimpleUserSet newUsers, UserAdjustmentReport report)
	    throws Exception {

	throw new Exception("Mock exception for testing exception handling");

    }
}
