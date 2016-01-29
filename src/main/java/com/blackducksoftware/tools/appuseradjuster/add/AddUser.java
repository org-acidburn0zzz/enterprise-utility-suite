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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.codecenter.application.data.Application;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationColumn;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationNameVersionToken;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationPageFilter;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.sdk.codecenter.role.data.RoleNameOrIdToken;
import com.blackducksoftware.sdk.codecenter.role.data.RoleNameToken;
import com.blackducksoftware.sdk.codecenter.user.data.UserNameOrIdToken;
import com.blackducksoftware.sdk.codecenter.user.data.UserNameToken;
import com.blackducksoftware.tools.appuseradjuster.AppUserAdjuster;
import com.blackducksoftware.tools.appuseradjuster.AppUserAdjusterStringConstants;
import com.blackducksoftware.tools.appuseradjuster.MultiThreadedUserAdjuster;
import com.blackducksoftware.tools.appuseradjuster.add.AddUserConfig.Mode;
import com.blackducksoftware.tools.appuseradjuster.add.lobuseradjust.MultiThreadedUserAdjusterLob;
import com.blackducksoftware.tools.appuseradjuster.add.lobuseradjust.SimpleUserSet;
import com.blackducksoftware.tools.appuseradjuster.add.lobuseradjust.applist.AppListProcessorFactory;
import com.blackducksoftware.tools.appuseradjuster.add.lobuseradjust.applist.AppListProcessorFactoryLobAdjust;
import com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser.AppIdentifierUserListMap;
import com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser.AppListProcessorFactoryAppIdentifiersPerUser;
import com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser.MultiThreadedUserAdjusterAppIdentifiersPerUser;
import com.blackducksoftware.tools.common.CommonHarness;
import com.blackducksoftware.tools.commonframework.core.config.server.ServerBean;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterServerWrapper;
import com.blackducksoftware.tools.connector.codecenter.ICodeCenterServerWrapper;

/**
 * The Main class for adduser.
 *
 * @author sbillings
 *
 */
public class AddUser implements UserAdder {
    private static final Logger logger = LoggerFactory
            .getLogger(AddUser.class.getName());

    private static final String UTILITY_VERSION = "1.0";

    private static final String USAGE_INFO = "\nExamples:\n"
            + "AddUser.sh -config <config_file>\n"
            + "AddUser.sh -config <config_file> -app-identifiers-per-user-file <filename>";

    /*
     * Authentication items
     */
    private String server = "";

    private String username = "";

    private String password = "";

    /*
     * Application details
     */
    private String appName = "";

    private String appVersion = "";

    /*
     * User details
     */
    private List<String> users;

    private String usersInput = "";

    private String role = "";

    private String userPassword = "blackduck";

    /*
     * Path to text file containing user/application data
     */
    private String userAppRoleMappingFilePath = "";

    // Instance fields
    private final AddUserConfig configProcessor;

    private MultiThreadedUserAdjuster multiThreadedUserAdjuster;

