package com.dbw.cfg;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ConfigParserTest 
{

    public static final String TEST_CONFIG_PATH = "config/test.yml";
    
    @Test
    public void shouldParseConfigFile()
    {
        Config config = ConfigParser.fromYMLFile(TEST_CONFIG_PATH);

        DatabaseConfig dbConfig = config.getDatabase();
        assertEquals(dbConfig.getName(), "dvdrental");
        assertEquals(dbConfig.getType(), "postgresql");
        assertEquals(dbConfig.getHost(), "127.0.0.1");
        assertEquals(dbConfig.getPort(), 5432);
        assertEquals(dbConfig.getUser(), "chris");
        assertEquals(dbConfig.getPassword(), "1111");

        Object[] tables = config.getTables().toArray();
        Object[] expectedTables = {"film", "actor", "address"};
        assertArrayEquals(expectedTables, tables);
    }
}