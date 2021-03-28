package com.dbw.app;

import com.dbw.actions.ClearCacheAction;
import com.dbw.actions.DbAction;
import com.dbw.actions.DeleteFirstNRowsAction;
import com.dbw.actions.PurgeAction;
import com.dbw.cache.Cache;
import com.dbw.cache.ConfigCachePersister;
import com.dbw.cfg.Config;
import com.dbw.cfg.ConfigParser;
import com.dbw.cli.CLI;
import com.dbw.db.DatabaseManager;
import com.dbw.err.AppInitException;
import com.dbw.err.ConfigException;
import com.dbw.err.DbwException;
import com.dbw.err.WatcherStartException;
import com.dbw.log.Level;
import com.dbw.log.LogMessages;
import com.dbw.log.Logger;
import com.dbw.output.OutputManager;
import com.dbw.watcher.WatcherManager;
import com.google.common.base.Strings;
import com.google.inject.Inject;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

public class App {
    public static final short DEFAULT_RUN_INTERVAL = 500;

    @Inject
    private WatcherManager watcherManager;
    @Inject
    private DatabaseManager databaseManager;
    @Inject
    private OutputManager outputManager;
    @Inject
    private Cache cache;

    public static CLI.ParsedOptions options;

    public static short getInterval() {
        return Optional.ofNullable(App.options.getInterval()).orElse(DEFAULT_RUN_INTERVAL);
    }

    public void init(String[] args) throws AppInitException {
        Logger.setWatcherManager(watcherManager);
        CLI cli = new CLI();
        cache.load();
        try {
            cli.init(args);
            options = cli.handleArgs();
            Optional<Set<String>> configPathsArg = options.getConfigPaths();
            Set<String> configPaths;
            if (configPathsArg.isPresent()) {
                configPaths = configPathsArg.get();
            } else {
                configPaths = chooseConfigFile();
            }
            for (String configPath : configPaths) {
                Config cfg = loadAndCacheConfig(configPath);
                cfg.validate();
                watcherManager.addWatcher(cfg);
            }
        } catch (Exception e) {
            throw new AppInitException(e.getMessage(), e);
        }
    }

    private Config loadAndCacheConfig(String configPath) throws IOException, NoSuchAlgorithmException {
        File configFile = new File(configPath);
        Config cfg = ConfigParser.fromYMLFile(configFile);
        String configFileChecksum = ConfigParser.getFileChecksum(configFile);
        boolean configChanged = cache.compareConfigFileChecksums(cfg.getPath(), configFileChecksum);
        cfg.setChanged(configChanged);
        if (configChanged && !options.getClearCache()) {
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

    public void start() throws DbwException {
        databaseManager.connectDbs();
        boolean clearCacheOption = options.getClearCache();
        if (clearCacheOption) {
            ClearCacheAction clearCacheAction = ObjectCreator.create(ClearCacheAction.class);
            Set<String> configPaths = watcherManager.getConfigPaths();
            clearCacheAction.setConfigPaths(configPaths);
            clearCacheAction.execute();
            shutdown();
            return;
        }
        String deleteFirstNRowsOption = options.getDeleteFirstNRows();
        if (!Strings.isNullOrEmpty(deleteFirstNRowsOption)) {
            DeleteFirstNRowsAction deleteFirstNRowsAction = ObjectCreator.create(DeleteFirstNRowsAction.class);
            deleteFirstNRowsAction.setNumberOfRowsToDelete(deleteFirstNRowsOption);
            deleteFirstNRowsAction.execute();
        }
        if (options.getPurge()) {
            DbAction purgeAction = ObjectCreator.create(PurgeAction.class);
            purgeAction.execute();
            shutdown();
            return;
        }
        addShutdownHook();
        startWatchers();
    }

    private void startWatchers() throws WatcherStartException {
        watcherManager.startAll();
        outputManager.pollAndOutput();
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Logger.log(Level.INFO, LogMessages.SHUTDOWN);
            shutdown();
        }));
    }

    private void shutdown() {
        try {
            watcherManager.terminateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
