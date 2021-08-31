package com.dbw.watcher;

import com.dbw.app.ObjectCreator;
import com.dbw.cfg.Config;
import com.dbw.cfg.DatabaseConfig;
import com.dbw.cfg.SettingsConfig;
import com.dbw.db.Database;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FindWatchedTablesTest {
    private final static List<String> tableNames = Lists.newArrayList(
            "film",
            "actor_test",
            "address",
            "inventory_test",
            "LARGE_CASE",
            "language",
            "staff",
            "_test3224",
            "MIxED_CaSE",
            "clients",
            "ada",
            "beefs",
            "moons",
            "COMPUTERS",
            "frIeND",
            "coLleGoe"
    );
    private final static WatcherManager watcherManager = ObjectCreator.create(WatcherManager.class);
    private final static Config cfgMock = Mockito.mock(Config.class);
    private final static DatabaseConfig databaseConfigMock = Mockito.mock(DatabaseConfig.class);
    private final static SettingsConfig settingsConfigMock = Mockito.mock(SettingsConfig.class);
    static {
        Mockito.when(databaseConfigMock.getName()).thenReturn("test");
        Mockito.when(settingsConfigMock.getTableNamesRegex()).thenReturn(true);
        Mockito.when(cfgMock.getSettings()).thenReturn(settingsConfigMock);
        Mockito.when(cfgMock.getDatabase()).thenReturn(databaseConfigMock);
    }

    private Watcher createWatcher(Set<String> regexPatterns) {
        Mockito.when(cfgMock.getTables()).thenReturn(regexPatterns);
        return new Watcher(watcherManager, cfgMock);
    }
    
    private void assertFoundWatchedTablesBasedOnRegex(Set<String> regexPatterns, Set<String> expectedTables) throws SQLException {
        Watcher watcher = createWatcher(regexPatterns);
        Database dbMock = Mockito.mock(Database.class);
        Mockito.when(dbMock.selectAllTables()).thenReturn(tableNames);
        watcher.setDb(dbMock);
        watcher.findWatchedTables();
        expectedTables.forEach(expectedTable -> assertTrue(watcher.getWatchedTables().contains(expectedTable)));
        assertEquals(expectedTables.size(), watcher.getWatchedTables().size());
    }

    @Test
    public void shouldFindWatchedTablesBasedOnRegex1() throws SQLException {
        Set<String> expectedTables = Sets.newHashSet("film", "coLleGoe", "ada");
        assertFoundWatchedTablesBasedOnRegex(Sets.newHashSet("^[a-rA-M]+$"), expectedTables);
    }

    @Test
    public void shouldFindWatchedTablesBasedOnRegex2() throws SQLException {
        Set<String> expectedTables = Sets.newHashSet("inventory_test", "actor_test");
        assertFoundWatchedTablesBasedOnRegex(Sets.newHashSet(".+_test$"), expectedTables);
    }

    @Test
    public void shouldFindWatchedTablesBasedOnRegex3() throws SQLException {
        Set<String> expectedTables = Sets.newHashSet("language", "MIxED_CaSE");
        assertFoundWatchedTablesBasedOnRegex(Sets.newHashSet(".*(e|E)$", "~^(c|L).+"), expectedTables);
    }

    @Test
    public void shouldFindWatchedTablesBasedOnRegex4() throws SQLException {
        Set<String> expectedTables = Sets.newHashSet("_test3224", "inventory_test");
        assertFoundWatchedTablesBasedOnRegex(Sets.newHashSet(".*", "~[^_]+", "~.{5}_.*"), expectedTables);
    }
}
