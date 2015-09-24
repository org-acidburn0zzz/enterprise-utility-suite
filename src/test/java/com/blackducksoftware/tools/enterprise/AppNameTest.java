package com.blackducksoftware.tools.enterprise;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.tools.common.EntAppName;
import com.blackducksoftware.tools.common.EntAppNameConfigurationManager;
import com.blackducksoftware.tools.common.EnterpriseUtilitySuiteException;

public class AppNameTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void testWithDescription() throws EnterpriseUtilitySuiteException {
	Properties props = getBasicProperties();

	EntAppNameConfigurationManager config = new TestEntAppNameConfigurationManager(
		props);
	EntAppName appName = new EntAppName(config,
		"123456-test description-RC2-CURRENT");

	assertEquals("123456", appName.getAppIdentifier());
	assertEquals("test description", appName.getDescription());
	assertEquals("RC2", appName.getSuffix(0));
	assertEquals("CURRENT", appName.getSuffix(1));
	assertTrue(appName.isConformant());
    }

    @Test
    public void testNonConformantAppIdentifier() {
	Properties props = getBasicProperties();

	EntAppNameConfigurationManager config = new TestEntAppNameConfigurationManager(
		props);

	EntAppName appName = new EntAppName(config,
		"123a-test description-PROD-CURRENT");
	assertFalse(appName.isConformant());
    }

    @Test
    public void testNonConformantSuffix2()
	    throws EnterpriseUtilitySuiteException {
	Properties props = getBasicProperties();

	EntAppNameConfigurationManager config = new TestEntAppNameConfigurationManager(
		props);
	EntAppName appName = new EntAppName(config,
		"123456-test description-RC2-01-01-2000-12:00:00");

	assertFalse(appName.isConformant());
    }

    @Test
    public void testNonConformantSuffix1() {
	Properties props = getBasicProperties();

	EntAppNameConfigurationManager config = new TestEntAppNameConfigurationManager(
		props);

	EntAppName appName = new EntAppName(config,
		"Test AppIdentifier-test description-test workstream-CURRENT");
	assertFalse(appName.isConformant());
    }

    @Test
    public void testWithoutDescription() throws EnterpriseUtilitySuiteException {

	Properties props = getBasicProperties();

	EntAppNameConfigurationManager config = new TestEntAppNameConfigurationManager(
		props);
	EntAppName appName = new EntAppName(config, "123456-RC2-CURRENT");

	assertEquals("123456", appName.getAppIdentifier());
	assertEquals(null, appName.getDescription());
	assertEquals("RC2", appName.getSuffix(0));
	assertEquals("CURRENT", appName.getSuffix(1));
    }

    @Test
    public void testDescriptionWithEmbeddedSeparator()
	    throws EnterpriseUtilitySuiteException {
	Properties props = getBasicProperties();

	EntAppNameConfigurationManager config = new TestEntAppNameConfigurationManager(
		props);
	EntAppName appName = new EntAppName(config,
		"123456-test - description-RC2-CURRENT");

	assertEquals("123456", appName.getAppIdentifier());
	assertEquals("test - description", appName.getDescription());
	assertEquals("RC2", appName.getSuffix(0));
	assertEquals("CURRENT", appName.getSuffix(1));
    }

    @Test
    public void testDescriptionWithEmbeddedPunctuation()
	    throws EnterpriseUtilitySuiteException {
	Properties props = getBasicProperties();

	EntAppNameConfigurationManager config = new TestEntAppNameConfigurationManager(
		props);
	EntAppName appName = new EntAppName(config,
		"123456-test.;;/description-RC2-CURRENT");

	assertEquals("123456", appName.getAppIdentifier());
	assertEquals("test.;;/description", appName.getDescription());
	assertEquals("RC2", appName.getSuffix(0));
	assertEquals("CURRENT", appName.getSuffix(1));
    }

    @Test
    public void testToString() throws EnterpriseUtilitySuiteException {
	Properties props = getBasicProperties();

	EntAppNameConfigurationManager config = new TestEntAppNameConfigurationManager(
		props);
	EntAppName appName = new EntAppName(config,
		"123456-test description-RC2-CURRENT");

	assertEquals(
		"EntAppName [appName=123456-test description-RC2-CURRENT, appIdentifier=123456, description=test description, suffixes=[RC2, CURRENT]]",
		appName.toString());
    }

    private Properties getBasicProperties() {
	Properties props = new Properties();

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
