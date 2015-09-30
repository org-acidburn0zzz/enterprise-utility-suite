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

package com.blackducksoftware.tools.common.protex;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.obligation.ObligationCategory;
import com.blackducksoftware.sdk.protex.project.CloneOption;
import com.blackducksoftware.sdk.protex.project.Project;
import com.blackducksoftware.sdk.protex.project.ProjectApi;
import com.blackducksoftware.sdk.protex.project.ProjectRequest;
import com.blackducksoftware.sdk.protex.project.RapidIdentificationMode;
import com.blackducksoftware.tools.commonframework.connector.protex.ProtexServerWrapper;
import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.commonframework.standard.common.ProjectOrApp;
import com.blackducksoftware.tools.commonframework.standard.common.ProjectPojo;
import com.blackducksoftware.tools.commonframework.standard.protex.ProtexProjectPojo;
import com.blackducksoftware.tools.snapshot.SnapshotException;
import com.blackducksoftware.tools.snapshot.SnapshotExceptionProjectNotFound;

/**
 * A Protex project that implements the ProjectOrApp interface.
 *
 * @author sbillings
 *
 */
public class ProtexProject implements ProjectOrApp {
    private final Logger log = LoggerFactory.getLogger(this.getClass()
	    .getName());
    private final ConfigurationManager config;
    private final ProtexServerWrapper<ProtexProjectPojo> wrapper;
    private ProjectPojo projectPojo;

    public ProtexProject(ConfigurationManager config, String name)
	    throws SnapshotException {
	this.config = config;

	try {
	    wrapper = new ProtexServerWrapper<ProtexProjectPojo>(
		    config.getServerBean(), config, true);
	} catch (Exception e) {
	    throw new SnapshotException("Error connecting to: "
		    + config.getServerBean().getServerName() + "; "
		    + e.getMessage());
	}

	projectPojo = getProjectByName(name);
    }

    public ProtexProject(ProtexServerWrapper<ProtexProjectPojo> protexWrapper,
	    ConfigurationManager config, String name) throws SnapshotException {
	this.config = config;
	wrapper = protexWrapper;
	projectPojo = getProjectByName(name);
    }

    /**
     *
     * Clone operation with attribute updates. appAttrUpdates and
     * associatedProjectId are ignored since this is a projex project.
     */
    @Override
    public ProjectOrApp clone(String newName,
	    Map<String, String> appAttrUpdates, String associatedProjectId)
	    throws CommonFrameworkException {
	return clone(newName);
    }

    /**
     * Simple clone operation.
     */
    @Override
    public ProjectOrApp clone(String newName) throws CommonFrameworkException {
	log.info("Cloning Protex project " + getName() + " to " + newName);
	List<CloneOption> options = new ArrayList<CloneOption>();
	options.add(CloneOption.ANALYSIS_RESULTS);
	options.add(CloneOption.ASSIGNED_USERS);
	options.add(CloneOption.COMPLETED_WORK);
	// It's important that
	// CloneOption.LINK_IDENTIFICATIONS_TO_ORIGINAL_PROJECT is NOT set
	// When set, cloning fails when rapid ID is used

	// List<String> obligations = new ArrayList<String>();
	List<ObligationCategory> obligations = new ArrayList<ObligationCategory>();

	try {
	    wrapper.getInternalApiWrapper()
		    .getProjectApi()
		    .cloneProject(projectPojo.getProjectKey(), newName,
			    options, obligations);
	} catch (SdkFault e) {
	    throw new CommonFrameworkException(config, "Error cloning project "
		    + projectPojo.getProjectName() + ": " + e.getMessage());
	}

	ProjectOrApp clone;
	try {
	    clone = new ProtexProject(config, newName);
	} catch (SnapshotException e) {
	    throw new CommonFrameworkException(config, e.getMessage());
	}
	return clone;
    }

    /**
     * Lock operation: disables RapidId on the project.
     *
     */
    @Override
    public void lock() throws CommonFrameworkException {
	try {
	    disableRapidId();
	} catch (SnapshotException e) {
	    throw new CommonFrameworkException(config, e.getMessage());
	}
    }

