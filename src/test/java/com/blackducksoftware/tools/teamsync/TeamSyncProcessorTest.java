package com.blackducksoftware.tools.teamsync;

import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.tools.connector.codecenter.ICodeCenterServerWrapper;
import com.blackducksoftware.tools.mocks.MockCodeCenterServerWrapper;

public class TeamSyncProcessorTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void test() throws Exception {
        Properties props = new Properties();
        props.setProperty("cc.server.name", "serverURL");
        props.setProperty("cc.user.name", "username");
        props.setProperty("cc.password", "password");
        props.setProperty("cc.password.isplaintext", "true");
        props.setProperty("new.app.list.filename",
                "src/test/resources/teamsync/newAppNames_sisters.txt");
        setAppNameProperties(props);
        TeamSyncConfig config = new TeamSyncConfig(props);
        ICodeCenterServerWrapper ccServerWrapper = new MockCodeCenterServerWrapper(true, true);

        TeamSyncProcessor processor = new TeamSyncProcessor(ccServerWrapper,
                config);

        processor.execute();

        // TODO: Verify
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