    /**
     * The main method.
     *
     * @param args
     */
    public static void main(String args[]) {
        logger.info("AddUsersToApplication v{}", UTILITY_VERSION);

        try {
            args = CommonHarness.processConfig(args, USAGE_INFO);
        } catch (Exception e1) {
            System.err.println(e1.getMessage());
            System.exit(-1);
        }

        File configFile = CommonHarness.getConfigFile();

        AddUserConfig config = new AddUserConfig(configFile);
        AddUser adder = new AddUser(config);

        // Username;projectname

        for (int argIndex = 0; argIndex < args.length; argIndex++) {
            if (args[argIndex].equals("-f")) {
                if (args.length > argIndex + 1
                        && !args[argIndex + 1].startsWith("-")) {
                    adder.setUserAppRoleMappingFilePath(args[argIndex + 1]);
                }
            }

            if (args[argIndex].equals(AddUserStringConstants.CODE_CENTER_SERVER)) {
                if (args.length > argIndex + 1
                        && !args[argIndex + 1].startsWith("-")) {
                    adder.setServer(args[argIndex + 1]);
                }
            }
            if (args[argIndex].equals(AddUserStringConstants.CODE_CENTER_USERNAME)) {
                if (args.length > argIndex + 1
                        && !args[argIndex + 1].startsWith("-")) {
                    adder.setUsername(args[argIndex + 1]);
                }
            }
            if (args[argIndex].equals(AddUserStringConstants.CODE_CENTER_PASSWORD)) {
                if (args.length > argIndex + 1
                        && !args[argIndex + 1].startsWith("-")) {
                    adder.setPassword(args[argIndex + 1]);
                }
            }
            if (args[argIndex].equals(AddUserStringConstants.APP_NAME)) {
                if (args.length > argIndex + 1
                        && !args[argIndex + 1].startsWith("-")) {
                    adder.setAppName(args[argIndex + 1]);
                }
            }
            if (args[argIndex].equals(AddUserStringConstants.APP_VERSION)) {
                if (args.length > argIndex + 1
                        && !args[argIndex + 1].startsWith("-")) {
                    adder.setAppVersion(args[argIndex + 1]);
                }
            }
            if (args[argIndex].equals(AddUserStringConstants.USERS)) {
                if (args.length > argIndex + 1
                        && !args[argIndex + 1].startsWith("-")) {
                    adder.setUsersInput(args[argIndex + 1]);
                }
            }
            if (args[argIndex].equals(AddUserStringConstants.ROLE)) {
                if (args.length > argIndex + 1
                        && !args[argIndex + 1].startsWith("-")) {
                    adder.setRole(args[argIndex + 1]);
                }
            }

            if (args[argIndex].equals(AddUserStringConstants.LOB)) {
                if (args.length > argIndex + 1
                        && !args[argIndex + 1].startsWith("-")) {
                    config.setMode(Mode.USERS_PER_LOB);
                    config.setLob(args[argIndex + 1]);
                }
            }
            if (args[argIndex].equals(AddUserStringConstants.LOB_USERLIST_STRING)) {
                if (args.length > argIndex + 1
                        && !args[argIndex + 1].startsWith("-")) {
                    config.setMode(Mode.USERS_PER_LOB);
                    SimpleUserSet lobUserList = null;
                    try {
                        lobUserList = new SimpleUserSet(args[argIndex + 1]);
                    } catch (Exception e) {
                        logger.error(
                                "Unable to load LOB user list from command line argument",
                                e);
                        System.exit(-1);
                    }
                    config.setLobUserSet(lobUserList);
                }
            }
            if (args[argIndex].equals(AddUserStringConstants.LOB_USERLIST_FILEPATH)) {
                if (args.length > argIndex + 1
                        && !args[argIndex + 1].startsWith("-")) {
                    config.setMode(Mode.USERS_PER_LOB);
                    String filename = args[argIndex + 1];
                    File lobUserListFile = new File(filename);
                    SimpleUserSet lobUserList = null;
                    try {
                        lobUserList = new SimpleUserSet(lobUserListFile);
                    } catch (Exception e) {
                        logger.error("Unable to load LOB user list from file "
                                + filename, e);
                        System.exit(-1);
                    }
                    config.setLobUserSet(lobUserList);
                } else {
                    logger.error("The LOB user list file is missing from command line");
                }
            }

            if (args[argIndex]
                    .equals(AppUserAdjusterStringConstants.APPIDENTIFIERS_PER_USER_FILEPATH)) {
                if (args.length > argIndex + 1
                        && !args[argIndex + 1].startsWith("-")) {
                    config.setMode(Mode.APPIDENTIFIERS_PER_USER);
                    String appIdentifiersPerUserFilename = args[argIndex + 1];
                    AppIdentifierUserListMap appIdentifierUserListMap = null;
                    try {
                        appIdentifierUserListMap = new AppIdentifierUserListMap(
                                appIdentifiersPerUserFilename,
                                config.getUsernamePattern(),
                                config.getAppIdentifierPattern());
                    } catch (Exception e) {
                        logger.error(
                                "Unable to load app identifiers per user data from file "
                                        + appIdentifiersPerUserFilename, e);
                        System.exit(-1);
                    }
                    config
                            .setAppIdentifierUserListMap(appIdentifierUserListMap);
                } else {
                    logger.error("The app-identifier-per-user file is missing from command line");
                }
            }

        }

        CodeCenterServerWrapper codeCenterServerWrapper = null;
        try {
            codeCenterServerWrapper = connectToCodeCenter(config);
        } catch (Exception e) {
            logger.error("Error connecting to Code Center: " + e.getMessage(),
                    e);
            System.exit(-1);
        }
        if (config.getMode() == Mode.USERS_PER_LOB) {
            try {
                AppListProcessorFactory appListProcessorFactory = new AppListProcessorFactoryLobAdjust(
                        codeCenterServerWrapper, config);
                adder.setMultiThreadedUserAdjuster(new MultiThreadedUserAdjusterLob(
                        config, appListProcessorFactory));
            } catch (Exception e) {
                logger.error("Error initializing LOB user adjustment mode.", e);
                System.exit(-1);
            }
        } else if (config.getMode() == Mode.APPIDENTIFIERS_PER_USER) {
            try {
                AppUserAdjuster appUserAdjuster = new AppUserAdder(codeCenterServerWrapper, config.getNewUserPassword());
                AppListProcessorFactory appListProcessorFactory = new AppListProcessorFactoryAppIdentifiersPerUser(
                        codeCenterServerWrapper, config, appUserAdjuster);
                MultiThreadedUserAdjuster adjuster = new MultiThreadedUserAdjusterAppIdentifiersPerUser(
                        config, codeCenterServerWrapper, appListProcessorFactory, appUserAdjuster);
                adder.setMultiThreadedUserAdjuster(adjuster);
            } catch (Exception e) {
                logger.error(
                        "Error initializing AppIdentifiers per user mode.", e);
                System.exit(-1);
            }
        }

        Boolean validConfig = adder.applyDefaultsFromConfig();

        if (!validConfig) {
            logger.error("Missing configuration information");
            System.exit(-1);
        }

        try {
            adder.run(codeCenterServerWrapper, config.getNumThreads());
        } catch (Exception e) {
            logger.error("Error adding users: " + e.getMessage());
            System.exit(-1);
        }
    }

