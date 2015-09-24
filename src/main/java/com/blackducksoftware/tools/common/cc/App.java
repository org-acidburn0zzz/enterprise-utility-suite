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

package com.blackducksoftware.tools.common.cc;

import com.blackducksoftware.sdk.codecenter.application.data.Application;

public class App {
    private String appName;
    private String appVersion;
    private String appKey;
    private Application application;

    public String getAppName() {
	return appName;
    }

    public void setAppName(String appName) {
	this.appName = appName;
    }

    public String getAppVersion() {
	return appVersion;
    }

    public void setAppVersion(String appVersion) {
	this.appVersion = appVersion;
    }

    public String getAppKey() {
	return appKey;
    }

    public void setAppKey(String appKey) {
	this.appKey = appKey;
    }

    public Application getApplication() {
	return application;
    }

    public void setApplication(Application application) {
	this.application = application;
	this.appName = application.getName();
	this.appVersion = application.getVersion();
	this.appKey = application.getId().getId();
    }

    @Override
    public String toString() {
	return "UaeApp [appName=" + appName + ", appVersion=" + appVersion
		+ ", appKey=" + appKey + "]";
    }

}
