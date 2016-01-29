package com.blackducksoftware.tools.appuseradjuster;

import java.util.List;
import java.util.Set;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.user.UserStatus;

public interface AppUserAdjuster {
    AppUserAdjusterType getType();

    List<String> preProcessUsers(Set<String> usernames) throws CommonFrameworkException;

    List<UserStatus> adjustAppUsers(String appId, Set<String> userSet, boolean circumventLocks) throws CommonFrameworkException;
}