    // Public instance methods

    public AddUser(AddUserConfig configProcessor) {
        this.configProcessor = configProcessor;
    }

    /**
     * Anything NOT specified on the command line: fill in from config file.
     */
    public boolean applyDefaultsFromConfig() {
        if (server.isEmpty()) {
            server = configProcessor.getServerBean().getServerName();
        }
        if (username.isEmpty()) {
            username = configProcessor.getServerBean().getUserName();
        }
        if (password.isEmpty()) {
            password = configProcessor.getServerBean().getPassword();
        }
        if (appName.isEmpty()) {
            appName = configProcessor.getApplicationName();
        }
        if (appVersion.isEmpty()) {
            appVersion = configProcessor.getApplicationVersion();
        }
        if (usersInput.isEmpty()) {
            usersInput = configProcessor.getUsersToAddListString();
        }
        if (role.isEmpty()) {
            role = configProcessor.getUserRole();
        }
        if (userAppRoleMappingFilePath.isEmpty()) {
            userAppRoleMappingFilePath = configProcessor.getFilePath();
            if (!StringUtils.isEmpty(userAppRoleMappingFilePath)) {
                if (configProcessor.getMode() == Mode.APPIDENTIFIERS_PER_USER) {
                    logger.warn("The path property should not be set for \"app identifiers per user\" mode. Ignoring it.");
                } else {
                    logger.info("The path property is set, forcing \"app and role per user\" mode.");
                    configProcessor.setMode(Mode.APP_AND_ROLE_PER_USER);
                }
            }
        }

        Boolean valid = true;

        if (server.isEmpty()) {
            logger.info("Missing protex server configuration");
            valid = false;
        }
        if (username.isEmpty()) {
            logger.info("Missing protex username configuration");
            valid = false;
        }
        if (password.isEmpty()) {
            logger.info("Missing protex password configuration");
            valid = false;
        }

        // LOB User Adjust mode doesn't need appName /
        // userAppRoleMappingFilePath
        if ((configProcessor.getMode() != Mode.USERS_PER_LOB)
                && (configProcessor.getMode() != Mode.APPIDENTIFIERS_PER_USER)) {
            if ((appName == null) || appName.isEmpty()) {
                if (userAppRoleMappingFilePath.isEmpty()) {
                    logger.info("Missing application name configuration");
                    valid = false;
                }
            }
        }
        if ((appVersion == null) || appVersion.isEmpty()) {
            logger.info("Missing application version configuration");
            valid = false;
        }

        // LOB User Adjust mode doesn't need usersInput /
        // userAppRoleMappingFilePath
        if ((configProcessor.getMode() != Mode.USERS_PER_LOB)
                && (configProcessor.getMode() != Mode.APPIDENTIFIERS_PER_USER)) {
            if ((usersInput == null) || usersInput.isEmpty()) {
                if (userAppRoleMappingFilePath.isEmpty()) {
                    logger.info("Missing users configuration");
                    valid = false;
                }
            }
        }
        if ((role == null) || role.isEmpty()) {
            logger.info("Missing role configuration");
            valid = false;
        }

        if (!valid) {
            return false;
        }

        users = parseUsers(usersInput);
        return true;
    }

