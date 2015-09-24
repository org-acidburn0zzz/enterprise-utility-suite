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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *******************************************************************************/

package com.blackducksoftware.tools.addusers;

import com.blackducksoftware.tools.commonframework.standard.codecenter.CodeCenterServerWrapper;
import com.blackducksoftware.tools.commonframework.standard.datatable.DataTable;

/**
 * A multi-threaded user adjuster strategy (as in strategy pattern).
 *
 * @author sbillings
 *
 */
public interface MultiThreadedUserAdjuster {
    /**
     * The run method performs all of the user creates, assignments,
     * un-assignments.
     *
     * @param ccWrapper
     * @param numThreads
     * @throws Exception
     */
    void run(CodeCenterServerWrapper ccWrapper, int numThreads)
	    throws Exception;

    /**
     * Returns the summary report generated during the run method.
     *
     * @return
     */
    DataTable getReport();
}
