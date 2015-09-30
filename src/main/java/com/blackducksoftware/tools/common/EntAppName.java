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

package com.blackducksoftware.tools.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * An application name, format described by the configuration. Consists of:
 * appIdentifier, optional description, followed by zero or more suffixes
 * (depending on the configuration). The format of each component and the
 * separator between them are configurable. A non-conformant boolean is true if
 * the application name conforms to the configured format, false otherwise.
 *
 * @author sbillings
 *
 */
public class EntAppName {
    private final EntAppNameConfigurationManager config;
    private final String appName;
    private String appIdentifier;
    private String description;

    private boolean conformant = true; // true = appName matches patterns

    private final List<String> suffixes;

    public EntAppName(EntAppNameConfigurationManager config, String appName) {
	this.config = config;
	this.appName = appName;
	suffixes = new ArrayList<>(config.getNumSuffixes());

	String separatorString = config.getSeparatorString();
	Pattern separatorPattern = Pattern.compile(separatorString);
	Pattern appIdentifierPattern = Pattern.compile(config
		.getAppIdentifierPatternString());
	Pattern followsDescriptionPattern = Pattern.compile(config
		.getFollowsDescriptionPatternString());

	Pattern withoutDescriptionFormatPattern = Pattern.compile(config
		.getWithoutDescriptionFormatPatternString());
	Pattern withDescriptionFormatPattern = Pattern.compile(config
		.getWithDescriptionFormatPatternString());

	Scanner scanner = new Scanner(appName);
	try {
	    // Distinguish between without-description and with-description
	    // formats:
	    // <appIdentifier>-<workstream>-CURRENT vs.
	    // <appIdentifier>-<appdescription>-<workstream>-CURRENT vs.
	    // non-conforming
	    // (other)

	    // Try: without description
	    String currentMatch = scanner
		    .findInLine(withoutDescriptionFormatPattern);
	    if (currentMatch != null) {
		// This app name is "without description" format:
		// <appIdentifier>-<workstream>-CURRENT
		parseAppNameWithoutDescription(appName, separatorPattern,
			appIdentifierPattern);
	    } else if (scanner.findInLine(withDescriptionFormatPattern) != null) {
		// This app name is "with description" format:
		// <appIdentifier>-<appdescription>-<workstream>-CURRENT
		parseAppNameWithDescription(appName, separatorPattern,
			appIdentifierPattern, followsDescriptionPattern);
	    } else {
		setNonConformant(false);
	    }
	} catch (EnterpriseUtilitySuiteException e) {
	    setNonConformant(false);
	} finally {
	    scanner.close();
	}
    }

    public String getAppName() {
	return appName;
    }

    private void setNonConformant(boolean conformant) {
	this.conformant = conformant;
    }

    public String getAppIdentifier() {
	return appIdentifier;
    }

    public String getDescription() {
	return description;
    }

    public String getSuffix(int suffixIndex) {
	return suffixes.get(suffixIndex);
    }

    public boolean isConformant() {
	return conformant;
    }

    @Override
    public String toString() {
	return "EntAppName [appName=" + appName + ", appIdentifier="
		+ appIdentifier + ", description=" + description
		+ ", suffixes=" + suffixes + "]";
    }

    private void parseAppNameWithoutDescription(String fullAppName,
	    Pattern separatorPattern, Pattern appIdentifierPattern)
	    throws EnterpriseUtilitySuiteException {

	Scanner scanner = new Scanner(fullAppName);
	scanner.useDelimiter(separatorPattern);
	try {

	    if (!scanner.hasNext(appIdentifierPattern)) {
		String msg = "Error parsing numeric prefix from app name "
			+ fullAppName;
		throw new EnterpriseUtilitySuiteException(msg);
	    }
	    appIdentifier = scanner.next(appIdentifierPattern);

	    parseSuffixes(scanner, separatorPattern);
	} finally {
	    scanner.close();
	}
    }

    private void parseSuffixes(Scanner scanner, Pattern separatorPattern) {
	for (int i = 0; i < config.getNumSuffixes(); i++) {
	    // parse separator
	    scanner.findInLine(separatorPattern);

	    // parse suffix i
	    Pattern suffixPattern = Pattern.compile(config
		    .getSuffixPatternString(i));
	    String suffix = scanner.findInLine(suffixPattern);
	    suffixes.add(suffix);
	}
    }

    private void parseAppNameWithDescription(String fullAppName,
	    Pattern separatorPattern, Pattern appIdentifierPattern,
	    Pattern followsDescriptionPattern)
	    throws EnterpriseUtilitySuiteException {

	Scanner scanner = new Scanner(fullAppName);
	scanner.useDelimiter(separatorPattern);
	try {

	    if (!scanner.hasNext(appIdentifierPattern)) {
		String msg = "Error parsing numeric prefix from app name "
			+ fullAppName;
		throw new EnterpriseUtilitySuiteException(msg);
	    }
	    appIdentifier = scanner.next(appIdentifierPattern);

	    // parse the separator (-) after numericPrefix
	    scanner.findInLine(separatorPattern);

	    // parse app description
	    scanner.useDelimiter(followsDescriptionPattern);
	    if (scanner.hasNext()) {
		description = scanner.next();
	    } else {
		String msg = "Error parsing app description from app name "
			+ fullAppName;
		throw new EnterpriseUtilitySuiteException(msg);
	    }

	    parseSuffixes(scanner, separatorPattern);
	} finally {
	    scanner.close();
	}
    }
}
