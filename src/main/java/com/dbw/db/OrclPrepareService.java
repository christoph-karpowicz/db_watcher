package com.dbw.db;

import com.dbw.err.PreparationException;
import com.dbw.log.ErrorMessages;
import com.dbw.state.XmlStateBuilder;
import lombok.AllArgsConstructor;

import java.sql.SQLException;

@AllArgsConstructor
public class OrclPrepareService {
    private final XmlStateBuilder xmlStateBuilder = new XmlStateBuilder();
    private final Orcl db;

    public void prepare() throws PreparationException {
        try {
            prepareAuditTable();
        } catch (SQLException e) {
            String errMsg = String.format(ErrorMessages.CREATE_AUDIT_TABLE, e.getMessage());
            throw new PreparationException(errMsg, e);
        }
        prepareAuditTriggers();
    }

    private void prepareAuditTable() throws SQLException {
        if (!db.auditTableExists()) {
            db.createAuditTable();
        }
    }

    private void prepareAuditTriggers() {
        dropUnusedAuditTriggers();
        createAuditTriggers();
    }

    private void dropUnusedAuditTriggers() {
        try {
            String[] auditTriggers = db.selectAuditTriggers();
            for (String auditTriggerName : auditTriggers) {
                if (!db.getWatchedTables().containsEntityName(auditTriggerName)) {
                    db.dropAuditTrigger(db.getWatchedTables().getTableByEntityName(auditTriggerName));
                }
            }
        } catch (SQLException e) {
            new PreparationException(e.getMessage(), e).setRecoverable().handle();
        }
    }

    private void createAuditTriggers() {
        for (WatchedTables.Entry tableEntry : db.getWatchedTables().entrySet()) {
            try {
                if (db.auditTriggerExists(tableEntry.getEntityName())) {
                    continue;
                }
                Column[] tableColumns = db.selectTableColumns(tableEntry.getTableName());
                String newStateConcat = xmlStateBuilder.build(OrclSpec.NEW_STATE_PREFIX, tableColumns);
                String oldStateConcat = xmlStateBuilder.build(OrclSpec.OLD_STATE_PREFIX, tableColumns);
                String auditTriggerName = tableEntry.getEntityName();
                String auditTriggerQuery = db.formatQuery(
                    OrclQueries.CREATE_AUDIT_TRIGGER,
                    auditTriggerName,
                    tableEntry,
                    oldStateConcat,
                    newStateConcat,
                    tableEntry,
                    tableEntry,
                    tableEntry
                );
                db.createAuditTrigger(tableEntry.getTableName(), auditTriggerQuery);
            } catch (SQLException e) {
                String errMsg = String.format(ErrorMessages.CREATE_AUDIT_TRIGGER, tableEntry.getTableName(), e.getMessage());
                new PreparationException(errMsg, e).setRecoverable().handle();
            }
        }
    }
    
}
