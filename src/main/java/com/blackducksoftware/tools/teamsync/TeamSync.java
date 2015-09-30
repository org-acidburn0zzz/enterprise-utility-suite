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

import com.blackducksoftware.tools.common.CommonHarness;
import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;
import com.blackducksoftware.tools.commonframework.core.config.server.ServerBean;
import com.blackducksoftware.tools.commonframework.standard.codecenter.CodeCenterServerWrapper;

/**
 * The Main class for the TeamSync utility.
 *
 * @author sbillings
 *
 */
public class TeamSync extends CommonHarness {
    private static final String USAGE = "Expecting -config <path to config file>";

    public static void main(String[] args) {

	if (args.length != 2) {
	    usage(USAGE);
	    System.exit(1);
	}
	try {
	    args = processConfig(args);
	} catch (Exception e1) {
	    System.err.println(e1.getMessage());
	    System.exit(1);
	}

	try {
	    TeamSyncConfig config = new TeamSyncConfig(getConfigFile());

	    CodeCenterServerWrapper ccServerWrapper = initCcServerWrapper(config);
	    TeamSyncProcessor teamSyncProcessor = new TeamSyncProcessor(
		    ccServerWrapper, config);
	    teamSyncProcessor.execute();

	    System.out
		    .println("TeamSyncProcessor utility has completed successfully.");
	} catch (Exception e) {
	    System.err.println("TeamSyncProcessor utility has failed: "
		    + e.getMessage());
	    System.exit(2);
	}

    }

    private static CodeCenterServerWrapper initCcServerWrapper(
	    ConfigurationManager config) throws Exception {
	ServerBean serverBean = config.getServerBean();
	CodeCenterServerWrapper ccServerWrapper = new CodeCenterServerWrapper(
		serverBean, config);
	return ccServerWrapper;
    }

}
