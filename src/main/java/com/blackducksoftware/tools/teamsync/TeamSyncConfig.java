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

package com.blackducksoftware.tools.teamsync;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Pattern;

import com.blackducksoftware.tools.common.EntAppNameConfigMgrDelegate;
import com.blackducksoftware.tools.common.EntAppNameConfigurationManager;
import com.blackducksoftware.tools.common.cc.AppList;
import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;

/**
 * The configuration manager for the TeamSync utility.
 *
 * @author sbillings
 *
 */
public class TeamSyncConfig extends ConfigurationManager implements
	EntAppNameConfigurationManager {
    private static final String APP_VERSION_PROPERTY = "app.version";
    private static final String APP_VERSION_DEFAULT = "Unspecified";

    private static final String NEW_APP_LIST_FILENAME_PROPERTY = "new.app.list.filename";

    private EntAppNameConfigMgrDelegate entAppNameConfigMgrDelegate;

    private String appVersion = APP_VERSION_DEFAULT;
    private AppList newAppList;

    public TeamSyncConfig(Properties props) throws IOException {
	super(props, APPLICATION.CODECENTER);
	loadAdditionalProperties();
    }

    public TeamSyncConfig(InputStream in) throws IOException {
	super(in, APPLICATION.CODECENTER);
	loadAdditionalProperties();
    }

    public TeamSyncConfig(String configFilename) throws IOException {
	super(configFilename, APPLICATION.CODECENTER);
	loadAdditionalProperties();
    }

    public TeamSyncConfig(File configFile) throws IOException {
	super(configFile.toString(), APPLICATION.CODECENTER);
	loadAdditionalProperties();
    }

    private void loadAdditionalProperties() throws IOException {

	entAppNameConfigMgrDelegate = new EntAppNameConfigMgrDelegate(
		getProps());

	String newAppListFilename = getProperty(NEW_APP_LIST_FILENAME_PROPERTY);
	newAppList = new AppList(newAppListFilename);

	appVersion = getOptionalProperty(APP_VERSION_PROPERTY,
		APP_VERSION_DEFAULT, String.class);
    }

    public AppList getNewAppList() {
	return newAppList;
    }

    public String getAppVersion() {
	return appVersion;
    }

    @Override
    public String getSeparatorString() {
	return entAppNameConfigMgrDelegate.getSeparatorString();
    }

    @Override
    public String getWithoutDescriptionFormatPatternString() {
	return entAppNameConfigMgrDelegate
		.getWithoutDescriptionFormatPatternString();
    }

    @Override
    public String getWithDescriptionFormatPatternString() {
	return entAppNameConfigMgrDelegate
		.getWithDescriptionFormatPatternString();
    }

    @Override
    public String getAppIdentifierPatternString() {
	return entAppNameConfigMgrDelegate.getAppIdentifierPatternString();
    }

    @Override
    public String getFollowsDescriptionPatternString() {
	return entAppNameConfigMgrDelegate.getFollowsDescriptionPatternString();
    }

    @Override
    public int getNumSuffixes() {
	return entAppNameConfigMgrDelegate.getNumSuffixes();
    }

    @Override
    public String getSuffixPatternString(int suffixIndex) {
	return entAppNameConfigMgrDelegate.getSuffixPatternString(suffixIndex);
    }

    @Override
    public Pattern getAppIdentifierPattern() {
	return entAppNameConfigMgrDelegate.getAppIdentifierPattern();
    }

    @Override
    public Pattern getFollowsDescriptionPattern() {
	return entAppNameConfigMgrDelegate.getFollowsDescriptionPattern();
    }

    @Override
    public Pattern getWithoutDescriptionFormatPattern() {
	return entAppNameConfigMgrDelegate.getWithoutDescriptionFormatPattern();
    }

    @Override
    public Pattern getWithDescriptionFormatPattern() {
	return entAppNameConfigMgrDelegate.getWithDescriptionFormatPattern();
    }

    @Override
    public Pattern getSuffixPattern(int suffixIndex) {
	return entAppNameConfigMgrDelegate.getSuffixPattern(suffixIndex);
    }
}
