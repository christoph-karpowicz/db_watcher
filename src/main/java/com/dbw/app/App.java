package com.dbw.app;

import java.sql.SQLException;

import com.dbw.cfg.Config;
import com.dbw.cfg.ConfigParser;
import com.dbw.cfg.DatabaseConfig;
import com.dbw.cli.CLI;
import com.dbw.db.Database;
import com.dbw.db.DatabaseFactory;
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
    
    public void init(String[] args) throws Exception {
        options = handleArgs(args);
        config = ConfigParser.fromYMLFile(options.configPath);
        setDb();
        connectToDb();
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

    public void start() throws SQLException {
        if (options.clean) {
            clean();
        } else {
            addShutdownHook();
            startWatcher();
        }
    }

    private void clean() throws SQLException {
        db.clean(config.getTables());
    }

    private void startWatcher() throws SQLException {
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
                } catch (Exception e) {
                    System.err.println(e.getClass().getName()+": "+e.getMessage());
                }
            }
        });
    }

    private void shutdown() throws SQLException {
        db.close();
    }
}
