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

package com.blackducksoftware.tools.addusers.appidentifiersperuser;

import com.blackducksoftware.tools.addusers.UserCreatorConfig;
import com.blackducksoftware.tools.addusers.lobuseradjust.applist.AppListProcessor;
import com.blackducksoftware.tools.addusers.lobuseradjust.applist.AppListProcessorFactory;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterServerWrapper;

/**
 * An AppListProcessorFactory that produces AppIdentifiersPerUser
 * AppListProcessors.
 *
 * @author sbillings
 *
 */
public class AppListProcessorFactoryAppIdentifiersPerUser implements
        AppListProcessorFactory {
    private final CodeCenterServerWrapper codeCenterServerWrapper;

    private final UserCreatorConfig config;

    private final AppIdentifierUserListMap appIdentifierUserListMap;

    public AppListProcessorFactoryAppIdentifiersPerUser(
            CodeCenterServerWrapper codeCenterServerWrapper,
            UserCreatorConfig config) {
        this.codeCenterServerWrapper = codeCenterServerWrapper;
        this.config = config;
        appIdentifierUserListMap = config.getAppIdentifierUserListMap();
    }

    /**
     * Create an AppListProcessor that executes the AppIdentifiersPerUser
     * algorithm.
     *
     * @param codeCenterServerWrapper
     * @param config
     * @param userManager
     */
    @Override
    public AppListProcessor createAppListProcessor() {
        AppListProcessor processor = new AppListProcessorAppIdentifiersPerUser(
                codeCenterServerWrapper, config,
                appIdentifierUserListMap);
        return processor;
    }
}
