package com.blackducksoftware.tools.teamsync;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.tools.connector.codecenter.ICodeCenterServerWrapper;
import com.blackducksoftware.tools.mocks.MockApplicationManager;
import com.blackducksoftware.tools.mocks.MockCodeCenterServerWrapper;

public class TeamSyncProcessorTest {

    private static String[] expectedUserToAppIdEntries = {
            "u000002: 000000,100000,",
            "u000001: 000000,100000,",
            "u000000: 200000,300000,900000,400000,700000,800000,500000,600000,000000,100000,",
            "u000006: 000000,",
            "u000005: 000000,",
            "u000004: 000000,",
            "u000003: 000000,"
    };

    private static String[] expectedOperations = {
            "add: 333333-PROD-CURRENT: [u000001]",
            "add: 333333-PROD-CURRENT: [u000002]",
            "add: 333333-PROD-CURRENT: [u000003]",
            "add: 333333-PROD-CURRENT: [u000004]",
            "add: 333333-test app-RC1-CURRENT: [u000001]",
            "add: 333333-test app-RC1-CURRENT: [u000002]",
            "add: 333333-test app-RC1-CURRENT: [u000003]",
            "add: 333333-test app-RC1-CURRENT: [u000004]",
            "add: 444444-test app-PROD-CURRENT: [u000001]",
            "add: 444444-test app-PROD-CURRENT: [u000002]",
            "add: 444444-test app-PROD-CURRENT: [u000003]",
            "add: 444444-test app-PROD-CURRENT: [u000004]"
    };

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void testTeamSync() throws Exception {
        Properties props = new Properties();
        props.setProperty("cc.server.name", "serverURL");
        props.setProperty("cc.user.name", "username");
        props.setProperty("cc.password", "password");
        props.setProperty("cc.password.isplaintext", "true");
        props.setProperty("new.app.list.filename",
                "src/test/resources/teamsync/newAppNames_complex.txt");
        setAppNameProperties(props);
        TeamSyncConfig config = new TeamSyncConfig(props);
        ICodeCenterServerWrapper ccServerWrapper = new MockCodeCenterServerWrapper(true, true);

        TeamSyncProcessor processor = new TeamSyncProcessor(ccServerWrapper,
                config);

        processor.updateNewAppsTeams();

        // Verify

        MockApplicationManager mockAppMgr = (MockApplicationManager) ccServerWrapper.getApplicationManager();
        SortedSet<String> ops = mockAppMgr.getOperations();
        System.out.println("Operations:");
        for (String op : ops) {
            System.out.println("\t" + op);
        }

        assertEquals(expectedOperations.length, ops.size());
        assertTrue(ops.containsAll(Arrays.asList(expectedOperations)));
    }

    @Test
    public void testGenerateUserMembershipDirectory() throws Exception {

        Properties props = new Properties();
        props.setProperty("cc.server.name", "serverURL");
        props.setProperty("cc.user.name", "username");
        props.setProperty("cc.password", "password");
        props.setProperty("cc.password.isplaintext", "true");
        props.setProperty("new.app.list.filename",
                "src/test/resources/teamsync/newAppNames_complex.txt");
        setAppNameProperties(props);
        TeamSyncConfig config = new TeamSyncConfig(props);
        ICodeCenterServerWrapper ccServerWrapper = new MockCodeCenterServerWrapper(true, true);

        TeamSyncProcessor processor = new TeamSyncProcessor(ccServerWrapper,
                config);

        Map<String, Set<String>> directory = processor.generateUserMembershipDirectory();

        // Verify

        List<String> expectedUserToAppIdEntriesList = Arrays.asList(expectedUserToAppIdEntries);
        for (String username : directory.keySet()) {
            StringBuilder entryString = new StringBuilder(username);
            entryString.append(": ");
            System.out.print("\t" + username + ": ");
            for (String appId : directory.get(username)) {
                System.out.print(appId + ",");
                entryString.append(appId);
                entryString.append(',');
            }
            System.out.println();
            assertTrue(expectedUserToAppIdEntriesList.contains(entryString.toString()));

        }

        assertEquals(expectedUserToAppIdEntries.length, directory.size());
    }

    private void setAppNameProperties(Properties props) {

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
    }
}
