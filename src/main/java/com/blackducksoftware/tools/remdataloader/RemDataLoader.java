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

package com.blackducksoftware.tools.remdataloader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.tools.commonframework.standard.codecenter.pojo.CcAppCompVulnCopier;
import com.blackducksoftware.tools.commonframework.standard.datatable.DataTable;
import com.blackducksoftware.tools.commonframework.standard.datatable.FieldDef;
import com.blackducksoftware.tools.commonframework.standard.datatable.FieldType;
import com.blackducksoftware.tools.commonframework.standard.datatable.Record;
import com.blackducksoftware.tools.commonframework.standard.datatable.RecordDef;
import com.blackducksoftware.tools.commonframework.standard.datatable.reader.DataTableReader;
import com.blackducksoftware.tools.commonframework.standard.datatable.reader.DataTableReaderExcel;

public class RemDataLoader {
    private final Logger log = LoggerFactory.getLogger(this.getClass()
	    .getName());

    private static final String FIELD_APPLICATION_NAME = "applicationName";
    private static final String FIELD_COMPONENT_NAME = "componentName";
    private static final String FIELD_COMPONENT_VERSION = "componentVersion";
    private static final String FIELD_VULNERABILITY_NAME = "vulnerabilityName";
    private static final String FIELD_TARGET_REMEDIATION_DATE = "targetRemediationDate";
    private static final String FIELD_ACTUAL_REMEDIATION_DATE = "actualRemediationDate";
    private static final String FIELD_STATUS_NAME = "statusName";
    private static final String FIELD_COMMENT = "comment";

    private final RemDataConfigManager config;
    private final DataTable table;
    private String appVersion = "Unspecified";

    public static void main(String[] args) {
	int exitCode = 0;

	if ((args.length != 3) || (!"-config".equalsIgnoreCase(args[0]))) {
	    System.out
		    .println("Expected arguments: -config <configfile> <remediation data file>");
	    exitCode = -1;
	    System.exit(exitCode);
	}
	String configFilename = args[1];
	String remDataFilename = args[2];

	RemDataConfigManager config = new RemDataConfigManager(configFilename);

	RemDataLoader loader = null;
	try {
	    loader = new RemDataLoader(config, remDataFilename);
	} catch (Exception e) {
	    System.out.println("Error reading data file: " + e.getMessage());
	    System.exit(-1);
	}
	try {
	    loader.load();
	} catch (Exception e) {
	    System.out
		    .println("Error setting metadata on Code Center vulnerabilities: "
			    + e.getMessage());
	    System.exit(-1);
	}
    }

    public RemDataLoader(RemDataConfigManager config, String remDataFilename)
	    throws Exception {
	this.config = config;
	appVersion = config.getAppVersion();
	DataTableReader reader = new DataTableReaderExcel(remDataFilename);

	List<FieldDef> fields = new ArrayList<FieldDef>();

	fields.add(new FieldDef(FIELD_APPLICATION_NAME, FieldType.STRING,
		"Application Name"));
	fields.add(new FieldDef(FIELD_COMPONENT_NAME, FieldType.STRING,
		"Application Name"));
	fields.add(new FieldDef(FIELD_COMPONENT_VERSION, FieldType.STRING,
		"Application Name"));
	fields.add(new FieldDef(FIELD_VULNERABILITY_NAME, FieldType.STRING,
		"Application Name"));
	fields.add(new FieldDef(FIELD_TARGET_REMEDIATION_DATE, FieldType.DATE,
		"Application Name", "MM-dd-yyyy")); // We don't use the date fmt
	fields.add(new FieldDef(FIELD_ACTUAL_REMEDIATION_DATE, FieldType.DATE,
		"Application Name", "MM-dd-yyyy"));
	fields.add(new FieldDef(FIELD_STATUS_NAME, FieldType.STRING,
		"Status Name"));
	fields.add(new FieldDef(FIELD_COMMENT, FieldType.STRING, "Comment"));
	RecordDef recDef = new RecordDef(fields);
	table = new DataTable(recDef);
	reader.read(table);
	adjustTimes(table);
    }

