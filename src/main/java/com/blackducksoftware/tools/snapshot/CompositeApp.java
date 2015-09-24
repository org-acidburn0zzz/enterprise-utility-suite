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

public class CompositeApp {

    private final ProjectOrApp protexProject;
    private final ProjectOrApp ccApp;

    public CompositeApp(ProjectOrApp protexProject, ProjectOrApp ccApp) {
	this.protexProject = protexProject;
	this.ccApp = ccApp;
    }

    public CompositeApp createSnapshot(String suffix, String suffixToRemove,
	    Map<String, String> appAttributesToSetOnClone,
	    boolean cloneAssociatedProject) throws CommonFrameworkException {

	return doCreateSnapshot(suffix, suffixToRemove,
		appAttributesToSetOnClone, true, cloneAssociatedProject);
    }

    public CompositeApp createSnapshotAppOnly(String suffix,
	    String suffixToRemove, Map<String, String> appAttributesToSetOnClone)
	    throws CommonFrameworkException {
	return doCreateSnapshot(suffix, suffixToRemove,
		appAttributesToSetOnClone, false, false);
    }

    public void rename(String newName) throws CommonFrameworkException {
	protexProject.rename(newName);
	ccApp.rename(newName); // TODO: what if this fails. Undo first??
    }

    public ProjectOrApp getProtexProject() {
	return protexProject;
    }

    public ProjectOrApp getCcApp() {
	return ccApp;
    }

    private CompositeApp doCreateSnapshot(String suffix, String suffixToRemove,
	    Map<String, String> appAttributesToSetOnClone,
	    boolean cloneProject, boolean cloneAssociatedProject)
	    throws CommonFrameworkException {

	ProjectOrApp protexProjectClone = null;
	String associatedProjectId = null;
	if (cloneProject) {
	    protexProjectClone = protexProject.clone(generateNewName(
		    protexProject.getName(), suffix, suffixToRemove));

	    protexProjectClone.lock();

	    if (cloneAssociatedProject) {
		associatedProjectId = protexProjectClone.getId();
	    }
	}

	ProjectOrApp ccAppClone = ccApp.clone(
		generateNewName(ccApp.getName(), suffix, suffixToRemove),
		appAttributesToSetOnClone, associatedProjectId);
	ccAppClone.lock();
	CompositeApp snapshot = new CompositeApp(protexProjectClone, ccAppClone);
	return snapshot;
    }

    private String generateNewName(String origName, String suffix,
	    String suffixToRemove) {
	String newName;
	if ((suffixToRemove != null) && (origName.endsWith(suffixToRemove))) {
	    newName = origName.substring(0,
		    origName.length() - suffixToRemove.length())
		    + suffix;
	} else {
	    newName = origName + suffix;
	}
	return newName;
    }

}
