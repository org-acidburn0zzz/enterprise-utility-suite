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
package com.blackducksoftware.tools.appadjuster;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.tools.addusers.TestUtils;

public class AppAdjusterConfigManagerTest {

    private static final String APP_VERSION = "Unspecified";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void test() {
	Properties props = TestUtils.configAppAdjuster("test server",
		"test user", "test password", APP_VERSION);
	props.setProperty("app.attr.0.name", "attr1");
	props.setProperty("app.attr.0.value", "attr1value");
	props.setProperty("app.attr.1.name", "attr2");
	props.setProperty("app.attr.1.value", "attr2value");
	AppAdjusterConfigManager config = new AppAdjusterConfigManager(props);

	assertEquals(APP_VERSION, config.getAppVersion());

	Map<String, String> attrValues = config.getAttrValues();
	assertEquals("attr1value", attrValues.get("attr1"));
	assertEquals("attr2value", attrValues.get("attr2"));
    }

}