    private void adjustTimes(DataTable table) throws Exception {
	for (Record rec : table) {

	    Date date = rec
		    .getDateFieldValueAsDate(FIELD_TARGET_REMEDIATION_DATE);
	    Date adjustedDate;
	    GregorianCalendar cal;
	    if (date != null) {
		adjustedDate = adjustDate(date, config.getTimeAdjustmentHours());
		cal = new GregorianCalendar();
		cal.setTime(adjustedDate);
		rec.setFieldValue(FIELD_TARGET_REMEDIATION_DATE, cal);
	    }

	    date = rec.getDateFieldValueAsDate(FIELD_ACTUAL_REMEDIATION_DATE);
	    if (date != null) {
		adjustedDate = adjustDate(date, config.getTimeAdjustmentHours());
		cal = new GregorianCalendar();
		cal.setTime(adjustedDate);
		rec.setFieldValue(FIELD_ACTUAL_REMEDIATION_DATE, cal);
	    }
	}
    }

    private Date adjustDate(Date date, int hourOffset) {
	if (date == null) {
	    return null;
	}
	Calendar cal = Calendar.getInstance(); // creates calendar
	cal.setTime(date); // sets calendar time/date
	cal.add(Calendar.HOUR_OF_DAY, hourOffset); // adds one hour
	Date adjustedDate = cal.getTime();
	log.debug("Adjusting date " + date + " (" + date.getTime() + ") to "
		+ adjustedDate + " (" + adjustedDate.getTime() + ")");
	return adjustedDate;
    }

    public void load() throws Exception {
	int failureCount = 0;
	int successCount = 0;

	// First, remove the header row
	Iterator<Record> iter = table.iterator();
	Record rec = iter.next();
	iter.remove();
	boolean foundEmptyApp = false;

	while (table.size() > 0) { // for each unique app
	    iter = table.iterator();
	    rec = iter.next(); // get first (remaining) record; whatever app
			       // it's for, that's the app we'll process next
	    String appName = rec.getStringFieldValue(FIELD_APPLICATION_NAME);
	    if ((appName == null) || (appName.length() == 0)) {
		foundEmptyApp = true;
		iter.remove();
		break;
	    } else {
		foundEmptyApp = false;
	    }
	    log.info("Processing changes for application: " + appName);
	    // Load the vulnerability data for this app (so we can tweak it,
	    // then write it back to CC)
	    CcAppCompVulnCopier cuv = new CcAppCompVulnCopier(config, appName,
		    appVersion);
	    cuv.loadVulnerabilityMetadataIntoCache();

	    boolean more = true;
	    while (more && !foundEmptyApp) { // for each row in table
		String recAppName = rec
			.getStringFieldValue(FIELD_APPLICATION_NAME);
		if ((recAppName == null) || (recAppName.length() == 0)) {
		    foundEmptyApp = true; // TODO this is ugly
		    iter.remove();
		    break;
		} else {
		    foundEmptyApp = false;
		}
		if (!appName.equals(recAppName)) {
		    if (iter.hasNext()) {
			rec = iter.next(); // get next record
		    } else {
			more = false;
		    }
		    continue; // this record is not about the app we're
			      // processing
		}
		String componentName = rec
			.getStringFieldValue(FIELD_COMPONENT_NAME);
		String componentVersion = rec
			.getStringFieldValue(FIELD_COMPONENT_VERSION);
		String vulnerabilityName = rec
			.getStringFieldValue(FIELD_VULNERABILITY_NAME);
		Date targetRemediationDate = rec
			.getDateFieldValueAsDate(FIELD_TARGET_REMEDIATION_DATE); // TODO
										 // timezone?
		Date actualRemediationDate = rec
			.getDateFieldValueAsDate(FIELD_ACTUAL_REMEDIATION_DATE);
		String statusName = rec.getStringFieldValue(FIELD_STATUS_NAME);
		String comment = rec.getStringFieldValue(FIELD_COMMENT);

		log.info("Processing changes for vulnerability "
			+ vulnerabilityName + " on component " + componentName
			+ " version " + componentVersion + " on application "
			+ appName + " version " + appVersion + " status Name: "
			+ statusName + " comment: " + comment);
		boolean hit = cuv.updateCachedVulnerabilityMetadata(
			componentName, componentVersion, vulnerabilityName,
			targetRemediationDate, actualRemediationDate,
			statusName, comment);

		if (hit) {
		    successCount++;
		} else {
		    failureCount++;
		    log.error("Did not find vulnerability " + vulnerabilityName
			    + " on component " + componentName + " version "
			    + componentVersion + " on application " + appName
			    + " version " + appVersion);
		}
		iter.remove();
		if (iter.hasNext()) {
		    rec = iter.next(); // get next record
		} else {
		    more = false;
		}
	    }

	    cuv.applyCachedVulnerabilityMetadataToGivenApp(config, appName,
		    appVersion);
	}
	log.info("Succeeded: " + successCount);
	log.info("Failed: " + failureCount);
    }
}
