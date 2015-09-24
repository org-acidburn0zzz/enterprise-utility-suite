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

import java.util.Map;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.commonframework.standard.common.ProjectOrApp;

public class MockProtexProject implements ProjectOrApp {
    private String name;
    private int clonedCount = 0;
    private int lockedCount = 0;

    public MockProtexProject(String name) {
	this.name = name;
    }

    public ProjectOrApp clone(String newName) {
	MockProtexProject newProject = new MockProtexProject(newName);
	clonedCount++;
	return newProject;
    }

    public ProjectOrApp clone(String newName,
	    Map<String, String> appAttrUpdates, String associatedProjectId)
	    throws CommonFrameworkException {
	return clone(newName);
    }

    public void rename(String newName) throws CommonFrameworkException {
	// TODO
    }

    public void lock() {
	lockedCount++;
    }

    public int getClonedCount() {
	return clonedCount;
    }

    public int getLockedCount() {
	return lockedCount;
    }

    public String getName() {
	return name;
    }

    public String getId() {
	return "testId";
    }

    public String getId(String name) {
	return "0";
    }
}
