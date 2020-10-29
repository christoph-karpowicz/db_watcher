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
import com.dbw.log.Level;
import com.dbw.log.Logger;
import com.dbw.watcher.AuditTableWatcher;
import com.google.inject.Inject;

public class App {

    @Inject
    private AuditTableWatcher watcher;
    
    private CLI.ParsedOptions options;
    private Config config;
    private Database db;
    
    public void init(String[] args) throws AppInitException {
        try {
            options = handleArgs(args);
            config = ConfigParser.fromYMLFile(options.configPath);
            setDb();
            connectToDb();
        } catch (Exception e) {
            throw new AppInitException(e.getMessage(), e.getClass());
        }
    }
    
    private CLI.ParsedOptions handleArgs(String[] args) {
        CLI cli = new CLI();
        cli.setArgs(args);
        cli.init();
        return cli.parseArgs();
    }

    private void setDb() {
        DatabaseConfig dbConfig = config.getDatabase();
        try {
            db = DatabaseFactory.getDatabase(dbConfig);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        }
    }

    private void connectToDb() throws Exception {
        db.connect();
    }

    public void start() throws Exception {
        if (options.clean) {
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
            throw new CleanupException(e.getMessage(), e.getClass());
        }
    }

    private void startWatcher() throws Exception {
        watcher.setWatchedTables(config.getTables());
        watcher.setDb(db);
        watcher.init();
        watcher.start();
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
