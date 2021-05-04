package com.dbw.log;

import com.dbw.cli.Opts;

public class LogMessages extends Messages {
    public final static String DB_OPENED = "Database connection opened successfully.";
    public final static String CHOOSE_CONFIG = "Choose one or more config files by entering comma-separated numbers from the list:";
    public final static String CONFIG_UNCHANGED = "Config file hasn't changed. Database preparation will be omitted.";
    public final static String CONFIRM_PURGE = "Are you sure you want to remove all \"%s\" database objects related to this application? (y/n)";
    public final static String WATCHER_INIT = "Initializing ...";
    public final static String DB_PREPARATION = "Preparing database objects...";
    public final static String WATCHER_STARTED = "Watcher started.";
    public final static String ONE_OFF_DONE = addCliFlagInfo(Opts.ONE_OFF) + "One-off output done. Remove this flag to watch operations in real time.";
    public final static String AUDIT_RECORDS_COUNT = "Audit records count: %d.";
    public final static String NUMBER_OF_LATEST_OP = addCliFlagInfo(Opts.SHOW_LATEST_OP) + "%d database operations found in the last %s.";
    public final static String AUDIT_TABLE_CREATED = "Audit table has been created.";
    public final static String AUDIT_TABLE_DROPPED = "Audit table has been dropped.";
    public final static String AUDIT_FUNCTION_CREATED = "Audit function has been created.";
    public final static String AUDIT_FUNCTION_DROPPED = "Audit function has been dropped.";
    public final static String AUDIT_TRIGGER_CREATED = "Audit trigger for table \"%s\" has been created.";
    public final static String AUDIT_TRIGGER_DROPPED = "Audit trigger for table \"%s\" has been dropped.";
}
