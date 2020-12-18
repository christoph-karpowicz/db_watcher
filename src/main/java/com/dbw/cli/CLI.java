package com.dbw.cli;

import com.dbw.cfg.Config;
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
    private final String OPTIONS_CONFIG = "config";
    private final String OPTIONS_DEBUG = "debug";
    private final String OPTIONS_DELETE_FIRST_N_ROWS = "deleteFirstNRows";
    private final String OPTIONS_MAX_COL_LENGTH = "maxColumnLength";
    private final String OPTIONS_MAX_ROW_LENGTH = "maxRowLength";
    private final String OPTIONS_PURGE = "purge";
    private final String OPTIONS_SHOW_LAST_N_CHANGES = "lastNChanges";
    public static final String ALL_SYMBOL = "*";
    
    private CommandLineParser parser;
    private Options options;
    private CommandLine cmd;
    private String[] args;
    private ParsedOptions parsedOptions;

    public void init() throws ParseException {
        parser = new DefaultParser();
        options = new Options();
        setOptions();
        setHelpFormatter();
        setCmd();
    }

    private void setOptions() {
        options.addOption("c", OPTIONS_CONFIG, true, "provide a path to the configuration file");
        options.addOption("d", OPTIONS_DEBUG, false, "show exception classes and stack traces");
        options.addOption("D", OPTIONS_DELETE_FIRST_N_ROWS, true, "delete the first n rows from the audit table (" + ALL_SYMBOL + " if all)");
        options.addOption("l", OPTIONS_MAX_COL_LENGTH, true, "specify the maximum length of a column (default: " + TableDiffBuilder.DEFAULT_MAX_COL_LENGTH + ")");
        options.addOption("L", OPTIONS_MAX_ROW_LENGTH, true, "specify the maximum length of a row (default: " + TableDiffBuilder.DEFAULT_MAX_ROW_LENGTH + ")");
        options.addOption("p", OPTIONS_PURGE, false, "remove database audit table, functions and triggers");
        options.addOption("n", OPTIONS_SHOW_LAST_N_CHANGES, true, "specify the number of last changes to display after the app starts");
    }

    private void setHelpFormatter() {
        HelpFormatter formatter = new HelpFormatter();
        // formatter.printHelp("help", options);
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

    private String getConfigOption() {
        String configPath;
        if(cmd.hasOption(OPTIONS_CONFIG)) {
            configPath = cmd.getOptionValue(OPTIONS_CONFIG);
        } else {
            configPath = Config.DEFAULT_PATH;
        }
        return configPath;
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
        private String configPath;
        private boolean debug;
        private String deleteFirstNRows;
        private Short maxColumnLength;
        private Short maxRowLength;
        private boolean purge;
        private Short showLastNChanges;

        public String getConfigPath() {
            return configPath;
        }
        
        public boolean getDebug() {
            return debug;
        }

        public String getDeleteFirstNRows() {
            return deleteFirstNRows;
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