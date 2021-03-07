package com.dbw.cli;

import java.util.Optional;

import com.dbw.diff.TableDiffBuilder;
import com.dbw.err.InvalidCLIOptionInputException;
import com.dbw.log.ErrorMessages;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CLI {
    private final String HELP_USAGE = 
        "\nAn example application start for PostgreSQL configuration:" + 
        "\njava -cp ./dbw.jar com.dbw.app.Dbw -c ./postgres-example-config.yml" + 
        "\nAn example application start for Oracle configuration:" + 
        "\njava -cp lib/ojdbc7.jar:./dbw.jar com.dbw.app.Dbw -c ./oracle-example-config.yml"
        ;
    private final String OPTIONS_CONFIG = "config";
    private final String OPTIONS_DEBUG = "debug";
    private final String OPTIONS_DELETE_FIRST_N_ROWS = "deleteFirstNRows";
    private final String OPTIONS_SHOW_HELP = "help";
    private final String OPTIONS_INTERVAL = "interval";
    private final String OPTIONS_MAX_COL_WIDTH = "maxColumnWidth";
    private final String OPTIONS_MAX_ROW_WIDTH = "maxRowWidth";
    private final String OPTIONS_PURGE = "purge";
    private final String OPTIONS_SHOW_LAST_N_CHANGES = "lastNChanges";
    private final String OPTIONS_TIME_DIFF_SEPARATOR = "timeDiffSeparatorMinVal";
    private final String OPTIONS_VERBOSE_DIFF = "verboseDiff";
    public static final String OPTIONS_CONFIG_FLAG = "c";
    public static final String OPTIONS_DEBUG_FLAG = "d";
    public static final String OPTIONS_DELETE_FIRST_N_ROWS_FLAG = "D";
    public static final String OPTIONS_SHOW_HELP_FLAG = "h";
    public static final String OPTIONS_INTERVAL_FLAG = "i";
    public static final String OPTIONS_MAX_COL_WIDTH_FLAG = "w";
    public static final String OPTIONS_MAX_ROW_WIDTH_FLAG = "W";
    public static final String OPTIONS_PURGE_FLAG = "p";
    public static final String OPTIONS_SHOW_LAST_N_CHANGES_FLAG = "n";
    public static final String OPTIONS_TIME_DIFF_SEPARATOR_FLAG = "t";
    public static final String OPTIONS_VERBOSE_DIFF_FLAG = "V";
    public static final String ALL_SYMBOL = "*";
    
    private CommandLineParser parser;
    private Options options;
    private CommandLine cmd;
    private String[] args;

    public void init(String[] args) throws ParseException, InvalidCLIOptionInputException {
        setArgs(args);
        parser = new DefaultParser();
        options = new Options();
        setOptions();
        setCmd();
    }

    public ParsedOptions handleArgs() throws InvalidCLIOptionInputException {
        ParsedOptions parsedOptions = parseArgs();
        if (parsedOptions.getShowHelp()) {
            printHelp();
            System.exit(1);
        }
        return parsedOptions;
    }

    private void setOptions() {
        options.addOption(OPTIONS_CONFIG_FLAG, OPTIONS_CONFIG, true, "provide a path to the configuration file");
        options.addOption(OPTIONS_DEBUG_FLAG, OPTIONS_DEBUG, false, "show exception classes and stack traces");
        options.addOption(OPTIONS_DELETE_FIRST_N_ROWS_FLAG, OPTIONS_DELETE_FIRST_N_ROWS, true, "delete the first n rows from the audit table (" + ALL_SYMBOL + " if all)");
        options.addOption(OPTIONS_SHOW_HELP_FLAG, OPTIONS_SHOW_HELP, false, "show help");
        options.addOption(OPTIONS_INTERVAL_FLAG, OPTIONS_INTERVAL, true, "set the interval in milliseconds in which the application checks whether there were changes in the watched database (default: 500ms)");
        options.addOption(OPTIONS_MAX_COL_WIDTH_FLAG, OPTIONS_MAX_COL_WIDTH, true, "specify the maximum width of a column (default: " + TableDiffBuilder.DEFAULT_MAX_COL_WIDTH + ")");
        options.addOption(OPTIONS_MAX_ROW_WIDTH_FLAG, OPTIONS_MAX_ROW_WIDTH, true, "specify the maximum width of a row (default: " + TableDiffBuilder.DEFAULT_MAX_ROW_WIDTH + ")");
        options.addOption(OPTIONS_PURGE_FLAG, OPTIONS_PURGE, false, "remove database audit table, functions and triggers");
        options.addOption(OPTIONS_SHOW_LAST_N_CHANGES_FLAG, OPTIONS_SHOW_LAST_N_CHANGES, true, "specify the number of last changes to display after the app starts");
        options.addOption(OPTIONS_TIME_DIFF_SEPARATOR_FLAG, OPTIONS_TIME_DIFF_SEPARATOR, true, "specify the time in milliseconds after which a time difference separator will appear between two frames (default: 5000)");
        options.addOption(OPTIONS_VERBOSE_DIFF_FLAG, OPTIONS_VERBOSE_DIFF, false, "show verbose output, i.e. with full before and after states of column values that exceeded the maximum column width");
    }

    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(HELP_USAGE, options);
    }

    private void setCmd() throws ParseException {
        cmd = parser.parse(options, args);
    }

    private void setArgs(String[] args) {
        this.args = args;
    }

    public ParsedOptions parseArgs() throws InvalidCLIOptionInputException {
        ParsedOptions parsedOptions = new ParsedOptions();
        parsedOptions.configPath = getConfigOption();
        parsedOptions.debug = getDebugOption();
        parsedOptions.showHelp = getShowHelpOption();
        parsedOptions.purge = getPurgeOption();
        parsedOptions.verboseDiff = getVerboseDiff();
        try {
            parsedOptions.deleteFirstNRows = getDeleteFirstNRowsOption();
            parsedOptions.interval = getInterval();
            parsedOptions.maxColumnWidth = getMaxColumnWidthOption();
            parsedOptions.maxRowWidth = getMaxRowWidthOption();
            parsedOptions.showLastNChanges = getShowLastNChangesOption();
            parsedOptions.timeDiffSeparatorMinVal = getTimeDiffSeparatorMinVal();
        } catch (NumberFormatException e) {
            throw new InvalidCLIOptionInputException(e.getMessage(), e, parsedOptions.debug);
        }
        return parsedOptions;
    }

    private Optional<String> getConfigOption() {
        String configPath = null;
        if(cmd.hasOption(OPTIONS_CONFIG)) {
            configPath = cmd.getOptionValue(OPTIONS_CONFIG);
        }
        return Optional.ofNullable(configPath);
    }
    
    private boolean getDebugOption() {
        return cmd.hasOption(OPTIONS_DEBUG);
    }

    private String getDeleteFirstNRowsOption() throws NumberFormatException {
        if(cmd.hasOption(OPTIONS_DELETE_FIRST_N_ROWS)) {
            String value = cmd.getOptionValue(OPTIONS_DELETE_FIRST_N_ROWS);
            boolean isNumeric = value.chars().allMatch(Character::isDigit);
            if (!isNumeric && !value.trim().equals(ALL_SYMBOL)) {
                throw new NumberFormatException(ErrorMessages.CLI_INVALID_DELETE_N_ROWS);
            }
            return value;
        }
        return null;
    }

    private boolean getShowHelpOption() {
        return cmd.hasOption(OPTIONS_SHOW_HELP);
    }

    private Short getInterval() throws NumberFormatException {
        if(cmd.hasOption(OPTIONS_INTERVAL)) {
            String optionValue = cmd.getOptionValue(OPTIONS_INTERVAL);
            Short value = Short.parseShort(optionValue);
            if (value < 10) {
                throw new NumberFormatException(ErrorMessages.CLI_INVALID_INTERVAL_SMALL);
            }
            if (value > 10000) {
                throw new NumberFormatException(ErrorMessages.CLI_INVALID_INTERVAL_BIG);
            }
            return value;
        }
        return null;
    }

    private Short getMaxColumnWidthOption() throws NumberFormatException {
        if(cmd.hasOption(OPTIONS_MAX_COL_WIDTH)) {
            String optionValue = cmd.getOptionValue(OPTIONS_MAX_COL_WIDTH);
            Short value = Short.parseShort(optionValue);
            if (value <= 3) {
                throw new NumberFormatException(ErrorMessages.CLI_INVALID_MAX_COLUMN_WIDTH);
            }
            return value;
        }
        return null;
    }

    private Short getMaxRowWidthOption() throws NumberFormatException {
        if(cmd.hasOption(OPTIONS_MAX_ROW_WIDTH)) {
            String optionValue = cmd.getOptionValue(OPTIONS_MAX_ROW_WIDTH);
            Short value = Short.parseShort(optionValue);
            if (value <= 10) {
                throw new NumberFormatException(ErrorMessages.CLI_INVALID_MAX_ROW_WIDTH);
            }
            return value;
        }
        return null;
    }

    private boolean getPurgeOption() {
        return cmd.hasOption(OPTIONS_PURGE);
    }
    
    private Short getShowLastNChangesOption() throws NumberFormatException {
        if(cmd.hasOption(OPTIONS_SHOW_LAST_N_CHANGES)) {
            String optionValue = cmd.getOptionValue(OPTIONS_SHOW_LAST_N_CHANGES);
            return Short.parseShort(optionValue);
        }
        return null;
    }

    private Short getTimeDiffSeparatorMinVal() throws NumberFormatException {
        if(cmd.hasOption(OPTIONS_TIME_DIFF_SEPARATOR)) {
            String optionValue = cmd.getOptionValue(OPTIONS_TIME_DIFF_SEPARATOR);
            Short value = Short.parseShort(optionValue);
            if (value < 0) {
                throw new NumberFormatException(ErrorMessages.CLI_TIME_DIFF_SEP_LT_ZERO);
            }
            return value;
        }
        return null;
    }

    private boolean getVerboseDiff() {
        return cmd.hasOption(OPTIONS_VERBOSE_DIFF);
    }

    public class ParsedOptions {
        private Optional<String> configPath;
        private boolean debug;
        private String deleteFirstNRows;
        private boolean showHelp;
        private Short interval;
        private Short maxColumnWidth;
        private Short maxRowWidth;
        private boolean purge;
        private Short showLastNChanges;
        private Short timeDiffSeparatorMinVal;
        private boolean verboseDiff;

        public Optional<String> getConfigPath() {
            return configPath;
        }
        
        public boolean getDebug() {
            return debug;
        }

        public String getDeleteFirstNRows() {
            return deleteFirstNRows;
        }

        public boolean getShowHelp() {
            return showHelp;
        }

        public Short getInterval() {
            return interval;
        }

        public Short getMaxColumnWidth() {
            return maxColumnWidth;
        }

        public Short getMaxRowWidth() {
            return maxRowWidth;
        }

        public boolean getPurge() {
            return purge;
        }

        public Short getShowLastNChanges() {
            return showLastNChanges;
        }

        public Short getTimeDiffSeparatorMinVal() {
            return timeDiffSeparatorMinVal;
        }

        public boolean getVerboseDiff() {
            return verboseDiff;
        }
    }
}
