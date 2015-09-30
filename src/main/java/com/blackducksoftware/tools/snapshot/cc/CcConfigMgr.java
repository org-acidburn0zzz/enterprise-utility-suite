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

package com.blackducksoftware.tools.snapshot.cc;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.blackducksoftware.tools.snapshot.SnapshotConfigMgr;
import com.blackducksoftware.tools.snapshot.SnapshotException;

/**
 * The Code Center configuration manager for the snapshot utility.
 *
 * @author sbillings
 *
 */
public class CcConfigMgr extends SnapshotConfigMgr {
    private static final String CC_APP_VERSION_PROPERTY = "cc.app.version";
    private static final String CC_CLONED_APP_WORKFLOW_PROPERTY = "cc.cloned.app.workflow";
    private static final String CC_APP_ATTR_NAME_PROPERTY_BASE = "cc.app.attr.name";
    private static final String CC_APP_ATTR_VALUE_PROPERTY_BASE = "cc.app.attr.value";
    private final String appVersion;
    private final String clonedAppWorkflowName;
    private final Map<String, String> appAttributesToSetOnClone = new HashMap<String, String>();

    public CcConfigMgr(Properties props) throws SnapshotException {
	super(props, APPLICATION.CODECENTER);
	appVersion = super.getProperty(CC_APP_VERSION_PROPERTY);
	clonedAppWorkflowName = super
		.getProperty(CC_CLONED_APP_WORKFLOW_PROPERTY);
	loadAttributeNameValues();
    }

    public CcConfigMgr(String filename) throws SnapshotException {
	super(filename, APPLICATION.CODECENTER);
	appVersion = super.getProperty(CC_APP_VERSION_PROPERTY);
	clonedAppWorkflowName = super
		.getProperty(CC_CLONED_APP_WORKFLOW_PROPERTY);
	loadAttributeNameValues();
    }

    public String getClonedAppWorkflowName() {
	return clonedAppWorkflowName;
    }

    public String getAppVersion() {
	return appVersion;
    }

    private void loadAttributeNameValues() throws SnapshotException {
	for (int i = 0;; i++) {
	    String attrName = super
		    .getOptionalProperty(CC_APP_ATTR_NAME_PROPERTY_BASE + "."
			    + i);
	    if (attrName == null) {
		break;
	    }
	    String attrValue = super
		    .getProperty(CC_APP_ATTR_VALUE_PROPERTY_BASE + "." + i);
	    appAttributesToSetOnClone.put(attrName, attrValue);
	}
    }

    public Map<String, String> getAppAttributesToSetOnClone() {
	if (appAttributesToSetOnClone.size() == 0) {
	    return null;
	}
	return appAttributesToSetOnClone;
    }

}
