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

package com.blackducksoftware.tools.appuseradjuster.add.lobuseradjust;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.junit.Test;

import com.blackducksoftware.tools.appuseradjuster.MockCodeCenterServerWrapper;
import com.blackducksoftware.tools.appuseradjuster.MockUserAdder;
import com.blackducksoftware.tools.appuseradjuster.MultiThreadedUserAdjuster;
import com.blackducksoftware.tools.appuseradjuster.TestUtils;
import com.blackducksoftware.tools.appuseradjuster.add.AddUserConfig;
import com.blackducksoftware.tools.appuseradjuster.add.AddUserConfig.Mode;
import com.blackducksoftware.tools.appuseradjuster.add.UserAdder;
import com.blackducksoftware.tools.appuseradjuster.add.lobuseradjust.applist.AppListProcessorFactory;
import com.blackducksoftware.tools.appuseradjuster.add.lobuseradjust.applist.MockAppListProcessorFactory;
import com.blackducksoftware.tools.appuseradjuster.add.lobuseradjust.applist.SuicidalAppListProcessorFactory;
import com.blackducksoftware.tools.commonframework.standard.datatable.DataTable;
import com.blackducksoftware.tools.commonframework.standard.datatable.Record;
import com.blackducksoftware.tools.commonframework.standard.datatable.writer.DataSetWriter;
import com.blackducksoftware.tools.commonframework.standard.datatable.writer.DataSetWriterStdOut;
import com.blackducksoftware.tools.connector.codecenter.ICodeCenterServerWrapper;
import com.blackducksoftware.tools.connector.codecenter.application.ApplicationPojo;
import com.blackducksoftware.tools.connector.common.ApprovalStatus;

public class LobUserAdjustTest {

    private static String APPLICATION_VERSION = "v100";

    private static String LOB_TO_ADJUST = "myLOB2";

    private static String USER1_USERNAME = "JUnit_adduser_user11";

    private static String USER2_USERNAME = "JUnit_adduser_user22";

    @Test
    public void testGetFilenameSafeString() {
        String s = MultiThreadedUserAdjusterLob.getFilenameSafeString("");
        assertEquals("", s);

        s = MultiThreadedUserAdjusterLob.getFilenameSafeString("abc123");
        assertEquals("abc123", s);

        s = MultiThreadedUserAdjusterLob.getFilenameSafeString("a%^&/");
        assertEquals("a____", s);
    }

    /**
     * Test a variety of array sizes to be sure multithreading algorithm
     * processes all apps in list for any size list.
     *
     * @throws Exception
     */
    @Test
    public void testMultiThreading() throws Exception {
        testMultiThreading(1, 2);
        testMultiThreading(2, 3);
        testMultiThreading(3, 2);
        testMultiThreading(5, 2);
        testMultiThreading(8, 2);
        testMultiThreading(8, 7);
        testMultiThreading(8, 8);
    }

    private void testMultiThreading(int size, int numThreads) throws Exception {
        List<ApplicationPojo> appList = createAppList(size);

        checkAppList(appList, size, false);

        Properties props = TestUtils.configUserCreatorForLobAdjustMode("role2",
                "test server", "test user", "test password",
                APPLICATION_VERSION);
        AddUserConfig config = new AddUserConfig(props);

        config.setMode(Mode.USERS_PER_LOB);
        config.setLob(LOB_TO_ADJUST);
        config.setLobAttrName("test custom attribute field");

        Set<String> users = new HashSet<String>();
        users.add(USER1_USERNAME);
        users.add(USER2_USERNAME);
        SimpleUserSet userSet = new SimpleUserSet(users);
        config.setLobUserSet(userSet);

        UserAdder userAdder = new MockUserAdder(config);

        AppListProcessorFactory appListProcessorFactory = new MockAppListProcessorFactory(
                appList);
        MultiThreadedUserAdjuster lobUserAdjuster = new MultiThreadedUserAdjusterLob(
                config, appListProcessorFactory);
        userAdder.setMultiThreadedUserAdjuster(lobUserAdjuster);
        ICodeCenterServerWrapper mockCcServerWrapper = new MockCodeCenterServerWrapper(false);
        userAdder.run(mockCcServerWrapper, numThreads);

        // appList = appListProcessor.getApplications(); // we've already got it

        checkAppList(appList, size, false);
    }

    @Test
    public void testThreadExceptionHandling() throws Exception {
        int size = 2;
        int numThreads = 1;

        List<ApplicationPojo> appList = createAppList(size);

        checkAppList(appList, size, false);

        Properties props = TestUtils.configUserCreatorForLobAdjustMode("role2",
                "test server", "test user", "test password",
                APPLICATION_VERSION);
        AddUserConfig config = new AddUserConfig(props);

        config.setMode(Mode.USERS_PER_LOB);
        config.setLob(LOB_TO_ADJUST);
        config.setLobAttrName("test custom attribute");

        Set<String> users = new HashSet<String>();
        users.add(USER1_USERNAME);
        users.add(USER2_USERNAME);
        SimpleUserSet userSet = new SimpleUserSet(users);
        config.setLobUserSet(userSet);

        UserAdder userAdder = new MockUserAdder(config);

        AppListProcessorFactory appListProcessorFactory = new SuicidalAppListProcessorFactory(
                appList);
        MultiThreadedUserAdjuster lobUserAdjuster = new MultiThreadedUserAdjusterLob(
                config, appListProcessorFactory);
        userAdder.setMultiThreadedUserAdjuster(lobUserAdjuster);
        ICodeCenterServerWrapper mockCcServerWrapper = new MockCodeCenterServerWrapper(false);
        try {
            userAdder.run(mockCcServerWrapper, numThreads);
            fail("Should have thrown exception");
        } catch (Exception e) {
            // Exception is expected
        }

        DataTable report = lobUserAdjuster.getReport();
        DataSetWriter writer = new DataSetWriterStdOut();
        writer.write(report);

        // Verify: report:
        // Header row
        // Error: a thread failed
        Iterator<Record> dataSetIter = report.iterator();
        assertTrue(dataSetIter.hasNext());
        Record record = dataSetIter.next();
        assertEquals(LOB_TO_ADJUST, record.getStringFieldValue("lob"));
        assertEquals("<all>", record.getStringFieldValue("applicationName"));

        assertTrue(dataSetIter.hasNext());
        record = dataSetIter.next();
        assertEquals("", record.getStringFieldValue("lob"));
        assertEquals("<all>", record.getStringFieldValue("applicationName"));
        assertEquals("Error", record.getStringFieldValue("status"));
    }

    private List<ApplicationPojo> createAppList(int size) {
        List<ApplicationPojo> appList = new ArrayList<>();

        ApplicationPojo app;

        for (int i = 0; i < size; i++) {
            app = new ApplicationPojo("app" + i, "app" + i, "Unspecified",
                    null,
                    ApprovalStatus.APPROVED, false, "testOwnerId");

            appList.add(app);
        }

        return appList;
    }

    private void checkAppList(List<ApplicationPojo> appList, int size,
            boolean lockedValue) {

        for (int i = 0; i < size; i++) {
            System.out.println("App: " + appList.get(i).getName());
            assertEquals("app" + i, appList.get(i).getName());
            assertEquals(lockedValue, appList.get(i).isLocked());
        }

    }

}
