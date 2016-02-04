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

package com.blackducksoftware.tools.common.cc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A list of application names.
 *
 * @author sbillings
 *
 */
public class AppList implements Iterable<String> {
    private final List<String> appNames = new ArrayList<String>();

    public AppList() throws IOException {
    }

    /**
     * Construct the list of app names from the given input text file. The file
     * contains one app name per line. Leading / trailing whitespace is OK, but
     * no other text must appear on the line. Empty lines are OK.
     *
     * @param filename
     * @throws IOException
     */
    public AppList(String filename) throws IOException {
	BufferedReader br = null;

	String line;
	try {
	    br = new BufferedReader(new FileReader(filename));
	    while ((line = br.readLine()) != null) {
		String appName = line.trim();
		if (appName.length() > 0) {
		    appNames.add(appName);
		}
	    }
	} finally {
	    if (br != null) {
		br.close();
	    }
	}
    }

    public void add(String appName) {
	appNames.add(appName);
    }

    public int size() {
	return appNames.size();
    }

    public boolean contains(String appName) {
	return appNames.contains(appName);
    }

    @Override
    public Iterator<String> iterator() {
	return appNames.iterator();
    }

    @Override
    public String toString() {
	return "AppList [appNames=" + appNames + "]";
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
		+ ((appNames == null) ? 0 : appNames.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	AppList other = (AppList) obj;
	if (appNames == null) {
	    if (other.appNames != null) {
		return false;
	    }
	} else if (!appNames.equals(other.appNames)) {
	    return false;
	}
	return true;
    }

}
