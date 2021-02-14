package com.dbw.log;

public class ErrorMessages {
    public final static String DB_CONN_FAILED = "Database connection attempt failed. Make sure you've provided the right path for %s.";
    public final static String COULDNT_SELECT_MAX = "Couldn't select audit table's max id.";
    public final static String STATE_VALIDATION_NULL_OR_EMPTY = "Audit record ID: %d. Could not parse state data. Provided %s state string is null or empty.";
    public final static String UNKNOWN_DB_OPERATION = "Uknown database operation.";
    public final static String UNKNOWN_DB_TYPE = "Unknown or not supported database type.";
    public final static String CLI_INVALID_DELETE_N_ROWS = "Invalid -D flag value. It has to be either a number or an asterisk.";
    public final static String CLI_PURGE = "Errors occured while deleting database objects.";
    public final static String INPUT_NAN = "Invalid input. Enter a number from the list.";
    public final static String INPUT_OUT_OF_BOUNDS = "Invalid input. Number out of list bounds.";
    public final static String CONFIG_NO_YML_FILES = "No YML files found in the current directory. Create a config file in the current directory, start the application from a directory with a config file or use the \"c\" flag to specify the config file's path";
    public final static String CREATE_AUDIT_TABLE = "Failed to create an audit table. (%s)";
    public final static String CREATE_AUDIT_TRIGGER = "Failed to create an audit trigger for \"%s\" table. (%s)";
}
