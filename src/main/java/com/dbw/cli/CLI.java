package com.dbw.cli;

import com.dbw.cfg.Config;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CLI {
    private CommandLineParser parser;
    private Options options;
    private CommandLine cmd;
    private String[] args;
    private ParsedOptions parsedOptions;

    public void init() {
        parser = new DefaultParser();
        options = new Options();
        this.setOptions();
        this.setCmd();
    }

    private void setOptions() {
        options.addOption("c", "config", true, "point to a configuration file");
    }

    private void setCmd() {
        try {
            cmd = parser.parse(options, args);
        } catch(ParseException exp) {
            System.out.println("Unexpected exception:" + exp.getMessage());
        }
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public ParsedOptions parseArgs() {
        parsedOptions = new ParsedOptions();
        parsedOptions.configPath = getConfigOption();
        return parsedOptions;
    }

    private String getConfigOption() {
        String configPath;
        if(cmd.hasOption("config")) {
            configPath = cmd.getOptionValue("config");
        } else {
            configPath = Config.DEFAULT_PATH;
        }
        return configPath;
    }

    public class ParsedOptions {
        public String configPath;
    }
}