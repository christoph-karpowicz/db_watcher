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
        prepareAuditObjects();
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

    private void prepareAuditObjects() {
        System.out.println(db.auditTableExists());
        System.out.println(db.auditFunctionExists());
        System.out.println(db.auditTriggerExists("actor"));
        if (!db.auditTableExists()) {
            db.createAuditTable();
        }
        if (!db.auditFunctionExists()) {
            db.createAuditFunction();
        }
        if (!db.auditTriggerExists("actor")) {
            db.createAuditTrigger("actor");
        }
    }

    public void start() {

    }

    public void end() {
        db.close();
    }
    
}