package com.dbw.cli;

import com.dbw.err.InvalidCLIOptionInputException;
import com.dbw.log.ErrorMessages;
import com.google.common.collect.Sets;
import org.apache.commons.cli.*;

import java.util.Optional;
import java.util.Set;

public class CLI {
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
        options.addOption(CLIStrings.CLEAR_CACHE_FLAG, CLIStrings.CLEAR_CACHE, false, CLIStrings.CLEAR_CACHE_FLAG_DESC);
        options.addOption(CLIStrings.CONFIG_FLAG, CLIStrings.CONFIG, true, CLIStrings.CONFIG_FLAG_DESC);
        options.addOption(CLIStrings.DEBUG_FLAG, CLIStrings.DEBUG, false, CLIStrings.DEBUG_FLAG_DESC);
        options.addOption(CLIStrings.DELETE_FIRST_N_ROWS_FLAG, CLIStrings.DELETE_FIRST_N_ROWS, true, CLIStrings.DELETE_FIRST_N_ROWS_FLAG_DESC);
        options.addOption(CLIStrings.SHOW_HELP_FLAG, CLIStrings.SHOW_HELP, false, CLIStrings.SHOW_HELP_FLAG_DESC);
        options.addOption(CLIStrings.INTERVAL_FLAG, CLIStrings.INTERVAL, true, CLIStrings.INTERVAL_FLAG_DESC);
        options.addOption(CLIStrings.MAX_COL_WIDTH_FLAG, CLIStrings.MAX_COL_WIDTH, true, CLIStrings.MAX_COL_WIDTH_FLAG_DESC);
        options.addOption(CLIStrings.MAX_ROW_WIDTH_FLAG, CLIStrings.MAX_ROW_WIDTH, true, CLIStrings.MAX_ROW_WIDTH_FLAG_DESC);
        options.addOption(CLIStrings.PURGE_FLAG, CLIStrings.PURGE, false, CLIStrings.PURGE_FLAG_DESC);
        options.addOption(CLIStrings.SHOW_LAST_N_CHANGES_FLAG, CLIStrings.SHOW_LAST_N_CHANGES, true, CLIStrings.SHOW_LAST_N_CHANGES_FLAG_DESC);
        options.addOption(CLIStrings.TIME_DIFF_SEPARATOR_FLAG, CLIStrings.TIME_DIFF_SEPARATOR, true, CLIStrings.TIME_DIFF_SEPARATOR_FLAG_DESC);
        options.addOption(CLIStrings.VERBOSE_DIFF_FLAG, CLIStrings.VERBOSE_DIFF, false, CLIStrings.VERBOSE_DIFF_FLAG_DESC);
    }

    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(CLIStrings.HELP_USAGE, options);
    }

    private void setCmd() throws ParseException {
        cmd = parser.parse(options, args);
    }

    private void setArgs(String[] args) {
        this.args = args;
    }

    public ParsedOptions parseArgs() throws InvalidCLIOptionInputException {
        ParsedOptions parsedOptions = new ParsedOptions();
        parsedOptions.clearCache = getClearCache();
        parsedOptions.configPaths = getConfigOption();
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

    private boolean getClearCache() {
        return cmd.hasOption(CLIStrings.CLEAR_CACHE);
    }

    private Optional<Set<String>> getConfigOption() {
        if(cmd.hasOption(CLIStrings.CONFIG)) {
            String configOption = cmd.getOptionValue(CLIStrings.CONFIG);
            String[] configPaths = configOption.split(",");
            Set<String> configPathsSet = Sets.newHashSet(configPaths);
            return Optional.of(configPathsSet);
        }
        return Optional.empty();
    }
    
    private boolean getDebugOption() {
        return cmd.hasOption(CLIStrings.DEBUG);
    }

    private String getDeleteFirstNRowsOption() throws NumberFormatException {
        if(cmd.hasOption(CLIStrings.DELETE_FIRST_N_ROWS)) {
            String value = cmd.getOptionValue(CLIStrings.DELETE_FIRST_N_ROWS);
            boolean isNumeric = value.chars().allMatch(Character::isDigit);
            if (!isNumeric && !value.trim().equals(CLIStrings.ALL_SYMBOL)) {
                throw new NumberFormatException(ErrorMessages.CLI_INVALID_DELETE_N_ROWS);
            }
            return value;
        }
        return null;
    }

    private boolean getShowHelpOption() {
        return cmd.hasOption(CLIStrings.SHOW_HELP);
    }

    private Short getInterval() throws NumberFormatException {
        if(cmd.hasOption(CLIStrings.INTERVAL)) {
            String optionValue = cmd.getOptionValue(CLIStrings.INTERVAL);
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
        if(cmd.hasOption(CLIStrings.MAX_COL_WIDTH)) {
            String optionValue = cmd.getOptionValue(CLIStrings.MAX_COL_WIDTH);
            Short value = Short.parseShort(optionValue);
            if (value <= 3) {
                throw new NumberFormatException(ErrorMessages.CLI_INVALID_MAX_COLUMN_WIDTH);
            }
            return value;
        }
        return null;
    }

    private Short getMaxRowWidthOption() throws NumberFormatException {
        if(cmd.hasOption(CLIStrings.MAX_ROW_WIDTH)) {
            String optionValue = cmd.getOptionValue(CLIStrings.MAX_ROW_WIDTH);
            Short value = Short.parseShort(optionValue);
            if (value <= 10) {
                throw new NumberFormatException(ErrorMessages.CLI_INVALID_MAX_ROW_WIDTH);
            }
            return value;
        }
        return null;
    }

    private boolean getPurgeOption() {
        return cmd.hasOption(CLIStrings.PURGE);
    }
    
    private Short getShowLastNChangesOption() throws NumberFormatException {
        if(cmd.hasOption(CLIStrings.SHOW_LAST_N_CHANGES)) {
            String optionValue = cmd.getOptionValue(CLIStrings.SHOW_LAST_N_CHANGES);
            return Short.parseShort(optionValue);
        }
        return null;
    }

    private Short getTimeDiffSeparatorMinVal() throws NumberFormatException {
        if(cmd.hasOption(CLIStrings.TIME_DIFF_SEPARATOR)) {
            String optionValue = cmd.getOptionValue(CLIStrings.TIME_DIFF_SEPARATOR);
            Short value = Short.parseShort(optionValue);
            if (value < 0) {
                throw new NumberFormatException(ErrorMessages.CLI_TIME_DIFF_SEP_LT_ZERO);
            }
            return value;
        }
        return null;
    }

    private boolean getVerboseDiff() {
        return cmd.hasOption(CLIStrings.VERBOSE_DIFF);
    }

    public class ParsedOptions {
        private boolean clearCache;
        private Optional<Set<String>> configPaths;
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

        public boolean getClearCache() {
            return clearCache;
        }

        public Optional<Set<String>> getConfigPaths() {
            return configPaths;
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
