package com.dbw.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

public class CLITest {
    private static final String TEST_CONFIG_VALUE = "config/test.yml";
    private static final String OPTIONS_DELETE_FIRST_N_ROWS_VALUE = "4";
    private static final String TEST_MAX_COLUMN_WIDTH_VALUE = "44";
    private static final String TEST_MAX_ROW_WIDTH_VALUE = "120";
    private static final String OPTIONS_SHOW_LAST_N_CHANGES_VALUE = "6";
    private static final String[] TEST_ARGS = new String[]{
        prependHyphen(CLI.OPTIONS_DEBUG_FLAG),
        prependHyphen(CLI.OPTIONS_DELETE_FIRST_N_ROWS_FLAG),
        OPTIONS_DELETE_FIRST_N_ROWS_VALUE,
        prependHyphen(CLI.OPTIONS_PURGE_FLAG),
        prependHyphen(CLI.OPTIONS_CONFIG_FLAG),
        TEST_CONFIG_VALUE,
        prependHyphen(CLI.OPTIONS_MAX_COL_WIDTH_FLAG),
        TEST_MAX_COLUMN_WIDTH_VALUE,
        prependHyphen(CLI.OPTIONS_MAX_ROW_WIDTH_FLAG),
        TEST_MAX_ROW_WIDTH_VALUE,
        prependHyphen(CLI.OPTIONS_SHOW_LAST_N_CHANGES_FLAG),
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
        assertEquals(TEST_CONFIG_VALUE, options.getConfigPath().get());
        assertTrue(options.getDebug());
        assertTrue(options.getPurge());
        assertEquals(toShort(TEST_MAX_COLUMN_WIDTH_VALUE), options.getMaxColumnWidth());
        assertEquals(toShort(TEST_MAX_ROW_WIDTH_VALUE), options.getMaxRowWidth());
        assertEquals(toShort(OPTIONS_SHOW_LAST_N_CHANGES_VALUE), options.getShowLastNChanges());
    }

    private Short toShort(String val) {
        return Short.parseShort(val);
    }

}
