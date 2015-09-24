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
package com.blackducksoftware.tools.capop;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

import com.blackducksoftware.sdk.codecenter.application.data.Application;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationIdToken;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationPageFilter;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationUpdate;
import com.blackducksoftware.sdk.codecenter.attribute.data.AbstractAttribute;
import com.blackducksoftware.sdk.codecenter.attribute.data.AttributeIdToken;
import com.blackducksoftware.sdk.codecenter.attribute.data.AttributePageFilter;
import com.blackducksoftware.sdk.codecenter.common.data.AttributeValue;
import com.blackducksoftware.tools.capop.model.CAPair;
import com.blackducksoftware.tools.capop.model.CARecord;
import com.blackducksoftware.tools.commonframework.standard.codecenter.CodeCenterServerWrapper;

public class CAPopProcessor {

    private final Logger log = LoggerFactory.getLogger(this.getClass()
	    .getName());

    private final CAPopConfigManager config;
    private final CodeCenterServerWrapper ccWrapper;

    private final List<CARecord> failedRecords = new ArrayList<CARecord>();

    public CAPopProcessor(File configFile) throws Exception {
	try {
	    config = new CAPopConfigManager(configFile);
	    ccWrapper = new CodeCenterServerWrapper(config.getServerBean(),
		    config);
	} catch (Exception e) {
	    throw new Exception("Could not process configuration file: "
		    + e.getMessage());

	}
    }

    public void process(File file) throws Exception {
	try {
	    List<AbstractAttribute> attributesToUpdate = new ArrayList<AbstractAttribute>();
	    List<CARecord> csvRecords = parseCSVFile(file);
	    log.info("Parsed [{}] records", csvRecords.size());

	    // Build the list of attributes that we want to perform updates on
	    // For each pair we have, look up the actual attribute object from
	    // the SDK
	    if (csvRecords.size() > 0) {
		CARecord firstRecord = csvRecords.get(0);
		for (CAPair pair : firstRecord.getCaPairings()) {
		    AbstractAttribute attributeToUpdate = findAttributeByName(pair
			    .getCaName());
		    pair.setAttributeID(attributeToUpdate.getId().getId());
		}

		// Assuming that attribute IDs were derived...
		// Iterate through the entire list and set the attribute IDs
		int recordCounter = 0;
		for (CARecord record : csvRecords) {
		    List<CAPair> pairings = record.getCaPairings();
		    for (int i = 0; i < pairings.size(); i++) {
			CAPair pair = pairings.get(i);
			// Take the ID of the attribute from the first record
			// and apply it.
			String attributeId = firstRecord.getCaPairings().get(i)
				.getAttributeID();
			log.debug(
				"Applying attribute ID [{}] from first record to record number: [{}]",
				attributeId, recordCounter++);
			pair.setAttributeID(attributeId);
		    }
		}
	    }

	    Map<String, CARecord> appMap = buildApplicationMap(csvRecords);
	    log.info("Found [{}] matching applications", appMap.size());

	    applyAttributes(appMap, attributesToUpdate);

	    // Display failed records
	    if (failedRecords.size() > 0) {
		log.warn("Following records failed to update");
		for (CARecord record : failedRecords) {
		    log.info("Record with ID [{}] failed for reason [{}]",
			    record.getApplicationID(), record.getErrorMsg());
		}
	    }

	    log.info("Finished processing.");

	} catch (Exception e) {
	    throw new Exception("Error during processing: " + e.getMessage());
	}
    }

    /**
     * For every element in the map, perform the application. Note those that
     * failed for whatever reason.
     *
     * @param appMap
     * @param attributesToUpdate
     */
    private void applyAttributes(Map<String, CARecord> appMap,
	    List<AbstractAttribute> attributesToUpdate) {
	log.info("Applying attributes");
	Set<String> keys = appMap.keySet();
	Integer count = 1;

	for (String appId : keys) {
	    CARecord record = appMap.get(appId);

	    try {
		log.info("[{}/{}] Applying custom attribute(s) for ID [{}]",
			count++, keys.size(), record.getApplicationID());

		ApplicationUpdate au = new ApplicationUpdate();

		ApplicationIdToken token = new ApplicationIdToken();
		token.setId(appId);
		au.setId(token);

		// Add all potential attributes
		List<AttributeValue> updateAttributeList = au
			.getAttributeValues();

		// For every single pair, create an attribute
		for (CAPair capair : record.getCaPairings()) {
		    log.info("\t" + capair.getCaName() + "="
			    + capair.getCaValue());
		    AttributeValue av = new AttributeValue();
		    AttributeIdToken attribToken = new AttributeIdToken();
		    attribToken.setId(capair.getAttributeID());
		    av.setAttributeId(attribToken);
		    av.getValues().add(capair.getCaValue());

		    updateAttributeList.add(av);
		}

		ccWrapper.getInternalApiWrapper().getApplicationApi()
			.updateApplication(au);
	    } catch (Exception e) {
		log.warn(
			"Unable to apply custom attribute(s)for ID [{}], reason: [{}]",
			record.getApplicationID(), e.getMessage());
		record.setErrorMsg(e.getMessage());
		failedRecords.add(record);
	    }

	}
    }

