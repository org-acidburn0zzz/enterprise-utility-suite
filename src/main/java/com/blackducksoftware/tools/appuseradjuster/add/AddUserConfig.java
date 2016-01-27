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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *******************************************************************************/

package com.blackducksoftware.tools.appuseradjuster.add;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import com.blackducksoftware.tools.appuseradjuster.AppUserAdjusterConfig;
import com.blackducksoftware.tools.appuseradjuster.add.lobuseradjust.SimpleUserSet;
import com.blackducksoftware.tools.common.EntAppNameConfigurationManager;
import com.blackducksoftware.tools.commonframework.core.config.ConfigurationPassword;

/**
 * The configuration object class.
 *
 * @author sbillings
 *
 */
public class AddUserConfig extends AppUserAdjusterConfig implements
        EntAppNameConfigurationManager {

    public enum Mode {
        /**
         * A list of AppIdentifiers is provided for each user
         */
        APPIDENTIFIERS_PER_USER,
        /**
         * A list of application+role pairs are provided for each user
         */
        APP_AND_ROLE_PER_USER,
        /**
         * A list of users is provided for each application
         */
        USERS_PER_APP,
        /**
         * A list of users is provided for each line of business
         */
        USERS_PER_LOB

    }

    private static final String APP_NAME_PROPERTY = "app.name";

    private static final String APP_VERSION_PROPERTY = "app.version";

    // Users to add, separated with a ; (semicolon)
    private static final String USERS_TO_ADD_LIST_PROPERTY = "add.user.request";

    // Role for the user in the application
    private static final String USER_ROLE_PROPERTY = "user.role";

    // Path to file containing configuration data
    private static final String FILE_PATH_PROPERTY = "path";

    private static final String NEW_USER_PASSWORD_PROPERTY_PREFIX = "new.user"; // property

    // is
    // new.user.password
    private static final String LOB_ATTR_NAME_PROPERTY = "lob.attr.name";

    private static final String DEFAULT_PASSWORD = "password";

    private static final String OMIT_MISSING_LOB_RECORDS_FROM_REPORT_PROPERTY = "omit.missing.lob.records.from.report";

    private Mode mode = Mode.USERS_PER_APP;

    private String lob;

    private SimpleUserSet lobUserSet;

    private String lobAttrName;

    private boolean omitMissingLobRecordsFromReport = false;

    /*
     * Application information
     */
    private String applicationName = "";

    private String applicationVersion = "";

    /*
     * User role
     */
    private String userRole = "";

    /*
     * User information
     */
    private String usersToAddListString = "";

    /*
     * File path info
     */
    private String filePath = "";

    private String newUserPassword = DEFAULT_PASSWORD;

    public AddUserConfig(Properties props) {
        super(props, APPLICATION.CODECENTER);
        loadAdditionalProperties();
    }

    public AddUserConfig(InputStream in) {
        super(in, APPLICATION.CODECENTER);
        loadAdditionalProperties();
    }

    public AddUserConfig(String configFilename) {
        super(configFilename, APPLICATION.CODECENTER);
        loadAdditionalProperties();
    }

    public AddUserConfig(File configFile) {
        super(configFile.toString(), APPLICATION.CODECENTER);
        loadAdditionalProperties();
    }

    public void setUsersToAddListString(String userInput) {
        usersToAddListString = userInput;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }

    public String getUserRole() {
        return userRole;
    }

    public String getUsersToAddListString() {
        return usersToAddListString;
    }

    public String getFilePath() {
        return filePath;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public String getLob() {
        return lob;
    }

    public void setLob(String lob) {
        this.lob = lob;
    }

    public SimpleUserSet getLobUserSet() {
        return lobUserSet;
    }

    public void setLobUserSet(SimpleUserSet lobUserSet) {
        this.lobUserSet = lobUserSet;
    }

    public String getLobAttrName() {
        return lobAttrName;
    }

    public void setLobAttrName(String lobAttrName) {
        this.lobAttrName = lobAttrName;
    }

    public String getNewUserPassword() {
        return newUserPassword;
    }

    public void setNewUserPassword(String newUserPassword) {
        this.newUserPassword = newUserPassword;
    }

    public boolean isOmitMissingLobRecordsFromReport() {
        return omitMissingLobRecordsFromReport;
    }

    private void loadAdditionalProperties() {
        applicationName = super.getOptionalProperty(APP_NAME_PROPERTY);
        applicationVersion = super.getProperty(APP_VERSION_PROPERTY);
        userRole = super.getProperty(USER_ROLE_PROPERTY);
        filePath = super.getOptionalProperty(FILE_PATH_PROPERTY);
        usersToAddListString = super
                .getOptionalProperty(USERS_TO_ADD_LIST_PROPERTY);

        ConfigurationPassword configurationPassword = ConfigurationPassword
                .createFromProperty(getProps(),
                        NEW_USER_PASSWORD_PROPERTY_PREFIX);
        if (configurationPassword.getPlainText() != null) {
            newUserPassword = configurationPassword.getPlainText(); // get the
            // plain
            // text
            // value of
            // the
            // password
        }

        lobAttrName = super.getOptionalProperty(LOB_ATTR_NAME_PROPERTY);

        String omitMissingLobRecordsFromReportString = super
                .getOptionalProperty(OMIT_MISSING_LOB_RECORDS_FROM_REPORT_PROPERTY);
        if ("true".equalsIgnoreCase(omitMissingLobRecordsFromReportString)) {
            omitMissingLobRecordsFromReport = true;
        }

    }

}
