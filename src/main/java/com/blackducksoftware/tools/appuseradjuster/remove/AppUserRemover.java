package com.blackducksoftware.tools.appuseradjuster.remove;

import java.util.List;
import java.util.Set;

import com.blackducksoftware.tools.appuseradjuster.AppUserAdjuster;
import com.blackducksoftware.tools.appuseradjuster.AppUserAdjusterType;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.ICodeCenterServerWrapper;
import com.blackducksoftware.tools.connector.codecenter.user.UserStatus;

public class AppUserRemover implements AppUserAdjuster {
    private ICodeCenterServerWrapper codeCenterServerWrapper;

    public AppUserRemover(ICodeCenterServerWrapper codeCenterServerWrapper) {
        this.codeCenterServerWrapper = codeCenterServerWrapper;
    }

    @Override
    public AppUserAdjusterType getType() {
        // TODO Auto-generated function stub
        return null;
    }

    @Override
    public List<String> preProcessUsers(Set<String> usernames) throws CommonFrameworkException {
        // TODO Auto-generated function stub
        return null;
    }

    @Override
    public List<UserStatus> adjustAppUsers(String appId, Set<String> userSet, Set<String> roleNames, boolean circumventLocks) throws CommonFrameworkException {
        // TODO Auto-generated function stub
        return null;
    }

}
