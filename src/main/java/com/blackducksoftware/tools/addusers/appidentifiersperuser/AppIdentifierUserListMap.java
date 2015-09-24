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

package com.blackducksoftware.tools.addusers.appidentifiersperuser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Given a file full of appIdentifier sets per username, reorganize them into
 * usernames per appIdentifier.
 *
 * Input lines are of the format:
 * Username;AppIdentifier;AppIdentifier;AppIdentifier... Data gets reorganized
 * into one list of usernames per appIdentifier.
 *
 * @author sbillings
 *
 */
public class AppIdentifierUserListMap implements Iterable<String> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass()
	    .getName());
    private static final String INPUT_LINE_EXPECTED_FORMAT_MESSAGE = "Expected format: <Username>;<AppIdentifier>... You must include one Username and one or more AppIdentifiers separated by semicolons";
    private Map<String, List<String>> usernameAppIdentifierListMap; // AppIdentifiers
								    // per
								    // Username:
								    // the
    // data as specified in
    // input file
    private Map<String, AppIdentifierAddUserDetails> appIdentifierUsernameListMap; // Usernames
    // per
    // AppIdentifier:
    // the
    // data
    // as
    // needed
    // by
    // the
    // adduser
    // utility
    private String inputFilePath = "<none>";
    private Pattern usernamePattern;
    private Pattern appIdentifierPattern;

    /**
     * Construct from a given input file.
     *
     * @param inputFilePath
     * @param usernamePattern
     * @param appIdentifierPattern
     * @throws Exception
     */
    public AppIdentifierUserListMap(String inputFilePath,
	    Pattern usernamePattern, Pattern appIdentifierPattern)
	    throws Exception {
	this.inputFilePath = inputFilePath;

	List<String> lines = getFileContents(inputFilePath);
	init(lines, usernamePattern, appIdentifierPattern);
    }

    /**
     * Construct from the given input data (lines).
     *
     * @param lines
     * @param usernamePattern
     * @param appIdentifierPattern
     * @throws Exception
     */
    public AppIdentifierUserListMap(List<String> lines,
	    Pattern usernamePattern, Pattern appIdentifierPattern)
	    throws Exception {
	init(lines, usernamePattern, appIdentifierPattern);
    }

    /**
     * Getter for the appIdentifer:Usernames+applications map.
     *
     * @return
     */
    public Map<String, AppIdentifierAddUserDetails> getAppIdentifierUsernameListMap() {
	return appIdentifierUsernameListMap;
    }

    private void init(List<String> lines, Pattern usernamePattern,
	    Pattern appIdentifierPattern) throws Exception {
	this.usernamePattern = usernamePattern;
	this.appIdentifierPattern = appIdentifierPattern;

	usernameAppIdentifierListMap = new HashMap<String, List<String>>();
	appIdentifierUsernameListMap = new HashMap<String, AppIdentifierAddUserDetails>();

	// input line: Username;AppIdentifier;AppIdentifier;AppIdentifier...
	int lineIndex = 0;
	for (String line : lines) {
	    lineIndex++;

	    line = line.trim();
	    if (line.startsWith("#") || line.length() == 0) {
		continue;
	    }

	    String[] fields = line.split(";");
	    if (fields.length < 2) {
		throw new Exception("Error in input file " + inputFilePath
			+ ": Invalid input format on line " + lineIndex
			+ "; line must contain at least 2 fields" + "; "
			+ INPUT_LINE_EXPECTED_FORMAT_MESSAGE);
	    }

	    String username = fixUsername(fields[0]);
	    Matcher m = this.usernamePattern.matcher(username);
	    if (!m.matches()) {
		throw new Exception("" + username + " is not a valid Username");
	    }

	    List<String> appIdentifierList = new ArrayList<String>();
	    for (int fieldIndex = 1; fieldIndex < fields.length; fieldIndex++) {

		String appIdentifier = fields[fieldIndex].trim();
		m = this.appIdentifierPattern.matcher(appIdentifier);
		if (!m.matches()) {
		    throw new Exception("" + appIdentifier
			    + " is not a valid AppIdentifier");
		}

		logger.info("Username " + username + ": AppIdentifier "
			+ appIdentifier);
		appIdentifierList.add(appIdentifier); // Update the
						      // appIdentifierList for
						      // the current
		// Username
		updateAppIdentifierUsernameListMap(username, appIdentifier); // Update
									     // the
		// appIdentifierUsernameListMap (Username
		// list for each AppIdentifier)
	    }

	    addAppIdentifierList(usernameAppIdentifierListMap, username,
		    appIdentifierList);
	}
	logger.info("Read AppIdentifiers for "
		+ usernameAppIdentifierListMap.size() + " unique Usernames");
    }

    private void updateAppIdentifierUsernameListMap(String username,
	    String appIdentifier) {
	if (appIdentifierUsernameListMap.containsKey(appIdentifier)) {
	    // add this Username to the existing AppIdentifier entry
	    AppIdentifierAddUserDetails addUserDetails = appIdentifierUsernameListMap
		    .get(appIdentifier);
	    List<String> usernameList = addUserDetails.getUsernames();
	    usernameList.add(username);
	} else {
	    // start a new AppIdentifier entry
	    List<String> usernameList = new ArrayList<String>();
	    usernameList.add(username);
	    AppIdentifierAddUserDetails appIdentifierAddUserDetails = new AppIdentifierAddUserDetails(
		    usernameList);
	    appIdentifierUsernameListMap.put(appIdentifier,
		    appIdentifierAddUserDetails);
	}
    }

    private void addAppIdentifierList(
	    Map<String, List<String>> appIdentifierUserListMap,
	    String username, List<String> appIdentifierList) {
	if (appIdentifierUserListMap.containsKey(username)) {
	    List<String> existingAppIdentifierList = appIdentifierUserListMap
		    .get(username);
	    existingAppIdentifierList.addAll(appIdentifierList);
	} else {
	    appIdentifierUserListMap.put(username, appIdentifierList);
	}
    }

    /**
     * Getter for username:appIdentifier map.
     *
     * @return
     */
    public Map<String, List<String>> getUsernameAppIdentifierListMap() {
	return usernameAppIdentifierListMap;
    }

    private List<String> getFileContents(String userAppRoleMappingFilePath)
	    throws Exception {
	List<String> contents = new ArrayList<String>();

	FileInputStream fis = null;

	try {
	    fis = new FileInputStream(userAppRoleMappingFilePath);
	} catch (FileNotFoundException e) {
	    String msg = "Unable to read the Username / AppIdentifiers mapping file: "
		    + userAppRoleMappingFilePath;
	    throw new Exception(msg + ": " + e.getMessage());
	}

	Scanner scanner = new Scanner(fis);

	logger.info("Reading Username / AppIdentifier mapping file ({})",
		userAppRoleMappingFilePath);

	while (scanner.hasNextLine()) {
	    contents.add(scanner.nextLine());
	}

	scanner.close();

	return contents;
    }

    private String fixUsername(String rawUsername) {
	String fixedUsername = rawUsername.trim();
	if (fixedUsername.length() > 0) {
	    char firstChar = fixedUsername.charAt(0);
	    if (Character.isUpperCase(firstChar)) {
		firstChar = Character.toLowerCase(firstChar);
	    }
	    fixedUsername = firstChar + fixedUsername.substring(1);
	}
	return fixedUsername;
    }

    @Override
    public Iterator<String> iterator() {
	return appIdentifierUsernameListMap.keySet().iterator();
    }
}
