package com.dbw.app;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

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
import com.dbw.err.InvalidCLIOptionInputException;
import com.dbw.err.PreparationException;
import com.dbw.err.UnknownDbTypeException;
import com.dbw.err.WatcherStartException;
import com.dbw.log.ErrorMessages;
import com.dbw.log.Level;
import com.dbw.log.LogMessages;
import com.dbw.log.Logger;
import com.dbw.log.SuccessMessages;
import com.dbw.watcher.AuditTableWatcher;
import com.google.inject.Inject;
import org.apache.commons.cli.ParseException;

// mvn package shade:shade -DskipTests=true
// mvn clean test -DtestConfigPath="./config/orcl-example.yml"
public class App {
    @Inject
    private AuditTableWatcher watcher;
    
    public static CLI.ParsedOptions options;
    private Config config;
    private Database db;
    
    public void init(String[] args) throws AppInitException {
        try {
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
            config = ConfigParser.fromYMLFile(configPath);
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

    public void start() throws WatcherStartException, InitialAuditRecordDeleteException {
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

    private void purge() {
        if (db.purge(config.getTables())) {
            Logger.log(Level.INFO, SuccessMessages.CLI_PURGE);
        } else {
            Logger.log(Level.ERROR, ErrorMessages.CLI_PURGE);
        }
    }

    private void startWatcher() throws WatcherStartException {
        try {
            watcher.setWatchedTables(config.getTables());
            watcher.setDb(db);
            watcher.init();
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
