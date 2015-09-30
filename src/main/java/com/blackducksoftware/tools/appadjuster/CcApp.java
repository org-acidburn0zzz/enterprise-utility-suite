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

import java.util.Map;

import com.blackducksoftware.sdk.codecenter.application.data.Application;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationNameVersionToken;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationUpdate;
import com.blackducksoftware.sdk.codecenter.attribute.data.AbstractAttribute;
import com.blackducksoftware.sdk.codecenter.attribute.data.AttributeNameToken;
import com.blackducksoftware.sdk.codecenter.common.data.AttributeValue;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.commonframework.standard.codecenter.CodeCenterServerWrapper;

/**
 * A Code Center application.
 *
 * TODO: Ideally the cf-7x-connector CcApp should be enhanced, and should
 * replace this class.
 */
public class CcApp {
    private Application app;
    private CodeCenterServerWrapper ccServerWrapper;
    private ConfigurationManager config;

    public CcApp(ConfigurationManager config, String name, String version)
	    throws CommonFrameworkException, AppNotFoundException {
	this.config = config;

	try {
	    ccServerWrapper = new CodeCenterServerWrapper(
		    config.getServerBean(), config);
	} catch (Exception e) {
	    throw new CommonFrameworkException(this.config,
		    "Error constructing CodeCenterServerWrapper: "
			    + e.getMessage());
	}
	app = loadApp(name, version);
    }

    private Application loadApp(String name, String version)
	    throws CommonFrameworkException, AppNotFoundException {
	ApplicationNameVersionToken appToken = new ApplicationNameVersionToken();
	appToken.setName(name);
	appToken.setVersion(version);

	Application app;
	try {
	    app = ccServerWrapper.getInternalApiWrapper().getApplicationApi()
		    .getApplication(appToken);
	} catch (SdkFault e) {
	    if (e.getMessage().contains("No application found")) {
		throw new AppNotFoundException(e.getMessage());
	    } else {
		throw new CommonFrameworkException(config,
			"Error getting project " + name + ": " + e.getMessage());
	    }
	} catch (Exception e) {
	    throw new CommonFrameworkException(config, "Error getting project "
		    + name + ": " + e.getMessage());
	}
	return app;
    }

    /**
     * Set custom attributes on this application as specified by the given map
     * of attribute name/values.
     *
     * @param appAttrUpdates
     * @throws CommonFrameworkException
     */
    public void setCustomAttributes(Map<String, String> appAttrUpdates)
	    throws CommonFrameworkException {
	ApplicationUpdate appUpdate = new ApplicationUpdate();
	appUpdate.setId(app.getId());

	addCustAttrsToAppUpdate(getName(), appUpdate, appAttrUpdates);

	updateApp(appUpdate);
    }

    private void updateApp(ApplicationUpdate appUpdate)
	    throws CommonFrameworkException {
	try {
	    ccServerWrapper.getInternalApiWrapper().getApplicationApi()
		    .updateApplication(appUpdate);
	} catch (SdkFault e) {
	    throw new CommonFrameworkException(config,
		    "Error updating cloned app " + getName() + ": "
			    + e.getMessage());
	}
    }

    private void addCustAttrsToAppUpdate(String appName,
	    ApplicationUpdate appUpdate, Map<String, String> appAttrUpdates)
	    throws CommonFrameworkException {
	for (String attrName : appAttrUpdates.keySet()) {

	    AbstractAttribute abstractAttr = getAttributeIdToken(attrName,
		    appName);

	    AttributeValue attrValueObject = new AttributeValue();
	    attrValueObject.setAttributeId(abstractAttr.getId());
	    attrValueObject.getValues().add(appAttrUpdates.get(attrName));
	    appUpdate.getAttributeValues().add(attrValueObject);
	}
    }

    private AbstractAttribute getAttributeIdToken(String attrName,
	    String appName) throws CommonFrameworkException {
	AttributeNameToken attrToken = new AttributeNameToken();
	attrToken.setName(attrName);
	try {
	    return ccServerWrapper.getInternalApiWrapper().getAttributeApi()
		    .getAttribute(attrToken);
	} catch (Exception e) {
	    throw new CommonFrameworkException(config, "Error looking up attr "
		    + attrName + " for app " + appName + ": " + e.getMessage());
	}
    }

    public String getName() {
	return app.getName();
    }

    public String getId() {
	return app.getId().getId();
    }
}
