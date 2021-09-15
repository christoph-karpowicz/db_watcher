package com.dbw.watcher;

import com.dbw.cfg.Config;
import com.dbw.cfg.DatabaseConfig;
import com.dbw.cfg.SettingsConfig;
import com.dbw.db.Database;
import com.dbw.db.TableRegexFinder;
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
            "coLleGoe",
            "y_gles"
    );
    private final static Config cfgMock = Mockito.mock(Config.class);
    private final static DatabaseConfig databaseConfigMock = Mockito.mock(DatabaseConfig.class);
    private final static SettingsConfig settingsConfigMock = Mockito.mock(SettingsConfig.class);
    static {
        Mockito.when(databaseConfigMock.getName()).thenReturn("test");
        Mockito.when(settingsConfigMock.getTableNamesRegex()).thenReturn(true);
        Mockito.when(cfgMock.getSettings()).thenReturn(settingsConfigMock);
        Mockito.when(cfgMock.getDatabase()).thenReturn(databaseConfigMock);
    }

    private void assertFoundWatchedTablesBasedOnRegex(Set<String> regexPatterns, Set<String> expectedTables) throws SQLException {
        Mockito.when(cfgMock.getTables()).thenReturn(regexPatterns);
        Database dbMock = Mockito.mock(Database.class);
        Mockito.when(dbMock.selectAllTables()).thenReturn(tableNames);
        TableRegexFinder tableRegexFinder = new TableRegexFinder(cfgMock, dbMock);
        Set<String> watchedTables = tableRegexFinder.findWatchedTables();
        expectedTables.forEach(expectedTable -> assertTrue(watchedTables.contains(expectedTable)));
        assertEquals(expectedTables.size(), watchedTables.size());
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
        Set<String> expectedTables = Sets.newHashSet("_test3224", "inventory_test", "y_gles");
        assertFoundWatchedTablesBasedOnRegex(Sets.newHashSet(".*", "~[^_]+", "~.{5}_.*"), expectedTables);
    }

    @Test
    public void shouldFindWatchedTablesBasedOnRegex5() throws SQLException {
        Set<String> expectedTables = Sets.newHashSet("actor_test", "LARGE_CASE", "language", "frIeND");
        assertFoundWatchedTablesBasedOnRegex(Sets.newHashSet("[a-uA-Z]{2,5}_.+", "language", "frIeND"), expectedTables);
    }
}
