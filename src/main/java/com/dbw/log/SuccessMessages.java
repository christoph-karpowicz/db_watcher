package com.dbw.log;

public class SuccessMessages {
    public final static String CLI_PURGE = "All DBW related database entities have been removed successfully.";
    public final static String CLI_N_ROWS_DELETED = "%s first rows of the audit table have been removed.";
    public final static String CLI_ALL_ROWS_DELETED = "All (%s) rows of the audit table have been removed.";
    public final static String CLI_AUDIT_TABLE_EMPTY = "The audit table is already empty.";

    public static String format(String msg, Object... params) {
        return String.format(msg, params);
    }
}
