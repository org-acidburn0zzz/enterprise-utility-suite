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

package com.blackducksoftware.tools.snapshot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.tools.commonframework.standard.common.ProjectOrApp;

public class CompositeAppTest {

    private static final String JAN_ONE_2000_FORMATTED = "20000101_000000.000";
    private static final String DATE_TIME_FORMAT = "yyyyMMdd_HHmmss.SSS";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void testThatOriginalsGotCloned() throws Exception {
	MockProtexProject protexProject = new MockProtexProject(
		"mockProtexProject1");
	MockCcApp ccApp = new MockCcApp("mockCcApp1");
	CompositeApp currentComposite = new CompositeApp(protexProject, ccApp);
	currentComposite.createSnapshot("_clone", null, null, false);

	assertEquals(1, protexProject.getClonedCount());
	assertEquals(1, ccApp.getClonedCount());
    }

    @Test
    public void testThatClonesGotLocked() throws Exception {
	MockProtexProject protexProject = new MockProtexProject(
		"mockProtexProject2");
	MockCcApp ccApp = new MockCcApp("mockCcApp2");
	CompositeApp currentComposite = new CompositeApp(protexProject, ccApp);
	CompositeApp snapshotComposite = currentComposite.createSnapshot(
		"_clone", null, null, false);

	ProjectOrApp snapshotProtexProject = snapshotComposite
		.getProtexProject();
	assertEquals(0,
		((MockProtexProject) snapshotProtexProject).getClonedCount());
	assertEquals(1,
		((MockProtexProject) snapshotProtexProject).getLockedCount());

	ProjectOrApp snapshotCcApp = snapshotComposite.getCcApp();
	assertEquals(0, ((MockCcApp) snapshotCcApp).getClonedCount());
	assertEquals(1, ((MockCcApp) snapshotCcApp).getLockedCount());
    }

    @Test
    public void testNamesWithoutSuffixToRemove() throws Exception {
	MockProtexProject protexProject = new MockProtexProject(
		"mockProtexProject2");
	assertEquals("mockProtexProject2", protexProject.getName());

	MockCcApp ccApp = new MockCcApp("mockCcApp2");
	assertEquals("mockCcApp2", ccApp.getName());

	CompositeApp currentComposite = new CompositeApp(protexProject, ccApp);
	CompositeApp snapshotComposite = currentComposite.createSnapshot(
		"_mySuffix", null, null, false);

	ProjectOrApp snapshotProtexProject = snapshotComposite
		.getProtexProject();
	assertEquals("mockProtexProject2_mySuffix",
		((MockProtexProject) snapshotProtexProject).getName());

	ProjectOrApp snapshotCcApp = snapshotComposite.getCcApp();
	assertEquals("mockCcApp2_mySuffix",
		((MockCcApp) snapshotCcApp).getName());
    }

    @Test
    public void testNamesWithSuffixToRemove() throws Exception {
	MockProtexProject protexProject = new MockProtexProject(
		"mockProtexProject2-removeme");
	assertEquals("mockProtexProject2-removeme", protexProject.getName());

	MockCcApp ccApp = new MockCcApp("mockCcApp2-removeme");
	assertEquals("mockCcApp2-removeme", ccApp.getName());

	CompositeApp currentComposite = new CompositeApp(protexProject, ccApp);
	CompositeApp snapshotComposite = currentComposite.createSnapshot(
		"_mySuffix", "-removeme", null, false);

	ProjectOrApp snapshotProtexProject = snapshotComposite
		.getProtexProject();
	assertEquals("mockProtexProject2_mySuffix",
		((MockProtexProject) snapshotProtexProject).getName());

	ProjectOrApp snapshotCcApp = snapshotComposite.getCcApp();
	assertEquals("mockCcApp2_mySuffix",
		((MockCcApp) snapshotCcApp).getName());
    }

    @Test
    public void testGenerateTimestamp() {

	DateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);

	Calendar janOne2000Cal = new GregorianCalendar(2000, 0, 1);
	Date janOne2000Date = janOne2000Cal.getTime();
	String timestamp = dateFormat.format(janOne2000Date);
	System.out.println("Jan 1, 2000 Formatted: " + timestamp);
	System.out.println("Jan 1, 2000 Raw time value: "
		+ janOne2000Date.getTime());

	assertEquals(JAN_ONE_2000_FORMATTED, SnapshotUtility.generateTimestamp(
		janOne2000Date, DATE_TIME_FORMAT));
    }

    @Test
    public void testSkipProtexSnapScenario() throws Exception {
	MockProtexProject protexProject = new MockProtexProject(
		"mockProtexProject1");
	MockCcApp ccApp = new MockCcApp("mockCcApp1");
	CompositeApp currentComposite = new CompositeApp(protexProject, ccApp);
	CompositeApp snapshotComposite = currentComposite
		.createSnapshotAppOnly("_clone", null, null);

	assertNull(snapshotComposite.getProtexProject());
	assertEquals(1, ccApp.getClonedCount());
    }

}
