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

package com.blackducksoftware.tools.appuseradjuster.add.lobuseradjust;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

/**
 * A set of usernames.
 *
 * @author sbillings
 *
 */
public class SimpleUserSet implements Iterable<String> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass()
	    .getName());
    private final Set<String> userSet;

    public SimpleUserSet() {
	userSet = new HashSet<String>();
    }

    /**
     * Construct a set of usernames from the given comma-separated list of
     * usernames.
     *
     * @param commaSeparatedUserList
     * @throws Exception
     */
    public SimpleUserSet(String commaSeparatedUserList) throws Exception {
	List<String> userList = Arrays
		.asList(commaSeparatedUserList.split(","));
	userSet = new HashSet<String>();

	int rowNum = 0;
	for (String rawUsername : userList) {
	    rowNum++;
	    logger.debug("Read user: " + rawUsername);
	    String username = fixUsername(rawUsername);
	    if (username.length() > 0) {
		if (isValidUsername(username)) {
		    userSet.add(username);
		} else {
		    String msg = "Username # " + rowNum + " is invalid";
		    logger.error(msg);
		    throw new Exception(msg);
		}
	    }
	}

    }

    /**
     * Construct a set of usernames from the given CSV file.
     *
     * @param csvFile
     * @throws Exception
     */
    public SimpleUserSet(File csvFile) throws Exception {
	List<String> userList = new ArrayList<String>();

	logger.debug("Reading LOB user list from " + csvFile.getAbsolutePath());

	try {
	    CSVReader reader = new CSVReader(new FileReader(csvFile), ',');
	    try {
		String[] record;

		// Go through each row in the file
		int rowNum = 0;
		while ((record = reader.readNext()) != null) {
		    rowNum++;
		    logger.debug("Read user: " + record[0]);
		    String username = fixUsername(record[0]);
		    if (username.length() > 0) {
			if (isValidUsername(username)) {
			    userList.add(username);
			} else {
			    String msg = "Username in row " + rowNum
				    + " of file " + csvFile.getAbsolutePath()
				    + " is invalid";
			    logger.error(msg);
			    throw new Exception(msg);
			}
		    }
		}
	    } finally {
		reader.close();
	    }
	} catch (IOException e) {
	    logger.error(
		    "Error reading user list file " + csvFile.getAbsolutePath(),
		    e);
	    throw e;
	}
	logger.debug("Read " + userList.size() + " users from "
		+ csvFile.getAbsolutePath());

	userSet = new HashSet<String>();
	userSet.addAll(userList);

	if (userSet.size() == 0) {
	    String msg = "Read zero users from "
		    + csvFile.getAbsolutePath()
		    + ". Please be sure the file complies with the format specified in the ReadMe file.";
	    logger.error(msg);
	    throw new Exception(msg);
	}
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

    private boolean isValidUsername(String username) {
	if (username.length() == 0) {
	    return false;
	}
	if (!Character.isLetter(username.charAt(0))) {
	    return false;
	}
	for (int i = 0; i < username.length(); i++) {
	    if (!isValidUsernameChar(username.charAt(i))) {
		return false;
	    }
	}
	return true;
    }

    private boolean isValidUsernameChar(char c) {
	if (Character.isLetter(c)) {
	    return true;
	}

	if (Character.isDigit(c)) {
	    return true;
	}

	return false;
    }

    /**
     * Construct a user set from the given Set of usernames.
     *
     * @param userSet
     */
    public SimpleUserSet(Set<String> userSet) {
	this.userSet = userSet;
    }

    /**
     * Returns the set of users in the form of a Set.
     *
     * @return
     */
    public Set<String> getUserSet() {
	return userSet;
    }

    /**
     * Given a set of possibly-new users, returns the subset that are actually
     * new (do not already exist in this user set).
     *
     * @param newUsers
     * @return
     */
    public SimpleUserSet getUsersToAdd(SimpleUserSet newUsers) {
	SimpleUserSet usersToAdd = new SimpleUserSet();
	if (newUsers == null) {
	    return usersToAdd;
	}
	for (String newUser : newUsers) {
	    logger.debug("newUser: " + newUser);
	    if (!userSet.contains(newUser)) {
		usersToAdd.add(newUser);
	    }
	}
	return usersToAdd;
    }

    /**
     * Given a set of possibly-removed users, returns the subset that actually
     * should be removed (do exist in this user set).
     * 
     * @param newUsers
     * @return
     */
    public SimpleUserSet getUsersToDelete(SimpleUserSet newUsers) {
	SimpleUserSet usersToDelete = new SimpleUserSet();
	if (newUsers == null) {
	    return usersToDelete;
	}
	for (String oldUser : userSet) {
	    logger.debug("oldUser: " + oldUser);
	    if (!newUsers.contains(oldUser)) {
		usersToDelete.add(oldUser);
	    }
	}
	return usersToDelete;
    }

    /**
     * Returns true if this user set contains the given username.
     *
     * @param s
     * @return
     */
    public boolean contains(String s) {
	return userSet.contains(s);
    }

    @Override
    public Iterator<String> iterator() {
	return userSet.iterator();
    }

    /**
     * Add the given username to this user set.
     *
     * @param s
     */
    public void add(String s) {
	userSet.add(s);
    }

    /**
     * returns the size of the user set (# users).
     *
     * @return
     */
    public int size() {
	return userSet.size();
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((userSet == null) ? 0 : userSet.hashCode());
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
	SimpleUserSet other = (SimpleUserSet) obj;
	if (userSet == null) {
	    if (other.userSet != null) {
		return false;
	    }
	} else if (!userSet.equals(other.userSet)) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "SimpleUserSet [userSet=" + userSet + "]";
    }

}
