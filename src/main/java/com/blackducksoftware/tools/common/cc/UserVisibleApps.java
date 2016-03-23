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
package com.blackducksoftware.tools.common.cc;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.sdk.codecenter.role.data.ApplicationRoleAssignment;
import com.blackducksoftware.sdk.codecenter.role.data.RoleAssignment;
import com.blackducksoftware.sdk.codecenter.role.data.UserRoleAssignment;
import com.blackducksoftware.sdk.codecenter.user.data.UserNameToken;
import com.blackducksoftware.tools.appuseradjuster.add.AddUserConfig;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterServerWrapper;
import com.blackducksoftware.tools.connector.codecenter.ICodeCenterServerWrapper;

// THIS IS JUST POC CODE at the moment

public class UserVisibleApps {

    public static void main(String args[]) throws Exception {
        AddUserConfig config = new AddUserConfig("/Temp/0000_EntUtilSuite/_config/adduser.properties");
        CodeCenterServerWrapper cc = new CodeCenterServerWrapper(config);
        UserVisibleApps uva = new UserVisibleApps(cc, "a500001", "Unspecified");
    }

    List<String> appNames = new ArrayList<>();

    public UserVisibleApps(ICodeCenterServerWrapper cc, String username, String appVersionRestriction) throws SdkFault {
        UserNameToken userToken = new UserNameToken();
        userToken.setName(username);
        List<UserRoleAssignment> roles = null;

        roles = cc.getInternalApiWrapper().getProxy().getRoleApi().getUserRoles(userToken);

        for (RoleAssignment role : roles) {

            if (role instanceof ApplicationRoleAssignment) {
                ApplicationRoleAssignment appRole = (ApplicationRoleAssignment) role;
                System.out
                        .println("App: " + appRole.getApplicationNameVersionToken().getName() + " / " + appRole.getApplicationNameVersionToken().getVersion());
                System.out.println("\tApp ID: " + appRole.getApplicationIdToken().getId());
                System.out.println("\tUser name: " + appRole.getUserNameToken().getName());
                System.out.println("\tUser ID: " + appRole.getUserIdToken().getId());
                System.out.println("\tRole name: " + appRole.getRoleNameToken().getName());
                System.out.println("\tRole ID: " + appRole.getRoleIdToken().getId());
                String appName = appRole.getApplicationNameVersionToken().getName();
                String appVersion = appRole.getApplicationNameVersionToken().getVersion();
                if (!StringUtils.isBlank(appVersionRestriction)) {
                    if (!appVersion.equals(appVersionRestriction)) {
                        continue; // If version provided, and this app doesn't match, skip it
                    }
                }
                appNames.add(appName);
            }
        }
    }

}
