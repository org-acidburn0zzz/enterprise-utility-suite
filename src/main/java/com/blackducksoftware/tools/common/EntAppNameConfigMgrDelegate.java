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

package com.blackducksoftware.tools.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * A configuration manager that specifies the format of application names.
 *
 * @author sbillings
 *
 */
public class EntAppNameConfigMgrDelegate implements
	EntAppNameConfigurationManager {

    private final String separatorString;

    private final String appIdentifierPatternString;
    private final String followsDescriptionPatternString;
    private final String withoutDescriptionFormatPatternString;
    private final String withDescriptionFormatPatternString;
    private final List<String> suffixPatternStrings;

    private final Pattern appIdentifierPattern;
    private final Pattern followsDescriptionPattern;
    private final Pattern withoutDescriptionFormatPattern;
    private final Pattern withDescriptionFormatPattern;
    private final List<Pattern> suffixPatterns;

    public EntAppNameConfigMgrDelegate(Properties props) {

	separatorString = props.getProperty(APPNAME_SEPARATOR_PROPERTY);

	appIdentifierPatternString = props
		.getProperty(APPNAME_PATTERN_APPIDENTIFIER_PROPERTY);
	if (appIdentifierPatternString == null) {
	    throw new IllegalArgumentException("Configuration property "
		    + APPNAME_PATTERN_APPIDENTIFIER_PROPERTY + " is required");
	}
	appIdentifierPattern = Pattern.compile(appIdentifierPatternString);

	followsDescriptionPatternString = props
		.getProperty(APPNAME_PATTERN_FOLLOWSDESCRIPTION_PROPERTY);
	if (followsDescriptionPatternString == null) {
	    throw new IllegalArgumentException("Configuration property "
		    + APPNAME_PATTERN_FOLLOWSDESCRIPTION_PROPERTY
		    + " is required");
	}
	followsDescriptionPattern = Pattern
		.compile(followsDescriptionPatternString);

	withoutDescriptionFormatPatternString = props
		.getProperty(APPNAME_PATTERN_WITHOUTDESCRIPTIONFORMAT_PROPERTY);
	if (withoutDescriptionFormatPatternString == null) {
	    throw new IllegalArgumentException("Configuration property "
		    + APPNAME_PATTERN_WITHOUTDESCRIPTIONFORMAT_PROPERTY
		    + " is required");
	}
	withoutDescriptionFormatPattern = Pattern
		.compile(withoutDescriptionFormatPatternString);

	withDescriptionFormatPatternString = props
		.getProperty(APPNAME_PATTERN_WITHDESCRIPTIONFORMAT_PROPERTY);
	if (withDescriptionFormatPatternString == null) {
	    throw new IllegalArgumentException("Configuration property "
		    + APPNAME_PATTERN_WITHDESCRIPTIONFORMAT_PROPERTY
		    + " is required");
	}
	withDescriptionFormatPattern = Pattern
		.compile(withDescriptionFormatPatternString);

	suffixPatternStrings = new ArrayList<>(3);
	suffixPatterns = new ArrayList<>(3);

	for (int i = 0;; i++) {
	    String suffixPatternString = props
		    .getProperty("appname.pattern.suffix." + i);
	    if (suffixPatternString == null) {
		break;
	    }
	    suffixPatternStrings.add(suffixPatternString);
	    suffixPatterns.add(Pattern.compile(suffixPatternString));
	}
    }

    @Override
    public String getSeparatorString() {
	return separatorString;
    }

    @Override
    public String getWithoutDescriptionFormatPatternString() {
	return withoutDescriptionFormatPatternString;
    }

    @Override
    public String getWithDescriptionFormatPatternString() {
	return withDescriptionFormatPatternString;
    }

    @Override
    public String getAppIdentifierPatternString() {
	return appIdentifierPatternString;
    }

    @Override
    public String getFollowsDescriptionPatternString() {
	return followsDescriptionPatternString;
    }

    @Override
    public int getNumSuffixes() {
	return suffixPatternStrings.size();
    }

    @Override
    public String getSuffixPatternString(int suffixIndex) {
	return suffixPatternStrings.get(suffixIndex);
    }

    @Override
    public Pattern getAppIdentifierPattern() {
	return appIdentifierPattern;
    }

    @Override
    public Pattern getFollowsDescriptionPattern() {
	return followsDescriptionPattern;
    }

    @Override
    public Pattern getWithoutDescriptionFormatPattern() {
	return withoutDescriptionFormatPattern;
    }

    @Override
    public Pattern getWithDescriptionFormatPattern() {
	return withDescriptionFormatPattern;
    }

    @Override
    public Pattern getSuffixPattern(int suffixIndex) {
	return suffixPatterns.get(suffixIndex);
    }
}
