package com.blackducksoftware.tools.appuseradjuster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.jmatrix.eproperties.EProperties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.tools.appuseradjuster.add.AddUserConfig;
import com.blackducksoftware.tools.appuseradjuster.add.AddUserConfig.Mode;
import com.blackducksoftware.tools.appuseradjuster.add.lobuseradjust.SimpleUserSet;
import com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser.AppIdentifierUserListMap;

public class UserCreatorConfigTest {
    private static final String APP_SUFFIX1_PATTERN_STRING = "CURRENT";
    private static final String APP_SUFFIX0_PATTERN_STRING = "(PROD|RC1|RC2|RC3|RC4|RC5)";
    private static final String APP_APPIDENTIFIER_PATTERN_STRING = "[0-9][0-9][0-9]+";
    private static final String APP_FOLLOWSDESCRIPTION_PATTERN_STRING = "-(PROD|RC1|RC2|RC3|RC4|RC5)-CURRENT";
    private static final String APP_WITH_DESCRIPTION_PATTERN_STRING = "[0-9][0-9][0-9]+-.*-(PROD|RC1|RC2|RC3|RC4|RC5)-CURRENT";
    private static final String APP_WITHOUT_DESCRIPTION_PATTERN_STRING = "[0-9][0-9][0-9]+-(PROD|RC1|RC2|RC3|RC4|RC5)-CURRENT";
    private static final String APP_SEPARATOR_STRING = "-";

    private static final String USERNAME_PATTERN_STRING = "[a-z][0-9][0-9][0-9][0-9][0-9][0-9]";

    private static AddUserConfig config;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
	EProperties props = new EProperties();
	props.setProperty("cc.user.name", "test cc user name");
	props.setProperty("cc.server.name", "test cc server name");
	props.setProperty("cc.password", "test cc password");
	props.setProperty("user.role", "test role");
	props.setProperty("username.pattern", "test username pattern");
	props.setProperty("app.name", "test app name");
	props.setProperty("add.user.request", "user1;user2");

	props.setProperty("appname.separator", APP_SEPARATOR_STRING);
	props.setProperty("appname.pattern.withoutdescriptionformat",
		APP_WITHOUT_DESCRIPTION_PATTERN_STRING);
	props.setProperty("appname.pattern.withdescriptionformat",
		APP_WITH_DESCRIPTION_PATTERN_STRING);
	props.setProperty("appname.pattern.followsdescription",
		APP_FOLLOWSDESCRIPTION_PATTERN_STRING);
	props.setProperty("appname.pattern.appidentifier",
		APP_APPIDENTIFIER_PATTERN_STRING);
	props.setProperty("appname.pattern.suffix.0",
		APP_SUFFIX0_PATTERN_STRING);
	props.setProperty("appname.pattern.suffix.1",
		APP_SUFFIX1_PATTERN_STRING);

	props.setProperty("app.version", "test version");
	props.setProperty("path", "test path");
	props.setProperty("lob.attr.name", "test lob attr name");
	props.setProperty("new.user.password", "test new user password");
	props.setProperty("report.dir", "testReportDir");
	props.setProperty("num.threads", "7");
	props.setProperty("omit.missing.lob.records.from.report", "true");
	props.setProperty("username.pattern", "test username pattern");
	props.setProperty("appname.pattern.live", "test live app pattern");

	props.setProperty("circumvent.locks", "true");
	config = new AddUserConfig(props);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void testGetApplicationName() {
	assertEquals("test app name", config.getApplicationName());
    }

    @Test
    public void testGetApplicationVersion() {
	assertEquals("test version", config.getApplicationVersion());
    }

    @Test
    public void testGetUserRole() {
	assertEquals("test role", config.getUserRole());
    }

    @Test
    public void testGetUsersToAddListString() {
	assertEquals("user1;user2", config.getUsersToAddListString());
    }

    @Test
    public void testGetFilePath() {
	assertEquals("test path", config.getFilePath());
    }

    @Test
    public void testGetMode() {
	config.setMode(Mode.APPIDENTIFIERS_PER_USER);
	assertEquals(Mode.APPIDENTIFIERS_PER_USER, config.getMode());
    }

    @Test
    public void testGetLob() {
	config.setLob("test lob");
	assertEquals("test lob", config.getLob());
    }

    @Test
    public void testGetLobUserSet() {
	SimpleUserSet expectedUserSet = new SimpleUserSet();
	expectedUserSet.add("test lob user1");
	config.setLobUserSet(expectedUserSet);
	assertEquals(expectedUserSet, config.getLobUserSet());
    }

    @Test
    public void testGetLobAttrName() {
	assertEquals("test lob attr name", config.getLobAttrName());
    }

