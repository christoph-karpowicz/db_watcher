package com.dbw.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import com.dbw.cfg.Config;
import com.dbw.cfg.ConfigParser;
import com.dbw.cfg.ConfigParserTest;
import com.dbw.cfg.DatabaseConfig;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class DatabaseTest 
{

    private static Database db;
    private static Config config;
    
    @BeforeClass
    public static void setup() {
        config = ConfigParser.fromYMLFile(ConfigParserTest.TEST_CONFIG_PATH);
    }
    
    @Test
    public void shouldBeAPostgresDatabase() {
        DatabaseConfig dbConfig = config.getDatabase();
        try {
            db = DatabaseFactory.getDatabase(dbConfig);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertEquals(Postgres.class, db.getClass());
    }

    @Test
    public void shouldConnect() {
        db.connect();
        
        boolean connectionClosed = true;
        try {
            connectionClosed = db.getConn().isClosed();
        } catch (SQLException e) {
            fail(e.getMessage());
        }
        assertFalse(connectionClosed);
    }

    @AfterClass
    public static void end() {
        db.close();
    }
}
