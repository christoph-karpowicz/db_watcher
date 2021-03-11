package com.dbw.app;

import com.dbw.cache.Cache;
import com.dbw.cache.ConfigCachePersister;
import com.dbw.cfg.Config;
import com.dbw.cfg.ConfigParser;
import com.dbw.cli.CLI;
import com.dbw.db.DatabaseManager;
import com.dbw.err.*;
import com.dbw.log.Level;
import com.dbw.log.LogMessages;
import com.dbw.log.Logger;
import com.dbw.watcher.WatcherManager;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class App {
    @Inject
    private WatcherManager watcherManager;
    @Inject
    private DatabaseManager databaseManager;
    @Inject
    private Cache cache;

    public static CLI.ParsedOptions options;
    private List<Config> configs;

    public void init(String[] args) throws AppInitException {
        CLI cli = new CLI();
        cache.load();
        try {
            cli.init(args);
            options = cli.handleArgs();
            configs = Lists.newArrayList();
            Optional<Set<String>> configPathsArg = options.getConfigPaths();
            Set<String> configPaths;
            if (configPathsArg.isPresent()) {
                configPaths = configPathsArg.get();
            } else {
                configPaths = chooseConfigFile();
            }
            for (String configPath : configPaths) {
                Config cfg = loadAndCacheConfig(configPath);
                watcherManager.addWatcher(cfg);
            }
        } catch (Exception e) {
            throw new AppInitException(e.getMessage(), e);
        }
    }

    private Config loadAndCacheConfig(String configPath) throws IOException, NoSuchAlgorithmException {
        File configFile = new File(configPath);
        Config cfg = ConfigParser.fromYMLFile(configFile);
        configs.add(cfg);
        String configFileChecksum = ConfigParser.getFileChecksum(configFile);
        boolean configChanged = cache.compareConfigFileChecksums(cfg.getPath(), configFileChecksum);
        cfg.setChanged(configChanged);
        if (configChanged) {
            ConfigCachePersister configCachePersister = new ConfigCachePersister();
            configCachePersister.setCache(cache);
            configCachePersister.setConfig(cfg);
            configCachePersister.setConfigFileChecksum(configFileChecksum);
            Thread configCachePersisterThread = new Thread(configCachePersister);
            configCachePersisterThread.start();
        }
        return cfg;
    }

    private Set<String> chooseConfigFile() throws IOException, ConfigException {
        return ConfigParser.getConfigFileNamesFromInput();
    }

    public void start() throws WatcherStartException, InitialAuditRecordDeleteException, PurgeException {
        String deleteFirstNRowsOption = options.getDeleteFirstNRows();
        if (!Objects.isNull(deleteFirstNRowsOption)) {
            databaseManager.deleteFirstNRows(deleteFirstNRowsOption);
        }
        if (options.getPurge()) {
            databaseManager.purge();
            return;
        }
        addShutdownHook();
        startWatchers();
    }

    private void startWatchers() throws WatcherStartException {
        try {
            watcherManager.startAll();
        } catch (PreparationException | SQLException e) {
            throw new WatcherStartException(e.getMessage(), e);
        }
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                Logger.log(Level.INFO, LogMessages.SHUTDOWN);
                try {
                    shutdown();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void shutdown() throws SQLException {
        watcherManager.terminateAll();
    }
}
