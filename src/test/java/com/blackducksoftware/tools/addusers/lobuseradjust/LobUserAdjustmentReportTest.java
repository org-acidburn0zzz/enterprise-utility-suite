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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.tools.addusers.UserAdjustmentReport;
import com.blackducksoftware.tools.addusers.UserCreatorConfig;
import com.blackducksoftware.tools.addusers.UserCreatorConfig.Mode;
import com.blackducksoftware.tools.commonframework.standard.datatable.DataTable;
import com.blackducksoftware.tools.commonframework.standard.datatable.Record;
import com.blackducksoftware.tools.commonframework.standard.datatable.writer.DataSetWriter;
import com.blackducksoftware.tools.commonframework.standard.datatable.writer.DataSetWriterStdOut;
import com.blackducksoftware.tools.connector.codecenter.user.UserStatus;

public class LobUserAdjustmentReportTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void test() throws Exception {
        Properties props = getBasicProperties();
        UserCreatorConfig config = new UserCreatorConfig(props);

        config.setMode(Mode.USERS_PER_LOB);
        config.setLob("testLOB");

        UserAdjustmentReport report = new UserAdjustmentReport(config, "TEST");
        report.setLob("testLob"); // LOB need only be set once, it persists

        List<String> createdUserList = new ArrayList<String>();
        createdUserList.add("createdUser1");
        createdUserList.add("userCreated2");
        report.addRecord("testAppName1", "testAppVersion1", true,
                createdUserList, listify("addedUser1"),
                userStatusListify("removedUser1"), "");

        List<UserStatus> removedUserList = new ArrayList<UserStatus>();
        removedUserList.add(new UserStatus("removedUser1", true, null));
        removedUserList.add(new UserStatus("removedUser2", true, null));
        report.addRecord("testAppName2", "testAppVersion2", false, null,
                listify("addedUser1"), removedUserList, "test message");

        DataTable table = report.getDataTable();
        DataSetWriter writer = new DataSetWriterStdOut();
        writer.write(table);

        // Iterator should get us two records
        Iterator<Record> dataSetIter = table.iterator();
        assertTrue(dataSetIter.hasNext());
        Record record = dataSetIter.next();
        assertEquals("testLob", record.getStringFieldValue("lob"));
        assertEquals("testAppName1",
                record.getStringFieldValue("applicationName"));
        assertEquals("createdUser1, userCreated2",
                record.getStringFieldValue("usersCreated"));

        // Go to 2nd record
        assertTrue(dataSetIter.hasNext());
        record = dataSetIter.next();

        assertEquals("", record.getStringFieldValue("lob"));
        assertEquals("", record.getStringFieldValue("usersCreated"));
        assertEquals("removedUser1, removedUser2",
                record.getStringFieldValue("usersRemoved"));
        assertEquals("test message", record.getStringFieldValue("message"));
        assertFalse(dataSetIter.hasNext());
    }

    @Test
    public void testDeletionErrors() throws Exception {
        Properties props = getBasicProperties();

        UserCreatorConfig config = new UserCreatorConfig(props);

        config.setMode(Mode.USERS_PER_LOB);
        config.setLob("testLOB");

        UserAdjustmentReport report = new UserAdjustmentReport(config, "TEST");
        report.setLob("testLob"); // LOB need only be set once, it persists

        List<String> createdUserList = new ArrayList<String>();
        createdUserList.add("createdUser1");
        createdUserList.add("userCreated2");
        report.addRecord("testAppName1", "testAppVersion1", true,
                createdUserList, listify("addedUser1"),
                userStatusListify("removedUser1"), "");

        List<UserStatus> removedUserList = new ArrayList<UserStatus>();
        removedUserList
                .add(new UserStatus("removedUser1", false, "testMessage"));
        removedUserList.add(new UserStatus("removedUser2", true, null));
        report.addRecord("testAppName2", "testAppVersion2", false, null,
                listify("addedUser1"), removedUserList, "test message");

        DataTable table = report.getDataTable();
        DataSetWriter writer = new DataSetWriterStdOut();
        writer.write(table);

        // Iterator should get us two records
        Iterator<Record> dataSetIter = table.iterator();
        assertTrue(dataSetIter.hasNext());
        Record record = dataSetIter.next();
        assertEquals("testLob", record.getStringFieldValue("lob"));
        assertEquals("testAppName1",
                record.getStringFieldValue("applicationName"));
        assertEquals("createdUser1, userCreated2",
                record.getStringFieldValue("usersCreated"));

        // Go to 2nd record
        assertTrue(dataSetIter.hasNext());
        record = dataSetIter.next();

        assertEquals("", record.getStringFieldValue("lob"));
        assertEquals("", record.getStringFieldValue("usersCreated"));
        assertEquals("removedUser2", record.getStringFieldValue("usersRemoved"));
        assertEquals(
                "test message; Error deleting user removedUser1: testMessage",
                record.getStringFieldValue("message"));
        assertFalse(dataSetIter.hasNext());
    }

    private List<UserStatus> userStatusListify(String s) {
        List<UserStatus> list = new ArrayList<UserStatus>(1);
        list.add(new UserStatus(s, true, null));
        return list;
    }

    private List<String> listify(String s) {
        List<String> list = new ArrayList<String>(1);
        list.add(s);
        return list;
    }

    private Properties getBasicProperties() {
        Properties props = new Properties();
        props.setProperty("cc.server.name", "notused");
        props.setProperty("cc.user.name", "notused");
        props.setProperty("cc.password", "notused");
        props.setProperty("app.version", "notused");
        props.setProperty("user.role", "notused");
        props.setProperty("username.pattern",
                "[a-z][0-9][0-9][0-9][0-9][0-9][0-9]");
        props.setProperty("appname.separator", "-");
        props.setProperty("appname.pattern.withoutdescriptionformat",
                "[0-9][0-9][0-9]+-(PROD|RC1|RC2|RC3|RC4|RC5)-CURRENT");
        props.setProperty("appname.pattern.withdescriptionformat",
                "[0-9][0-9][0-9]+-.*-(PROD|RC1|RC2|RC3|RC4|RC5)-CURRENT");
        props.setProperty("appname.pattern.followsdescription",
                "-(PROD|RC1|RC2|RC3|RC4|RC5)-CURRENT");
        props.setProperty("appname.pattern.appidentifier", "[0-9][0-9][0-9]+");
        props.setProperty("appname.pattern.suffix.0",
                "(PROD|RC1|RC2|RC3|RC4|RC5)");
        props.setProperty("appname.pattern.suffix.1", "CURRENT");

        return props;
    }
}
