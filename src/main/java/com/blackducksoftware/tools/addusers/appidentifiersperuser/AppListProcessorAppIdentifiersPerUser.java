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

package com.blackducksoftware.tools.addusers.appidentifiersperuser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.codecenter.application.data.Application;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationPageFilter;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.tools.addusers.UserAdjustmentReport;
import com.blackducksoftware.tools.addusers.UserCreatorConfig;
import com.blackducksoftware.tools.addusers.lobuseradjust.SimpleUserSet;
import com.blackducksoftware.tools.addusers.lobuseradjust.applist.AppListProcessor;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterServerWrapper;

public class AppListProcessorAppIdentifiersPerUser implements AppListProcessor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass()
            .getName());

    private final UserCreatorConfig config;

    private CodeCenterServerWrapper codeCenterServerWrapper;

    private final AppIdentifierUserListMap appIdentifierUserListMap;

    public AppListProcessorAppIdentifiersPerUser(
            CodeCenterServerWrapper codeCenterServerWrapper,
            UserCreatorConfig config,
            AppIdentifierUserListMap appIdentifierUserListMap) {
        this.codeCenterServerWrapper = codeCenterServerWrapper;
        this.config = config;
        this.appIdentifierUserListMap = appIdentifierUserListMap;
    }

    /**
     * Load the applications to be processed from Code Center.
     *
     * @return
     * @throws Exception
     */
    @Override
    public List<Application> loadApplications() throws SdkFault {

        List<Application> apps = new ArrayList<Application>();

        for (String appIdentifier : appIdentifierUserListMap) {

            ApplicationPageFilter filter = new ApplicationPageFilter();
            filter.setFirstRowIndex(0);
            filter.setLastRowIndex(Integer.MAX_VALUE);
            String startsWithString = appIdentifier
                    + config.getAppNameSeparator();
            List<Application> appIdentifierApps = codeCenterServerWrapper
                    .getInternalApiWrapper().getApplicationApi()
                    .searchApplications(startsWithString, filter);
            logger.info("Loaded " + appIdentifierApps.size()
                    + " apps for AppIdentifier: " + appIdentifier);
            AppListFilter appListFilter = new AppListFilter(config,
                    appIdentifierApps, appIdentifier);
            List<Application> appIdentifierFilteredApps = appListFilter
                    .getFilteredList();
            logger.info("Filtered that down to "
                    + appIdentifierFilteredApps.size()
                    + " apps to process for AppIdentifier: " + appIdentifier);
            AppIdentifierAddUserDetails details = appIdentifierUserListMap
                    .getAppIdentifierUsernameListMap().get(appIdentifier);
            details.setApplications(appIdentifierFilteredApps); // Record the
            // apps for
            // this
            // AppIdentifier
            apps.addAll(appIdentifierFilteredApps);
        }

        return apps;
    }

    /**
     * Add users to applications for the given list of applications.
     *
     * @param appList
     * @param newUsers
     * @param report
     * @throws Exception
     */
    @Override
    public void processAppList(List<Application> apps, SimpleUserSet newUsers,
            UserAdjustmentReport report) throws Exception {

        int matchingAppCount = 0;
        for (String appIdentifier : appIdentifierUserListMap) {
            logger.info("Processing AppIdentifier: " + appIdentifier);

            AppIdentifierAddUserDetails details = appIdentifierUserListMap
                    .getAppIdentifierUsernameListMap().get(appIdentifier);
            for (Application app : details.getApplications()) {
                logger.info("Potentially processing app " + app.getName()
                        + " / " + app.getVersion());
                if (!apps.contains(app)) {
                    logger.info("Skipping app " + app.getName()
                            + "; it is not assigned to this thread.");
                    continue;
                }

                logger.info("Processing app " + app.getName() + " / "
                        + app.getVersion());
                matchingAppCount++;
                Set<String> userSet = new HashSet<String>();
                userSet.addAll(details.getUsernames());
                List<String> usersAdded = userManager.addUsers(app, userSet);

                report.addRecord(app.getName(), app.getVersion(), true, null,
                        usersAdded, null, null);
            }
        }
        if (matchingAppCount == 0) {
            report.addRecord("<all>", "", false, null, null, null,
                    "No applications match the AppIdentifiers specified in the input file");
        }
    }

}
