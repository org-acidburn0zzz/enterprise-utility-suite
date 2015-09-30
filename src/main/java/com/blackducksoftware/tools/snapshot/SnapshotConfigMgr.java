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

package com.blackducksoftware.tools.snapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;
import com.blackducksoftware.tools.commonframework.standard.codecenter.dao.CodeCenterDaoConfigManager;

/**
 * The configuration Manager for the Snapshot utility.
 *
 * @author sbillings
 *
 */
public class SnapshotConfigMgr extends ConfigurationManager implements
	CodeCenterDaoConfigManager {
    private static final String CUSTOM_ATTRIBUTES_APPLICATION_PROPERTY = "custom.attributes.application";
    private static final String EST_NUM_APPS_PROPERTY = "est.number.applications";
    private static final String ASSOCIATE_APPLICATION_PROPERTY = "associate.project.with.app";
    private static final String SNAPSHOT_APP_ONLY_PROPERTY = "snapshot.app.only";
    private final Logger log = LoggerFactory.getLogger(this.getClass()
	    .getName());
    public static final String APPNAME_SUFFIX_TIMESTAMP_FORMAT_PROPERTY = "snapshot.suffix.dateformat";
    public static final String APPNAME_SUFFIX_TO_REMOVE_PROPERTY = "suffix.to.remove";
    public static final String APPNAME_SUFFIX_TIMESTAMP_FORMAT_DEFAULT = "-MM-dd-yyyy";
    public static final String SKIP_NON_KB_COMPONENTS_PROPERTY = "skip.non.kb.components";
    private int estNumApps = 100;
    private final List<String> applicationAttributeNames = new ArrayList<String>(
	    8);
    private boolean cloneProtexAssociation = false;
    private boolean snapshotAppOnly = false; // If true, skips the Protex
					     // snapshot
    private boolean skipNonKbComponents = true;

    public SnapshotConfigMgr(Properties props, APPLICATION appType) {
	super(props, appType);
	loadAdditionalProperties();
	loadApplicationAttributeNames();
    }

    public SnapshotConfigMgr(String filename, APPLICATION appType) {
	super(filename, appType);
	loadAdditionalProperties();
	loadApplicationAttributeNames();
    }

    private void loadAdditionalProperties() {
	try {
	    String estNumApplications = super
		    .getOptionalProperty(EST_NUM_APPS_PROPERTY);
	    if (estNumApplications != null) {
		estNumApps = Integer.parseInt(estNumApplications);
	    }

	    String associateString = getOptionalProperty(ASSOCIATE_APPLICATION_PROPERTY);
	    if ("true".equalsIgnoreCase(associateString)) {
		cloneProtexAssociation = true;
	    }

	    String snapshotAppOnlyString = getOptionalProperty(SNAPSHOT_APP_ONLY_PROPERTY);
	    if ("true".equalsIgnoreCase(snapshotAppOnlyString)) {
		snapshotAppOnly = true;
	    }

	    String skipNonKbComponentsString = getOptionalProperty(SKIP_NON_KB_COMPONENTS_PROPERTY);
	    if ("false".equalsIgnoreCase(skipNonKbComponentsString)) {
		skipNonKbComponents = false;
	    }
	} catch (IllegalArgumentException e) {
	    log.error("A required property is missing from the configuration file: "
		    + e.getMessage());
	    throw e;
	}
    }

    private void loadApplicationAttributeNames() {

	String appCustAttrsString = super
		.getOptionalProperty(CUSTOM_ATTRIBUTES_APPLICATION_PROPERTY);
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

    public boolean isCloneProtexAssociation() {
	return cloneProtexAssociation;
    }

    public boolean isSnapshotAppOnly() {
	return snapshotAppOnly;
    }

    @Override
    public boolean isSkipNonKbComponents() {
	return skipNonKbComponents;
    }

    @Override
    public String getCcDbServerName() {
	throw new UnsupportedOperationException(
		"Database access is no longer required");
    }

    @Override
    public void setCcDbServerName(String dbServer) {
	throw new UnsupportedOperationException(
		"Database access is no longer required");
    }

    @Override
    public int getCcDbPort() {
	throw new UnsupportedOperationException(
		"Database access is no longer required");
    }

    @Override
    public void setCcDbPort(int dbPort) {
	throw new UnsupportedOperationException(
		"Database access is no longer required");
    }

    @Override
    public String getCcDbUserName() {
	throw new UnsupportedOperationException(
		"Database access is no longer required");
    }

    @Override
    public void setCcDbUserName(String dbUser) {
	throw new UnsupportedOperationException(
		"Database access is no longer required");
    }

    @Override
    public String getCcDbPassword() {
	throw new UnsupportedOperationException(
		"Database access is no longer required");
    }

    @Override
    public void setCcDbPassword(String dbPassword) {
	throw new UnsupportedOperationException(
		"Database access is no longer required");
    }

}
