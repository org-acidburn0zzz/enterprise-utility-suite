package com.blackducksoftware.tools.appuseradjuster.remove;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.tools.appuseradjuster.MockApplicationManager;
import com.blackducksoftware.tools.appuseradjuster.MockCodeCenterServerWrapper;
import com.blackducksoftware.tools.appuseradjuster.MockCodeCenterUserManager;
import com.blackducksoftware.tools.appuseradjuster.MultiThreadedUserAdjuster;
import com.blackducksoftware.tools.appuseradjuster.TestUtils;
import com.blackducksoftware.tools.appuseradjuster.add.lobuseradjust.applist.AppListProcessorFactory;
import com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser.AppIdentifierUserListMap;
import com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser.AppListProcessorFactoryAppIdentifiersPerUser;
import com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser.AppUserAdjuster;
import com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser.MultiThreadedUserAdjusterAppIdentifiersPerUser;
import com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser.remove.AppUserRemover;
import com.blackducksoftware.tools.commonframework.standard.datatable.DataTable;
import com.blackducksoftware.tools.commonframework.standard.datatable.Record;
import com.blackducksoftware.tools.commonframework.standard.datatable.writer.DataSetWriter;
import com.blackducksoftware.tools.commonframework.standard.datatable.writer.DataSetWriterStdOut;
import com.blackducksoftware.tools.connector.codecenter.ICodeCenterServerWrapper;

public class RemoveUsersTest {
    private static final String DEACTIVATED_USER_NAME1 = "f123456";

    private static final String DEACTIVATED_USER_NAME2 = "f234567";

    private static final String DEACTIVATED_USER_ID1 = DEACTIVATED_USER_NAME1; // Mock userManager creates userId ==
                                                                               // userName

    private static final String DEACTIVATED_USER_ID2 = DEACTIVATED_USER_NAME2;

    private static String APPLICATION_VERSION = "v100";

    private static String[] expectedOperationsRemoveFromGivenApps = {
            "remove: 111-App0-PROD-CURRENT: [a000000, f566884]",
            "remove: 111-App1-PROD-CURRENT: [a000000, f566884]",
            "remove: 111-App2-PROD-CURRENT: [a000000, f566884]",
            "remove: 111-App3-PROD-CURRENT: [a000000, f566884]",
            "remove: 222-App0-PROD-CURRENT: [f111222]",
            "remove: 222-App1-PROD-CURRENT: [f111222]",
            "remove: 222-App2-PROD-CURRENT: [f111222]",
            "remove: 222-App3-PROD-CURRENT: [f111222]",
            "remove: 333-App0-PROD-CURRENT: [f111222]",
            "remove: 333-App1-PROD-CURRENT: [f111222]",
            "remove: 333-App2-PROD-CURRENT: [f111222]",
            "remove: 333-App3-PROD-CURRENT: [f111222]",
            "remove: 444-App0-PROD-CURRENT: [f444555]",
            "remove: 444-App1-PROD-CURRENT: [f444555]",
            "remove: 444-App2-PROD-CURRENT: [f444555]",
            "remove: 444-App3-PROD-CURRENT: [f444555]",
            "remove: 555-App0-PROD-CURRENT: [f444555]",
            "remove: 555-App1-PROD-CURRENT: [f444555]",
            "remove: 555-App2-PROD-CURRENT: [f444555]",
            "remove: 555-App3-PROD-CURRENT: [f444555]"
    };

    private static String[] expectedOperationsRemoveFromAllApps = {
            "remove: 0000-App0-PROD-CURRENT: [f566884]",
            "remove: 0000-App1-PROD-CURRENT: [f566884]",
            "remove: 0000-App2-PROD-CURRENT: [f566884]",
            "remove: 0000-App3-PROD-CURRENT: [f566884]",
            "remove: 1000-App0-PROD-CURRENT: [f566884]",
            "remove: 1000-App1-PROD-CURRENT: [f566884]",
            "remove: 1000-App2-PROD-CURRENT: [f566884]",
            "remove: 1000-App3-PROD-CURRENT: [f566884]"
    };

