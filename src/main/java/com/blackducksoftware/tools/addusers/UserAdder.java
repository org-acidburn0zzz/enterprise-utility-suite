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

package com.blackducksoftware.tools.addusers;

import com.blackducksoftware.tools.connector.codecenter.CodeCenterServerWrapper;

/**
 * A "user adder" interface (allows mocking of the user-creation logic for
 * isolating/testing the multi-threading infrastructure).
 *
 * @author sbillings
 *
 */
public interface UserAdder {
    /**
     * Use the given multi-threaded user adjuster.
     *
     * @param lobUserAdjuster
     */
    void setMultiThreadedUserAdjuster(MultiThreadedUserAdjuster lobUserAdjuster);

    /**
     * Use the provided multi-threaded user adjuster to do the user creations,
     * assignments, un-assignments.
     *
     * @param codeCenterServerWrapper
     * @param numThreads
     * @throws Exception
     */
    void run(CodeCenterServerWrapper codeCenterServerWrapper, int numThreads)
            throws Exception;

}
