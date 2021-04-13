package com.dbw.cli;

import com.dbw.diff.TableDiffBuilder;
import com.google.common.collect.Lists;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.util.List;

public class Opts extends Options {
    public static final String HELP_USAGE =
            "\nAn example application start for PostgreSQL configuration:" +
                    "\njava -cp ./dbw.jar com.dbw.app.Dbw -c ./postgres-example-config.yml" +
                    "\nAn example application start for Oracle configuration:" +
                    "\njava -cp ./dbw.jar com.dbw.app.Dbw -c ./oracle-example-config.yml"
            ;

    public static final String ALL_SYMBOL = "*";

    public static final String CONFIG = "c";
    public static final String CLEAR_CACHE = "C";
    public static final String DEBUG = "d";
    public static final String DELETE_FIRST_N_ROWS = "D";
    public static final String SHOW_HELP = "h";
    public static final String INTERVAL = "i";
    public static final String MAX_COL_WIDTH = "w";
    public static final String MAX_ROW_WIDTH = "W";
    public static final String ONE_OFF = "o";
    public static final String PURGE = "p";
    public static final String SHOW_LATEST_OP = "n";
    public static final String TIME_DIFF_SEPARATOR = "t";
    public static final String VERBOSE_DIFF = "V";

    public Opts() {
        super();
        prepare();
    }

    private void prepare() {
        buildOptions().forEach(this::addOption);
    }

    private List<Option> buildOptions() {
        return Lists.newArrayList(
            buildOption(
                    CONFIG,
                    "config",
                    true,
                    "provide a path to the configuration file"
            ),
            buildOption(
                    CLEAR_CACHE,
                    "clear-cache", true, "clear the given config's cache"
            ),
            buildOption(
                    DEBUG,
                    "debug",
                    false,
                    "show exception classes and stack traces"
            ),
            buildOption(
                    DELETE_FIRST_N_ROWS,
                    "delete-first-n-rows",
                    true,
                    "delete the first n rows from the audit table (" + ALL_SYMBOL + " if all)"
            ),
            buildOption(
                    SHOW_HELP,
                    "help",
                    false,
                    "show help"
            ),
            buildOption(
                    INTERVAL,
                    "interval",
                    true,
                    "set the interval in milliseconds in which the application checks whether there were changes in the watched database (default: 500ms)"
            ),
            buildOption(
                    MAX_COL_WIDTH,
                    "max-column-width",
                    true,
                    "specify the maximum width of a column (default: " + TableDiffBuilder.DEFAULT_MAX_COL_WIDTH + ")"
            ),
            buildOption(
                    MAX_ROW_WIDTH,
                    "max-row-width",
                    true,
                    "specify the maximum width of a row (default: " + TableDiffBuilder.DEFAULT_MAX_ROW_WIDTH + ")"
            ),
            buildOption(
                    ONE_OFF,
                    "one-off",
                    false,

                    "show the database operations matching with the given --latest-changes flag value and close the application (don't start the watchers)."
            ),
            buildOption(
                    PURGE,
                    "purge",
                    false,
                    "remove database audit table, functions and triggers"
            ),
            buildOption(
                    SHOW_LATEST_OP,
                    "latest-changes",
                    true,
                    "show the latest operations after the app starts. Accepts a number or a number combined " +
                            "with the seconds, minutes or hours symbol, where for example 3 means three latest operations " +
                            "and 3s means all operations from the last 3 seconds - use \"m\" or \"h\" for minutes or hours"
            ),
            buildOption(
                    TIME_DIFF_SEPARATOR,
                    "time-diff-separator-min-val",
                    true,
                    "specify the time in milliseconds after which a time difference separator will appear between two frames (default: 5000)"
            ),
            buildOption(
                    VERBOSE_DIFF,
                    "verbose-diff",
                    false,

                    "show verbose output, i.e. with full before and after states of column values that exceeded the maximum column width"
            ));
    }

    private Option buildOption(String opt, String longOpt, boolean hasArg, String description) {
        return new Option(opt, longOpt, hasArg, description);
    }
}
