package com.dbw.log;

import com.dbw.db.Common;

public class ErrorMessages {
    public final static String DB_CONN_FAILED = "Database connection attempt failed. Make sure you've provided the right path for the driver. Error message: %s";
    public final static String COULDNT_SELECT_MAX = "Couldn't select audit table's max id.";
    public final static String STATE_VALIDATION_NULL_OR_EMPTY = "Audit record ID: %d. Could not parse state data. Provided %s state string is null or empty.";
    public final static String UNKNOWN_DB_OPERATION = "Uknown database operation.";
    public final static String UNKNOWN_DB_TYPE = "Unknown or not supported database type.";
    public final static String AUDIT_TABLE_WATCH_ATTEMPT = Common.DBW_AUDIT_TABLE_NAME + " table name found on tables list. An audit trigger must not be created on the audit table.";
    public final static String CLI_INVALID_DELETE_N_ROWS = "Invalid -D flag value. It has to be either a number or an asterisk.";
    public final static String CLI_INVALID_INTERVAL_SMALL = "The interval is too small. It cannot be less than 10 milliseconds.";
    public final static String CLI_INVALID_INTERVAL_BIG = "The interval is too big. You probably don't want it to be bigger than 10 seconds.";
    public final static String CLI_INVALID_MAX_COLUMN_WIDTH = "Maximum column width has to be greater than 3.";
    public final static String CLI_INVALID_MAX_ROW_WIDTH = "Maximum row width has to be greater than 10.";
    public final static String CLI_PURGE = "Errors occured while deleting database objects.";
    public final static String CLI_TIME_DIFF_SEP_LT_ZERO = "Minimal value after which a time difference separator appears must not be less than 0.";
    public final static String INPUT_NAN = "Invalid input. Enter a number from the list.";
    public final static String INPUT_OUT_OF_BOUNDS = "Invalid input. Number out of list bounds.";
    public final static String CONFIG_NO_YML_FILES = "No YML files found in the current directory. Create a config file in the current directory, start the application from a directory with a config file or use the \"c\" flag to specify the config file's path";
    public final static String CREATE_AUDIT_TABLE = "Failed to create an audit table. (%s)";
    public final static String CREATE_AUDIT_TRIGGER = "Failed to create an audit trigger for \"%s\" table. (%s)";
    public final static String CACHE_PERSIST_FAILED = "Saving cache to dbw.cache file failed.";
}
