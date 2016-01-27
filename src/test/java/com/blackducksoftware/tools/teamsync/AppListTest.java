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

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.tools.common.cc.AppList;

public class AppListTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void test() throws Exception {
	AppList f = new AppList("src/test/resources/teamsync/newAppNames.txt");

	assertEquals(3, f.size());
	assertTrue(f.contains("123456-RC1-CURRENT"));
	assertTrue(f.contains("123456-Some Application-RC1-CURRENT"));
	assertTrue(f.contains("123456-Some - app-RC1-CURRENT"));
	assertFalse(f.contains(""));
	assertFalse(f.contains("invalid"));

	Iterator<String> iter = f.iterator();
	assertTrue(iter.hasNext());
	iter.next();
	iter.next();
	iter.next();
	assertFalse(iter.hasNext());
    }

}
