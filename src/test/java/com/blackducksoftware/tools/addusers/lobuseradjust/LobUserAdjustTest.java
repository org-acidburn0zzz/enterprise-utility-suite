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

package com.blackducksoftware.tools.addusers.lobuseradjust;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.junit.Test;

import com.blackducksoftware.tools.commonframework.standard.datatable.DataTable;
import com.blackducksoftware.tools.commonframework.standard.datatable.Record;
import com.blackducksoftware.tools.commonframework.standard.datatable.writer.DataSetWriter;
import com.blackducksoftware.tools.commonframework.standard.datatable.writer.DataSetWriterStdOut;

import com.blackducksoftware.sdk.codecenter.application.data.Application;
import com.blackducksoftware.tools.addusers.MockUserAdder;
import com.blackducksoftware.tools.addusers.MultiThreadedUserAdjuster;
import com.blackducksoftware.tools.addusers.TestUtils;
import com.blackducksoftware.tools.addusers.UserAdder;
import com.blackducksoftware.tools.addusers.UserCreatorConfig;
import com.blackducksoftware.tools.addusers.UserCreatorConfig.Mode;
import com.blackducksoftware.tools.addusers.lobuseradjust.MultiThreadedUserAdjusterLob;
import com.blackducksoftware.tools.addusers.lobuseradjust.SimpleUserSet;
import com.blackducksoftware.tools.addusers.lobuseradjust.applist.AppListProcessorFactory;
import com.blackducksoftware.tools.addusers.lobuseradjust.applist.MockAppListProcessorFactory;
import com.blackducksoftware.tools.addusers.lobuseradjust.applist.SuicidalAppListProcessorFactory;
import com.blackducksoftware.tools.common.cc.UserManager;

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
	List<Application> appList = createAppList(size);

	checkAppList(appList, size, false);

	Properties props = TestUtils.configUserCreatorForLobAdjustMode("role2",
		"test server", "test user", "test password",
		APPLICATION_VERSION);
	UserCreatorConfig config = new UserCreatorConfig(props);

	config.setMode(Mode.USERS_PER_LOB);
	config.setLob(LOB_TO_ADJUST);
	config.setLobAttrName("test custom attribute field");

	Set<String> users = new HashSet<String>();
	users.add(USER1_USERNAME);
	users.add(USER2_USERNAME);
	SimpleUserSet userSet = new SimpleUserSet(users);
	config.setLobUserSet(userSet);

	UserAdder userAdder = new MockUserAdder(config);

	UserManager userManager = new MockUserManager();
	AppListProcessorFactory appListProcessorFactory = new MockAppListProcessorFactory(
		appList);
	MultiThreadedUserAdjuster lobUserAdjuster = new MultiThreadedUserAdjusterLob(
		config, userManager, appListProcessorFactory);
	userAdder.setMultiThreadedUserAdjuster(lobUserAdjuster);
	userAdder.run(null, numThreads);

	// appList = appListProcessor.getApplications(); // we've already got it

	checkAppList(appList, size, true);
    }

    @Test
    public void testThreadExceptionHandling() throws Exception {
	int size = 2;
	int numThreads = 1;

	List<Application> appList = createAppList(size);

	checkAppList(appList, size, false);

	Properties props = TestUtils.configUserCreatorForLobAdjustMode("role2",
		"test server", "test user", "test password",
		APPLICATION_VERSION);
	UserCreatorConfig config = new UserCreatorConfig(props);

	config.setMode(Mode.USERS_PER_LOB);
	config.setLob(LOB_TO_ADJUST);
	config.setLobAttrName("test custom attribute");

	Set<String> users = new HashSet<String>();
	users.add(USER1_USERNAME);
	users.add(USER2_USERNAME);
	SimpleUserSet userSet = new SimpleUserSet(users);
	config.setLobUserSet(userSet);

	UserAdder userAdder = new MockUserAdder(config);

	UserManager userManager = new MockUserManager();
	AppListProcessorFactory appListProcessorFactory = new SuicidalAppListProcessorFactory(
		appList);
	MultiThreadedUserAdjuster lobUserAdjuster = new MultiThreadedUserAdjusterLob(
		config, userManager, appListProcessorFactory);
	userAdder.setMultiThreadedUserAdjuster(lobUserAdjuster);
	try {
	    userAdder.run(null, numThreads);
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

    private List<Application> createAppList(int size) {
	List<Application> appList = new ArrayList<Application>();

	Application app;

	for (int i = 0; i < size; i++) {
	    app = new Application();
	    app.setName("app" + i);
	    app.setLocked(false);
	    appList.add(app);
	}

	return appList;
    }

    private void checkAppList(List<Application> appList, int size,
	    boolean lockedValue) {

	for (int i = 0; i < size; i++) {
	    assertEquals("app" + i, appList.get(i).getName());
	    assertEquals(lockedValue, appList.get(i).isLocked());
	}

    }

}