    private AbstractAttribute findAttributeByName(String caName)
	    throws Exception {
	AbstractAttribute aa = null;

	AttributePageFilter apf = new AttributePageFilter();
	apf.setFirstRowIndex(0);
	apf.setLastRowIndex(Integer.MAX_VALUE);

	List<AbstractAttribute> attributes = ccWrapper.getInternalApiWrapper()
		.getAttributeApi().searchAttributes("", apf);

	for (AbstractAttribute potentialAttribute : attributes) {
	    String name = potentialAttribute.getName();
	    if (name.equalsIgnoreCase(caName)) {
		aa = potentialAttribute;
		break;
	    }
	}

	if (aa == null) {
	    throw new Exception("Unable to find attribute with name: " + caName);
	}

	return aa;
    }

    /**
     * Grabs all the applications from Code Center Then matches that application
     * to the specified user parameter using a preferred lookup
     *
     * @param csvRecords
     * @return
     * @throws Exception
     */
    private Map<String, CARecord> buildApplicationMap(List<CARecord> csvRecords)
	    throws Exception {
	Map<String, CARecord> map = new HashMap<String, CARecord>();
	try {
	    // First we grab all the apps
	    ApplicationPageFilter apf = new ApplicationPageFilter();
	    apf.setFirstRowIndex(0);
	    apf.setLastRowIndex(Integer.MAX_VALUE);

	    log.info("Collecting all applications");
	    List<Application> apps = ccWrapper.getInternalApiWrapper()
		    .getApplicationApi().searchApplications("", apf);

	    // Then for each map, check to see if the name of the map
	    // matches to the provided user ID
	    log.info("Sorting [{}] applications", apps.size());
	    for (Application app : apps) {
		String appName = app.getName().toLowerCase();
		String appID = app.getId().getId();
		for (CARecord record : csvRecords) {
		    String id = record.getApplicationID().toLowerCase();
		    if (appName.contains(id)) {
			log.debug(
				"Found match between id [{}] and application [{}]",
				id, appName);
			map.put(appID, record);
			csvRecords.remove(record);
			break;
		    }
		}
	    }

	    if (csvRecords.size() > 0) {
		log.warn("Not all IDs matched to application, following were not found: ");
		for (CARecord record : csvRecords) {
		    log.info("Unable to determine matching application to id: "
			    + record.getApplicationID());
		}
	    }

	} catch (Exception e) {
	    throw new Exception("Unable to get Code Center applications: "
		    + e.getMessage());
	}

	return map;
    }

    /**
     * Parses the CSV file and creates a list of objects that represent the
     * user's ID and all potential Custom Attributes
     *
     * @param file
     * @return
     * @throws Exception
     */
    private List<CARecord> parseCSVFile(File file) throws Exception {

	List<CARecord> records = new ArrayList<CARecord>();
	CSVReader reader = null;

	try {
	    // read line by line
	    String[] record = null;
	    // skip header row

	    reader = new CSVReader(new FileReader(file), ',');

	    // Examine the header and determine how many CA pairs are provided
	    String[] headerRecord = reader.readNext();
	    List<CAPair> derivedPairings = findCAPairings(headerRecord);

	    // Go through each row in the file
	    while ((record = reader.readNext()) != null) {
		CARecord car = new CARecord();
		car.setApplicationID(record[0]);

		// Use our list of pairings, which only contain name/position
		// to derive value form the row
		List<CAPair> pairings = new ArrayList<CAPair>();
		for (CAPair derivedPair : derivedPairings) {
		    CAPair pair = new CAPair();
		    String caValue = record[derivedPair.getPosition()];
		    if (caValue != null && caValue.length() > 0) {
			pair.setCaName(derivedPair.getCaName());
			pair.setPosition(derivedPair.getPosition());
			pair.setCaValue(caValue.trim());
		    } else {
			log.warn(
				"Value for ID [{}] and Attribute [{}] appears to be empty",
				car.getApplicationID(), derivedPair.getCaName());
		    }

		    // Add our new pair
		    pairings.add(pair);
		}
		// Add all the pairings
		car.setCaPairings(pairings);
		records.add(car);
	    }

	    reader.close();

	} catch (Exception e) {
	    throw new Exception("Unable to parse CSV file: " + e.getMessage());
	} finally {
	    reader.close();
	}
	return records;
    }

    private List<CAPair> findCAPairings(String[] headerRecord) throws Exception {

	List<CAPair> pairings = new ArrayList<CAPair>();

	// First is the id
	// All subsequent are CA names
	String caName = headerRecord[1];

	if (caName.isEmpty()) {
	    throw new Exception(
		    "At least ONE Custom Attribute name must be provided!");
	}

	for (int i = 1; i < headerRecord.length; i++) {
	    String headerName = headerRecord[i];
	    if (headerName != null && headerName.length() > 0) {
		log.info("Found Custom Attribute name [{}]", headerName);
		CAPair pair = new CAPair();
		pair.setCaName(headerName);
		pair.setPosition(i);
		pairings.add(pair);
	    }
	}

	return pairings;
    }

}
