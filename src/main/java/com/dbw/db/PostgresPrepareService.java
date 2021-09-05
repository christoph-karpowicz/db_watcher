package com.dbw.db;

import com.dbw.err.PreparationException;
import com.dbw.log.ErrorMessages;
import lombok.AllArgsConstructor;

import java.sql.SQLException;

@AllArgsConstructor
public class PostgresPrepareService {
    private final Postgres db;

    public void prepare() throws PreparationException {
        try {
            prepareAuditTable();
            prepareAuditFunction();
            prepareAuditTriggers();
        } catch (SQLException e) {
            throw new PreparationException(e.getMessage(), e);
        }
    }

    private void prepareAuditTable() throws SQLException, PreparationException {
        if (!db.auditTableExists()) {
            try {
                db.createAuditTable();
            } catch (SQLException e) {
                String errMsg = String.format(ErrorMessages.CREATE_AUDIT_TABLE, e.getMessage());
                throw new PreparationException(errMsg, e);
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
            if (!db.getWatchedTables().containsEntityName(auditTriggerName)) {
                try {
                    db.dropAuditTrigger(db.getWatchedTables().getTableByEntityName(auditTriggerName));
                } catch (SQLException e) {
                    new PreparationException(e.getMessage(), e).setRecoverable().handle();
                }
            }
        }
    }

    private void createAuditTriggers() {
        for (WatchedTables.Entry tableEntry : db.getWatchedTables().entrySet()) {
            try {
                if (!db.auditTriggerExists(tableEntry.getEntityName())) {
                    db.createAuditTrigger(tableEntry);
                }
            } catch (SQLException e) {
                String errMsg = String.format(ErrorMessages.CREATE_AUDIT_TRIGGER, tableEntry.getTableName(), e.getMessage());
                new PreparationException(errMsg, e).setRecoverable().handle();
            }
        }
    }
}
