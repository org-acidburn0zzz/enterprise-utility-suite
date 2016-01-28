package com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.tools.appuseradjuster.AppUserAdder;
import com.blackducksoftware.tools.appuseradjuster.AppUserAdjuster;
import com.blackducksoftware.tools.appuseradjuster.MultiThreadedUserAdjuster;
import com.blackducksoftware.tools.appuseradjuster.TestUtils;
import com.blackducksoftware.tools.appuseradjuster.add.AddUser;
import com.blackducksoftware.tools.appuseradjuster.add.AddUserConfig;
import com.blackducksoftware.tools.appuseradjuster.add.AddUserConfig.Mode;
import com.blackducksoftware.tools.appuseradjuster.add.MockCodeCenterServerWrapper;
import com.blackducksoftware.tools.appuseradjuster.add.lobuseradjust.applist.AppListProcessorFactory;
import com.blackducksoftware.tools.commonframework.standard.datatable.DataTable;
import com.blackducksoftware.tools.commonframework.standard.datatable.writer.DataSetWriter;
import com.blackducksoftware.tools.commonframework.standard.datatable.writer.DataSetWriterStdOut;
import com.blackducksoftware.tools.commonframework.standard.datatable.writer.DataSetWriterStringList;
import com.blackducksoftware.tools.connector.codecenter.ICodeCenterServerWrapper;

public class AppListIdentifiersPerUserTest {
    private static String APPLICATION_VERSION = "v100";

    private static String[] expectedStringArray = {
            "|<all>|||||||",
            "|444-App0-PROD-CURRENT|v100|||f444555|||",
            "|444-App2-PROD-CURRENT|v100|||f444555|||",
            "|444-App1-PROD-CURRENT|v100|||f444555|||",
            "|444-App3-PROD-CURRENT|v100|||f444555|||",
            "|222-App0-PROD-CURRENT|v100|||f111222|||",
            "|222-App1-PROD-CURRENT|v100|||f111222|||",
            "|222-App2-PROD-CURRENT|v100|||f111222|||",
            "|222-App3-PROD-CURRENT|v100|||f111222|||",
            "|111-App0-PROD-CURRENT|v100|||f566884, a000000|||",
            "|111-App1-PROD-CURRENT|v100|||f566884, a000000|||",
            "|111-App2-PROD-CURRENT|v100|||f566884, a000000|||",
            "|111-App3-PROD-CURRENT|v100|||f566884, a000000|||",
            "|333-App2-PROD-CURRENT|v100|||f111222|||",
            "|333-App3-PROD-CURRENT|v100|||f111222|||",
            "|555-App0-PROD-CURRENT|v100|||f444555|||",
            "|555-App1-PROD-CURRENT|v100|||f444555|||",
            "|555-App2-PROD-CURRENT|v100|||f444555|||",
            "|555-App3-PROD-CURRENT|v100|||f444555|||",
            "|333-App0-PROD-CURRENT|v100|||f111222|||",
            "|333-App1-PROD-CURRENT|v100|||f111222|||"
    };

    private static ICodeCenterServerWrapper codeCenterServerWrapper;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        codeCenterServerWrapper = new MockCodeCenterServerWrapper();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void test() throws Exception {
        Properties props = TestUtils.configUserCreatorForLobAdjustMode("role2",
                "test server", "test user", "test password",
                APPLICATION_VERSION);
        AddUserConfig config = new AddUserConfig(props);
        config.setMode(Mode.APPIDENTIFIERS_PER_USER);

        String appIdentifiersPerUserFilename = "src/test/resources/addusers/appIdentifierUserListMapFileMultUsersPerApp.txt";
        AppIdentifierUserListMap appIdentifierUserListMap = null;

        appIdentifierUserListMap = new AppIdentifierUserListMap(
                appIdentifiersPerUserFilename,
                config.getUsernamePattern(),
                config.getAppIdentifierPattern());

        config
                .setAppIdentifierUserListMap(appIdentifierUserListMap);

        AddUser adder = new AddUser(config);

        AppUserAdjuster appUserAdjuster = new AppUserAdder(codeCenterServerWrapper);
        AppListProcessorFactory appListProcessorFactory = new AppListProcessorFactoryAppIdentifiersPerUser(
                codeCenterServerWrapper, config, appUserAdjuster);
        MultiThreadedUserAdjuster adjuster = new MultiThreadedUserAdjusterAppIdentifiersPerUser(
                config, codeCenterServerWrapper, appListProcessorFactory);

        adder.setMultiThreadedUserAdjuster(adjuster);
        adder.applyDefaultsFromConfig();

        adder.run(codeCenterServerWrapper, config.getNumThreads());
        DataTable report = adjuster.getReport();
        DataSetWriter writer = new DataSetWriterStdOut();
        writer.write(report);

        List<String> reportStrings = new ArrayList<>(report.size());
        writer = new DataSetWriterStringList(reportStrings, false);
        writer.write(report);

        assertEquals(21, report.size());
        assertTrue(reportStrings.containsAll(Arrays.asList(expectedStringArray)));
    }

}
