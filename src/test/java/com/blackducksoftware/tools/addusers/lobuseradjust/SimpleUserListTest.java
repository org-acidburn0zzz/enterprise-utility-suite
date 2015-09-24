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

package com.blackducksoftware.tools.addusers.lobuseradjust;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleUserListTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void testAsString() throws Exception {
	SimpleUserSet simpleUserList = new SimpleUserSet("user1,user2");
	Set<String> users = simpleUserList.getUserSet();

	assertEquals(2, users.size());
	assertTrue(users.contains("user1"));
	assertTrue(users.contains("user2"));
    }

    @Test
    public void testUsernameFixing() throws Exception {
	SimpleUserSet simpleUserList = new SimpleUserSet("F123456 , G123456   ");
	Set<String> users = simpleUserList.getUserSet();

	assertEquals(2, users.size());
	assertTrue(users.contains("f123456"));
	assertTrue(users.contains("g123456"));
    }

    @Test
    public void testAsFile() throws Exception {
	File userFile = new File(
		"src/test/resources/addusers/simpleuserfile.csv");
	SimpleUserSet simpleUserList = new SimpleUserSet(userFile);
	Set<String> users = simpleUserList.getUserSet();

	assertEquals(2, users.size());
	assertTrue(users.contains("user001"));
	assertTrue(users.contains("user002"));
    }

    @Test
    public void testAsNonExistentFile() throws Exception {
	try {
	    File userFile = new File(
		    "src/test/resources/addusers/simpleuserfileNONEXISTENT.csv");
	    new SimpleUserSet(userFile);
	    fail("Should have thrown an exception on non-existent file");
	} catch (Exception e) {
	    // expect this
	}
    }

    @Test
    public void testAsEmptyFile() throws Exception {
	try {
	    File userFile = new File(
		    "src/test/resources/addusers/emptyuserfile.csv");
	    new SimpleUserSet(userFile);
	    fail("Should have thrown an exception on empty file");
	} catch (Exception e) {
	    // expect this
	}
    }

    @Test
    public void testAsWrongFormatFile() throws Exception {
	try {
	    File userFile = new File(
		    "src/test/resources/addusers/wrongformatuserfile.xlsx");
	    new SimpleUserSet(userFile);
	    fail("Should have thrown an exception on invalid format file");
	} catch (Exception e) {
	    System.out.println(e.getMessage());
	}
    }

    @Test
    public void testDupElimination() throws Exception {
	SimpleUserSet simpleUserList = new SimpleUserSet("user1,user2,user1");
	Set<String> users = simpleUserList.getUserSet();

	assertEquals(2, users.size());
	assertTrue(users.contains("user1"));
	assertTrue(users.contains("user2"));
    }

    @Test
    public void testCompareToWhenNoChange() {
	Set<String> oldUsers = new HashSet<String>();
	SimpleUserSet userSet = new SimpleUserSet(oldUsers);

	Set<String> newUsers = new HashSet<String>();
	SimpleUserSet newUserSet = new SimpleUserSet(newUsers);

	SimpleUserSet usersToAdd = userSet.getUsersToAdd(newUserSet);
	SimpleUserSet usersToDelete = userSet.getUsersToDelete(newUserSet);

	assertEquals(0, usersToAdd.size());
	assertEquals(0, usersToDelete.size());
    }

    @Test
    public void testCompareToWhenChanges() {
	Set<String> oldUsers = new HashSet<String>();
	oldUsers.add("olduser");
	oldUsers.add("commonuser");
	oldUsers.add("olduser2");
	SimpleUserSet userSet = new SimpleUserSet(oldUsers);

	Set<String> newUsers = new HashSet<String>();
	newUsers.add("commonuser");
	newUsers.add("newuser");
	SimpleUserSet newUserSet = new SimpleUserSet(newUsers);

	SimpleUserSet usersToAdd = userSet.getUsersToAdd(newUserSet);
	SimpleUserSet usersToDelete = userSet.getUsersToDelete(newUserSet);

	assertEquals(1, usersToAdd.size());
	assertEquals(2, usersToDelete.size());
	assertTrue(usersToAdd.contains("newuser"));
	assertTrue(usersToDelete.contains("olduser"));
	assertTrue(usersToDelete.contains("olduser2"));
    }
}
