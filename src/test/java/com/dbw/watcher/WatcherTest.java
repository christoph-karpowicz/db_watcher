package com.dbw.watcher;

import static org.junit.Assert.fail;

import java.io.File;

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
            File configFile = new File(ConfigParserTest.TEST_CONFIG_PATH);
            config = ConfigParser.fromYMLFile(configFile);
        } catch(Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void shouldInit()
    {
        // watcher.init();
    }

}
