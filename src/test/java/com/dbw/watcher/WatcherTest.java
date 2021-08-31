package com.dbw.watcher;

import com.dbw.app.ObjectCreator;
import com.dbw.cfg.Config;
import com.dbw.cfg.ConfigParser;
import com.dbw.cfg.ConfigParserTest;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.fail;

public class WatcherTest {
    private static Watcher watcher;
    private static Config config;
    
    @BeforeClass
    public static void setup() {
        WatcherManager watcherManager = ObjectCreator.create(WatcherManager.class);
        try {
            File configFile = new File(ConfigParserTest.TEST_CONFIG_PATH);
            config = ConfigParser.fromYMLFile(configFile);
        } catch(Exception e) {
            fail(e.getMessage());
        }
        watcher = new Watcher(watcherManager, config);
    }
    
    @Test
    public void shouldInit()
    {
        // watcher.init();
    }

}
