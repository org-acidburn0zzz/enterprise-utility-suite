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
package com.blackducksoftware.tools.appuseradjuster.remove;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.tools.appuseradjuster.AppUserAdjusterStringConstants;
import com.blackducksoftware.tools.appuseradjuster.MultiThreadedUserAdjuster;
import com.blackducksoftware.tools.appuseradjuster.add.lobuseradjust.applist.AppListProcessorFactory;
import com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser.AppIdentifierUserListMap;
import com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser.AppListProcessorFactoryAppIdentifiersPerUser;
import com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser.AppUserAdjuster;
import com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser.MultiThreadedUserAdjusterAppIdentifiersPerUser;
import com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser.remove.AppUserRemover;
import com.blackducksoftware.tools.common.CommonHarness;
import com.blackducksoftware.tools.common.EntAppName;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterServerWrapper;
import com.blackducksoftware.tools.connector.codecenter.ICodeCenterServerWrapper;
import com.blackducksoftware.tools.connector.codecenter.common.ApplicationRolePojo;

public class RemoveUsers {
    private static final Logger logger = LoggerFactory
            .getLogger(RemoveUsers.class.getName());

    private static final String USAGE_INFO = "\nExamples:\n"
            + "RemoveUsers.sh -config <config_file> -app-identifiers-per-user-file <filename>";

    private final ICodeCenterServerWrapper codeCenterServerWrapper;

    private final MultiThreadedUserAdjuster multiThreadedUserAdjuster;

    private final RemoveUsersConfig config;

    public static void main(String[] args) {
        logger.info("RemoveUsers utility removes users from applications' teams");
        try {
            args = CommonHarness.processConfig(args, USAGE_INFO);
        } catch (Exception e1) {
            System.err.println(e1.getMessage());
            System.exit(-1);
        }

        File configFile = CommonHarness.getConfigFile();

        RemoveUsersConfig config = new RemoveUsersConfig(configFile);

        int argIndex = 0;

        if (!args[argIndex]
                .equals(AppUserAdjusterStringConstants.APPIDENTIFIERS_PER_USER_FILEPATH)) {
            logger.error("The app-identifier-per-user file option is missing from command line");
            System.err.println("Invalid arguments. " + USAGE_INFO);
            System.exit(-1);
        }
        if (args.length <= argIndex + 1
                || args[argIndex + 1].startsWith("-")) {
            logger.error("The app-identifier-per-user file is missing from command line");
            System.err.println("Invalid arguments. " + USAGE_INFO);
            System.exit(-1);
        }

        String appIdentifiersPerUserFilename = args[argIndex + 1];

        // Translate input into <appIdentifier>:<Username>,<Username>,...
        // and other useful permutations
        AppIdentifierUserListMap appIdentifierUserListMap = null;
        try {
            appIdentifierUserListMap = new AppIdentifierUserListMap(
                    appIdentifiersPerUserFilename,
                    config.getUsernamePattern(),
                    config.getAppIdentifierPattern(), true);
        } catch (Exception e) {
            logger.error(
                    "Unable to load app identifiers per user data from file "
                            + appIdentifiersPerUserFilename, e);
            System.err.println("Invalid input file: " + appIdentifiersPerUserFilename);
            System.exit(-1);
        }
        config
                .setAppIdentifierUserListMap(appIdentifierUserListMap);

        ICodeCenterServerWrapper codeCenterServerWrapper = null;
        try {
            codeCenterServerWrapper = connectToCodeCenter(config);
        } catch (Exception e) {
            logger.error("Error connecting to Code Center: " + e.getMessage(),
                    e);
            System.exit(-1);
        }

        try {
            AppUserAdjuster appUserAdjuster = new AppUserRemover(codeCenterServerWrapper);
            AppListProcessorFactory appListProcessorFactory = new AppListProcessorFactoryAppIdentifiersPerUser(
                    codeCenterServerWrapper, config, appUserAdjuster);
            MultiThreadedUserAdjuster adjuster = new MultiThreadedUserAdjusterAppIdentifiersPerUser(
                    config, codeCenterServerWrapper, appListProcessorFactory, appUserAdjuster);

            RemoveUsers remover = new RemoveUsers(config, codeCenterServerWrapper, adjuster);
            remover.globAppIds(appIdentifierUserListMap);
            remover.run(config.getNumThreads());

        } catch (Exception e) {
            logger.error(
                    "Error initializing AppIdentifiers per user mode.", e);
            System.exit(-1);
        }

    }

    public RemoveUsers(RemoveUsersConfig config, ICodeCenterServerWrapper codeCenterServerWrapper, MultiThreadedUserAdjuster adjuster) {
        this.codeCenterServerWrapper = codeCenterServerWrapper;
        multiThreadedUserAdjuster = adjuster;
        this.config = config;
    }

    public void globAppIds(AppIdentifierUserListMap appIdentifierUserListMap) throws CommonFrameworkException {
        Map<String, List<String>> additionalUsernameAppIdentifierListMap = new HashMap<>();

        Set<String> barrenUsernames = appIdentifierUserListMap.getBarrenUsers();
        for (String barrenUsername : barrenUsernames) {
            logger.info("Input requests removal from ALL applications for user: " + barrenUsername);
            List<ApplicationRolePojo> roles = codeCenterServerWrapper.getUserManager().getApplicationRolesByUserName(barrenUsername);
            if (roles == null) {
                logger.warn("User " + barrenUsername + " has no application roles and won't be removed from any applications");
            } else {
                Set<String> barrenUsersAppIds = new HashSet<>();
                for (ApplicationRolePojo role : roles) {
                    logger.info("User " + barrenUsername + " has a role on app: " + role.getApplicationName() + " / " + role.getApplicationVersion());

                    EntAppName appName = new EntAppName(config,
                            role.getApplicationName());
                    logger.info("App ID: " + appName.getAppIdentifier());
                    if (appName.getAppIdentifier() == null) {
                        logger.error("Application " + role.getApplicationName() + " does not match the configured app name pattern; skipping this application");
                        continue;
                    }
                    barrenUsersAppIds.add(appName.getAppIdentifier());
                }
                List<String> appIdList = new ArrayList<String>(barrenUsersAppIds);
                logger.info("Adding to remove-from-all user:appIds map: " + barrenUsername + ": " + appIdList);
                additionalUsernameAppIdentifierListMap.put(barrenUsername, appIdList);
            }
        }

        appIdentifierUserListMap.addMoreUsernameToAppIdsMappings(additionalUsernameAppIdentifierListMap);
    }

    public void run(int numThreads) throws Exception {
        multiThreadedUserAdjuster.run(codeCenterServerWrapper, numThreads);
    }

    private static CodeCenterServerWrapper connectToCodeCenter(
            RemoveUsersConfig configProcessor) throws Exception {
        CodeCenterServerWrapper codeCenterServerWrapper = new CodeCenterServerWrapper(configProcessor);
        return codeCenterServerWrapper;
    }
}
