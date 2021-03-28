package com.dbw.cli;

import com.dbw.diff.TableDiffBuilder;

public class CLIStrings {
    public static final String HELP_USAGE =
            "\nAn example application start for PostgreSQL configuration:" +
                    "\njava -cp ./dbw.jar com.dbw.app.Dbw -c ./postgres-example-config.yml" +
                    "\nAn example application start for Oracle configuration:" +
                    "\njava -cp ./dbw.jar com.dbw.app.Dbw -c ./oracle-example-config.yml"
            ;

    public static final String CLEAR_CACHE = "clear-cache";
    public static final String CONFIG = "config";
    public static final String DEBUG = "debug";
    public static final String DELETE_FIRST_N_ROWS = "delete-first-n-rows";
    public static final String SHOW_HELP = "help";
    public static final String INTERVAL = "interval";
    public static final String MAX_COL_WIDTH = "max-column-width";
    public static final String MAX_ROW_WIDTH = "max-row-width";
    public static final String PURGE = "purge";
    public static final String SHOW_LAST_N_CHANGES = "last-n-changes";
    public static final String TIME_DIFF_SEPARATOR = "time-diff-separator-min-val";
    public static final String VERBOSE_DIFF = "verbose-diff";

    public static final String CLEAR_CACHE_FLAG = "C";
    public static final String CONFIG_FLAG = "c";
    public static final String DEBUG_FLAG = "d";
    public static final String DELETE_FIRST_N_ROWS_FLAG = "D";
    public static final String SHOW_HELP_FLAG = "h";
    public static final String INTERVAL_FLAG = "i";
    public static final String MAX_COL_WIDTH_FLAG = "w";
    public static final String MAX_ROW_WIDTH_FLAG = "W";
    public static final String PURGE_FLAG = "p";
    public static final String SHOW_LAST_N_CHANGES_FLAG = "n";
    public static final String TIME_DIFF_SEPARATOR_FLAG = "t";
    public static final String VERBOSE_DIFF_FLAG = "V";

    public static final String CLEAR_CACHE_FLAG_DESC = "clear the given config's cache";
    public static final String CONFIG_FLAG_DESC = "provide a path to the configuration file";
    public static final String DEBUG_FLAG_DESC = "show exception classes and stack traces";
    public static final String DELETE_FIRST_N_ROWS_FLAG_DESC = "delete the first n rows from the audit table (" + CLIStrings.ALL_SYMBOL + " if all)";
    public static final String SHOW_HELP_FLAG_DESC = "show help";
    public static final String INTERVAL_FLAG_DESC = "set the interval in milliseconds in which the application checks whether there were changes in the watched database (default: 500ms)";
    public static final String MAX_COL_WIDTH_FLAG_DESC = "specify the maximum width of a column (default: " + TableDiffBuilder.DEFAULT_MAX_COL_WIDTH + ")";
    public static final String MAX_ROW_WIDTH_FLAG_DESC = "specify the maximum width of a row (default: " + TableDiffBuilder.DEFAULT_MAX_ROW_WIDTH + ")";
    public static final String PURGE_FLAG_DESC = "remove database audit table, functions and triggers";
    public static final String SHOW_LAST_N_CHANGES_FLAG_DESC = "specify the number of last changes to display after the app starts";
    public static final String TIME_DIFF_SEPARATOR_FLAG_DESC = "specify the time in milliseconds after which a time difference separator will appear between two frames (default: 5000)";
    public static final String VERBOSE_DIFF_FLAG_DESC = "show verbose output, i.e. with full before and after states of column values that exceeded the maximum column width";

    public static final String ALL_SYMBOL = "*";
}
