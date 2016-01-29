package com.blackducksoftware.tools.appuseradjuster.remove;

import java.util.ArrayList;
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
        return AppUserAdjusterType.REMOVE;
    }

    @Override
    public List<String> preProcessUsers(Set<String> usernames) throws CommonFrameworkException {
        return new ArrayList<String>(usernames);
    }

    @Override
    public List<UserStatus> adjustAppUsers(String appId, Set<String> userNames, boolean circumventLocks) throws CommonFrameworkException {
        List<UserStatus> results = codeCenterServerWrapper.getApplicationManager().removeUsersByNameFromApplicationAllRoles(appId, userNames, circumventLocks);
        return results;
    }

}
