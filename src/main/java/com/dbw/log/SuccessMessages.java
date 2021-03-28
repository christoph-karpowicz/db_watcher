package com.dbw.log;

import com.dbw.cli.CLIStrings;

public class SuccessMessages {
    public final static String CLI_PURGE = "All DBW related database objects have been removed successfully.";
    public final static String CLI_N_ROWS_DELETED = "%s first rows of the audit table have been removed.";
    public final static String CLI_ALL_ROWS_DELETED = "All (%s) rows of the audit table have been removed.";
    public final static String CLI_AUDIT_TABLE_EMPTY = "The audit table is already empty.";
    public final static String CLEAR_CACHE_SUCCESS = "[-" + CLIStrings.CLEAR_CACHE_FLAG + "] Config %s cleared successfully.";
}
