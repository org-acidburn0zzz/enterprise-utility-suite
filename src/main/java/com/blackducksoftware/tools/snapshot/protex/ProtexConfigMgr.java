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

package com.blackducksoftware.tools.snapshot.protex;

import java.util.Properties;

import com.blackducksoftware.tools.snapshot.SnapshotConfigMgr;

/**
 * The Protex configuration manager for the snapshot utility.
 *
 * @author sbillings
 *
 */
public class ProtexConfigMgr extends SnapshotConfigMgr {

    private String appNameSuffixTimestampFormat = APPNAME_SUFFIX_TIMESTAMP_FORMAT_DEFAULT;
    private String suffixToRemove;

    public ProtexConfigMgr(Properties props) {
	super(props, APPLICATION.PROTEX);
	init();
    }

    public ProtexConfigMgr(String filename) {
	super(filename, APPLICATION.PROTEX);
	init();
    }

    private void init() {
	String dateFormatPropValue = super
		.getOptionalProperty(APPNAME_SUFFIX_TIMESTAMP_FORMAT_PROPERTY);
	if (dateFormatPropValue != null) {
	    appNameSuffixTimestampFormat = dateFormatPropValue;
	}

	String suffixToRemovePropValue = super
		.getOptionalProperty(APPNAME_SUFFIX_TO_REMOVE_PROPERTY);
	if (suffixToRemovePropValue != null) {
	    suffixToRemove = suffixToRemovePropValue;
	}
    }

    public String getAppNameSuffixTimestampFormat() {
	return appNameSuffixTimestampFormat;
    }

    public String getSuffixToRemove() {
	return suffixToRemove;
    }

}
