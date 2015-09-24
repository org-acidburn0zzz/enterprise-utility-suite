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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.codecenter.application.data.Application;
import com.blackducksoftware.tools.addusers.UserAdjustmentReport;
import com.blackducksoftware.tools.addusers.lobuseradjust.SimpleUserSet;

public class AppProcessorThread implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass()
	    .getName());
    private final AppListProcessorFactory appListProcessorFactory;
    private final List<Application> partialAppList;
    private final SimpleUserSet newUsers;
    private final UserAdjustmentReport report;

    public AppProcessorThread(AppListProcessorFactory appListProcessorFactory,
	    List<Application> partialAppList, SimpleUserSet newUsers,
	    UserAdjustmentReport report) {

	this.appListProcessorFactory = appListProcessorFactory;
	this.partialAppList = partialAppList;
	this.newUsers = newUsers;
	this.report = report;
    }

    @Override
    public void run() {
	logger.info("run() called");
	try {
	    AppListProcessor partialAppListProcessor = appListProcessorFactory
		    .createAppListProcessor();
	    partialAppListProcessor.processAppList(partialAppList, newUsers,
		    report);
	} catch (Exception e) {
	    logger.error(e.getMessage());
	    throw new UnsupportedOperationException(e.getMessage());
	}
    }

}