    private static String[] expectedOperationsRemoveFromAllAppsAndDeactivate = {
            "remove: 0000-App0-PROD-CURRENT: [" + DEACTIVATED_USER_NAME1 + ", " + DEACTIVATED_USER_NAME2 + "]",
            "remove: 0000-App1-PROD-CURRENT: [" + DEACTIVATED_USER_NAME1 + ", " + DEACTIVATED_USER_NAME2 + "]",
            "remove: 0000-App2-PROD-CURRENT: [" + DEACTIVATED_USER_NAME1 + ", " + DEACTIVATED_USER_NAME2 + "]",
            "remove: 0000-App3-PROD-CURRENT: [" + DEACTIVATED_USER_NAME1 + ", " + DEACTIVATED_USER_NAME2 + "]",
            "remove: 1000-App0-PROD-CURRENT: [" + DEACTIVATED_USER_NAME1 + ", " + DEACTIVATED_USER_NAME2 + "]",
            "remove: 1000-App1-PROD-CURRENT: [" + DEACTIVATED_USER_NAME1 + ", " + DEACTIVATED_USER_NAME2 + "]",
            "remove: 1000-App2-PROD-CURRENT: [" + DEACTIVATED_USER_NAME1 + ", " + DEACTIVATED_USER_NAME2 + "]",
            "remove: 1000-App3-PROD-CURRENT: [" + DEACTIVATED_USER_NAME1 + ", " + DEACTIVATED_USER_NAME2 + "]"
    };

    private static String[] expectedOperationsNoRolesToRemove = {
            };

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void testRemoveFromGivenApps() throws Exception {
        ICodeCenterServerWrapper codeCenterServerWrapper = new MockCodeCenterServerWrapper(true, true);
        Properties props = TestUtils.configUserCreatorForAppIdentifiersPerUserMode("role2",
                "test server", "test user", "test password",
                APPLICATION_VERSION);
        RemoveUsersConfig config = new RemoveUsersConfig(props);

        List<String> inputLines = new ArrayList<>();
        inputLines.add("");
        inputLines.add("#");
        inputLines.add("");
        inputLines.add("f566884;111");
        inputLines.add("f111222; 222 ; 333 ");
        inputLines.add("f444555;444;");
        inputLines.add("F444555 ;555 ;");
        inputLines.add("a000000;111");

        AppIdentifierUserListMap appIdentifierUserListMap = null;

        appIdentifierUserListMap = new AppIdentifierUserListMap(
                inputLines,
                config.getUsernamePattern(),
                config.getAppIdentifierPattern(), true);

        config
                .setAppIdentifierUserListMap(appIdentifierUserListMap);

        AppUserAdjuster appUserAdjuster = new AppUserRemover(codeCenterServerWrapper);
        AppListProcessorFactory appListProcessorFactory = new AppListProcessorFactoryAppIdentifiersPerUser(
                codeCenterServerWrapper, config, appUserAdjuster);
        MultiThreadedUserAdjuster adjuster = new MultiThreadedUserAdjusterAppIdentifiersPerUser(
                config, codeCenterServerWrapper, appListProcessorFactory, appUserAdjuster);

        RemoveUsers adder = new RemoveUsers(config, codeCenterServerWrapper, adjuster);

        adder.run(config.getNumThreads());

        MockApplicationManager mockAppMgr = (MockApplicationManager) codeCenterServerWrapper.getApplicationManager();
        SortedSet<String> removeOperations = mockAppMgr.getOperations();

        System.out.println("(Mocked) operations:");
        for (String op : removeOperations) {
            System.out.println("\t" + op);
        }
        assertTrue(removeOperations.containsAll(Arrays.asList(expectedOperationsRemoveFromGivenApps)));
    }

