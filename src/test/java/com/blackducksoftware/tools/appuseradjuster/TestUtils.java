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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *******************************************************************************/

package com.blackducksoftware.tools.appuseradjuster;

import java.util.Properties;

public class TestUtils {
    private static final String LIVE_APP_PATTERN_STRING = "^.*-CURRENT$";
    private static final String USERNAME_PATTERN_STRING = "[a-z][0-9][0-9][0-9][0-9][0-9][0-9]";
    private static final String DESCRIPTION_PATTERN_STRING = ".*";
    private static final String APP_IDENTIFIER_PATTERN_STRING = "[0-9][0-9][0-9]+";
    private static final String SUFFIX_0_PATTERN_STRING = "(PROD|RC1|RC2|RC3|RC4|RC5)";
    private static final String DATE_TIME_PATTERN_STRING = "[0-1][0-9]-[0-3][0-9]-20[0-9][0-9]-[0-2][0-9]:[0-5][0-9]:[0-5][0-9]";
    private static final String SUFFIX_1_PATTERN_STRING = "(CURRENT|"
	    + DATE_TIME_PATTERN_STRING + ")$";

    public static Properties configAppAdjuster(String url, String username,
	    String password, String appVersion) {
	Properties props = createMinimalProperties(url, username, password,
		appVersion);
	return props;
    }

    public static Properties configUserCreatorForLobAdjustMode(String role,
	    String url, String username, String password, String appVersion) {
	Properties props = createCommonPropertiesForLobAdjustMode(url,
		username, password, role, appVersion);
	return props;
    }

    public static Properties configUserCreatorForAppIdentifiersPerUserMode(
	    String role, String url, String username, String password,
	    String appVersion) {
	Properties props = createCommonPropertiesForAppIdentifiersPerUserMode(
		url, username, password, role, appVersion);
	return props;
    }

    private static Properties createMinimalProperties(String url,
	    String username, String password, String appVersion) {
	Properties props = new Properties();
	props.setProperty("cc.server.name", url);
	props.setProperty("cc.user.name", username);
	props.setProperty("cc.password", password);
	props.setProperty("app.version", appVersion);
	return props;
    }

    private static Properties createCommonPropertiesForLobAdjustMode(
	    String url, String username, String password, String role,
	    String appVersion) {
	Properties props = new Properties();
	props.setProperty("lob.adjust.mode", "true");
	props.setProperty("app.version", appVersion);
	props.setProperty("cc.server.name", url);
	props.setProperty("cc.user.name", username);
	props.setProperty("cc.password", password);
	props.setProperty("user.role", role);

	addUserAndAppNamePatterns(props);
	return props;
    }

    private static Properties createCommonPropertiesForAppIdentifiersPerUserMode(
	    String url, String username, String password, String role,
	    String appVersion) {
	Properties props = new Properties();
	props.setProperty("app.version", appVersion);
	props.setProperty("cc.server.name", url);
	props.setProperty("cc.user.name", username);
	props.setProperty("cc.password", password);
	props.setProperty("user.role", role);

	addUserAndAppNamePatterns(props);
	return props;
    }

    private static void addUserAndAppNamePatterns(Properties props) {

	props.setProperty("username.pattern", USERNAME_PATTERN_STRING);
	props.setProperty("appname.separator", "-");
	props.setProperty("appname.pattern.withoutdescriptionformat",
		APP_IDENTIFIER_PATTERN_STRING + "-" + SUFFIX_0_PATTERN_STRING
			+ "-" + SUFFIX_1_PATTERN_STRING + "$");
	props.setProperty("appname.pattern.withdescriptionformat",
		APP_IDENTIFIER_PATTERN_STRING + "-"
			+ DESCRIPTION_PATTERN_STRING + "-"
			+ SUFFIX_0_PATTERN_STRING + "-"
			+ SUFFIX_1_PATTERN_STRING + "$");
	props.setProperty("appname.pattern.followsdescription", "-"
		+ SUFFIX_0_PATTERN_STRING + "-" + SUFFIX_1_PATTERN_STRING + "$");
	props.setProperty("appname.pattern.appidentifier",
		APP_IDENTIFIER_PATTERN_STRING);
	props.setProperty("appname.pattern.suffix.0", SUFFIX_0_PATTERN_STRING);
	props.setProperty("appname.pattern.suffix.1", SUFFIX_1_PATTERN_STRING);
	props.setProperty("appname.pattern.live", LIVE_APP_PATTERN_STRING);
    }

}
