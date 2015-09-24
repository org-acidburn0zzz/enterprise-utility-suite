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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.codecenter.role.data.ApplicationRoleAssignment;

/**
 * Derives an appIdentifier's team, given the set of teams from all its
 * applications.
 *
 * @author sbillings
 *
 */
public class DeriveAppIdentifierTeamAlgorithm {
    private static final Logger log = LoggerFactory
	    .getLogger(DeriveAppIdentifierTeamAlgorithm.class.getName());

    public static List<ApplicationRoleAssignment> deriveTeam(
	    List<AppTeam> appTeams) {
	List<ApplicationRoleAssignment> appIdentifierTeam = new ArrayList<ApplicationRoleAssignment>();
	for (AppTeam appTeam : appTeams) {
	    addTo(appIdentifierTeam, appTeam.getTeam());

	}
	log.info("AppIdentifier Team size: " + appIdentifierTeam.size());
	return appIdentifierTeam;
    }

    /**
     * Add the listToAdd to the combinedList, avoiding duplicates
     *
     * @param combinedList
     * @param listToAdd
     */
    private static void addTo(List<ApplicationRoleAssignment> combinedList,
	    List<ApplicationRoleAssignment> listToAdd) {
	for (ApplicationRoleAssignment assignment : listToAdd) {
	    if (!contains(combinedList, assignment)) {
		combinedList.add(assignment);
	    }
	}
    }

    /**
     * Return true if the given assignment is already in the given
     * assignmentList
     *
     * @param assignmentList
     * @param assignment
     * @return
     */
    private static boolean contains(
	    List<ApplicationRoleAssignment> assignmentList,
	    ApplicationRoleAssignment assignment) {
	for (ApplicationRoleAssignment assignmentFromList : assignmentList) {
	    if (assignmentFromList.getUserNameToken().getName()
		    .equals(assignment.getUserNameToken().getName())) {
		return true;
	    }
	}
	return false;
    }

}
