/*******************************************************************************
 * Copyright (C) 2015 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version 2 only
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *******************************************************************************/

package com.blackducksoftware.tools.appuseradjuster;

import com.blackducksoftware.tools.appuseradjuster.MultiThreadedUserAdjuster;
import com.blackducksoftware.tools.appuseradjuster.UserAdder;
import com.blackducksoftware.tools.appuseradjuster.UserCreatorConfig;
import com.blackducksoftware.tools.appuseradjuster.UserCreatorConfig.Mode;
import com.blackducksoftware.tools.connector.codecenter.ICodeCenterServerWrapper;

public class MockUserAdder implements UserAdder {
    private UserCreatorConfig configProcessor;

    private MultiThreadedUserAdjuster lobUserAdjuster = null;

    public MockUserAdder(UserCreatorConfig configProcessor) {
        this.configProcessor = configProcessor;
    }

    @Override
    public void setMultiThreadedUserAdjuster(
            MultiThreadedUserAdjuster lobUserAdjuster) {
        this.lobUserAdjuster = lobUserAdjuster;
    }

    @Override
    public void run(ICodeCenterServerWrapper codeCenterServerWrapper,
            int numThreads) throws Exception {
        if (configProcessor.getMode() == Mode.USERS_PER_LOB) {
            lobUserAdjuster.run(codeCenterServerWrapper, numThreads);
        } else {
            throw new UnsupportedOperationException(
                    "MockUserAdder only supports LOB Adjustment mode");
        }

    }

}
