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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

import com.blackducksoftware.sdk.codecenter.role.data.ApplicationRoleAssignment;
import com.blackducksoftware.sdk.codecenter.user.data.UserNameToken;
import com.blackducksoftware.tools.common.EntAppName;
import com.blackducksoftware.tools.common.EntAppNameConfigurationManager;
import com.blackducksoftware.tools.enterprise.TestEntAppNameConfigurationManager;

public class DeriveAppIdentifierTeamAlgorithmTest {

    @Test
    public void testZero() throws Exception {

	// no teams
	List<AppTeam> appTeams = new ArrayList<AppTeam>();

	// derive team from no teams
	List<ApplicationRoleAssignment> team = DeriveAppIdentifierTeamAlgorithm
		.deriveTeam(appTeams);

	// check result
	assertEquals(0, team.size());
    }

    @Test
    public void testOne() throws Exception {

	// user1
	ApplicationRoleAssignment user1Assignment = new ApplicationRoleAssignment();
	UserNameToken userToken = new UserNameToken();
	userToken.setName("user1");
	user1Assignment.setUserNameToken(userToken);

	// app1
	Properties props = getBasicProperties();
	EntAppNameConfigurationManager config = new TestEntAppNameConfigurationManager(
		props);
	EntAppName appNameObject = new EntAppName(config, "app1");
	List<ApplicationRoleAssignment> assignments = new ArrayList<ApplicationRoleAssignment>();
	assignments.add(user1Assignment);
	AppTeam appTeam1 = new AppTeam(assignments, appNameObject);

	// app teams for appIdentifier
	List<AppTeam> appTeams = new ArrayList<AppTeam>();
	appTeams.add(appTeam1);

	// Derive appIdentifier teams
	List<ApplicationRoleAssignment> team = DeriveAppIdentifierTeamAlgorithm
		.deriveTeam(appTeams);

	// Check appIdentifier team
	assertEquals("user1", team.get(0).getUserNameToken().getName());
    }

    @Test
    public void testTwoUsersOneApp() throws Exception {

	// user1
	ApplicationRoleAssignment user1Assignment = new ApplicationRoleAssignment();
	UserNameToken user1Token = new UserNameToken();
	user1Token.setName("user1");
	user1Assignment.setUserNameToken(user1Token);

	// user2
	ApplicationRoleAssignment user2Assignment = new ApplicationRoleAssignment();
	UserNameToken user2Token = new UserNameToken();
	user2Token.setName("user2");
	user2Assignment.setUserNameToken(user2Token);

	// app1
	Properties props = getBasicProperties();
	EntAppNameConfigurationManager config = new TestEntAppNameConfigurationManager(
		props);
	EntAppName appNameObject = new EntAppName(config, "app1");
	List<ApplicationRoleAssignment> assignments = new ArrayList<ApplicationRoleAssignment>();
	assignments.add(user1Assignment);
	assignments.add(user2Assignment);
	AppTeam appTeam1 = new AppTeam(assignments, appNameObject);

	// app teams for appIdentifier
	List<AppTeam> appTeams = new ArrayList<AppTeam>();
	appTeams.add(appTeam1);

	// Derive appIdentifier apps' team
	List<ApplicationRoleAssignment> team = DeriveAppIdentifierTeamAlgorithm
		.deriveTeam(appTeams);

	// Check appIdentifier team
	assertEquals(2, team.size());
	boolean foundUser1 = true;
	boolean foundUser2 = true;
	for (int i = 0; i < 2; i++) {
	    if (team.get(i).getUserNameToken().getName().equals("user1")) {
		foundUser1 = true;
	    } else if (team.get(i).getUserNameToken().getName().equals("user2")) {
		foundUser2 = true;
	    }
	}
	assertTrue(foundUser1);
	assertTrue(foundUser2);
    }

