package com.blackducksoftware.tools.teamsync;

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.tools.common.cc.AppList;

public class TeamSyncConfigTest {
	private static final String APP_SUFFIX1_PATTERN_STRING = "CURRENT";
	private static final String APP_SUFFIX0_PATTERN_STRING = "(PROD|RC1|RC2|RC3|RC4|RC5)";
	private static final String APP_APPIDENTIFIER_PATTERN_STRING = "[0-9][0-9][0-9]+";
	private static final String APP_FOLLOWSDESCRIPTION_PATTERN_STRING = "-(PROD|RC1|RC2|RC3|RC4|RC5)-CURRENT";
	private static final String APP_WITH_DESCRIPTION_PATTERN_STRING = "[0-9][0-9][0-9]+-.*-(PROD|RC1|RC2|RC3|RC4|RC5)-CURRENT";
	private static final String APP_WITHOUT_DESCRIPTION_PATTERN_STRING = "[0-9][0-9][0-9]+-(PROD|RC1|RC2|RC3|RC4|RC5)-CURRENT";
	private static final String APP_SEPARATOR_STRING = "-";
	private static final String TEST_APP_NAME = "12345-test-PROD-CURRENT";
	private static TeamSyncConfig config;
	private static File newAppListFile;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		newAppListFile = File.createTempFile("TeamSyncConfigTest",
				"newAppListFilename");
		newAppListFile.deleteOnExit();

		final FileWriter fw = new FileWriter(newAppListFile.getAbsoluteFile());
		final BufferedWriter bw = new BufferedWriter(fw);
		bw.write(TEST_APP_NAME);
		bw.close();

		final Properties props = new Properties();
		props.setProperty("cc.user.name", "test cc user name");
		props.setProperty("cc.server.name", "test cc server name");
		props.setProperty("cc.password", "test cc password");
		props.setProperty("new.app.list.filename",
				newAppListFile.getAbsolutePath());

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
		config = new TeamSyncConfig(props);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}

	@Test
	public void testGetNewAppList() throws IOException {
		final AppList expectedAppList = new AppList();
		expectedAppList.add(TEST_APP_NAME);
		assertEquals(expectedAppList, config.getNewAppList());
	}

	@Test
	public void testGetAppVersion() {
		assertEquals("test version", config.getAppVersion());
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
		final Pattern expectedPattern = Pattern
				.compile(APP_WITHOUT_DESCRIPTION_PATTERN_STRING);
		assertEquals(expectedPattern.toString(), config
				.getWithoutDescriptionFormatPattern().toString());
	}

	@Test
	public void testGetWithDescriptionFormatPattern() {
		final Pattern expectedPattern = Pattern
				.compile(APP_WITH_DESCRIPTION_PATTERN_STRING);
		assertEquals(expectedPattern.toString(), config
				.getWithDescriptionFormatPattern().toString());
	}

	@Test
	public void testGetAppIdentifierPattern() {
		final Pattern expectedPattern = Pattern
				.compile(APP_APPIDENTIFIER_PATTERN_STRING);
		assertEquals(expectedPattern.toString(), config
				.getAppIdentifierPattern().toString());
	}

	@Test
	public void testGetFollowsDescriptionPattern() {
		final Pattern expectedPattern = Pattern
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

}
