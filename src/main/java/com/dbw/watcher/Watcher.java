package com.dbw.watcher;

import java.util.List;

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
        if (!db.auditTableExists()) {
            db.createAuditTable();
        }
        if (!db.auditFunctionExists()) {
            db.createAuditFunction();
        }
        String[] auditTriggers = db.selectAuditTriggers();
        for (String auditTriggerName : auditTriggers) {
            if (!config.getTables().contains(auditTriggerName)) {
                System.out.println(auditTriggerName);
                db.dropAuditTrigger(auditTriggerName);
            }
        }
        for (String tableName : config.getTables()) {
            if (!db.auditTriggerExists(tableName)) {
                System.out.println(tableName);
                db.createAuditTrigger(tableName);
            }
        }
    }

    public void start() {

    }

    public void end() {
        db.close();
    }
    
}