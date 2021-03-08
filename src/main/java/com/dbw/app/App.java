package com.dbw.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.dbw.cache.Cache;
import com.dbw.cache.ConfigCachePersister;
import com.dbw.cfg.Config;
import com.dbw.cfg.ConfigParser;
import com.dbw.cli.CLI;
import com.dbw.db.Database;
import com.dbw.db.DatabaseFactory;
import com.dbw.err.AppInitException;
import com.dbw.err.ConfigException;
import com.dbw.err.DbConnectionException;
import com.dbw.err.InitialAuditRecordDeleteException;
import com.dbw.err.PreparationException;
import com.dbw.err.PurgeException;
import com.dbw.err.UnknownDbTypeException;
import com.dbw.err.WatcherStartException;
import com.dbw.log.ErrorMessages;
import com.dbw.log.Level;
import com.dbw.log.LogMessages;
import com.dbw.log.Logger;
import com.dbw.log.SuccessMessages;
import com.dbw.watcher.AuditTableWatcher;
import com.google.inject.Inject;

public class App {
    @Inject
    private AuditTableWatcher watcher;
    @Inject
    private Cache cache;

    public static CLI.ParsedOptions options;
    private Config configs;
    private boolean configChanged;
    private Database db;
    
    public void init(String[] args) throws AppInitException {
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
                loadAndCacheConfig(configPath);
            }
            setDb();
            connectToDb();
        } catch (Exception e) {
            throw new AppInitException(e.getMessage(), e);
        }
    }

    private void loadAndCacheConfig(String configPath) throws IOException, NoSuchAlgorithmException {
        File configFile = new File(configPath);
        configs = ConfigParser.fromYMLFile(configFile);
        String configFileChecksum = ConfigParser.getFileChecksum(configFile);
        configChanged = cache.compareConfigFileChecksums(configs.getPath(), configFileChecksum);
        if (configChanged) {
            ConfigCachePersister configCachePersister = new ConfigCachePersister();
            configCachePersister.setCache(cache);
            configCachePersister.setConfig(configs);
            configCachePersister.setConfigFileChecksum(configFileChecksum);
            Thread configCachePersisterThread = new Thread(configCachePersister);
            configCachePersisterThread.start();
        }
    }

    private Set<String> chooseConfigFile() throws IOException, ConfigException {
        return ConfigParser.getConfigFileNamesFromInput();
    }

    private void setDb() throws UnknownDbTypeException {
        db = DatabaseFactory.getDatabase(configs);
    }

    private void connectToDb() throws DbConnectionException {
        db.connect();
    }

    public void start() throws WatcherStartException, InitialAuditRecordDeleteException, PurgeException {
        String deleteFirstNRowsOption = options.getDeleteFirstNRows();
        if (!Objects.isNull(deleteFirstNRowsOption)) {
            deleteFirstNRows(deleteFirstNRowsOption);
        }
        if (options.getPurge()) {
            purge();
            return;
        }
        addShutdownHook();
        startWatcher();
    }

    private void deleteFirstNRows(String nRows) throws InitialAuditRecordDeleteException {
        String successMessage;
        try {
            successMessage = db.deleteFirstNRows(nRows);
        } catch (SQLException e) {
            throw new InitialAuditRecordDeleteException(e.getMessage(), e);
        }
        Logger.log(Level.INFO, String.format(successMessage, nRows));
    }

    private void purge() throws PurgeException {
        boolean isConfirmed = confirmPurge();
        if (!isConfirmed) {
            return;
        }
        List<String> tables = cache.getConfigTables(configs.getPath());
        if (db.purge(tables)) {
            Logger.log(Level.INFO, SuccessMessages.CLI_PURGE);
        } else {
            Logger.log(Level.ERROR, ErrorMessages.CLI_PURGE);
        }
        cache.removeConfig(configs.getPath());
    }

    private boolean confirmPurge() throws PurgeException {
        try {
            System.out.println(LogMessages.CONFIRM_PURGE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();
            return input.equals("y") || input.equals("Y");
        } catch (IOException e) {
            throw new PurgeException(e.getMessage(), e);
        }
    }

    private void startWatcher() throws WatcherStartException {
        try {
            watcher.setDb(db);
            watcher.init(configChanged);
            watcher.start();
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
        db.close();
    }
}
