package com.dbw.db;

import com.dbw.db.query.QueryHelper;
import com.dbw.err.PreparationException;
import com.dbw.log.ErrorMessages;
import com.dbw.state.XmlStateBuilder;

import java.sql.SQLException;

public class OrclPrepareService {
    private final XmlStateBuilder xmlStateBuilder;
    private final Orcl db;

    public OrclPrepareService(Orcl db) {
        this.db = db;
        this.xmlStateBuilder = new XmlStateBuilder();
    }

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
                if (!db.getWatchedTables().contains(auditTriggerName)) {
                    db.dropAuditTrigger(auditTriggerName);
                }
            }
        } catch (SQLException e) {
            new PreparationException(e.getMessage(), e).setRecoverable().handle();
        }
    }

    private void createAuditTriggers() {
        for (String tableName : db.getWatchedTables()) {
            try {
                if (db.auditTriggerExists(tableName)) {
                    continue;
                }
                Column[] tableColumns = db.selectTableColumns(tableName);
                String newStateConcat = xmlStateBuilder.build(OrclSpec.NEW_STATE_PREFIX, tableColumns);
                String oldStateConcat = xmlStateBuilder.build(OrclSpec.OLD_STATE_PREFIX, tableColumns);
                String auditTriggerName = QueryHelper.buildAuditTriggerName(tableName);
                String auditTriggerQuery = db.formatQuery(
                    OrclQueries.CREATE_AUDIT_TRIGGER,
                    auditTriggerName,
                    tableName,
                    oldStateConcat,
                    newStateConcat,
                    tableName,
                    tableName,
                    tableName
                );
                db.createAuditTrigger(tableName, auditTriggerQuery);
            } catch (SQLException e) {
                String errMsg = String.format(ErrorMessages.CREATE_AUDIT_TRIGGER, tableName, e.getMessage());
                new PreparationException(errMsg, e).setRecoverable().handle();
            }
        }
    }
    
}
