package com.blackducksoftware.tools.appuseradjuster.lobuseradjust;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.user.CodeCenterUserPojo;
import com.blackducksoftware.tools.connector.codecenter.user.ICodeCenterUserManager;

public class MockCodeCenterUserManager implements ICodeCenterUserManager {

    @Override
    public String createUser(String username, String password, String firstName, String lastName, String email, boolean active) throws CommonFrameworkException {
        // TODO Auto-generated function stub
        return null;
    }

    @Override
    public CodeCenterUserPojo getUserById(String userId) throws CommonFrameworkException {
        // TODO Auto-generated function stub
        return null;
    }

    @Override
    public CodeCenterUserPojo getUserByName(String userName) throws CommonFrameworkException {
        CodeCenterUserPojo user = new CodeCenterUserPojo(userName.replace(" ", "_"), userName, "test", "user", "", true);
        return user;
    }

    @Override
    public void deleteUserById(String userId) throws CommonFrameworkException {
        // TODO Auto-generated function stub

    }

    @Override
    public void setUserActiveStatus(String userId, boolean active) throws CommonFrameworkException {
        // TODO Auto-generated function stub

    }

}
