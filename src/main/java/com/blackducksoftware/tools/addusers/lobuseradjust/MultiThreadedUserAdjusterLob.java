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

package com.blackducksoftware.tools.addusers.lobuseradjust;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.codecenter.application.data.Application;
import com.blackducksoftware.tools.addusers.MultiThreadedUserAdjuster;
import com.blackducksoftware.tools.addusers.UserAdjustmentReport;
import com.blackducksoftware.tools.addusers.UserCreatorConfig;
import com.blackducksoftware.tools.addusers.lobuseradjust.applist.AppListProcessor;
import com.blackducksoftware.tools.addusers.lobuseradjust.applist.AppListProcessorFactory;
import com.blackducksoftware.tools.addusers.lobuseradjust.applist.AppProcessorThread;
import com.blackducksoftware.tools.common.cc.UserManager;
import com.blackducksoftware.tools.commonframework.core.multithreading.ListDistributor;
import com.blackducksoftware.tools.commonframework.standard.datatable.DataTable;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterServerWrapper;

/**
 * A MultiThreadedUserAdjuster that executes the "users per LOB" algorithm.
 *
 * @author sbillings
 *
 */
public class MultiThreadedUserAdjusterLob implements MultiThreadedUserAdjuster {
    private static final String THREAD_FAILED_ERROR_MESSAGE = "One or more threads failed. Please check the log for errors (search for \"ERROR\")";

    private final Logger logger = LoggerFactory.getLogger(this.getClass()
            .getName());

    private final UserCreatorConfig config;

    private final UserManager userManager;

    private final UserAdjustmentReport report;

    private final SimpleUserSet newUsers;

    private final AppListProcessorFactory appListProcessorFactory;

    private boolean threadExceptionThrown = false;

    public MultiThreadedUserAdjusterLob(UserCreatorConfig config,
            UserManager userManager,
            AppListProcessorFactory appListProcessorFactory) throws Exception {
        this.config = config;
        this.userManager = userManager;
        this.appListProcessorFactory = appListProcessorFactory;
        report = new UserAdjustmentReport(config,
                getFilenameSafeString(config.getLob()));
        report.setLob(config.getLob());

        newUsers = config.getLobUserSet();
        if ((newUsers == null) || (newUsers.size() == 0)) {
            String msg = "The LOB user list provided is empty.";
            logger.error(msg);
            throw new Exception(msg);
        }

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

    /**
     * Add users to applications based on "users per LOB" input file.
     *
     */
    @Override
    public void run(CodeCenterServerWrapper codeCenterServerWrapper,
            int numThreads) throws Exception {
        logger.info("Adjusting users for LOB " + config.getLob());
        logger.info("Creating any users that don't already exist.");
        List<String> usersCreated = userManager.createUsers(newUsers
                .getUserSet());
        report.addRecord("<all>", "", true, usersCreated, null, null, null);
        logger.info("Fetching applications from Code Center");

        AppListProcessor fullAppListGetter = appListProcessorFactory
                .createAppListProcessor();
        List<Application> fullAppList = fullAppListGetter.loadApplications();

        ListDistributor distrib = new ListDistributor(numThreads,
                fullAppList.size());

        // Launch a bunch of threads to process apps
        List<Thread> startedThreads = new ArrayList<Thread>(
                distrib.getNumThreads());
        for (int i = 0; i < distrib.getNumThreads(); i++) {
            List<Application> partialAppList = fullAppList.subList(
                    distrib.getFromListIndex(i), distrib.getToListIndex(i));

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

    static String getFilenameSafeString(String s) {
        char[] stringAsChars = s.toCharArray();
        for (int i = 0; i < stringAsChars.length; i++) {
            stringAsChars[i] = getFilenameSafeChar(stringAsChars[i]);
        }
        return new String(stringAsChars);
    }

    static char getFilenameSafeChar(char c) {
        if (Character.isAlphabetic(c) || Character.isDigit(c) || (c == '-')) {
            return c;
        } else {
            return '_';
        }
    }
}
