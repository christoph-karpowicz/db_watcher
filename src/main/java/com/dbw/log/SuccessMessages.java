package com.dbw.log;

import com.dbw.cli.CLIStrings;

public class SuccessMessages extends Messages {
    public final static String CLI_PURGE = addCliFlagInfo(CLIStrings.PURGE_FLAG) + "All DBW related database objects have been removed successfully.";
    public final static String CLI_N_ROWS_DELETED = addCliFlagInfo(CLIStrings.DELETE_FIRST_N_ROWS_FLAG) + "%s first rows of the audit table have been removed.";
    public final static String CLI_ALL_ROWS_DELETED = addCliFlagInfo(CLIStrings.DELETE_FIRST_N_ROWS_FLAG) + "All (%s) rows of the audit table have been removed.";
    public final static String CLI_AUDIT_TABLE_EMPTY = addCliFlagInfo(CLIStrings.DELETE_FIRST_N_ROWS_FLAG) + "The audit table is already empty.";
    public final static String CLEAR_CACHE_SUCCESS = addCliFlagInfo(CLIStrings.CLEAR_CACHE_FLAG) + "Config %s cleared successfully.";
}
