package com.dbw.log;

import com.dbw.cli.Opts;
import com.dbw.db.Common;

public class ErrorMessages extends Messages {
    public final static String DB_CONN_FAILED = "Database connection attempt failed. Make sure you've provided the right path for the driver. Error message: %s";
    public final static String STATE_VALIDATION_NULL_OR_EMPTY = "Audit record ID: %d. Could not parse state data. Provided %s state string is null or empty.";
    public final static String UNKNOWN_DB_OPERATION = "%s: Uknown database operation.";
    public final static String UNKNOWN_DB_TYPE = "%s: Unknown or not supported database type.";
    public final static String AUDIT_TABLE_WATCH_ATTEMPT = Common.DBW_AUDIT_TABLE_NAME + " table name found on tables list. An audit trigger must not be created on the audit table.";
    public final static String CLI_INVALID_DELETE_N_ROWS = addCliFlagInfo(Opts.DELETE_FIRST_N_ROWS) + "Invalid value. It has to be either a number or an asterisk.";
    public final static String CLI_INVALID_INTERVAL_SMALL = addCliFlagInfo(Opts.INTERVAL) + "The interval is too small. It cannot be less than 10 milliseconds.";
    public final static String CLI_INVALID_INTERVAL_BIG = addCliFlagInfo(Opts.INTERVAL) + "The interval is too big. You probably don't want it to be bigger than 10 seconds.";
    public final static String CLI_INVALID_MAX_COLUMN_WIDTH = addCliFlagInfo(Opts.MAX_COL_WIDTH) + "Maximum column width has to be greater than 3.";
    public final static String CLI_INVALID_MAX_ROW_WIDTH = addCliFlagInfo(Opts.MAX_ROW_WIDTH) + "Maximum row width has to be greater than 10.";
    public final static String CLI_INVALID_LATEST_OP = addCliFlagInfo(Opts.SHOW_LATEST_OP) + "Latest operations option value is invalid.";
    public final static String CLI_ONE_OFF_NO_LASTEST_OP = addCliFlagInfo(Opts.ONE_OFF) + "This flag has to be used together with the -" + Opts.SHOW_LATEST_OP + " flag, which specifies the number or time of the latest operations to display.";
    public final static String CLI_PURGE = addCliFlagInfo(Opts.PURGE) + "Errors occured while deleting database objects.";
    public final static String CLI_TABLES_NOT_FOUND = addCliFlagInfo(Opts.TABLES) + "%s tables not found in the config files.";
    public final static String CLI_TIME_DIFF_SEP_LT_ZERO = addCliFlagInfo(Opts.TIME_DIFF_SEPARATOR) + "Minimal value after which a time difference separator appears must not be less than 0.";
    public final static String INPUT_NAN = "Invalid input. Enter a number from the list.";
    public final static String INPUT_OUT_OF_BOUNDS = "Invalid input. Number out of list bounds.";
    public final static String CONFIG_NO_YML_FILES = "No YML files found in the current directory. Create a config file in the current directory, start the application from a directory with a config file or use the \"c\" flag to specify the config file's path";
    public final static String CREATE_AUDIT_TABLE = "Failed to create an audit table. (%s)";
    public final static String CREATE_AUDIT_TRIGGER = "Failed to create an audit trigger for \"%s\" table. (%s)";
    public final static String CACHE_PERSIST_FAILED = "Saving cache to dbw.cache file failed.";
}
