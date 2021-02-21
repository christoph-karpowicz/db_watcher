package com.dbw.db;

import com.dbw.err.PreparationException;
import com.dbw.log.ErrorMessages;

import java.sql.SQLException;
import java.util.List;

public class PostgresPrepareService {
    private final Postgres db;
    private final List<String> watchedTables;

    public PostgresPrepareService(Postgres db, List<String> watchedTables) {
        this.db = db;
        this.watchedTables = watchedTables;
    }

    public void prepare() throws PreparationException {
        try {
            prepareAuditTable();
            prepareAuditFunction();
            prepareAuditTriggers();
        } catch (SQLException e) {
            throw new PreparationException(e.getMessage(), e);
        }
    }

    private void prepareAuditTable() throws SQLException {
        if (!db.auditTableExists()) {
            try {
                db.createAuditTable();
            } catch (SQLException e) {
                String errMsg = String.format(ErrorMessages.CREATE_AUDIT_TABLE, e.getMessage());
                new PreparationException(errMsg, e).handle();
            }
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
                try {
                    db.dropAuditTrigger(auditTriggerName);
                } catch (SQLException e) {
                    new PreparationException(e.getMessage(), e).setRecoverable().handle();
                }
            }
        }
    }

    private void createAuditTriggers() {
        for (String tableName : watchedTables) {
            try {
                if (!db.auditTriggerExists(tableName)) {
                    db.createAuditTrigger(tableName);
                }
            } catch (SQLException e) {
                String errMsg = String.format(ErrorMessages.CREATE_AUDIT_TRIGGER, tableName, e.getMessage());
                new PreparationException(errMsg, e).setRecoverable().handle();
            }
        }
    }
}
