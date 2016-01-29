package com.blackducksoftware.tools.appuseradjuster.remove;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Properties;
import java.util.SortedSet;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.tools.appuseradjuster.MultiThreadedUserAdjuster;
import com.blackducksoftware.tools.appuseradjuster.TestUtils;
import com.blackducksoftware.tools.appuseradjuster.add.MockApplicationManager;
import com.blackducksoftware.tools.appuseradjuster.add.MockCodeCenterServerWrapper;
import com.blackducksoftware.tools.appuseradjuster.add.lobuseradjust.applist.AppListProcessorFactory;
import com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser.AppIdentifierUserListMap;
import com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser.AppListProcessorFactoryAppIdentifiersPerUser;
import com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser.AppUserAdjuster;
import com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser.MultiThreadedUserAdjusterAppIdentifiersPerUser;
import com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser.remove.AppUserRemover;
import com.blackducksoftware.tools.connector.codecenter.ICodeCenterServerWrapper;

public class RemoveUsersTest {
    private static String APPLICATION_VERSION = "v100";

    private static String[] expectedAddUserOperations = {
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

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void testRemoveFromGivenApps() throws Exception {
        ICodeCenterServerWrapper codeCenterServerWrapper = new MockCodeCenterServerWrapper(true);
        Properties props = TestUtils.configUserCreatorForAppIdentifiersPerUserMode("role2",
                "test server", "test user", "test password",
                APPLICATION_VERSION);
        RemoveUsersConfig config = new RemoveUsersConfig(props);

        String appIdentifiersPerUserFilename = "src/test/resources/addusers/appIdentifierUserListMapFileMultUsersPerApp.txt";
        AppIdentifierUserListMap appIdentifierUserListMap = null;

        appIdentifierUserListMap = new AppIdentifierUserListMap(
                appIdentifiersPerUserFilename,
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
            System.out.println(op);
        }
        assertTrue(removeOperations.containsAll(Arrays.asList(expectedAddUserOperations)));
    }

    @Test
    public void testRemoveFromAllApps() throws Exception {
        ICodeCenterServerWrapper codeCenterServerWrapper = new MockCodeCenterServerWrapper(true);
        Properties props = TestUtils.configUserCreatorForAppIdentifiersPerUserMode("role2",
                "test server", "test user", "test password",
                APPLICATION_VERSION);
        RemoveUsersConfig config = new RemoveUsersConfig(props);

        String appIdentifiersPerUserFilename = "src/test/resources/addusers/appIdentifierUserListMapFileNoAppSpecified.txt";
        AppIdentifierUserListMap appIdentifierUserListMap = null;

        appIdentifierUserListMap = new AppIdentifierUserListMap(
                appIdentifiersPerUserFilename,
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
            System.out.println(op);
        }
        // assertTrue(removeOperations.containsAll(Arrays.asList(expectedAddUserOperations)));
        // TODO: Need to get this functionality working, and verify it here
    }

}
