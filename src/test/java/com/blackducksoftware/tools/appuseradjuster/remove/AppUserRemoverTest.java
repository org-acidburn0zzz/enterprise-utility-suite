package com.blackducksoftware.tools.appuseradjuster.remove;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.tools.appuseradjuster.add.MockApplicationManager;
import com.blackducksoftware.tools.appuseradjuster.add.MockCodeCenterServerWrapper;
import com.blackducksoftware.tools.appuseradjuster.appidentifiersperuser.remove.AppUserRemover;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.ICodeCenterServerWrapper;
import com.blackducksoftware.tools.connector.codecenter.user.UserStatus;

public class AppUserRemoverTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void testAdjustAppUsers() throws CommonFrameworkException {
        ICodeCenterServerWrapper cc = new MockCodeCenterServerWrapper(false);
        AppUserRemover remover = new AppUserRemover(cc);
        Set<String> userNames = new HashSet<>();
        userNames.add("User1");
        userNames.add("User2");
        List<UserStatus> results = remover.adjustAppUsers("testAppId1", userNames, true);

        userNames = new HashSet<>();
        userNames.add("User3");
        userNames.add("User4");
        results = remover.adjustAppUsers("testAppId2", userNames, false);

        MockApplicationManager mockAppMgr = (MockApplicationManager) cc.getApplicationManager();
        Set<String> operations = mockAppMgr.getOperations();
        assertEquals(2, operations.size());
        assertTrue(operations.contains("remove: testAppId1: [User1, User2]"));
        assertTrue(operations.contains("remove: testAppId2: [User3, User4]"));
    }

}
