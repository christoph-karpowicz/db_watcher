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
    private final String OPTIONS_FILTER = "filter";
    private final String OPTIONS_SHOW_HELP = "help";
    private final String OPTIONS_MAX_COL_LENGTH = "maxColumnLength";
    private final String OPTIONS_MAX_ROW_LENGTH = "maxRowLength";
    private final String OPTIONS_PURGE = "purge";
    private final String OPTIONS_SHOW_LAST_N_CHANGES = "lastNChanges";
    public static final String OPTIONS_CONFIG_FLAG = "c";
    public static final String OPTIONS_DEBUG_FLAG = "d";
    public static final String OPTIONS_DELETE_FIRST_N_ROWS_FLAG = "D";
    public static final String OPTIONS_FILTER_FLAG = "f";
    public static final String OPTIONS_SHOW_HELP_FLAG = "h";
    public static final String OPTIONS_MAX_COL_LENGTH_FLAG = "l";
    public static final String OPTIONS_MAX_ROW_LENGTH_FLAG = "L";
    public static final String OPTIONS_PURGE_FLAG = "p";
    public static final String OPTIONS_SHOW_LAST_N_CHANGES_FLAG = "n";
    public static final String ALL_SYMBOL = "*";
    
    private CommandLineParser parser;
    private Options options;
    private CommandLine cmd;
    private String[] args;
    private ParsedOptions parsedOptions;

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
        options.addOption(OPTIONS_FILTER_FLAG, OPTIONS_FILTER, true, "show only the specified operations and filter out the rest (i - inserts, u - updates, d - deletes, for example: \"-f u,d\")");
        options.addOption(OPTIONS_SHOW_HELP_FLAG, OPTIONS_SHOW_HELP, false, "show help");
        options.addOption(OPTIONS_MAX_COL_LENGTH_FLAG, OPTIONS_MAX_COL_LENGTH, true, "specify the maximum length of a column (default: " + TableDiffBuilder.DEFAULT_MAX_COL_LENGTH + ")");
        options.addOption(OPTIONS_MAX_ROW_LENGTH_FLAG, OPTIONS_MAX_ROW_LENGTH, true, "specify the maximum length of a row (default: " + TableDiffBuilder.DEFAULT_MAX_ROW_LENGTH + ")");
        options.addOption(OPTIONS_PURGE_FLAG, OPTIONS_PURGE, false, "remove database audit table, functions and triggers");
        options.addOption(OPTIONS_SHOW_LAST_N_CHANGES_FLAG, OPTIONS_SHOW_LAST_N_CHANGES, true, "specify the number of last changes to display after the app starts");
    }

    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(HELP_USAGE, options);
    }

    private void setCmd() throws ParseException {
        cmd = parser.parse(options, args);
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public ParsedOptions parseArgs() throws InvalidCLIOptionInputException {
        parsedOptions = new ParsedOptions();
        parsedOptions.configPath = getConfigOption();
        parsedOptions.debug = getDebugOption();
        parsedOptions.showHelp = getShowHelpOption();
        parsedOptions.purge = getPurgeOption();
        try {
            parsedOptions.deleteFirstNRows = getDeleteFirstNRowsOption();
            parsedOptions.maxColumnLength = getMaxColumnLengthOption();
            parsedOptions.maxRowLength = getMaxRowLengthOption();
            parsedOptions.showLastNChanges = getShowLastNChangesOption();
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

    private Short getMaxColumnLengthOption() throws NumberFormatException {
        if(cmd.hasOption(OPTIONS_MAX_COL_LENGTH)) {
            String optionValue = cmd.getOptionValue(OPTIONS_MAX_COL_LENGTH);
            return Short.parseShort(optionValue);
        }
        return null;
    }

    private Short getMaxRowLengthOption() throws NumberFormatException {
        if(cmd.hasOption(OPTIONS_MAX_ROW_LENGTH)) {
            String optionValue = cmd.getOptionValue(OPTIONS_MAX_ROW_LENGTH);
            return Short.parseShort(optionValue);
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

    public class ParsedOptions {
        private Optional<String> configPath;
        private boolean debug;
        private String deleteFirstNRows;
        private boolean showHelp;
        private Short maxColumnLength;
        private Short maxRowLength;
        private boolean purge;
        private Short showLastNChanges;

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

        public Short getMaxColumnLength() {
            return maxColumnLength;
        }

        public Short getMaxRowLength() {
            return maxRowLength;
        }

        public boolean getPurge() {
            return purge;
        }

        public Short getShowLastNChanges() {
            return showLastNChanges;
        }
    }
}