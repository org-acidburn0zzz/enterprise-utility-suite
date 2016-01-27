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

package com.blackducksoftware.tools.appuseradjuster.lobuseradjust.applist;

import com.blackducksoftware.tools.appuseradjuster.UserCreatorConfig;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterServerWrapper;

/**
 * An factory that creates AppListProcessors that implement the "LOB adjust"
 * algorithm.
 *
 * @author sbillings
 *
 */
public class AppListProcessorFactoryLobAdjust implements
        AppListProcessorFactory {
    private final CodeCenterServerWrapper codeCenterServerWrapper;

    private final UserCreatorConfig config;

    public AppListProcessorFactoryLobAdjust(
            CodeCenterServerWrapper codeCenterServerWrapper,
            UserCreatorConfig config) {
        this.codeCenterServerWrapper = codeCenterServerWrapper;
        this.config = config;

    }

    /**
     * Create an AppListProcessor that implement the "LOB adjust" algorithm.
     *
     */
    @Override
    public AppListProcessor createAppListProcessor() {
        AppListProcessor processor = new AppListProcessorLobAdjust(
                codeCenterServerWrapper, config);
        return processor;
    }
}
