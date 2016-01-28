package com.blackducksoftware.tools.appuseradjuster;

import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Pattern;

import com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser.AppIdentifierUserListMap;
import com.blackducksoftware.tools.common.EntAppNameConfigMgrDelegate;
import com.blackducksoftware.tools.common.EntAppNameConfigurationManager;
import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;
import com.blackducksoftware.tools.commonframework.core.config.user.CommonUser;

public abstract class AppUserAdjusterConfig extends ConfigurationManager implements
        EntAppNameConfigurationManager {
    private static final String APP_VERSION_PROPERTY = "app.version";

    // Role for the user in the application
    private static final String USER_ROLE_PROPERTY = "user.role";

    private static final String DEFAULT_REPORT_DIR = ".";

    private static final String NUM_THREADS_PROPERTY = "num.threads";

    private static final String REPORT_DIR_PROPERTY = "report.dir";

    private static final String NUM_THREADS_DEFAULT_STRING = "8";

    private static final String USERNAME_PATTERN_STRING_PROPERTY = "username.pattern";

    private static final String LIVE_APP_PATTERN_PROPERTY = "appname.pattern.live";

    private static final String CIRCUMVENT_LOCKS_PROPERTY = "circumvent.locks";

    private String applicationVersion = "";

    /*
     * User role
     */
    private String userRole = "";

    private int numThreads = 8;

    private String reportDir = ".";

    private Pattern liveAppPattern;

    private Pattern usernamePattern;

    private boolean circumventLocks = false;

    private AppIdentifierUserListMap appIdentifierUserListMap;

    private EntAppNameConfigMgrDelegate entAppNameConfigMgrDelegate;

    public AppUserAdjusterConfig(CommonUser user, APPLICATION applicationName) {
        super(user, applicationName);
        loadAdditionalProperties();
    }

    public AppUserAdjusterConfig(InputStream is, APPLICATION applicationName) {
        super(is, applicationName);
        loadAdditionalProperties();
    }

    public AppUserAdjusterConfig(Properties props, APPLICATION applicationName) {
        super(props, applicationName);
        loadAdditionalProperties();
    }

    public AppUserAdjusterConfig(String configFileLocation, APPLICATION applicationName) {
        super(configFileLocation, applicationName);
        loadAdditionalProperties();
    }

    private void loadAdditionalProperties() {
        applicationVersion = super.getProperty(APP_VERSION_PROPERTY);
        userRole = super.getProperty(USER_ROLE_PROPERTY);
        entAppNameConfigMgrDelegate = new EntAppNameConfigMgrDelegate(
                getProps());

        String numThreadsString = super.getOptionalProperty(
                NUM_THREADS_PROPERTY, NUM_THREADS_DEFAULT_STRING, String.class);
        numThreads = Integer.parseInt(numThreadsString);

        reportDir = super.getOptionalProperty(REPORT_DIR_PROPERTY);
        if (reportDir == null) {
            reportDir = DEFAULT_REPORT_DIR;
        }

        String liveAppPatternString = getOptionalProperty(LIVE_APP_PATTERN_PROPERTY);
        if (liveAppPatternString != null) {
            liveAppPattern = Pattern.compile(liveAppPatternString);
        }
        String usernamePatternString = getProperty(USERNAME_PATTERN_STRING_PROPERTY);
        usernamePattern = Pattern.compile(usernamePatternString);

        String circumventLocksString = getOptionalProperty(CIRCUMVENT_LOCKS_PROPERTY);
        if ("true".equalsIgnoreCase(circumventLocksString)) {
            circumventLocks = true;
        }
    }

    @Override
    public String getSeparatorString() {
        return entAppNameConfigMgrDelegate.getSeparatorString();
    }

    @Override
    public String getWithoutDescriptionFormatPatternString() {
        return entAppNameConfigMgrDelegate
                .getWithoutDescriptionFormatPatternString();
    }

    @Override
    public String getWithDescriptionFormatPatternString() {
        return entAppNameConfigMgrDelegate
                .getWithDescriptionFormatPatternString();
    }

    @Override
    public String getAppIdentifierPatternString() {
        return entAppNameConfigMgrDelegate.getAppIdentifierPatternString();
    }

    @Override
    public String getFollowsDescriptionPatternString() {
        return entAppNameConfigMgrDelegate.getFollowsDescriptionPatternString();
    }

    @Override
    public int getNumSuffixes() {
        return entAppNameConfigMgrDelegate.getNumSuffixes();
    }

    @Override
    public String getSuffixPatternString(int suffixIndex) {
        return entAppNameConfigMgrDelegate.getSuffixPatternString(suffixIndex);
    }

    @Override
    public Pattern getAppIdentifierPattern() {
        return entAppNameConfigMgrDelegate.getAppIdentifierPattern();
    }

    @Override
    public Pattern getFollowsDescriptionPattern() {
        return entAppNameConfigMgrDelegate.getFollowsDescriptionPattern();
    }

    @Override
    public Pattern getWithoutDescriptionFormatPattern() {
        return entAppNameConfigMgrDelegate.getWithoutDescriptionFormatPattern();
    }

    @Override
    public Pattern getWithDescriptionFormatPattern() {
        return entAppNameConfigMgrDelegate.getWithDescriptionFormatPattern();
    }

    @Override
    public Pattern getSuffixPattern(int suffixIndex) {
        return entAppNameConfigMgrDelegate.getSuffixPattern(suffixIndex);
    }

    public int getNumThreads() {
        return numThreads;
    }

    public void setNumThreads(int numThreads) {
        this.numThreads = numThreads;
    }

    public String getAppNameSeparator() {
        return entAppNameConfigMgrDelegate.getSeparatorString();
    }

    public String getReportDir() {
        return reportDir;
    }

    public void setReportDir(String reportDir) {
        this.reportDir = reportDir;
    }

    public AppIdentifierUserListMap getAppIdentifierUserListMap() {
        return appIdentifierUserListMap;
    }

    public void setAppIdentifierUserListMap(
            AppIdentifierUserListMap appIdentifierUserListMap) {
        this.appIdentifierUserListMap = appIdentifierUserListMap;
    }

    /**
     * May return null, which means all apps are "live" apps
     *
     * @return
     */
    public Pattern getLiveAppPattern() {
        return liveAppPattern;
    }

    public Pattern getUsernamePattern() {
        return usernamePattern;
    }

    public boolean isCircumventLocks() {
        return circumventLocks;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }

    public String getUserRole() {
        return userRole;
    }
}