    @Test
    public void testRemoveFromAllApps() throws Exception {
        ICodeCenterServerWrapper codeCenterServerWrapper = new MockCodeCenterServerWrapper(true, true);
        Properties props = TestUtils.configUserCreatorForAppIdentifiersPerUserMode("role2",
                "test server", "test user", "test password",
                APPLICATION_VERSION);
        RemoveUsersConfig config = new RemoveUsersConfig(props);

        List<String> inputLines = new ArrayList<>();
        inputLines.add("f566884; ");
        AppIdentifierUserListMap appIdentifierUserListMap = null;

        appIdentifierUserListMap = new AppIdentifierUserListMap(
                inputLines,
                config.getUsernamePattern(),
                config.getAppIdentifierPattern(), true);

        config
                .setAppIdentifierUserListMap(appIdentifierUserListMap);

        AppUserAdjuster appUserAdjuster = new AppUserRemover(codeCenterServerWrapper);
        AppListProcessorFactory appListProcessorFactory = new AppListProcessorFactoryAppIdentifiersPerUser(
                codeCenterServerWrapper, config, appUserAdjuster);
        MultiThreadedUserAdjuster adjuster = new MultiThreadedUserAdjusterAppIdentifiersPerUser(
                config, codeCenterServerWrapper, appListProcessorFactory, appUserAdjuster);

        RemoveUsers adder = new RemoveUsers(config, codeCenterServerWrapper, adjuster);
        adder.globAppIds(appIdentifierUserListMap);
        adder.run(config.getNumThreads());

        MockApplicationManager mockAppMgr = (MockApplicationManager) codeCenterServerWrapper.getApplicationManager();
        SortedSet<String> removeOperations = mockAppMgr.getOperations();

        System.out.println("(Mocked) operations:");
        for (String op : removeOperations) {
            System.out.println("\t" + op);
        }
        assertTrue(removeOperations.containsAll(Arrays.asList(expectedOperationsRemoveFromAllApps)));
    }

    @Test
    public void testRemoveFromAllAppsAndDeactivate() throws Exception {
        ICodeCenterServerWrapper codeCenterServerWrapper = new MockCodeCenterServerWrapper(true, true);
        Properties props = TestUtils.configUserCreatorForAppIdentifiersPerUserMode("role2",
                "test server", "test user", "test password",
                APPLICATION_VERSION);
        props.setProperty("deactivate.users.removed.from.all", "true");
        RemoveUsersConfig config = new RemoveUsersConfig(props);

        List<String> inputLines = new ArrayList<>();
        inputLines.add(DEACTIVATED_USER_NAME1 + "; ");
        inputLines.add(DEACTIVATED_USER_NAME2);
        AppIdentifierUserListMap appIdentifierUserListMap = null;

        appIdentifierUserListMap = new AppIdentifierUserListMap(
                inputLines,
                config.getUsernamePattern(),
                config.getAppIdentifierPattern(), true);

        config
                .setAppIdentifierUserListMap(appIdentifierUserListMap);

        AppUserAdjuster appUserAdjuster = new AppUserRemover(codeCenterServerWrapper);
        AppListProcessorFactory appListProcessorFactory = new AppListProcessorFactoryAppIdentifiersPerUser(
                codeCenterServerWrapper, config, appUserAdjuster);
        MultiThreadedUserAdjuster adjuster = new MultiThreadedUserAdjusterAppIdentifiersPerUser(
                config, codeCenterServerWrapper, appListProcessorFactory, appUserAdjuster);

        RemoveUsers adder = new RemoveUsers(config, codeCenterServerWrapper, adjuster);
        adder.globAppIds(appIdentifierUserListMap);
        adder.run(config.getNumThreads());

        MockApplicationManager mockAppMgr = (MockApplicationManager) codeCenterServerWrapper.getApplicationManager();
        SortedSet<String> removeOperations = mockAppMgr.getOperations();

        System.out.println("(Mocked) operations:");
        for (String op : removeOperations) {
            System.out.println("\t" + op);
        }
        assertTrue(removeOperations.containsAll(Arrays.asList(expectedOperationsRemoveFromAllAppsAndDeactivate)));

        MockCodeCenterUserManager mockUserManager = (MockCodeCenterUserManager) codeCenterServerWrapper.getUserManager();
        Map<String, Boolean> activeStatusChangedUsers = mockUserManager.getActiveStatusChangedUsers();
        assertEquals(2, activeStatusChangedUsers.size());
        // In this test, userId == userName
        assertEquals(Boolean.FALSE, activeStatusChangedUsers.get(DEACTIVATED_USER_ID1));
        assertEquals(Boolean.FALSE, activeStatusChangedUsers.get(DEACTIVATED_USER_ID2));
    }