    @Test
    public void testTwoUsersTwoApps() throws Exception {

	// user1
	ApplicationRoleAssignment user1Assignment = new ApplicationRoleAssignment();
	UserNameToken user1Token = new UserNameToken();
	user1Token.setName("user1");
	user1Assignment.setUserNameToken(user1Token);

	// user2
	ApplicationRoleAssignment user2Assignment = new ApplicationRoleAssignment();
	UserNameToken user2Token = new UserNameToken();
	user2Token.setName("user2");
	user2Assignment.setUserNameToken(user2Token);

	// app1
	Properties props = getBasicProperties();
	EntAppNameConfigurationManager config = new TestEntAppNameConfigurationManager(
		props);
	EntAppName app1NameObject = new EntAppName(config, "app1");
	List<ApplicationRoleAssignment> assignments1 = new ArrayList<ApplicationRoleAssignment>();
	assignments1.add(user1Assignment);
	AppTeam appTeam1 = new AppTeam(assignments1, app1NameObject);

	// app2
	EntAppName app2NameObject = new EntAppName(config, "app2");
	List<ApplicationRoleAssignment> assignments2 = new ArrayList<ApplicationRoleAssignment>();
	assignments2.add(user2Assignment);
	AppTeam appTeam2 = new AppTeam(assignments2, app2NameObject);

	// appIdentifier apps' teams
	List<AppTeam> appTeams = new ArrayList<AppTeam>();
	appTeams.add(appTeam1);
	appTeams.add(appTeam2);

	// Derive appIdentifier team
	List<ApplicationRoleAssignment> team = DeriveAppIdentifierTeamAlgorithm
		.deriveTeam(appTeams);

	// check appIdentifier team
	assertEquals(2, team.size());
	boolean foundUser1 = true;
	boolean foundUser2 = true;
	for (int i = 0; i < 2; i++) {
	    if (team.get(i).getUserNameToken().getName().equals("user1")) {
		foundUser1 = true;
	    } else if (team.get(i).getUserNameToken().getName().equals("user2")) {
		foundUser2 = true;
	    }
	}
	assertTrue(foundUser1);
	assertTrue(foundUser2);
    }

    @Test
    public void testTwoUsersTwoAppsWithOverlap() throws Exception {

	// user1
	ApplicationRoleAssignment user1Assignment = new ApplicationRoleAssignment();
	UserNameToken user1Token = new UserNameToken();
	user1Token.setName("user1");
	user1Assignment.setUserNameToken(user1Token);

	// user2
	ApplicationRoleAssignment user2Assignment = new ApplicationRoleAssignment();
	UserNameToken user2Token = new UserNameToken();
	user2Token.setName("user2");
	user2Assignment.setUserNameToken(user2Token);

	// app1
	Properties props = getBasicProperties();
	EntAppNameConfigurationManager config = new TestEntAppNameConfigurationManager(
		props);
	EntAppName app1NameObject = new EntAppName(config, "app1");
	List<ApplicationRoleAssignment> assignments1 = new ArrayList<ApplicationRoleAssignment>();
	assignments1.add(user1Assignment);
	AppTeam appTeam1 = new AppTeam(assignments1, app1NameObject);

	// app2
	EntAppName app2NameObject = new EntAppName(config, "app2");
	List<ApplicationRoleAssignment> assignments2 = new ArrayList<ApplicationRoleAssignment>();
	assignments2.add(user1Assignment);
	assignments2.add(user2Assignment);
	AppTeam appTeam2 = new AppTeam(assignments2, app2NameObject);

	// appIdentifier apps' teams
	List<AppTeam> appTeams = new ArrayList<AppTeam>();
	appTeams.add(appTeam1);
	appTeams.add(appTeam2);

	// Derive appIdentifier team
	List<ApplicationRoleAssignment> team = DeriveAppIdentifierTeamAlgorithm
		.deriveTeam(appTeams);

	// check appIdentifier team
	assertEquals(2, team.size());
	boolean foundUser1 = true;
	boolean foundUser2 = true;
	for (int i = 0; i < 2; i++) {
	    if (team.get(i).getUserNameToken().getName().equals("user1")) {
		foundUser1 = true;
	    } else if (team.get(i).getUserNameToken().getName().equals("user2")) {
		foundUser2 = true;
	    }
	}
	assertTrue(foundUser1);
	assertTrue(foundUser2);
    }

    private Properties getBasicProperties() {
	Properties props = new Properties();

	props.setProperty("appname.separator", "-");
	props.setProperty("appname.pattern.withoutdescriptionformat",
		"[0-9][0-9][0-9]+-(PROD|RC1|RC2|RC3|RC4|RC5)-CURRENT");
	props.setProperty("appname.pattern.withdescriptionformat",
		"[0-9][0-9][0-9]+-.*-(PROD|RC1|RC2|RC3|RC4|RC5)-CURRENT");
	props.setProperty("appname.pattern.followsdescription",
		"-(PROD|RC1|RC2|RC3|RC4|RC5)-CURRENT");
	props.setProperty("appname.pattern.appidentifier", "[0-9][0-9][0-9]+");
	props.setProperty("appname.pattern.suffix.0",
		"(PROD|RC1|RC2|RC3|RC4|RC5)");
	props.setProperty("appname.pattern.suffix.1", "CURRENT");

	return props;
    }
}
