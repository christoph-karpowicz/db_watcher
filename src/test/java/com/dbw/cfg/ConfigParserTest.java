package com.dbw.cfg;

import com.google.common.collect.Sets;
import org.junit.Test;

import java.io.File;
import java.util.Set;

import static org.junit.Assert.*;

public class ConfigParserTest {

    public static final String TEST_CONFIG_PATH = "config/test-config.yml";
    
    @Test
    public void shouldParseConfigFile() {
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

        Set<String> tables = config.getTables();
        Set<String> expectedTables = Sets.newHashSet("film", "actor", "address", "inventory", "language", "staff");
        expectedTables.forEach(expectedTable -> assertTrue(tables.contains(expectedTable)));
    }
}
