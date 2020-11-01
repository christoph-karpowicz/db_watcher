package com.dbw.app;

import java.sql.SQLException;

import com.dbw.cfg.Config;
import com.dbw.cfg.ConfigParser;
import com.dbw.cfg.DatabaseConfig;
import com.dbw.cli.CLI;
import com.dbw.db.Database;
import com.dbw.db.DatabaseFactory;
import com.dbw.err.AppInitException;
import com.dbw.err.CleanupException;
import com.dbw.err.WatcherStartException;
import com.dbw.log.Level;
import com.dbw.log.Logger;
import com.dbw.watcher.AuditTableWatcher;
import com.google.inject.Inject;
import org.apache.commons.cli.ParseException;

public class App {
    @Inject
    private AuditTableWatcher watcher;
    
    public static CLI.ParsedOptions options;
    private Config config;
    private Database db;
    
    public void init(String[] args) throws AppInitException {
        try {
            options = handleArgs(args);
            config = ConfigParser.fromYMLFile(options.getConfigPath());
            setDb();
            connectToDb();
        } catch (Exception e) {
            throw new AppInitException(e.getMessage(), e);
        }
    }
    
    private CLI.ParsedOptions handleArgs(String[] args) throws ParseException {
        CLI cli = new CLI();
        cli.setArgs(args);
        cli.init();
        return cli.parseArgs();
    }

    private void setDb() throws Exception {
        DatabaseConfig dbConfig = config.getDatabase();
        db = DatabaseFactory.getDatabase(dbConfig);
    }

    private void connectToDb() throws Exception {
        db.connect();
    }

    public void start() throws CleanupException, WatcherStartException {
        if (options.getClean()) {
            clean();
        } else {
            addShutdownHook();
            startWatcher();
        }
    }

    private void clean() throws CleanupException {
        try {
            db.clean(config.getTables());
        } catch (SQLException e) {
            throw new CleanupException(e.getMessage(), e);
        }
    }

    private void startWatcher() throws WatcherStartException {
        try {
            watcher.setWatchedTables(config.getTables());
            watcher.setDb(db);
            watcher.init();
            watcher.start();
        } catch (Exception e) {
            throw new WatcherStartException(e.getMessage(), e);
        }
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                Logger.log(Level.INFO, "Shutting down ...");
                try {
                    shutdown();
                } catch (SQLException e) {
                    System.err.println(e.getClass().getName()+": "+e.getMessage());
                }
            }
        });
    }

    private void shutdown() throws SQLException {
        db.close();
    }
}