    @Test
    public void testDeactivateUserWithNoRoles() throws Exception {
        ICodeCenterServerWrapper codeCenterServerWrapper = new MockCodeCenterServerWrapper(true, false);
        Properties props = TestUtils.configUserCreatorForAppIdentifiersPerUserMode("role2",
                "test server", "test user", "test password",
                APPLICATION_VERSION);
        props.setProperty("deactivate.users.removed.from.all", "true");
        RemoveUsersConfig config = new RemoveUsersConfig(props);

        List<String> inputLines = new ArrayList<>();
        inputLines.add(DEACTIVATED_USER_NAME1 + "; ");
        inputLines.add(DEACTIVATED_USER_NAME2);
        AppIdentifierUserListMap appIdentifierUserListMap = null;

        appIdentifierUserListMap = new AppIdentifierUserListMap(
                inputLines,
                config.getUsernamePattern(),
                config.getAppIdentifierPattern(), true);

        config
                .setAppIdentifierUserListMap(appIdentifierUserListMap);

        AppUserAdjuster appUserAdjuster = new AppUserRemover(codeCenterServerWrapper);
        AppListProcessorFactory appListProcessorFactory = new AppListProcessorFactoryAppIdentifiersPerUser(
                codeCenterServerWrapper, config, appUserAdjuster);
        MultiThreadedUserAdjuster adjuster = new MultiThreadedUserAdjusterAppIdentifiersPerUser(
                config, codeCenterServerWrapper, appListProcessorFactory, appUserAdjuster);

        RemoveUsers adder = new RemoveUsers(config, codeCenterServerWrapper, adjuster);
        adder.globAppIds(appIdentifierUserListMap);
        adder.run(config.getNumThreads());

        MockApplicationManager mockAppMgr = (MockApplicationManager) codeCenterServerWrapper.getApplicationManager();
        SortedSet<String> removeOperations = mockAppMgr.getOperations();

        System.out.println("(Mocked) operations:");
        for (String op : removeOperations) {
            System.out.println("\t" + op);
        }
        assertEquals(0, removeOperations.size());

        MockCodeCenterUserManager mockUserManager = (MockCodeCenterUserManager) codeCenterServerWrapper.getUserManager();
        Map<String, Boolean> activeStatusChangedUsers = mockUserManager.getActiveStatusChangedUsers();
        assertEquals(2, activeStatusChangedUsers.size());
        // In this test, userId == userName
        assertEquals(Boolean.FALSE, activeStatusChangedUsers.get(DEACTIVATED_USER_ID1));
        assertEquals(Boolean.FALSE, activeStatusChangedUsers.get(DEACTIVATED_USER_ID2));

        DataTable report = adjuster.getReport();
        DataSetWriter writer = new DataSetWriterStdOut();
        writer.write(report);

        Iterator<Record> iter = report.iterator();
        iter.next(); // Get header
        Record record = iter.next(); // Get first data row
        assertEquals(DEACTIVATED_USER_ID1, record.getStringFieldValue("usersDeActivated"));
        assertEquals("", record.getStringFieldValue("status"));
        assertEquals("", record.getStringFieldValue("message"));
        record = iter.next(); // Get second data row
        assertEquals(DEACTIVATED_USER_ID2, record.getStringFieldValue("usersDeActivated"));
        assertEquals("", record.getStringFieldValue("status"));
        assertEquals("", record.getStringFieldValue("message"));
    }

