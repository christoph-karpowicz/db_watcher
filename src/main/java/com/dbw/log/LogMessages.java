package com.dbw.log;

public class LogMessages {
    public final static String DB_OPENED = "Database connection opened successfully.";
    public final static String DB_CLOSED = "Database connection closed.";
    public final static String CHOOSE_CONFIG = "Choose the config file by entering its number from the list:";
    public final static String CONFIRM_PURGE = "Do you want to remove the audit table and all functions/triggers related to this application from the database? (y/n)";
    public final static String WATCHER_INIT = "Initializing ...";
    public final static String WATCHER_STARTED = "Watcher started.";
    public final static String AUDIT_RECORDS_COUNT = "Audit records count: %d.";
    public final static String SHUTDOWN = "Shutting down ...";
    public final static String AUDIT_TABLE_CREATED = "Audit table has been created.";
    public final static String AUDIT_TABLE_DROPPED = "Audit table has been dropped.";
    public final static String AUDIT_FUNCTION_CREATED = "Audit function has been created.";
    public final static String AUDIT_FUNCTION_DROPPED = "Audit function has been dropped.";
    public final static String AUDIT_TRIGGER_CREATED = "Audit trigger for table \"%s\" has been created.";
    public final static String AUDIT_TRIGGER_DROPPED = "Audit trigger for table \"%s\" has been dropped.";
}
