package com.dbw.cli;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class CLITest {
    private static final String TEST_CONFIG_VALUE = "config/test-config.yml";
    private static final String OPTIONS_DELETE_FIRST_N_ROWS_VALUE = "4";
    private static final String TEST_MAX_COLUMN_WIDTH_VALUE = "44";
    private static final String TEST_MAX_ROW_WIDTH_VALUE = "120";
    private static final String OPTIONS_SHOW_LAST_N_CHANGES_VALUE = "6";
    private static final String[] TEST_ARGS = new String[]{
        prependHyphen(Opts.DEBUG),
        prependHyphen(Opts.DELETE_FIRST_N_ROWS),
        OPTIONS_DELETE_FIRST_N_ROWS_VALUE,
        prependHyphen(Opts.PURGE),
        prependHyphen(Opts.CONFIG),
        TEST_CONFIG_VALUE,
        prependHyphen(Opts.MAX_COL_WIDTH),
        TEST_MAX_COLUMN_WIDTH_VALUE,
        prependHyphen(Opts.MAX_ROW_WIDTH),
        TEST_MAX_ROW_WIDTH_VALUE,
        prependHyphen(Opts.LATEST),
        OPTIONS_SHOW_LAST_N_CHANGES_VALUE,
    };
    
    private static CLI cli;

    private static String prependHyphen(String option) {
        return "-" + option;
    }
    
    @BeforeClass
    public static void setup() {
        cli = new CLI();
        try {
            cli.init(TEST_ARGS);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void shouldParseConfigOptions() {
        CLI.ParsedOptions options = null;
        try {
            options = cli.parseArgs();
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertEquals(OPTIONS_DELETE_FIRST_N_ROWS_VALUE, options.getDeleteFirstNRows());
        assertTrue(options.getConfigPaths().get().contains(TEST_CONFIG_VALUE));
        assertEquals(1, options.getConfigPaths().get().size());
        assertTrue(options.getDebug());
        assertTrue(options.getPurge());
        assertEquals(toShort(TEST_MAX_COLUMN_WIDTH_VALUE), options.getMaxColumnWidth());
        assertEquals(toShort(TEST_MAX_ROW_WIDTH_VALUE), options.getMaxRowWidth());
        assertEquals(toLong(OPTIONS_SHOW_LAST_N_CHANGES_VALUE), options.getShowLatestOperations().getValue());
    }

    private Short toShort(String val) {
        return Short.parseShort(val);
    }

    private long toLong(String val) {
        return Long.parseLong(val);
    }
}
