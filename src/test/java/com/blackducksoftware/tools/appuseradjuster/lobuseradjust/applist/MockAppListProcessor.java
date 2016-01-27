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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *******************************************************************************/

package com.blackducksoftware.tools.appuseradjuster.lobuseradjust.applist;

import java.util.List;

import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.tools.appuseradjuster.UserAdjustmentReport;
import com.blackducksoftware.tools.appuseradjuster.lobuseradjust.SimpleUserSet;
import com.blackducksoftware.tools.appuseradjuster.lobuseradjust.applist.AppListProcessor;
import com.blackducksoftware.tools.connector.codecenter.application.ApplicationPojo;

/**
 * Mark each app as "locked". Test will use this to see if all apps were
 * "processed".
 *
 * @author Steve Billings
 * @date Sep 2, 2014
 *
 */
public class MockAppListProcessor implements AppListProcessor {
    List<ApplicationPojo> apps;

    /**
     * Constructor provides app list (not loaded from Code Center).
     *
     * @param apps
     */
    public MockAppListProcessor(List<ApplicationPojo> apps) {
        this.apps = apps;
    }

    public void setOmitMissingLobRecordsFromReport(boolean value) {
    }

    /**
     * Constructor provides app list (not loaded from Code Center).
     */
    @Override
    public List<ApplicationPojo> loadApplications() throws SdkFault {
        return apps;
    }

    public List<ApplicationPojo> getApplications() {
        return apps;
    }

    @Override
    public void processAppList(List<ApplicationPojo> appList,
            SimpleUserSet newUsers, UserAdjustmentReport report)
            throws Exception {

        for (ApplicationPojo app : appList) {
            System.out.println("(Mock) processing app: " + app.getName());
        }

    }
}
