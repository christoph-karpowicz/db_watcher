package com.dbw.watcher;

import com.dbw.cfg.Config;
import com.dbw.cfg.DatabaseConfig;
import com.dbw.db.Database;
import com.dbw.db.DatabaseFactory;

public class Watcher {
    private Config config;
    private Database db;

    public void setConfig(Config config) {
        this.config = config;
    }

    public void init() {
        this.setDb();
        this.connectToDb();
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
}