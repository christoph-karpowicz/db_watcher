package com.dbw.log;

public class WarningMessages {
    public final static String LATEST_OPS_NUM_GT_AUDIT_RECORD_COUNT = "Requested number of latest operations is greater than the audit record count. Outputting all operations...";
    public final static String LATEST_OPS_TIME_SPANS_ALL = "There are no operations older than %s. Outputting all operations...";
    public final static String LATEST_OPS_TIME_SPANS_NONE = "There were no operations in the last %s.";
    public final static String NO_LATEST_OPS = "Last n changes flag given but there are no audit records.";
    public final static String NO_CACHE_FILE = "Cache file not found.";
    public final static String CLEAR_CACHE_NOT_FOUND = "Clear config cache request: %s not found in cache.";
}
