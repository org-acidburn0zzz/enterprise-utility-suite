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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *******************************************************************************/

package com.blackducksoftware.tools.teamsync.membership;

import java.io.File;
import java.io.PrintStream;
import java.util.Map;
import java.util.Set;

import com.blackducksoftware.tools.common.CommonHarness;
import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterServerWrapper;
import com.blackducksoftware.tools.teamsync.TeamSyncConfig;
import com.blackducksoftware.tools.teamsync.TeamSyncProcessor;

/**
 * The Main class for the TeamSyncReport utility.
 *
 * @author sbillings
 *
 */
public class TeamSyncReport extends CommonHarness {
    private static final String USAGE = "Expecting -config <path to config file> <output filename>";

    public static void main(String[] args) {
        File outputFile = null;

        if (args.length != 3) {
            usage(USAGE);
            System.exit(1);
        }
        try {
            args = processConfig(args);

            outputFile = new File(args[0]);
            outputFile.delete();
            System.out.println("Output file: " + outputFile.getAbsolutePath());
        } catch (Exception e1) {
            System.err.println(e1.getMessage());
            System.exit(1);
        }

        try {
            TeamSyncConfig config = new TeamSyncConfig(getConfigFile());

            CodeCenterServerWrapper ccServerWrapper = initCcServerWrapper(config);
            TeamSyncProcessor teamSyncProcessor = new TeamSyncProcessor(
                    ccServerWrapper, config);
            Map<String, Set<String>> userMembershipDirectory = teamSyncProcessor.generateUserMembershipDirectory();

            PrintStream ps = new PrintStream(outputFile);

            DirectoryWriter writer = new AddUserInputFileWriter(ps);
            writer.write(userMembershipDirectory);

            System.out
                    .println("Completed successfully.");
        } catch (Exception e) {
            System.err.println("Error: "
                    + e.getMessage());
            System.exit(2);
        }

    }

    private static CodeCenterServerWrapper initCcServerWrapper(
            ConfigurationManager config) throws Exception {
        CodeCenterServerWrapper ccServerWrapper = new CodeCenterServerWrapper(config);
        return ccServerWrapper;
    }

}