    /**
     * Delegate the work to the appropriate object based on the user-specified
     * mode.
     */
    @Override
    public void run(ICodeCenterServerWrapper codeCenterServerWrapper,
            int numThreads) throws Exception {

        if (configProcessor.getMode() == Mode.USERS_PER_LOB) {
            logger.info("Mode: Users per LOB");
            multiThreadedUserAdjuster.run(codeCenterServerWrapper, numThreads);
        } else if (configProcessor.getMode() == Mode.APPIDENTIFIERS_PER_USER) {
            logger.info("Mode: App Identifiers per user");
            multiThreadedUserAdjuster.run(codeCenterServerWrapper, numThreads);
        } else if ((userAppRoleMappingFilePath == null)
                || userAppRoleMappingFilePath.isEmpty()) {
            logger.info("No user data file was provided; using defaults from config file");
            logger.info("Mode: Users per app");
            processUsers(codeCenterServerWrapper);
        } else {
            logger.info("Mode: App and role per user");
            createAndAssignUsers(codeCenterServerWrapper);
        }
    }

    // private methods

    private void setServer(String server) {
        this.server = server;
    }

    private void setUsername(String username) {
        this.username = username;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    private void setAppName(String appName) {
        this.appName = appName;
    }

    private void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    private void setRole(String role) {
        this.role = role;
    }

    private void setUserAppRoleMappingFilePath(String userAppRoleMappingFilePath) {
        this.userAppRoleMappingFilePath = userAppRoleMappingFilePath;
    }

    private void setUsersInput(String usersInput) {
        this.usersInput = usersInput;
    }

    private List<String> parseUsers(String usersInput) {
        users = new ArrayList<String>();

        if (usersInput == null) {
            return users;
        }
        if (usersInput.contains(";")) {
            for (String currentUserName : usersInput.trim().split(";")) {
                users.add(currentUserName.trim());
            }
        } else {
            users.add(usersInput);
        }

        return users;
    }

    /**
     * App and role per user mode.
     *
     * @param codeCenterServerWrapper
     * @throws Exception
     */
    private void createAndAssignUsers(
            ICodeCenterServerWrapper codeCenterServerWrapper) throws Exception {
        Set<String> userIds = null;
        Set<String> roleNames = null;

        int countSuccess = 0;
        int countFailed = 0;

        // get the list of Applications so we can find the the ones that match
        // the
        List<Application> allApps = new ArrayList<Application>();
        ApplicationPageFilter apf = new ApplicationPageFilter();
        apf.setFirstRowIndex(0);
        apf.setLastRowIndex(1000000);
        apf.setSortAscending(false);
        apf.setSortedColumn(ApplicationColumn.APPLICATION_NAME);

        List<String> userAppMapping = getFileContents();

        for (String current : userAppMapping) {
            if (!current.startsWith("#")) {
                String[] addData = current.trim().split(";");

                String currentUser = null;
                String currentApplication = null;
                String currentRole = null;

                if (addData.length >= 2) {
                    currentUser = addData[0];

                    if (currentUser.length() < 7) {
                        throw new Exception(
                                "Username \""
                                        + currentUser
                                        + "\" is invalid. It must be at least 7 characters");
                    }
                    currentApplication = addData[1];
                    if (currentApplication.length() < 3) {
                        throw new Exception(
                                "Application name \""
                                        + currentApplication
                                        + "\" is invalid. It must be at least 3 characters");
                    }

                    if (addData.length >= 3) {
                        currentRole = addData[2];
                        if (currentRole.length() < 3) {
                            throw new Exception(
                                    "Role name \""
                                            + currentRole
                                            + "\" is invalid. It must be at least 3 characters");
                        }
                    }
                }

                if (currentRole == null) {
                    currentRole = role;
                }

                if (currentUser != null && currentApplication != null) {
                    logger.info("Adding {} as a '{}' user to application [{}]",
                            currentUser, currentRole, currentApplication);

                    userIds = new HashSet<>();
                    for (String user : currentUser.trim().split(",")) {
                        try {

                            String userId = codeCenterServerWrapper.getUserManager().createUser(user, userPassword, "", "", "", true);
                            userIds.add(userId);
                            logger.info("User {} created", user);
                        } catch (CommonFrameworkException createuserException) {
                            logger.info("User {} may already exist", user);
                        }
                    }

                    /*
                     * Establish the role here
                     */
                    roleNames = new HashSet<>();
                    roleNames.add(currentRole);

                    // get the list of Apps
                    try {
                        logger.info("\nRetrieving list of Applications...\n");
                        allApps = codeCenterServerWrapper
                                .getInternalApiWrapper().getApplicationApi()
                                .searchApplications(currentApplication, apf);
                    } catch (SdkFault e) {
                        logger.error(
                                "Error fetching applications from CodeCenter: "
                                        + e.getMessage(), e);
                    }
                    // cycle through apps and add user to each one
                    for (Application theApp : allApps) {
                        String name = theApp.getName();
                        if (!name.startsWith(currentApplication)) {
                            continue;
                        }

                        boolean worked = true;

                        logger.info(
                                "Submitting users {} to the application [{} / {}] team",
                                currentUser, theApp.getName(),
                                theApp.getVersion());
                        try {
                            codeCenterServerWrapper.getApplicationManager().addUsersByIdToApplicationTeam(theApp.getId().getId(), userIds, roleNames, false);
                        } catch (Exception e) {
                            logger.error(
                                    "Unable to add {} to application [{} / {}]!\r\n{}",
                                    currentUser, theApp.getName(),
                                    theApp.getVersion(), e.getMessage());
                            worked = false;
                            countFailed++;
                        }

                        if (worked) {
                            countSuccess++;
                            logger.info(
                                    "{} '{}' user was added to application [{} / {}] team successfully",
                                    userIds.size(), role, theApp.getName(),
                                    theApp.getVersion());
                        }
                    }

                } else {
                    logger.error("Not enough information was provided in this input line: "
                            + current);
                }
            }
        }

        logger.info("------------------------------------------");
        logger.info("Failed imports: {}", countFailed);
        logger.info("Successful imports: {}", countSuccess);
        logger.info("COMPLETE!");
        logger.info("------------------------------------------");
    }

    private List<String> getFileContents() {
        List<String> contents = new ArrayList<String>();

        FileInputStream fis = null;

        try {
            fis = new FileInputStream(userAppRoleMappingFilePath);
        } catch (FileNotFoundException e) {
            logger.error(
                    "Unable to read in the Application/User mapping file: {}",
                    userAppRoleMappingFilePath);
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(fis);

        logger.info("Reading file for Application/User+role mapping ({})",
                userAppRoleMappingFilePath);

        while (scanner.hasNextLine()) {
            contents.add(scanner.nextLine());
        }

        scanner.close();

        return contents;
    }

    /**
     * Users per app mode.
     *
     * @param codeCenterServerWrapper
     * @throws SdkFault
     */
    private void processUsers(ICodeCenterServerWrapper codeCenterServerWrapper)
            throws SdkFault {
        /*
         * List of usernames to submit for the application
         */
        List<UserNameOrIdToken> usersIds = new ArrayList<UserNameOrIdToken>();
        UserNameToken token = null;

        for (String userName : users) {
            token = new UserNameToken();
            token.setName(userName);
            usersIds.add(token);
        }

        /*
         * Establish the role here
         */
        List<RoleNameOrIdToken> roleIds = new ArrayList<RoleNameOrIdToken>();
        RoleNameToken roleToken = new RoleNameToken();
        roleToken.setName(role);
        roleIds.add(roleToken);

        /*
         * Set the application name and version
         */
        ApplicationNameVersionToken appNameVersionToken = new ApplicationNameVersionToken();
        appNameVersionToken.setName(appName);
        appNameVersionToken.setVersion(appVersion);

        logger.info("Submitting {} users to the application [{}]",
                usersIds.size(), appName);
        try {
            codeCenterServerWrapper
                    .getInternalApiWrapper()
                    .getApplicationApi()
                    .addUserToApplicationTeam(appNameVersionToken, usersIds,
                            roleIds);
        } catch (SdkFault e) {
            if (usersIds.size() == 1) {
                logger.error("Unable to add {} to application [{}]!\r\n{}",
                        users.get(0), appName, e.getMessage());
            } else {
                logger.error(
                        "Unable to add the {} users to application [{}]!\r\n{}",
                        username, appName, e.getMessage());
            }
            throw e;
        }

        // Print some information returned
        if (usersIds.size() == 1) {
            logger.info(
                    "{} '{}' user was added to application [{}] team successfully",
                    usersIds.size(), role, appName);
        } else {
            logger.info(
                    "{} '{}' users were added to application [{}] team successfully",
                    usersIds.size(), role, appName);
        }

        logger.info("COMPLETE!");
    }

    private static CodeCenterServerWrapper connectToCodeCenter(
            AddUserConfig configProcessor) throws Exception {
        ServerBean serverBean = configProcessor.getServerBean();
        CodeCenterServerWrapper codeCenterServerWrapper = new CodeCenterServerWrapper(
                serverBean, configProcessor);
        return codeCenterServerWrapper;
    }

    @Override
    public void setMultiThreadedUserAdjuster(
            MultiThreadedUserAdjuster multiThreadedUserAdjuster) {
        this.multiThreadedUserAdjuster = multiThreadedUserAdjuster;
    }
}
