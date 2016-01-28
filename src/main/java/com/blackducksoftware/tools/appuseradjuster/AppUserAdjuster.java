package com.blackducksoftware.tools.appuseradjuster;

import java.util.List;
import java.util.Set;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.user.UserStatus;

public interface AppUserAdjuster {
    AppUserAdjusterType getType();

    List<UserStatus> adjustAppUsers(String appId, Set<String> userSet, Set<String> roleNames, boolean circumventLocks) throws CommonFrameworkException;
}
