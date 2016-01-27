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

package com.blackducksoftware.tools.addusers.lobuseradjust.applist;

import java.util.List;

import com.blackducksoftware.tools.addusers.UserAdjustmentReport;
import com.blackducksoftware.tools.addusers.lobuseradjust.SimpleUserSet;
import com.blackducksoftware.tools.connector.codecenter.application.ApplicationPojo;

/**
 * An Application List Processor that can load its applications from Code
 * Center, and process it (add/remove users as dictated by the utility's
 * configuration/input.
 *
 * @author sbillings
 *
 */
public interface AppListProcessor {

    /**
     * Load the applications to be processed from Code Center.
     *
     * @return
     * @throws Exception
     */
    List<ApplicationPojo> loadApplications() throws Exception;

    /**
     * Add users to applications for the given list of applications.
     *
     * @param appList
     * @param newUsers
     * @param report
     * @throws Exception
     */
    void processAppList(List<ApplicationPojo> appList, SimpleUserSet newUsers,
            UserAdjustmentReport report) throws Exception;
}
