package com.dbw.db;

import java.sql.SQLException;
import java.util.List;

public class PostgresPrepareService {
    private Postgres db;
    private List<String> watchedTables;
    
    public PostgresPrepareService(Postgres db, List<String> watchedTables) {
        this.db = db;
        this.watchedTables = watchedTables;
    }

    public void prepare() throws SQLException {
        prepareAuditTable();
        prepareAuditFunction();
        prepareAuditTriggers();
    }

    private void prepareAuditTable() throws SQLException {
        if (!db.auditTableExists()) {
            db.createAuditTable();
        }
    }

    private void prepareAuditFunction() throws SQLException {
        if (!db.auditFunctionExists()) {
            db.createAuditFunction();
        }
    }

    private void prepareAuditTriggers() throws SQLException {
        dropUnusedAuditTriggers();
        createAuditTriggers();
    }

    private void dropUnusedAuditTriggers() throws SQLException {
        String[] auditTriggers = db.selectAuditTriggers();
        for (String auditTriggerName : auditTriggers) {
            if (!watchedTables.contains(auditTriggerName)) {
                db.dropAuditTrigger(auditTriggerName);
            }
        }
    }

    private void createAuditTriggers() throws SQLException {
        for (String tableName : watchedTables) {
            if (!db.auditTriggerExists(tableName)) {
                db.createAuditTrigger(tableName);
            }
        }
    }
}
