package com.dbw.app;

import com.dbw.cfg.Config;
import com.dbw.cfg.ConfigParser;
import com.dbw.cfg.DatabaseConfig;
import com.dbw.cli.CLI;
import com.dbw.db.Database;
import com.dbw.db.DatabaseFactory;
import com.dbw.log.Level;
import com.dbw.log.Logger;
import com.dbw.watcher.Watchable;
import com.google.inject.Inject;

public class App {

    @Inject
    private Watchable watcher;
    
    private CLI.ParsedOptions options;
    private Config config;
    private Database db;
    
    public void init(String[] args) {
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
        }
    }

    private void connectToDb() {
        db.connect();
    }

    public void start() {
        if (options.clean) {
            clean();
        } else {
            addShutdownHook();
            startWatcher();
        }
    }

    private void clean() {
        db.clean(config.getTables());
    }

    private void startWatcher() {
        watcher.setWatchedTables(config.getTables());
        watcher.setDb(db);
        watcher.init();
        // watcher.start();
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                Logger.log(Level.INFO, "Shutting down ...");
                shutdown();
            }
        });
    }

    private void shutdown() {
        db.close();
    }
}
