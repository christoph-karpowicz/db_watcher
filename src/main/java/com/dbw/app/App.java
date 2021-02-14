package com.dbw.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

import com.dbw.cache.Cache;
import com.dbw.cache.ConfigCache;
import com.dbw.cfg.Config;
import com.dbw.cfg.ConfigParser;
import com.dbw.cfg.DatabaseConfig;
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
    
    private Cache cache;
    public static CLI.ParsedOptions options;
    private Config config;
    private boolean configChanged;
    private Database db;
    
    public void init(String[] args) throws AppInitException {
        try {
            cache = new Cache();
            cache.load();
            CLI cli = new CLI();
            cli.init(args);
            options = cli.handleArgs();
            Optional<String> configPathArg = options.getConfigPath();
            String configPath;
            if (configPathArg.isPresent()) {
                configPath = configPathArg.get();
            } else {
                configPath = chooseConfigFile();
            }
            File configFile = new File(configPath);
            config = ConfigParser.fromYMLFile(configFile);
            String configFileChecksum = ConfigParser.getFileChecksum(configFile);
            configChanged = cache.compareConfigFileChecksums(configFileChecksum);
            if (configChanged) {
                ConfigCache configCache = new ConfigCache();
                configCache.setChecksum(configFileChecksum);
                cache.createPersistentCacheIfDoesntExist();
                cache.getPersistentCache().get().setConfig(configCache);
                cache.persist();
            }
            setDb();
            connectToDb();
        } catch (Exception e) {
            throw new AppInitException(e.getMessage(), e);
        }
    }
    
    private String chooseConfigFile() throws IOException, ConfigException {
        return ConfigParser.getConfigFileNameFromInput();
    }

    private void setDb() throws UnknownDbTypeException {
        DatabaseConfig dbConfig = config.getDatabase();
        db = DatabaseFactory.getDatabase(dbConfig);
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
        if (db.purge(config.getTables())) {
            Logger.log(Level.INFO, SuccessMessages.CLI_PURGE);
        } else {
            Logger.log(Level.ERROR, ErrorMessages.CLI_PURGE);
        }
        cache.delete();
    }

    private boolean confirmPurge() throws PurgeException {
        try {
            System.out.println(LogMessages.CONFIRM_PURGE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();
            if (input.equals("y") || input.equals("Y")) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            throw new PurgeException(e.getMessage(), e);
        }
    }

    private void startWatcher() throws WatcherStartException {
        try {
            watcher.setWatchedTables(config.getTables());
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
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                }
            }
        });
    }

    private void shutdown() throws SQLException {
        db.close();
    }
}
