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

/**
 *
 */
package com.blackducksoftware.tools.highsev;

import com.blackducksoftware.tools.common.CommonHarness;

/**
 * Main class for the High Severity application.
 *
 * Using custom provided 'custom attribute' field, does the following per
 * component:
 *
 * Grabs all catalog components and:
 *
 * 1. Gets a list of vulnerabilities 2. Determines the highest vulnerability out
 * of them 3. Populates the highest vulnerability level to the custom provided
 * attribute.
 *
 * Runs against Code Center.
 *
 * @author Ari Kamen
 * @date Jul 22, 2014
 *
 */

public class HighSevHarness extends CommonHarness {
    public static void main(String[] args) {
	try {
	    args = processConfig(args);
	} catch (Exception e1) {
	    System.err.println(e1.getMessage());
	    System.exit(-1);
	}

	try {

	    HighSevProcessor hsp = new HighSevProcessor(getConfigFile());
	    hsp.process();

	    System.out.println("Finished with High Sev!");

	} catch (Exception e) {
	    System.err.println("Error: " + e.getMessage());
	}
    }
}
