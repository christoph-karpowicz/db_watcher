package com.dbw.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.File;
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
    private static String testConfigPathFromArgument;
    
    @BeforeClass
    public static void setup() {
        try {
            testConfigPathFromArgument = System.getProperty("testConfigPath");
            File configFile = new File(testConfigPathFromArgument);
            config = ConfigParser.fromYMLFile(configFile);
        } catch(Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void shouldBeTheRightDatabaseType() {
        try {
            db = DatabaseFactory.getDatabase(config);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        if (testConfigPathFromArgument.contains("orcl")) {
            assertEquals(Orcl.class, db.getClass());
        } else {
            assertEquals(Postgres.class, db.getClass());
        }
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
