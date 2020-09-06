package com.dbw.cli;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

public class CLITest 
{

    private static final String ARG_CONFIG_FLAG = "-c";
    private static final String TEST_CONFIG_ARG = "config/example.yml";
    private static final String[] TEST_ARGS = new String[]{ARG_CONFIG_FLAG, TEST_CONFIG_ARG};
    private static CLI cli;
    
    @BeforeClass
    public static void setup() {
        cli = new CLI();
        cli.setArgs(TEST_ARGS);
        cli.init();
    }
    
    @Test
    public void shouldParseConfigOptions() {
        CLI.ParsedOptions options = cli.parseArgs();
        assertEquals(TEST_CONFIG_ARG, options.configPath);
    }

}
