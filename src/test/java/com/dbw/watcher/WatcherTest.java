package com.dbw.watcher;

import static org.junit.Assert.fail;

import com.dbw.cfg.Config;
import com.dbw.cfg.ConfigParser;
import com.dbw.cfg.ConfigParserTest;

import org.junit.BeforeClass;
import org.junit.Test;

public class WatcherTest 
{

    private static Watcher watcher;
    private static Config config;
    
    @BeforeClass
    public static void setup() {
        watcher = new AuditTableWatcher();
        try {
            config = ConfigParser.fromYMLFile(ConfigParserTest.TEST_CONFIG_PATH);
        } catch(Exception e) {
            fail(e.getMessage());
        }
        watcher.setWatchedTables(config.getTables());
    }
    
    @Test
    public void shouldInit()
    {
        // watcher.init();
    }

}
