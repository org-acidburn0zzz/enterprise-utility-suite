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

/**
 *
 */
package com.blackducksoftware.tools.highsev;

import java.io.File;

import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;

/**
 * The configuration manager for the "Identify the Highest Severity
 * vulnerability for each component". utility.
 *
 * @author Ari Kamen
 * @date Jul 22, 2014
 *
 */
public class HighSevConfigManager extends ConfigurationManager {

    private final String customAttributeName;

    public HighSevConfigManager(File file) {
	super(file.toString(), APPLICATION.CODECENTER);
	customAttributeName = getProperty(HighSevConstants.CUSTOM_ATTRIBUTE_NAME);
    }

    public String getCustomAttributeName() {
	return customAttributeName;
    }
}
