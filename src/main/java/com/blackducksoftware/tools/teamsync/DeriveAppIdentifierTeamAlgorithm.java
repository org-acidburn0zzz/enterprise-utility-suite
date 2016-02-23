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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *******************************************************************************/
package com.blackducksoftware.tools.teamsync;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.tools.connector.codecenter.application.ApplicationUserPojo;

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

    public static List<ApplicationUserPojo> deriveTeam(
            List<AppTeam> appTeams) {
        List<ApplicationUserPojo> appIdentifierTeam = new ArrayList<ApplicationUserPojo>();
        for (AppTeam appTeam : appTeams) {
            addTo(appIdentifierTeam, appTeam.getTeam());

        }
        log.debug("AppIdentifier Team: " + appIdentifierTeam);
        return appIdentifierTeam;
    }

    /**
     * Add the listToAdd to the combinedList, avoiding duplicates
     *
     * @param combinedList
     * @param listToAdd
     */
    private static void addTo(List<ApplicationUserPojo> combinedList,
            List<ApplicationUserPojo> listToAdd) {
        for (ApplicationUserPojo assignment : listToAdd) {
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
            List<ApplicationUserPojo> assignmentList,
            ApplicationUserPojo assignment) {
        for (ApplicationUserPojo assignmentFromList : assignmentList) {
            if (assignmentFromList.getUserId()
                    .equals(assignment.getUserId())) {
                return true;
            }
        }
        return false;
    }

}
