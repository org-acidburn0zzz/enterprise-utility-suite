package com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser.remove;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.tools.appuseradjuster.AppUserAdjusterType;
import com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser.AppUserAdjuster;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.ICodeCenterServerWrapper;
import com.blackducksoftware.tools.connector.codecenter.user.UserStatus;

public class AppUserRemover implements AppUserAdjuster {
    private final Logger logger = LoggerFactory.getLogger(this.getClass()
            .getName());

    private ICodeCenterServerWrapper codeCenterServerWrapper;

    public AppUserRemover(ICodeCenterServerWrapper codeCenterServerWrapper) {
        this.codeCenterServerWrapper = codeCenterServerWrapper;
    }

    @Override
    public AppUserAdjusterType getType() {
        return AppUserAdjusterType.REMOVE;
    }

    @Override
    public List<String> preProcessUsers(Set<String> usernames) throws CommonFrameworkException {
        return new ArrayList<String>(0);
    }

    @Override
    public List<UserStatus> adjustAppUsers(String appId, Set<String> userNames, boolean circumventLocks) throws CommonFrameworkException {
        logger.info("Removing from appId " + appId + " users: " + userNames);
        List<UserStatus> results = codeCenterServerWrapper.getApplicationManager().removeUsersByNameFromApplicationAllRoles(appId, userNames, circumventLocks);
        return results;
    }

}
