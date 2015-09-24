package com.blackducksoftware.tools.addusers.appidentifiersperuser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.sdk.codecenter.application.data.Application;
import com.blackducksoftware.tools.addusers.TestUtils;
import com.blackducksoftware.tools.addusers.UserCreatorConfig;

public class AppListFilterTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void test() {
	Properties props = TestUtils
		.configUserCreatorForAppIdentifiersPerUserMode("not used",
			"not used", "not used", "not used", "Unspecified");
	UserCreatorConfig config = new UserCreatorConfig(props);

	List<Application> unfilteredAppList = new ArrayList<Application>(6);

	unfilteredAppList
		.add(createApp("123-test-PROD-CURRENT", "Unspecified")); // a
									 // match
	unfilteredAppList
		.add(createApp("1230-test-PROD-CURRENT", "Unspecified")); // wrong
									  // appIdentifier
	unfilteredAppList.add(createApp("123-test-PROD-CURRENT", "v100")); // wrong
									   // version
	unfilteredAppList.add(createApp("123-test-PROD-100", "v100")); // not
								       // "live"

	String appIdentifier = "123";
	AppListFilter filter = new AppListFilter(config, unfilteredAppList,
		appIdentifier);

	List<Application> filteredApps = filter.getFilteredList();
	assertEquals(1, filteredApps.size());
	assertEquals("123-test-PROD-CURRENT", filteredApps.get(0).getName());
	assertEquals("Unspecified", filteredApps.get(0).getVersion());
    }

    @Test
    public void testEverythingIsLive() {
	Properties props = TestUtils
		.configUserCreatorForAppIdentifiersPerUserMode("not used",
			"not used", "not used", "not used", "Unspecified");
	String t = props.getProperty("appname.pattern.live");
	assertTrue(t != null); // make sure we're
			       // about to remove
			       // the right
			       // property
	props.remove("appname.pattern.live"); // consider everything live
	t = props.getProperty("appname.pattern.live");
	assertTrue(t == null);

	UserCreatorConfig config = new UserCreatorConfig(props);

	List<Application> unfilteredAppList = new ArrayList<Application>(6);

	unfilteredAppList
		.add(createApp("123-test-PROD-CURRENT", "Unspecified")); // a
									 // match
	unfilteredAppList
		.add(createApp("1230-test-PROD-CURRENT", "Unspecified")); // wrong
									  // appIdentifier
	unfilteredAppList.add(createApp("123-test-PROD-CURRENT", "v100")); // wrong
									   // version
	unfilteredAppList.add(createApp("123-test-PROD-100", "Unspecified")); // not
									      // "live",
									      // but
									      // should
									      // still
									      // match

	String appIdentifier = "123";
	AppListFilter filter = new AppListFilter(config, unfilteredAppList,
		appIdentifier);

	List<Application> filteredApps = filter.getFilteredList();
	assertEquals(2, filteredApps.size()); // the -100 app should match too
    }

    private Application createApp(String name, String version) {
	Application app = new Application();
	app.setName(name);
	app.setVersion(version);
	return app;
    }

}
