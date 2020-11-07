package com.dbw.cli;

import com.dbw.cfg.Config;
import com.dbw.diff.TableDiffBuilder;
import com.dbw.err.InvalidCLIOptionInputException;
import com.google.common.base.Strings;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CLI {
    private final String OPTIONS_DEBUG = "debug";
    private final String OPTIONS_DELETE_FIRST_N_ROWS = "deleteFirstNRows";
    private final String OPTIONS_CONFIG = "config";
    private final String OPTIONS_CLEAN = "clean";
    private final String OPTIONS_MAX_COL_LENGTH = "maxColumnLength";
    private final String OPTIONS_MAX_ROW_LENGTH = "maxRowLength";
    private final String OPTIONS_SHOW_LAST_N_CHANGES = "lastNChanges";
    
    private CommandLineParser parser;
    private Options options;
    private CommandLine cmd;
    private String[] args;
    private ParsedOptions parsedOptions;

    public void init() throws ParseException {
        parser = new DefaultParser();
        options = new Options();
        setOptions();
        setCmd();
    }

    private void setOptions() {
        options.addOption("d", OPTIONS_DEBUG, false, "show exception classes and stack traces");
        options.addOption("D", OPTIONS_DELETE_FIRST_N_ROWS, true, "delete the first n rows from the audit table (* if all)");
        options.addOption("c", OPTIONS_CONFIG, true, "provice a path to a configuration file");
        options.addOption("C", OPTIONS_CLEAN, false, "remove database audit table, function and triggers");
        options.addOption("l", OPTIONS_MAX_COL_LENGTH, true, "specify the maximum length of a column (default: " + TableDiffBuilder.DEFAULT_MAX_COL_LENGTH + ")");
        options.addOption("L", OPTIONS_MAX_ROW_LENGTH, true, "specify the maximum length of a row (default: " + TableDiffBuilder.DEFAULT_MAX_ROW_LENGTH + ")");
        options.addOption("n", OPTIONS_SHOW_LAST_N_CHANGES, true, "specify the number of last changes to display after the watcher starts");
    }

    private void setCmd() throws ParseException {
        cmd = parser.parse(options, args);
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public ParsedOptions parseArgs() throws InvalidCLIOptionInputException {
        parsedOptions = new ParsedOptions();
        parsedOptions.debug = getDebugOption();
        parsedOptions.configPath = getConfigOption();
        parsedOptions.clean = getCleanOption();
        try {
            parsedOptions.deleteFirstNRows = getDeleteFirstNRowsOption();
            parsedOptions.maxColumnLength = getMaxColumnLengthOption();
            parsedOptions.maxRowLength = getMaxRowLengthOption();
            parsedOptions.lastNChanges = getLastNChangesOption();
        } catch (NumberFormatException e) {
            throw new InvalidCLIOptionInputException(e.getMessage(), e, parsedOptions.debug);
        }
        return parsedOptions;
    }

    private boolean getDebugOption() {
        return cmd.hasOption(OPTIONS_DEBUG);
    }

    private String getDeleteFirstNRowsOption() {
        if(cmd.hasOption(OPTIONS_DELETE_FIRST_N_ROWS)) {
            return cmd.getOptionValue(OPTIONS_DELETE_FIRST_N_ROWS);
        }
        return null;
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

    private boolean getCleanOption() {
        return cmd.hasOption(OPTIONS_CLEAN);
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

    private Short getLastNChangesOption() throws NumberFormatException {
        if(cmd.hasOption(OPTIONS_SHOW_LAST_N_CHANGES)) {
            String optionValue = cmd.getOptionValue(OPTIONS_SHOW_LAST_N_CHANGES);
            return Short.parseShort(optionValue);
        }
        return null;
    }
    
    public class ParsedOptions {
        private boolean debug;
        private String deleteFirstNRows;
        private String configPath;
        private boolean clean;
        private Short maxColumnLength;
        private Short maxRowLength;
        private Short lastNChanges;

        public boolean getDebug() {
            return debug;
        }

        public String getDeleteFirstNRows() {
            return deleteFirstNRows;
        }

        public String getConfigPath() {
            return configPath;
        }

        public boolean getClean() {
            return clean;
        }

        public Short getMaxColumnLength() {
            return maxColumnLength;
        }

        public Short getMaxRowLength() {
            return maxRowLength;
        }

        public Short getLastNChanges() {
            return lastNChanges;
        }
    }
}