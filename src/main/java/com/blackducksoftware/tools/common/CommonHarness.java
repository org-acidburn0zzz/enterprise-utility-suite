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
package com.blackducksoftware.tools.common;

import java.io.File;
import java.util.ArrayList;

/**
 * Common harness for the various suites
 *
 * @author akamen
 *
 */
public abstract class CommonHarness {

    private static final String CONFIG_PREFIX = "-config";
    private static File configFile;

    public static String[] processConfig(String args[]) throws Exception {
	return processConfig(args, "");
    }

    /**
     * Takes in main args and processes out the config file
     *
     * @param args
     * @return
     * @throws Exception
     */
    public static String[] processConfig(String args[], String usageInfo)
	    throws Exception {
	ArrayList<String> newargs = new ArrayList<String>();

	if (args.length == 0) {
	    throw new Exception(
		    "No arguments provided, expecting at least location of config file. "
			    + usageInfo);
	}

	if (args.length >= 2) {
	    String prefix = args[0];
	    if (prefix != null && prefix.equals(CONFIG_PREFIX)) {
		String path = args[1];
		if (path != null && path.length() > 0) {
		    configFile = new File(path);
		    if (!configFile.exists()) {
			throw new Exception(
				"The config file does not exist at location: "
					+ path);
		    }

		    // Here we remove the first two arguments and pass along the
		    // remaining arguments for
		    // custom processing
		    for (int i = 2; i < args.length; i++) {
			String newValue = args[i];
			newargs.add(newValue);
		    }

		} else {
		    throw new Exception("Expecting [path of config file]");
		}
	    } else {
		throw new Exception("Expecting prefix: " + CONFIG_PREFIX);
	    }
	} else {
	    throw new Exception("Expecting -config [path of file]");
	}

	String[] stringArgArray = newargs.toArray(new String[newargs.size()]);

	return stringArgArray;
    }

    protected static void usage(String msg) {
	System.out.println(msg);
    }

    /**
     * Returns the location of the config file during processing
     *
     * @return
     */
    public static File getConfigFile() {
	return configFile;
    }

}
