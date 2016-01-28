package com.blackducksoftware.tools.appuseradjuster;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.ICodeCenterServerWrapper;
import com.blackducksoftware.tools.connector.codecenter.user.UserStatus;

public class AppUserAdder implements AppUserAdjuster {
    private ICodeCenterServerWrapper codeCenterServerWrapper;

    public AppUserAdder(ICodeCenterServerWrapper codeCenterServerWrapper) {
        this.codeCenterServerWrapper = codeCenterServerWrapper;
    }

    @Override
    public AppUserAdjusterType getType() {
        return AppUserAdjusterType.ADD;
    }

    @Override
    public List<UserStatus> adjustAppUsers(String appId, Set<String> userSet, Set<String> roleNames, boolean circumventLocks) throws CommonFrameworkException {
        codeCenterServerWrapper.getApplicationManager().addUsersByNameToApplicationTeam(appId,
                userSet, roleNames, circumventLocks);
        List<UserStatus> userStatusList = new ArrayList<>(userSet.size());
        for (String username : userSet) {
            UserStatus userStatus = new UserStatus(username, true, null);
            userStatusList.add(userStatus);
        }
        return userStatusList;
    }

}
