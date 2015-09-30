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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.blackducksoftware.tools.common.protex.ProtexProject;
import com.blackducksoftware.tools.commonframework.standard.codecenter.pojo.CcApp;
import com.blackducksoftware.tools.snapshot.cc.CcConfigMgr;
import com.blackducksoftware.tools.snapshot.protex.ProtexConfigMgr;

/**
 * The Main class for the snapshot utility.
 *
 * @author sbillings
 *
 */
public class SnapshotUtility {

    private static final int STATUS_SUCCESS = 0;
    private static final int STATUS_PROJECT_NOT_FOUND = 1;
    private static final int STATUS_ERROR = 2;
    private static final long TIME_VALUE_OF_JAN1_2000 = 946702800000L;
    private static final long TIME_VALUE_NEVER_ANALYZED = TIME_VALUE_OF_JAN1_2000;

    /**
     * Mainline for the snapshot utility: Takes a snapshot of the app/project
     * pair with the given name (<appname>). Exit codes: 0: Success 1: Protex
     * project does not exist -1: Error (other than Protex project does not
     * exist)
     *
     * @param args
     */
    public static void main(String[] args) {
	int exitCode = STATUS_SUCCESS;

	if ((args.length != 3) || (!"-config".equalsIgnoreCase(args[0]))) {
	    System.out
		    .println("Expected arguments: -config <configfile> <appname>");
	    exitCode = STATUS_ERROR;
	    System.exit(exitCode);
	}
	String configFilename = args[1];
	String appName = args[2];

	ProtexConfigMgr protexConfig = new ProtexConfigMgr(configFilename);

	try {
	    ProtexProject protexProject = null;
	    try {
		protexProject = new ProtexProject(protexConfig, appName);
	    } catch (SnapshotExceptionProjectNotFound e) {
		exitCode = STATUS_PROJECT_NOT_FOUND; // Protex project not found
		System.out.println(e.getMessage() + "; Exit code: " + exitCode);
		System.exit(exitCode);
	    }

	    CcConfigMgr ccConfig = new CcConfigMgr(configFilename);
	    CcApp ccApp = new CcApp(ccConfig, appName,
		    ccConfig.getAppVersion(),
		    ccConfig.getClonedAppWorkflowName());

	    CompositeApp currentComposite = new CompositeApp(protexProject,
		    ccApp);

	    String appNameSuffix = generateTimestamp(
		    protexProject.getLastAnalyzedDate(),
		    protexConfig.getAppNameSuffixTimestampFormat());
	    String suffixToRemove = protexConfig.getSuffixToRemove();
	    if (protexConfig.isSnapshotAppOnly()) {
		currentComposite
			.createSnapshotAppOnly(appNameSuffix, suffixToRemove,
				ccConfig.getAppAttributesToSetOnClone());
	    } else {
		currentComposite.createSnapshot(appNameSuffix, suffixToRemove,
			ccConfig.getAppAttributesToSetOnClone(),
			ccConfig.isCloneProtexAssociation());
	    }
	} catch (Exception e) {
	    exitCode = STATUS_ERROR; // Error
	    System.out.println(e.getMessage() + "; Exit code: " + exitCode);
	    System.exit(exitCode);
	}

	exitCode = STATUS_SUCCESS; // Success
	System.out.println("Done. Exit code: " + exitCode);
	System.exit(exitCode);
    }

    static String generateTimestamp(Date date, String dateFormatString) {
	if (date == null) {
	    date = new Date(TIME_VALUE_NEVER_ANALYZED);
	}
	DateFormat dateFormat = new SimpleDateFormat(dateFormatString);
	String timestamp = dateFormat.format(date);
	return timestamp;
    }

}
