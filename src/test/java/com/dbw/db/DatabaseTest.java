package com.dbw.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import com.dbw.cfg.Config;
import com.dbw.cfg.ConfigParser;
import com.dbw.cfg.DatabaseConfig;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class DatabaseTest {
    private static Database db;
    private static Config config;
    
    @BeforeClass
    public static void setup() {
        try {
            String testConfigPathFromArgument = System.getProperty("testConfigPath");
            config = ConfigParser.fromYMLFile(testConfigPathFromArgument);
        } catch(Exception e) {
            fail(e.getMessage());
        }
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
        try {
            db.connect();
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
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
        try {
            db.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
    }
}