    @Test
    public void testGetNewUserPassword() {
	assertEquals("test new user password", config.getNewUserPassword());
    }

    @Test
    public void testGetReportDir() {
	assertEquals("testReportDir", config.getReportDir());
    }

    @Test
    public void testGetNumThreads() {
	assertEquals(7, config.getNumThreads());
    }

    @Test
    public void testIsOmitMissingLobRecordsFromReport() {
	assertTrue(config.isOmitMissingLobRecordsFromReport());
    }

    @Test
    public void testGetUsernamePattern() {
	assertEquals(Pattern.compile("test username pattern").toString(),
		config.getUsernamePattern().toString());
    }

    @Test
    public void testGetLiveAppPattern() {
	assertEquals("test live app pattern", config.getLiveAppPattern()
		.toString());
    }

    @Test
    public void testGetAppIdentifierUserListMap() throws Exception {
	List<String> lines = new ArrayList<>();
	lines.add("a123456;11111;22222;33333");
	lines.add("b123456;22222");

	AppIdentifierUserListMap expectedAppIdentifierUserListMap = new AppIdentifierUserListMap(
		lines, Pattern.compile(USERNAME_PATTERN_STRING),
		Pattern.compile(APP_APPIDENTIFIER_PATTERN_STRING));
	config.setAppIdentifierUserListMap(expectedAppIdentifierUserListMap);
	assertEquals(expectedAppIdentifierUserListMap,
		config.getAppIdentifierUserListMap());
    }

    @Test
    public void testGetSeparatorString() {
	assertEquals(APP_SEPARATOR_STRING, config.getSeparatorString());
    }

    @Test
    public void testGetWithoutDescriptionFormatPatternString() {
	assertEquals(APP_WITHOUT_DESCRIPTION_PATTERN_STRING,
		config.getWithoutDescriptionFormatPatternString());
    }

    @Test
    public void testGetWithDescriptionFormatPatternString() {
	assertEquals(APP_WITH_DESCRIPTION_PATTERN_STRING,
		config.getWithDescriptionFormatPatternString());
    }

    @Test
    public void testGetAppIdentifierPatternString() {
	assertEquals(APP_APPIDENTIFIER_PATTERN_STRING,
		config.getAppIdentifierPatternString());
    }

    @Test
    public void testGetFollowsDescriptionPatternString() {
	assertEquals(APP_FOLLOWSDESCRIPTION_PATTERN_STRING,
		config.getFollowsDescriptionPatternString());
    }

    @Test
    public void testGetNumSuffixes() {
	assertEquals(2, config.getNumSuffixes());
    }

    @Test
    public void testGetSuffixPatternString() {
	assertEquals(APP_SUFFIX0_PATTERN_STRING,
		config.getSuffixPatternString(0));
	assertEquals(APP_SUFFIX1_PATTERN_STRING,
		config.getSuffixPatternString(1));
    }

    @Test
    public void testGetWithoutDescriptionFormatPattern() {
	Pattern expectedPattern = Pattern
		.compile(APP_WITHOUT_DESCRIPTION_PATTERN_STRING);
	assertEquals(expectedPattern.toString(), config
		.getWithoutDescriptionFormatPattern().toString());
    }

    @Test
    public void testGetWithDescriptionFormatPattern() {
	Pattern expectedPattern = Pattern
		.compile(APP_WITH_DESCRIPTION_PATTERN_STRING);
	assertEquals(expectedPattern.toString(), config
		.getWithDescriptionFormatPattern().toString());
    }

    @Test
    public void testGetAppIdentifierPattern() {
	Pattern expectedPattern = Pattern
		.compile(APP_APPIDENTIFIER_PATTERN_STRING);
	assertEquals(expectedPattern.toString(), config
		.getAppIdentifierPattern().toString());
    }

    @Test
    public void testGetFollowsDescriptionPattern() {
	Pattern expectedPattern = Pattern
		.compile(APP_FOLLOWSDESCRIPTION_PATTERN_STRING);
	assertEquals(expectedPattern.toString(), config
		.getFollowsDescriptionPattern().toString());
    }

    @Test
    public void testGetSuffixPattern() {
	Pattern expectedPattern = Pattern.compile(APP_SUFFIX0_PATTERN_STRING);
	assertEquals(expectedPattern.toString(), config.getSuffixPattern(0)
		.toString());

	expectedPattern = Pattern.compile(APP_SUFFIX1_PATTERN_STRING);
	assertEquals(expectedPattern.toString(), config.getSuffixPattern(1)
		.toString());
    }

    @Test
    public void testGetCircumventLocks() {
	assertTrue(config.isCircumventLocks());
    }

}
