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

import java.util.ArrayList;
import java.util.List;

public class AppList {
    private boolean userSpecifiedSubset = false;
    private List<App> list;
    private final List<App> invalidList; // list of user specified but invalid
					 // projects

    public AppList() {
	list = new ArrayList<App>(1000);
	invalidList = new ArrayList<App>(10);
    }

    public AppList(List<App> list) {
	this.list = list;
	invalidList = new ArrayList<App>(10);
    }

    public void setList(List<App> list) {
	this.list = list;
    }

    public void addApp(App app) {
	list.add(app);
    }

    public void addInvalidProject(String appName, String appVersion) {
	App project = new App();
	project.setAppName(appName);
	project.setAppVersion(appVersion);
	invalidList.add(project);
    }

    public boolean isUserSpecifiedSubset() {
	return userSpecifiedSubset;
    }

    public void setUserSpecifiedSubset(boolean userSpecifiedSubset) {
	this.userSpecifiedSubset = userSpecifiedSubset;
    }

    public List<App> getList() {
	return list;
    }

    public List<App> getInvalidList() {
	return invalidList;
    }

    public int size() {
	return list.size();
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	for (App app : getList()) {
	    builder.append("App name: " + app.getAppName() + "; ");
	    builder.append("version: " + app.getAppVersion() + "; ");
	    builder.append("ID: " + app.getAppKey());
	    builder.append("\n");
	}
	return builder.toString();
    }
}