    /**
     * Rename operation.
     *
     */
    @Override
    public void rename(String newName) throws CommonFrameworkException {
	ProjectRequest projectUpdateRequest = new ProjectRequest();
	projectUpdateRequest.setName(newName);
	try {
	    wrapper.getInternalApiWrapper().getProjectApi()
		    .updateProject(this.getId(), projectUpdateRequest);
	} catch (SdkFault e) {
	    throw new CommonFrameworkException(config,
		    "Error renaming project " + getName() + ": "
			    + e.getMessage());
	}

	projectPojo = this.populateProjectBean(projectPojo.getProjectKey(),
		newName, projectPojo.getAnalyzedDate());
    }

    @Override
    public String getName() {
	return projectPojo.getProjectName();
    }

    @Override
    public String getId() {
	return projectPojo.getProjectKey();
    }

    /**
     * Get the ID of a different project/app, or null if it doesn't exist.
     *
     * @param targetAppName
     * @return
     */
    @Override
    public String getId(String targetAppName) {
	log.info("Fetching the ID of project: " + targetAppName);

	String targetProjectId = null;

	try {
	    ProjectPojo targetProject = getProjectByName(targetAppName);
	    targetProjectId = targetProject.getProjectKey();
	} catch (SnapshotException e) {
	    log.warn("Project " + targetAppName + " not found: "
		    + e.getMessage());
	}

	return targetProjectId;
    }

    public Date getLastAnalyzedDate() {
	Project project = null;
	try {
	    project = wrapper.getInternalApiWrapper().getProjectApi()
		    .getProjectById(this.getId());
	} catch (SdkFault e) {
	    log.error("Unable to get project " + getName());
	}

	return project.getLastAnalyzedDate();
    }

    /**
     * Get a project from Protex. TODO: This is duplicated (all but the
     * exception handling) from ProtexServerWrapper. Would be better if
     * ProtexServerWrapper.getProjectByName(String) threw a "project not
     * found"-specific exception, so we could call it instead of duplicating the
     * code here.
     *
     * @param projectName
     * @return
     * @throws CommonFrameworkException
     */
    private ProjectPojo getProjectByName(String projectName)
	    throws SnapshotException {
	ProtexProjectPojo pojo = null;

	ProjectApi projectAPI = wrapper.getInternalApiWrapper().getProjectApi();
	Project proj = null;
	try {
	    proj = projectAPI.getProjectByName(projectName.trim());
	} catch (SdkFault e) {
	    throw new SnapshotExceptionProjectNotFound(
		    "Unable to find project by the name of: " + projectName);
	}

	if (proj == null) {
	    throw new SnapshotException(
		    "Project name specified, resulted in empty project object:"
			    + projectName);
	}

	pojo = populateProjectBean(proj);

	return pojo;
    }

    private ProtexProjectPojo populateProjectBean(Project proj) {
	DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
	Date lastAnalyzedDate = proj.getLastAnalyzedDate();
	String prettyAnalyzedDate = null;
	if (lastAnalyzedDate != null) {
	    prettyAnalyzedDate = df.format(lastAnalyzedDate);
	}

	return populateProjectBean(proj.getProjectId(), proj.getName(),
		prettyAnalyzedDate);
    }

    private ProtexProjectPojo populateProjectBean(String id, String name,
	    String analyzedDate) {
	ProtexProjectPojo pojo = new ProtexProjectPojo(id, name);

	if (analyzedDate != null) {
	    try {
		pojo.setAnalyzedDate(analyzedDate);
		log.debug("Set project last analyzed date: " + analyzedDate);
	    } catch (Exception e) {
		log.warn("Unable to set analyzed date in project " + name);
	    }
	}

	return pojo;
    }

    private void disableRapidId() throws SnapshotException {
	try {
	    wrapper.getInternalApiWrapper()
		    .getProjectApi()
		    .updateRapidIdentificationMode(projectPojo.getProjectKey(),
			    RapidIdentificationMode.DISABLED);
	} catch (SdkFault e) {
	    throw new SnapshotException("Error disabling RapidId on "
		    + getName() + ": " + e.getMessage());
	}
    }
}
