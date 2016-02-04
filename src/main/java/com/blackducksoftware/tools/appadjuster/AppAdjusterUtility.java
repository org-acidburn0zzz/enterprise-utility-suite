/*******************************************************************************
 * Copyright (C) 2016 Black Duck Software, Inc.
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

package com.blackducksoftware.tools.appadjuster;

/**
 * The Main class for the AppAdjuster utility.
 *
 * @author sbillings
 *
 */
public class AppAdjusterUtility {

    private static final int STATUS_SUCCESS = 0;
    private static final int STATUS_APP_NOT_FOUND = 1;
    private static final int STATUS_ERROR = 2;

    /**
     * Mainline for the AppAdjuster utility: Changes custom attr values on a
     * single app. Exit codes: 0: Success 1: Protex project does not exist -1:
     * Error (other than Protex project does not exist)
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

	AppAdjusterConfigManager config = new AppAdjusterConfigManager(
		configFilename);

	try {
	    AppAdjuster appAdjuster = new AppAdjuster(config);
	    appAdjuster.adjustApp(appName);
	} catch (AppNotFoundException e) {
	    exitCode = STATUS_APP_NOT_FOUND;
	    System.out.println(e.getMessage() + "; Exit code: " + exitCode);
	    System.exit(exitCode);
	} catch (Exception e) {
	    exitCode = STATUS_ERROR; // Error
	    System.out.println(e.getMessage() + "; Exit code: " + exitCode);
	    System.exit(exitCode);
	}

	exitCode = STATUS_SUCCESS; // Success
	System.out.println("Done. Exit code: " + exitCode);
	System.exit(exitCode);
    }
}
