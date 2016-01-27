package com.blackducksoftware.tools.common.cc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.ICodeCenterServerWrapper;
import com.blackducksoftware.tools.connector.codecenter.user.CodeCenterUserPojo;

public class UserUtils {
    public static List<String> createOrActivateUsers(ICodeCenterServerWrapper codeCenterServerWrapper, Set<String> usernames, String password)
            throws CommonFrameworkException {
        List<String> usersCreated = new ArrayList<>(usernames.size());
        String userId = null;
        for (String username : usernames) {
            try {
                CodeCenterUserPojo existingUser = codeCenterServerWrapper.getUserManager().getUserByName(username);
                userId = existingUser.getId();
                // user exists; make sure it's active
                if (!existingUser.isActive()) {
                    codeCenterServerWrapper.getUserManager().setUserActiveStatus(userId, true);
                }
            } catch (CommonFrameworkException e) {
                // user does not exist; create it
                userId = codeCenterServerWrapper.getUserManager().createUser(username, password, "", "", "", true);
                usersCreated.add(username);
            }
        }
        return usersCreated;
    }
}
