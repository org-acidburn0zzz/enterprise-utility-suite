/*******************************************************************************
 * Copyright (C) 2016 Black Duck Software, Inc.
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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;

/**
 * The configuration manager for the AppAdjuster utility.
 *
 * @author sbillings
 *
 */
public class AppAdjusterConfigManager extends ConfigurationManager {
    private static final String APP_VERSION_PROPERTY = "app.version";
    private static final String APP_ATTR_PROPERTY_NAME_SUFFIX = "name";
    private static final String APP_ATTR_PROPERTY_VALUE_SUFFIX = "value";
    private static final String APP_ATTR_PROPERTY_BASE = "app.attr";
    private final String appVersion;
    private final Map<String, String> attrValues = new HashMap<String, String>();

    public AppAdjusterConfigManager(Properties props) {
	super(props, APPLICATION.CODECENTER);
	appVersion = getProperty(APP_VERSION_PROPERTY);
	loadOptionalProperties();
    }

    public AppAdjusterConfigManager(String filename) {
	super(filename, APPLICATION.CODECENTER);
	appVersion = getProperty(APP_VERSION_PROPERTY);
	loadOptionalProperties();
    }

    /**
     * Load properties of the form: app.attr.0.name= app.attr.0.value=
     */
    private void loadOptionalProperties() {
	for (int i = 0;; i++) {
	    String attrName = getOptionalProperty(APP_ATTR_PROPERTY_BASE + "."
		    + i + "." + APP_ATTR_PROPERTY_NAME_SUFFIX);
	    if (attrName == null) {
		break;
	    }
	    String attrValue = getOptionalProperty(APP_ATTR_PROPERTY_BASE + "."
		    + i + "." + APP_ATTR_PROPERTY_VALUE_SUFFIX);
	    if (attrValue == null) {
		attrValue = "";
	    }
	    attrValues.put(attrName, attrValue);
	}
    }

    public String getAppVersion() {
	return appVersion;
    }

    public Map<String, String> getAttrValues() {
	return attrValues;
    }
}
