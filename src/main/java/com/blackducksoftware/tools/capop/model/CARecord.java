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
package com.blackducksoftware.tools.capop.model;

import java.util.ArrayList;
import java.util.List;

/*
 *
 * Bean representing the CSV row
 */
public class CARecord {

    // This can be name, part of a name, etc
    private String applicationID;

    // In case it fails to update, save reason
    private String errorMsg;

    private List<CAPair> caPairings = new ArrayList<CAPair>();

    public String getApplicationID() {
	return applicationID;
    }

    public void setApplicationID(String appIdentifier) {
	applicationID = appIdentifier;
    }

    public String getErrorMsg() {
	return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
	this.errorMsg = errorMsg;
    }

    public List<CAPair> getCaPairings() {
	return caPairings;
    }

    public void setCaPairings(List<CAPair> caPairings) {
	this.caPairings = caPairings;
    }

}
