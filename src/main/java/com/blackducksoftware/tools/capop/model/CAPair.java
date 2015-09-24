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
package com.blackducksoftware.tools.capop.model;

/**
 * Bean representing a Custom Attribute Pairing Name/Value
 * 
 * @author akamen
 *
 */
public class CAPair {

    private String caName;
    private String caValue;
    // Column number in the file
    private Integer position;
    // This is the attribute ID (sdk)
    private String attributeID;

    public String getCaName() {
	return caName;
    }

    public void setCaName(String caName) {
	this.caName = caName;
    }

    public String getCaValue() {
	return caValue;
    }

    public void setCaValue(String caValue) {
	this.caValue = caValue;
    }

    public Integer getPosition() {
	return position;
    }

    public void setPosition(Integer position) {
	this.position = position;
    }

    public String getAttributeID() {
	return attributeID;
    }

    public void setAttributeID(String attributeID) {
	this.attributeID = attributeID;
    }

}
