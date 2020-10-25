package com.dbw.cli;

import com.dbw.cfg.Config;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CLI {
    private final String OPTIONS_CONFIG = "config";
    private final String OPTIONS_CLEAN = "clean";
    
    private CommandLineParser parser;
    private Options options;
    private CommandLine cmd;
    private String[] args;
    private ParsedOptions parsedOptions;

    public void init() {
        parser = new DefaultParser();
        options = new Options();
        setOptions();
        setCmd();
    }

    private void setOptions() {
        options.addOption("c", OPTIONS_CONFIG, true, "point to a configuration file");
        options.addOption("C", OPTIONS_CLEAN, false, "remove database audit table, function and triggers");
    }

    private void setCmd() {
        try {
            cmd = parser.parse(options, args);
        } catch(ParseException exp) {
            System.out.println("Unexpected exception:" + exp.getMessage());
            System.err.println(exp.getStackTrace());
        }
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public ParsedOptions parseArgs() {
        parsedOptions = new ParsedOptions();
        parsedOptions.configPath = getConfigOption();
        parsedOptions.clean = getCleanOption();
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

    private boolean getCleanOption() {
        return cmd.hasOption(OPTIONS_CLEAN);
    }

    public class ParsedOptions {
        public String configPath;
        public boolean clean;
    }
}