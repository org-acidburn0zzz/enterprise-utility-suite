/*******************************************************************************
 * Copyright (C) 2016 Black Duck Software, Inc.
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

package com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.tools.appuseradjuster.AppUserAdjusterConfig;
import com.blackducksoftware.tools.appuseradjuster.MultiThreadedUserAdjuster;
import com.blackducksoftware.tools.appuseradjuster.UserAdjustmentReport;
import com.blackducksoftware.tools.appuseradjuster.add.lobuseradjust.SimpleUserSet;
import com.blackducksoftware.tools.appuseradjuster.add.lobuseradjust.applist.AppListProcessor;
import com.blackducksoftware.tools.appuseradjuster.add.lobuseradjust.applist.AppListProcessorFactory;
import com.blackducksoftware.tools.appuseradjuster.add.lobuseradjust.applist.AppProcessorThread;
import com.blackducksoftware.tools.commonframework.core.multithreading.ListDistributor;
import com.blackducksoftware.tools.commonframework.standard.datatable.DataTable;
import com.blackducksoftware.tools.connector.codecenter.ICodeCenterServerWrapper;
import com.blackducksoftware.tools.connector.codecenter.application.ApplicationPojo;

/**
 * A MultiThreadedUserAdjuster that executes the AppIdentifiersPerUser
 * algorithm.
 *
 * @author sbillings
 *
 */
public class MultiThreadedUserAdjusterAppIdentifiersPerUser implements
        MultiThreadedUserAdjuster {
    private final Logger logger = LoggerFactory.getLogger(this.getClass()
            .getName());

    private static String THREAD_FAILED_ERROR_MESSAGE = "One or more threads failed. Please check the log for errors (search for \"ERROR\")";

    private final UserAdjustmentReport report;

    private final SimpleUserSet newUsers;

    private final AppListProcessorFactory appListProcessorFactory;

    private final AppUserAdjuster appUserAdjuster;

    private boolean threadExceptionThrown = false;

    private final AppUserAdjusterConfig config;

    public MultiThreadedUserAdjusterAppIdentifiersPerUser(
            AppUserAdjusterConfig config, ICodeCenterServerWrapper codeCenterServerWrapper,
            AppListProcessorFactory appListProcessorFactory,
            AppUserAdjuster appUserAdjuster) throws Exception {

        this.config = config;
        this.appListProcessorFactory = appListProcessorFactory;
        this.appUserAdjuster = appUserAdjuster;
        report = new UserAdjustmentReport(config, "report");
        report.setLob("");

        newUsers = new SimpleUserSet(config.getAppIdentifierUserListMap()
                .getUsernameAppIdentifierListMap().keySet());
        if ((newUsers == null) || (newUsers.size() == 0)) {
            String msg = "The input file is empty.";
            logger.error(msg);
            throw new Exception(msg);
        }
    }

    /**
     * Add users to applications based on AppIdentifiers per Username input
     * file.
     *
     */
    @Override
    public void run(ICodeCenterServerWrapper codeCenterServerWrapper,
            int numThreads) throws Exception {
        logger.info("Adjusting application team (users) based on AppIdentifiers per Username input file");

        List<String> usersCreated = appUserAdjuster.preProcessUsers(newUsers.getUserSet());

        report.addRecord("<all>", "", true, usersCreated, null, null, null);
        logger.info("Fetching applications from Code Center");

        AppListProcessor fullAppListGetter = appListProcessorFactory
                .createAppListProcessor();
        List<ApplicationPojo> fullAppList = fullAppListGetter.loadApplications();

        if (fullAppList.size() == 0) {
            logger.warn("No applications found that match the AppIdentifiers specified in the input file");
            return;
        }

        ListDistributor distrib = new ListDistributor(numThreads,
                fullAppList.size());

        // Launch a bunch of threads to process apps
        List<Thread> startedThreads = new ArrayList<Thread>(
                distrib.getNumThreads());
        for (int i = 0; i < distrib.getNumThreads(); i++) {
            int fromIndex = distrib.getFromListIndex(i);
            int toIndex = distrib.getToListIndex(i);
            logger.info("partialAppList indices: " + fromIndex + ", " + toIndex);
            List<ApplicationPojo> partialAppList = fullAppList.subList(fromIndex,
                    toIndex);
            logger.info("partialAppList.size(): " + partialAppList.size());

            AppProcessorThread threadWorker = new AppProcessorThread(
                    appListProcessorFactory, partialAppList, newUsers, report);

            Thread t = new Thread(threadWorker, "AppProcessorThread" + i);
            t.setUncaughtExceptionHandler(new WorkerThreadExceptionHandler());
            logger.info("Starting thread " + t.getName());
            t.start();
            startedThreads.add(t);
        }

        // Now wait for all threads to finish
        for (Thread startedThread : startedThreads) {
            logger.info("Waiting for thread " + startedThread.getName());
            startedThread.join();
        }
        logger.info("Done waiting for threads.");

        if (threadExceptionThrown) {
            report.addRecord("<all>", "", false, null, null, null,
                    THREAD_FAILED_ERROR_MESSAGE);
        }
        report.write();
        if (threadExceptionThrown) {
            throw new Exception(THREAD_FAILED_ERROR_MESSAGE);
        }
    }

    /**
     * Returns the report generated during the run() method.
     *
     */
    @Override
    public DataTable getReport() {
        return report.getDataTable();
    }

    private class WorkerThreadExceptionHandler implements
            Thread.UncaughtExceptionHandler {
        public WorkerThreadExceptionHandler() {
            logger.debug("WorkerThreadExceptionHandler constructed");
        }

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            logger.error(
                    "Thread " + t.getName() + " failed: " + e.getMessage(), e);
            threadExceptionThrown = true;
        }
    }

}
