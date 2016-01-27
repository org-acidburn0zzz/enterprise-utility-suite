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

package com.blackducksoftware.tools.appuseradjuster.add.lobuseradjust.applist;

import java.util.List;

import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.tools.appuseradjuster.UserAdjustmentReport;
import com.blackducksoftware.tools.appuseradjuster.add.lobuseradjust.SimpleUserSet;
import com.blackducksoftware.tools.appuseradjuster.add.lobuseradjust.applist.AppListProcessor;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterServerWrapper;
import com.blackducksoftware.tools.connector.codecenter.application.ApplicationPojo;

public class SuicidalAppListProcessor implements AppListProcessor {
    List<ApplicationPojo> apps;

    /**
     * Constructor provides app list (not loaded from Code Center).
     *
     * @param apps
     */
    public SuicidalAppListProcessor(List<ApplicationPojo> apps) {
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

        throw new Exception("Mock exception for testing exception handling");

    }
}
