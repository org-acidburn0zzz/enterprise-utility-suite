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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *******************************************************************************/

package com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class AppIdentifierUserListMapTest {
    private Pattern usernamePattern = Pattern
            .compile("[a-zA-Z][0-9][0-9][0-9][0-9][0-9][0-9]");

    private Pattern appIdentifierPattern = Pattern.compile("[0-9][0-9][0-9]+");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void test() throws Exception {
        AppIdentifierUserListMap appIdentifierUserListMap = new AppIdentifierUserListMap(
                "src/test/resources/addusers/appIdentifierUserListMapFile.txt",
                usernamePattern, appIdentifierPattern, false);

        Map<String, List<String>> usernameToAppIdentifiersMap = appIdentifierUserListMap
                .getUsernameAppIdentifierListMap();

        assertEquals(3, usernameToAppIdentifiersMap.keySet().size());
        assertEquals("111", usernameToAppIdentifiersMap.get("f566884").get(0));

        List<String> appIdentifiers = usernameToAppIdentifiersMap
                .get("f111222");
        assertTrue(appIdentifiers.contains("222"));
        assertTrue(appIdentifiers.contains("333"));

        appIdentifiers = usernameToAppIdentifiersMap.get("f444555");
        for (String appIdentifier : appIdentifiers) {
            System.out.println(appIdentifier);
        }
        assertTrue(appIdentifiers.contains("444"));
        assertTrue(appIdentifiers.contains("555"));

        assertEquals(0, appIdentifierUserListMap.getBarrenUsers().size());
    }

    @Test
    public void testAltConstructor() throws Exception {
        List<String> inputLines = new ArrayList<String>();
        inputLines.add("f566884;111");
        inputLines.add("f111222;222;333");
        inputLines.add("f444555;444;");
        inputLines.add("F444555 ;555 ;");

        AppIdentifierUserListMap appIdentifierUserListMap = new AppIdentifierUserListMap(
                inputLines, usernamePattern, appIdentifierPattern, false);

        Map<String, List<String>> usernameToAppIdentifiersMap = appIdentifierUserListMap
                .getUsernameAppIdentifierListMap();

        assertEquals(3, usernameToAppIdentifiersMap.keySet().size());
        assertEquals("111", usernameToAppIdentifiersMap.get("f566884").get(0));

        List<String> appIdentifiers = usernameToAppIdentifiersMap
                .get("f111222");
        assertTrue(appIdentifiers.contains("222"));
        assertTrue(appIdentifiers.contains("333"));

        appIdentifiers = usernameToAppIdentifiersMap.get("f444555");
        for (String appIdentifier : appIdentifiers) {
            System.out.println(appIdentifier);
        }
        assertTrue(appIdentifiers.contains("444"));
        assertTrue(appIdentifiers.contains("555"));

        assertEquals(0, appIdentifierUserListMap.getBarrenUsers().size());
    }

    @Test
    public void testEmptyAppIdList() throws Exception {
        List<String> inputLines = new ArrayList<String>();
        inputLines.add("f566884;111");
        inputLines.add("f111222;222;333");
        inputLines.add("f444555;444;");
        inputLines.add("F444555 ;555 ;");
        inputLines.add("F000000;");
        inputLines.add("F000001");
        inputLines.add("F000002 ; ");

        AppIdentifierUserListMap appIdentifierUserListMap = new AppIdentifierUserListMap(
                inputLines, usernamePattern, appIdentifierPattern, true);

        Map<String, List<String>> usernameToAppIdentifiersMap = appIdentifierUserListMap
                .getUsernameAppIdentifierListMap();

        assertEquals(6, usernameToAppIdentifiersMap.keySet().size());

        assertNull(usernameToAppIdentifiersMap.get("F000000"));
        assertNull(usernameToAppIdentifiersMap.get("F000001"));
        assertNull(usernameToAppIdentifiersMap.get("F000002"));

        Set<String> barrenUsers = appIdentifierUserListMap.getBarrenUsers();
        assertEquals(3, appIdentifierUserListMap.getBarrenUsers().size());
        assertTrue(barrenUsers.contains("f000000"));
        assertTrue(barrenUsers.contains("f000001"));
        assertTrue(barrenUsers.contains("f000002"));
    }

    @Test
    public void testAddMore() throws Exception {
        List<String> inputLines = new ArrayList<String>();
        inputLines.add("f566884;111");
        inputLines.add("f111222;222;333");
        inputLines.add("f444555;444;");
        inputLines.add("F444555 ;555 ;");

        AppIdentifierUserListMap appIdentifierUserListMap = new AppIdentifierUserListMap(
                inputLines, usernamePattern, appIdentifierPattern, true);
        assertEquals(5, appIdentifierUserListMap.getAppIdentifierUsernameListMap().size());
        assertEquals(3, appIdentifierUserListMap.getUsernameAppIdentifierListMap().size());

        List<String> additionalAppIds = new ArrayList<>();
        additionalAppIds.add("666");
        additionalAppIds.add("777777");
        Map<String, List<String>> additionalMap = new HashMap<>();
        additionalMap.put("g000000", additionalAppIds);
        appIdentifierUserListMap.addMoreUsernameToAppIdsMappings(additionalMap);

        assertEquals(7, appIdentifierUserListMap.getAppIdentifierUsernameListMap().size());
        assertEquals(4, appIdentifierUserListMap.getUsernameAppIdentifierListMap().size());
    }

    @Test
    public void testMissingAppIdentifier() throws Exception {
        List<String> inputLines = new ArrayList<String>();
        inputLines.add("f566884");

        try {
            new AppIdentifierUserListMap(inputLines, usernamePattern,
                    appIdentifierPattern, false);
            fail("Exception wasn't thrown");
        } catch (Exception e) {

        }
    }

    @Test
    public void testUsernameNoLeadingLetter() throws Exception {
        List<String> inputLines = new ArrayList<String>();
        inputLines.add("566884");

        try {
            new AppIdentifierUserListMap(inputLines, usernamePattern,
                    appIdentifierPattern, false);
            fail("Exception wasn't thrown");
        } catch (Exception e) {

        }
    }

    @Test
    public void testUsernameTooFewDigits() throws Exception {
        List<String> inputLines = new ArrayList<String>();
        inputLines.add("f56688");

        try {
            new AppIdentifierUserListMap(inputLines, usernamePattern,
                    appIdentifierPattern, false);
            fail("Exception wasn't thrown");
        } catch (Exception e) {

        }
    }

    @Test
    public void testInvalidAppIdentifierAlpha() throws Exception {
        List<String> inputLines = new ArrayList<String>();
        inputLines.add("f566884;1111a");

        try {
            new AppIdentifierUserListMap(inputLines, usernamePattern,
                    appIdentifierPattern, false);
            fail("Exception wasn't thrown");
        } catch (Exception e) {

        }
    }

    @Test
    public void testInvalidSeparator() throws Exception {
        List<String> inputLines = new ArrayList<String>();
        inputLines.add("f566884,1111,222");

        try {
            new AppIdentifierUserListMap(inputLines, usernamePattern,
                    appIdentifierPattern, false);
            fail("Exception wasn't thrown");
        } catch (Exception e) {

        }
    }

    @Test
    public void testAppIdentifierUserList() throws Exception {
        AppIdentifierUserListMap appIdentifierUserListMap = new AppIdentifierUserListMap(
                "src/test/resources/addusers/appIdentifierUserListMapFile.txt",
                usernamePattern, appIdentifierPattern, false);

        Map<String, AppIdentifierAddUserDetails> appIdentifierUsernameListMap = appIdentifierUserListMap
                .getAppIdentifierUsernameListMap();

        assertEquals(5, appIdentifierUsernameListMap.keySet().size());

        AppIdentifierAddUserDetails details = appIdentifierUsernameListMap
                .get("111");
        List<String> usernameList = details.getUsernames();
        assertEquals(1, usernameList.size());
        assertEquals("f566884", usernameList.get(0));

        details = appIdentifierUsernameListMap.get("222");
        usernameList = details.getUsernames();
        assertEquals(1, usernameList.size());
        assertEquals("f111222", usernameList.get(0));

        details = appIdentifierUsernameListMap.get("333");
        usernameList = details.getUsernames();
        assertEquals(1, usernameList.size());
        assertEquals("f111222", usernameList.get(0));

        details = appIdentifierUsernameListMap.get("444");
        usernameList = details.getUsernames();
        assertEquals(1, usernameList.size());
        assertEquals("f444555", usernameList.get(0));

        details = appIdentifierUsernameListMap.get("555");
        usernameList = details.getUsernames();
        assertEquals(1, usernameList.size());
        assertEquals("f444555", usernameList.get(0));
    }

    @Test
    public void testAppIdentifierUserListMultUsersPerApp() throws Exception {
        AppIdentifierUserListMap appIdentifierUserListMap = new AppIdentifierUserListMap(
                "src/test/resources/addusers/appIdentifierUserListMapFileMultUsersPerApp.txt",
                usernamePattern, appIdentifierPattern, false);

        Map<String, AppIdentifierAddUserDetails> appIdentifierUsernameListMap = appIdentifierUserListMap
                .getAppIdentifierUsernameListMap();

        assertEquals(5, appIdentifierUsernameListMap.keySet().size());

        AppIdentifierAddUserDetails details = appIdentifierUsernameListMap
                .get("111");
        List<String> usernameList = details.getUsernames();
        assertEquals(2, usernameList.size());
        assertTrue(usernameList.contains("f566884"));
        assertTrue(usernameList.contains("a000000"));
    }

}
