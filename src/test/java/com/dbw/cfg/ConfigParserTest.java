package com.dbw.cfg;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

public class ConfigParserTest {

    public static final String TEST_CONFIG_PATH = "config/test.yml";
    
    @Test
    public void shouldParseConfigFile()
    {
        Config config = null;
        try {
            File configFile = new File(TEST_CONFIG_PATH);
            config = ConfigParser.fromYMLFile(configFile);
        } catch(Exception e) {
            fail(e.getMessage());
        }

        DatabaseConfig dbConfig = config.getDatabase();
        assertEquals(dbConfig.getName(), "example");
        assertEquals(dbConfig.getType(), "postgresql");
        assertEquals(dbConfig.getHost(), "127.0.0.1");
        assertEquals(dbConfig.getPort(), 5432);
        assertEquals(dbConfig.getUser(), "christoph");
        assertEquals(dbConfig.getPassword(), "pwd1234");

        Object[] tables = config.getTables().toArray();
        Object[] expectedTables = {"film", "actor", "address", "inventory", "language", "staff"};
        assertArrayEquals(expectedTables, tables);
    }
}
