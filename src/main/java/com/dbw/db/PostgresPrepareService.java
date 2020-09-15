package com.dbw.db;

import java.util.List;

public class PostgresPrepareService {
    private Postgres db;
    private List<String> watchedTables;
    
    public PostgresPrepareService(Postgres db, List<String> watchedTables) {
        this.db = db;
        this.watchedTables = watchedTables;
    }

    public void prepare() {
        prepareAuditTable();
        prepareAuditFunction();
        prepareAuditTriggers();
    }

    private void prepareAuditTable() {
        if (!db.auditTableExists()) {
            db.createAuditTable();
        }
    }

    private void prepareAuditFunction() {
        if (!db.auditFunctionExists()) {
            db.createAuditFunction();
        }
    }

    private void prepareAuditTriggers() {
        dropUnusedAuditTriggers();
        createAuditTriggers();
    }

    private void dropUnusedAuditTriggers() {
        String[] auditTriggers = db.selectAuditTriggers();
        for (String auditTriggerName : auditTriggers) {
            if (!watchedTables.contains(auditTriggerName)) {
                db.dropAuditTrigger(auditTriggerName);
            }
        }
    }

    private void createAuditTriggers() {
        for (String tableName : watchedTables) {
            if (!db.auditTriggerExists(tableName)) {
                db.createAuditTrigger(tableName);
            }
        }
    }
}
