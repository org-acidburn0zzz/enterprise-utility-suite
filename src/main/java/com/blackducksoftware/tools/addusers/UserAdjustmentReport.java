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

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.tools.commonframework.standard.datatable.DataTable;
import com.blackducksoftware.tools.commonframework.standard.datatable.FieldDef;
import com.blackducksoftware.tools.commonframework.standard.datatable.FieldType;
import com.blackducksoftware.tools.commonframework.standard.datatable.Record;
import com.blackducksoftware.tools.commonframework.standard.datatable.RecordDef;
import com.blackducksoftware.tools.commonframework.standard.datatable.writer.DataSetWriter;
import com.blackducksoftware.tools.commonframework.standard.datatable.writer.DataSetWriterExcel;
import com.blackducksoftware.tools.connector.codecenter.user.UserStatus;

/**
 * A report summarizing user creations, assignments, un-assignments, including
 * any error messages.
 *
 * @author sbillings
 *
 */
public class UserAdjustmentReport {
    private final UserCreatorConfig config;

    private final String reportFilenameSuffix;

    private final RecordDef recordDef;

    private final DataTable dataTable;

    private String lob = ""; // This is a field, but it need only be set once.

    // It never gets reset

    public UserAdjustmentReport(UserCreatorConfig config,
            String reportFilenameSuffix) {
        this.config = config;
        this.reportFilenameSuffix = reportFilenameSuffix;
        List<FieldDef> fields = new ArrayList<FieldDef>();
        fields.add(new FieldDef("lob", FieldType.STRING, "LOB Name"));
        fields.add(new FieldDef("applicationName", FieldType.STRING,
                "Application Name"));
        fields.add(new FieldDef("applicationVersion", FieldType.STRING,
                "Application Version"));
        fields.add(new FieldDef("status", FieldType.STRING, "Status"));
        fields.add(new FieldDef("usersCreated", FieldType.STRING,
                "Users created in CC"));
        fields.add(new FieldDef("usersAdded", FieldType.STRING,
                "Users added to app"));
        fields.add(new FieldDef("usersRemoved", FieldType.STRING,
                "Users removed from app"));
        fields.add(new FieldDef("message", FieldType.STRING, "Error message"));

        recordDef = new RecordDef(fields);

        // Create a new/empty DataSet that uses that RecordDef
        dataTable = new DataTable(recordDef);
    }

    /**
     * Adds a record to the report. Called from multiple threads, so it's
     * synchronized.
     *
     * @param appName
     * @param appVersion
     * @param ok
     * @param usersCreated
     * @param usersAdded
     * @param usersRemoved
     * @param message
     * @throws Exception
     */
    public synchronized void addRecord(String appName, String appVersion,
            boolean ok, List<String> usersCreated, List<String> usersAdded,
            List<UserStatus> usersRemoved, String message) throws Exception {
        // Add a record to the dataset
        Record record = new Record(recordDef);
        if (ok) {
            record.setFieldValue("lob", lob);
        } else {
            record.setFieldValue("lob", ""); // Error might be "missing LOB", so
            // don't report LOB on errors
        }

        if (appName == null) {
            appName = "";
        }
        if (appVersion == null) {
            appVersion = "";
        }
        if (message == null) {
            message = "";
        }

        record.setFieldValue("applicationName", appName);
        record.setFieldValue("applicationVersion", appVersion);
        record.setFieldValue("status", ok ? "" : "Error");
        record.setFieldValue("usersCreated", stringify(usersCreated));
        record.setFieldValue("usersAdded", stringify(usersAdded));

        boolean allRemovalsOk = check(usersRemoved);
        if (!allRemovalsOk) {
            if (message.length() > 0) {
                message += "; ";
            }
            message += buildErrorMessage(usersRemoved, "deleting");
        }
        String removedUserListString = successfulList(usersRemoved);
        record.setFieldValue("usersRemoved", removedUserListString);

        record.setFieldValue("message", message);
        dataTable.add(record);
    }

    private boolean check(List<UserStatus> list) {
        if (list == null) {
            return true;
        }

        for (UserStatus userStatus : list) {
            if (!userStatus.isOk()) {
                return false;
            }
        }
        return true;
    }

    private String buildErrorMessage(List<UserStatus> list, String operation) {
        if (list == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (UserStatus userStatus : list) {
            if (userStatus.isOk()) {
                continue; // this one succeeded; skip it
            }
            String s = userStatus.getUsername();
            if (result.length() > 0) {
                result.append(", ");
            }
            result.append("Error ");
            result.append(operation);
            result.append(" user ");
            result.append(s);
            result.append(": ");
            result.append(userStatus.getMessage());
        }
        return result.toString();
    }

    private String successfulList(List<UserStatus> list) {
        if (list == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (UserStatus userStatus : list) {
            if (!userStatus.isOk()) {
                continue; // this one failed; skip it
            }
            String s = userStatus.getUsername();
            if (result.length() > 0) {
                result.append(", ");
            }
            result.append(s);
        }
        return result.toString();
    }

    private String stringify(List<String> list) {
        if (list == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (String s : list) {
            if (result.length() > 0) {
                result.append(", ");
            }
            result.append(s);
        }
        return result.toString();
    }

    /**
     * Setter for the line of business.
     *
     * @param lob
     */
    public void setLob(String lob) {
        this.lob = lob;
    }

    /**
     * Getter for the report data.
     *
     * @return
     */
    public DataTable getDataTable() {
        return dataTable;
    }

    /**
     * Writes the report out to the file specified in the config.
     *
     * @throws Exception
     */
    public void write() throws Exception {
        DataSetWriter writer = new DataSetWriterExcel(config.getReportDir()
                + "/UserAdjustmentReport_" + reportFilenameSuffix + ".xlsx");
        writer.write(dataTable);
    }
}
