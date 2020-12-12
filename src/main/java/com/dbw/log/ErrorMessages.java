package com.dbw.log;

public class ErrorMessages {
    public final static String COULDNT_SELECT_MAX = "Couldn't select audit table's max id.";
    public final static String STATE_VALIDATION_NULL_OR_EMPTY = "Audit record ID: %d. Could not parse state data. Provided %s state string is null or empty.";
    public final static String UNKNOWN_DB_OPERATION = "Uknown database operation.";
    public final static String UNKNOWN_DB_TYPE = "Unknown / not supported database type.";
    public final static String CLI_INVALID_DELETE_N_ROWS = "Invalid -D flag value. It has to be either a number or an asterisk.";
    public final static String CLI_CLEAN = "Errors occured while deleting database entities. ";
}
