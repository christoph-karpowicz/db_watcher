package com.dbw.app;

import com.dbw.cache.Cache;
import com.dbw.cache.ConfigCachePersister;
import com.dbw.cfg.Config;
import com.dbw.cfg.ConfigParser;
import com.dbw.cli.CLI;
import com.dbw.db.DatabaseManager;
import com.dbw.err.UnrecoverableException;
import com.dbw.log.ErrorMessages;
import com.dbw.log.Level;
import com.dbw.log.LogMessages;
import com.dbw.log.Logger;
import com.dbw.util.FileUtils;
import com.dbw.watcher.Watcher;
import com.dbw.watcher.WatcherManager;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AppInitializer {
    @Inject
    private WatcherManager watcherManager;
    @Inject
    private DatabaseManager databaseManager;
    @Inject
    private Cache cache;

    private String[] commandLineArgs;

    public void setCommandLineArgs(String[] commandLineArgs) {
        this.commandLineArgs = commandLineArgs;
    }

    public void init() throws UnrecoverableException {
        Logger.setWatcherManager(watcherManager);
        cache.load();
        try {
            getAppOptions();
            Set<String> configPaths = getConfigPaths();
            List<Config> configs = getConfigs(configPaths);
            validateConfigs(configs);
            createWatchers(configs);
            databaseManager.connectDbs();
            databaseManager.findAllTables();
            watcherManager.findAndAssignWatchedTables();
            persistConfigsInCacheIfChangedOrAbsent(configPaths, watcherManager.getWatchers());
        } catch (Exception e) {
            throw new UnrecoverableException(this.getClass().getName(), e.getMessage(), e);
        }
    }

    private void getAppOptions() throws Exception {
        CLI cli = new CLI();
        cli.init(commandLineArgs);
        App.options = cli.handleArgs();
    }

    private Set<String> getConfigPaths() throws Exception {
        Optional<Set<String>> configPathsArg = App.options.getConfigPaths();
        Set<String> configPaths;
        if (cache.exists() && App.options.getReuseConfig()) {
            configPaths = cache.getLastUsedConfigPaths();
            if (configPaths == null || configPaths.isEmpty()) {
                throw new UnrecoverableException(this.getClass().getName(), ErrorMessages.CLI_REUSE_CONFIG);
            } else {
                Logger.log(Level.INFO, String.format(LogMessages.REUSED_CONFIG, String.join(", ", configPaths)));
            }
        } else if (configPathsArg.isPresent()) {
            configPaths = configPathsArg.get();
        } else {
            configPaths = chooseConfigFile();
        }
        return configPaths;
    }

    private Set<String> chooseConfigFile() throws IOException, UnrecoverableException {
        return ConfigParser.getConfigFileNamesFromInput();
    }

    private List<Config> getConfigs(Set<String> configPaths) throws Exception {
        List<Config> configs = Lists.newArrayList();
        for (String configPath : configPaths) {
            Config cfg = loadConfig(configPath);
            configs.add(cfg);
        }
        return configs;
    }

    private Config loadConfig(String configPath) throws IOException, NoSuchAlgorithmException {
        File configFile = new File(configPath);
        Config cfg = ConfigParser.fromYMLFile(configFile);
        String configFileChecksum = FileUtils.getFileChecksum(configFile);
        boolean configChanged = cache.compareConfigFileChecksums(cfg.getPath(), configFileChecksum);
        cfg.setChanged(configChanged);
        cfg.setCheckSum(configFileChecksum);
        return cfg;
    }

    private void validateConfigs(List<Config> configs) throws UnrecoverableException {
        for (Config cfg : configs) {
            cfg.validate();
        }
    }

    private void createWatchers(List<Config> configs) {
        for (Config cfg : configs) {
            watcherManager.addWatcher(cfg);
        }
    }

    private void persistConfigsInCacheIfChangedOrAbsent(Set<String> configPaths, List<Watcher> watchers) {
        boolean pathsChanged = cache.haveLastUsedConfigPathsChanged(configPaths);
        boolean cfgsChanged = watchers.stream().map(Watcher::getCfg).anyMatch(Config::isChanged);
        if (!App.options.getClearCache() && (cfgsChanged || pathsChanged)) {
            cache.setLastUsedConfigPaths(configPaths);
            persistCache(watchers);
        }
    }

    private void persistCache(List<Watcher> watchers) {
        ConfigCachePersister configCachePersister = new ConfigCachePersister();
        configCachePersister.setCache(cache);
        watchers.stream()
                .filter(watcher -> watcher.getCfg().isChanged())
                .forEach(configCachePersister::addConfigCache);
        Thread configCachePersisterThread = new Thread(configCachePersister);
        configCachePersisterThread.start();
    }
}