    @Test
    public void testUserHasNoRolesOnMatchingApps() throws Exception {
        ICodeCenterServerWrapper codeCenterServerWrapper = new MockCodeCenterServerWrapper(true, true);

        MockCodeCenterUserManager mockUserMgr = (MockCodeCenterUserManager) codeCenterServerWrapper.getUserManager();
        mockUserMgr.setReturnRoles(false); // tell mock to return no roles
        Properties props = TestUtils.configUserCreatorForAppIdentifiersPerUserMode("role2",
                "test server", "test user", "test password",
                APPLICATION_VERSION);
        RemoveUsersConfig config = new RemoveUsersConfig(props);

        List<String> inputLines = new ArrayList<>();
        inputLines.add("f566884");
        AppIdentifierUserListMap appIdentifierUserListMap = null;

        appIdentifierUserListMap = new AppIdentifierUserListMap(
                inputLines,
                config.getUsernamePattern(),
                config.getAppIdentifierPattern(), true);

        config
                .setAppIdentifierUserListMap(appIdentifierUserListMap);

        AppUserAdjuster appUserAdjuster = new AppUserRemover(codeCenterServerWrapper);
        AppListProcessorFactory appListProcessorFactory = new AppListProcessorFactoryAppIdentifiersPerUser(
                codeCenterServerWrapper, config, appUserAdjuster);
        MultiThreadedUserAdjuster adjuster = new MultiThreadedUserAdjusterAppIdentifiersPerUser(
                config, codeCenterServerWrapper, appListProcessorFactory, appUserAdjuster);

        RemoveUsers adder = new RemoveUsers(config, codeCenterServerWrapper, adjuster);
        adder.globAppIds(appIdentifierUserListMap);
        adder.run(config.getNumThreads());

        MockApplicationManager mockAppMgr = (MockApplicationManager) codeCenterServerWrapper.getApplicationManager();
        SortedSet<String> removeOperations = mockAppMgr.getOperations();

        System.out.println("(Mocked) operations:");
        for (String op : removeOperations) {
            System.out.println("\t" + op);
        }
        assertTrue(removeOperations.containsAll(Arrays.asList(expectedOperationsNoRolesToRemove)));
    }

    @Test
    public void testBadUsername() throws Exception {

        Properties props = TestUtils.configUserCreatorForAppIdentifiersPerUserMode("role2",
                "test server", "test user", "test password",
                APPLICATION_VERSION);
        RemoveUsersConfig config = new RemoveUsersConfig(props);

        List<String> inputLines = new ArrayList<>();
        inputLines.add("badUserName");

        try {
            new AppIdentifierUserListMap(
                    inputLines,
                    config.getUsernamePattern(),
                    config.getAppIdentifierPattern(), true);
            fail("Provided a bad username, expected an exception");
        } catch (Exception e) {
            // expected this
        }

    }

    @Test
    public void testBadAppId() throws Exception {
        Properties props = TestUtils.configUserCreatorForAppIdentifiersPerUserMode("role2",
                "test server", "test user", "test password",
                APPLICATION_VERSION);
        RemoveUsersConfig config = new RemoveUsersConfig(props);

        List<String> inputLines = new ArrayList<>();
        inputLines.add("");
        inputLines.add("#");
        inputLines.add("");
        inputLines.add("f566884;xxx");
        inputLines.add("f111222; 222 ; 333 ");
        inputLines.add("f444555;444;");
        inputLines.add("F444555 ;555 ;");
        inputLines.add("a000000;111");

        try {
            new AppIdentifierUserListMap(
                    inputLines,
                    config.getUsernamePattern(),
                    config.getAppIdentifierPattern(), true);
            fail("Provided a bad appId, expected an exception");
        } catch (Exception e) {
            // expected this
        }
    }

    @Test
    public void testEmptyInputFile() throws Exception {
        ICodeCenterServerWrapper codeCenterServerWrapper = new MockCodeCenterServerWrapper(true, true);
        Properties props = TestUtils.configUserCreatorForAppIdentifiersPerUserMode("role2",
                "test server", "test user", "test password",
                APPLICATION_VERSION);
        RemoveUsersConfig config = new RemoveUsersConfig(props);

        List<String> inputLines = new ArrayList<>();
        inputLines.add("");
        inputLines.add("#");
        inputLines.add("");

        AppIdentifierUserListMap appIdentifierUserListMap = null;

        appIdentifierUserListMap = new AppIdentifierUserListMap(
                inputLines,
                config.getUsernamePattern(),
                config.getAppIdentifierPattern(), true);

        config
                .setAppIdentifierUserListMap(appIdentifierUserListMap);

        AppUserAdjuster appUserAdjuster = new AppUserRemover(codeCenterServerWrapper);
        AppListProcessorFactory appListProcessorFactory = new AppListProcessorFactoryAppIdentifiersPerUser(
                codeCenterServerWrapper, config, appUserAdjuster);

        try {
            new MultiThreadedUserAdjusterAppIdentifiersPerUser(config, codeCenterServerWrapper, appListProcessorFactory, appUserAdjuster);
            fail("Provided empty input, expected an exception");
        } catch (Exception e) {
            // expected this
        }

    }
}
