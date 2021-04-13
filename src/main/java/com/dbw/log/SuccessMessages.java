package com.dbw.log;

import com.dbw.cli.Opts;

public class SuccessMessages extends Messages {
    public final static String CLI_PURGE = addCliFlagInfo(Opts.PURGE) + "All DBW related database objects have been removed successfully.";
    public final static String CLI_N_ROWS_DELETED = addCliFlagInfo(Opts.DELETE_FIRST_N_ROWS) + "%s first rows of the audit table have been removed.";
    public final static String CLI_ALL_ROWS_DELETED = addCliFlagInfo(Opts.DELETE_FIRST_N_ROWS) + "All (%s) rows of the audit table have been removed.";
    public final static String CLI_AUDIT_TABLE_EMPTY = addCliFlagInfo(Opts.DELETE_FIRST_N_ROWS) + "The audit table is already empty.";
    public final static String CLEAR_CACHE_SUCCESS = addCliFlagInfo(Opts.CLEAR_CACHE) + "Config %s cleared successfully.";
}
