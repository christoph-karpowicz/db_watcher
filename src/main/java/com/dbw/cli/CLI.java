package com.dbw.cli;

import com.dbw.cfg.Config;
import com.dbw.diff.TableDiffBuilder;
import com.google.common.base.Strings;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CLI {
    private final String OPTIONS_DEBUG = "debug";
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

    public ParsedOptions parseArgs() {
        parsedOptions = new ParsedOptions();
        parsedOptions.debug = getDebugOption();
        parsedOptions.configPath = getConfigOption();
        parsedOptions.clean = getCleanOption();
        parsedOptions.maxColumnLength = getMaxColumnLengthOption();
        parsedOptions.maxRowLength = getMaxRowLengthOption();
        parsedOptions.lastNChanges = getLastNChangesOption();
        return parsedOptions;
    }

    private boolean getDebugOption() {
        return cmd.hasOption(OPTIONS_DEBUG);
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

    private String getMaxColumnLengthOption() {
        if(cmd.hasOption(OPTIONS_MAX_COL_LENGTH)) {
            return cmd.getOptionValue(OPTIONS_MAX_COL_LENGTH);
        }
        return null;
    }

    private String getMaxRowLengthOption() {
        if(cmd.hasOption(OPTIONS_MAX_ROW_LENGTH)) {
            return cmd.getOptionValue(OPTIONS_MAX_ROW_LENGTH);
        }
        return null;
    }

    private String getLastNChangesOption() {
        if(cmd.hasOption(OPTIONS_SHOW_LAST_N_CHANGES)) {
            return cmd.getOptionValue(OPTIONS_SHOW_LAST_N_CHANGES);
        }
        return null;
    }
    
    public class ParsedOptions {
        private boolean debug;
        private String configPath;
        private boolean clean;
        private String maxColumnLength;
        private String maxRowLength;
        private String lastNChanges;

        public boolean getDebug() {
            return debug;
        }

        public String getConfigPath() {
            return configPath;
        }

        public boolean getClean() {
            return clean;
        }

        public String getMaxColumnLength() {
            return maxColumnLength;
        }

        public String getMaxRowLength() {
            return maxRowLength;
        }

        public short getLastNChanges() {
            return Strings.isNullOrEmpty(lastNChanges) ? 0 : Short.parseShort(lastNChanges);
        }
    }
}