package com.dbw.log;

public class WarningMessages {
    public final static String LAST_N_CHANGES_GT_AUDIT_RECORD_COUNT = "Requested n changes is greater than the audit record count. Outputting all operations...";
    public final static String NO_LAST_N_CHANGES = "Last n changes flag given but there are no audit records.";
    public final static String NO_CACHE_FILE = "Cache file not found.";
}
