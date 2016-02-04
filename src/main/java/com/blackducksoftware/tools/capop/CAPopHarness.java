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
package com.blackducksoftware.tools.capop;

import java.io.File;

import com.blackducksoftware.tools.common.CommonHarness;

/**
 *
 * Main entry point into the Custom Attribute Populator
 *
 * @author akamen
 *
 */
public class CAPopHarness extends CommonHarness {

    private static final String CAPOP_USAGE = "Expecting [path to custom attribute values]";

    public static void main(String[] args) {

	try {
	    args = processConfig(args);
	} catch (Exception e1) {
	    System.err.println(e1.getMessage());
	    System.exit(-1);
	}

	try {

	    String fileStr = args[0];
	    if (fileStr != null) {
		File file = new File(fileStr);
		if (file.exists()) {
		    try {
			CAPopProcessor capProcessor = new CAPopProcessor(
				getConfigFile());
			capProcessor.process(file);

			System.out
				.println("Custom Attribute Populator has completed!");
		    } catch (Exception e) {
			System.err.println("Failure: " + e.getMessage());
		    }
		} else {
		    System.err.println("File does not exist at location: "
			    + fileStr);
		}
	    }

	} catch (Exception e) {
	    usage(CAPOP_USAGE);
	}

    }
}
