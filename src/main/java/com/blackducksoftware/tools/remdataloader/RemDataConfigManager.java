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

package com.blackducksoftware.tools.remdataloader;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;
import com.blackducksoftware.tools.commonframework.standard.codecenter.dao.CodeCenterDaoConfigManager;

public class RemDataConfigManager extends ConfigurationManager implements
	CodeCenterDaoConfigManager {
    private static final String CC_APP_VERSION_PROPERTY = "cc.app.version";
    private static final String TIME_ADJUSTMENT_HOURS = "time.correction.hours";
    private int estNumApps = 100;
    private final List<String> applicationAttributeNames = new ArrayList<String>(
	    8);
    private final String appVersion;
    private int timeAdjustmentHours = 0;

    public RemDataConfigManager(Properties props) {
	super(props, APPLICATION.CODECENTER);
	appVersion = super.getProperty(CC_APP_VERSION_PROPERTY);
	loadOptionalProperties();
	loadApplicationAttributeNames();
    }

    public RemDataConfigManager(String configFilename) {
	super(configFilename, APPLICATION.CODECENTER);
	appVersion = super.getProperty(CC_APP_VERSION_PROPERTY);
	loadOptionalProperties();
	loadApplicationAttributeNames();
    }

    private void loadOptionalProperties() {
	String estNumApplications = super
		.getOptionalProperty("est.number.applications");
	if (estNumApplications != null) {
	    estNumApps = Integer.parseInt(estNumApplications);
	}

	String timeAdjustmentHoursString = super
		.getOptionalProperty(TIME_ADJUSTMENT_HOURS);
	if (timeAdjustmentHoursString != null) {
	    timeAdjustmentHours = Integer.parseInt(timeAdjustmentHoursString);
	}
    }

    private void loadApplicationAttributeNames() {

	String appCustAttrsString = super
		.getOptionalProperty("custom.attributes.application");
	if (appCustAttrsString == null) {
	    return;
	}
	String[] appCustAttrs = appCustAttrsString.split(",\\s*");

	for (String appCustAttr : appCustAttrs) {
	    addApplicationAttribute(appCustAttr);
	}
    }

    @Override
    public int getEstNumApps() {
	return estNumApps;
    }

    @Override
    public void setEstNumApps(int estNumApps) {
	this.estNumApps = estNumApps;
    }

    @Override
    public void addApplicationAttribute(String attrName) {
	applicationAttributeNames.add(attrName);
    }

    @Override
    public List<String> getApplicationAttributeNames() {
	return applicationAttributeNames;
    }

    @Override
    public String getCcDbServerName() {
	throw new UnsupportedOperationException(
		"Database connectivity is no longer required.");
    }

    @Override
    public void setCcDbServerName(String dbServer) {
	throw new UnsupportedOperationException(
		"Database connectivity is no longer required.");
    }

    @Override
    public int getCcDbPort() {
	throw new UnsupportedOperationException(
		"Database connectivity is no longer required.");
    }

    @Override
    public void setCcDbPort(int dbPort) {
	throw new UnsupportedOperationException(
		"Database connectivity is no longer required.");
    }

    @Override
    public String getCcDbUserName() {
	throw new UnsupportedOperationException(
		"Database connectivity is no longer required.");
    }

    @Override
    public void setCcDbUserName(String dbUser) {
	throw new UnsupportedOperationException(
		"Database connectivity is no longer required.");
    }

    @Override
    public String getCcDbPassword() {
	throw new UnsupportedOperationException(
		"Database connectivity is no longer required.");
    }

    @Override
    public void setCcDbPassword(String dbPassword) {
	throw new UnsupportedOperationException(
		"Database connectivity is no longer required.");
    }

    public String getAppVersion() {
	return appVersion;
    }

    public int getTimeAdjustmentHours() {
	return timeAdjustmentHours;
    }

    @Override
    public boolean isSkipNonKbComponents() {
	return false;
    }

}
