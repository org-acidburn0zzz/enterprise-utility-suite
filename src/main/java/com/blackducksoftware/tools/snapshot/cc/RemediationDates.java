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

package com.blackducksoftware.tools.snapshot.cc;

import java.util.Date;

/**
 * Remediation dates: target and actual.
 *
 * @author sbillings
 *
 */
public class RemediationDates {
    private final Date targetRemediationDate;
    private final Date actualRemediationDate;

    public RemediationDates(Date targetRemediationDate,
	    Date actualRemediationDate) {

	this.targetRemediationDate = targetRemediationDate;
	this.actualRemediationDate = actualRemediationDate;
    }

    public Date getTargetRemediationDate() {
	return targetRemediationDate;
    }

    public Date getActualRemediationDate() {
	return actualRemediationDate;
    }
}
