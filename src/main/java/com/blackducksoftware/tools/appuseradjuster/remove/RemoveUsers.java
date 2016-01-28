package com.blackducksoftware.tools.appuseradjuster.remove;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.tools.appuseradjuster.AppUserAdjuster;
import com.blackducksoftware.tools.appuseradjuster.AppUserAdjusterStringConstants;
import com.blackducksoftware.tools.appuseradjuster.MultiThreadedUserAdjuster;
import com.blackducksoftware.tools.appuseradjuster.add.lobuseradjust.applist.AppListProcessorFactory;
import com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser.AppIdentifierUserListMap;
import com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser.AppListProcessorFactoryAppIdentifiersPerUser;
import com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser.MultiThreadedUserAdjusterAppIdentifiersPerUser;
import com.blackducksoftware.tools.common.CommonHarness;
import com.blackducksoftware.tools.commonframework.core.config.server.ServerBean;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterServerWrapper;
import com.blackducksoftware.tools.connector.codecenter.ICodeCenterServerWrapper;

public class RemoveUsers {
    private static final Logger logger = LoggerFactory
            .getLogger(RemoveUsers.class.getName());

    private static final String USAGE_INFO = "\nExamples:\n"
            + "RemoveUsers.sh -config <config_file> -app-identifiers-per-user-file <filename>";

    private final ICodeCenterServerWrapper codeCenterServerWrapper;

    private final MultiThreadedUserAdjuster multiThreadedUserAdjuster;

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

        int argIndex = 2;

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
    }

    public void run(int numThreads) throws Exception {
        multiThreadedUserAdjuster.run(codeCenterServerWrapper, numThreads);
    }

    private static CodeCenterServerWrapper connectToCodeCenter(
            RemoveUsersConfig configProcessor) throws Exception {
        ServerBean serverBean = configProcessor.getServerBean();
        CodeCenterServerWrapper codeCenterServerWrapper = new CodeCenterServerWrapper(
                serverBean, configProcessor);
        return codeCenterServerWrapper;
    }
}
