package com.dbw.db;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dbw.err.PreparationException;
import com.google.common.collect.ImmutableMap;

public class PostgresPrepareService {
    private Postgres db;
    private List<String> watchedTables;
    private Map<String, String[]> watchedTablesColumnNames;
    
    public PostgresPrepareService(Postgres db, List<String> watchedTables) {
        this.db = db;
        this.watchedTables = watchedTables;
    }

    public Map<String, String[]> getWatchedTablesColumnNames() {
        return watchedTablesColumnNames;
    }

    public void prepare() throws PreparationException {
        try {
            prepareAuditTable();
            prepareAuditFunction();
            prepareAuditTriggers();
            watchedTablesColumnNames = prepareWatchedTablesColumnNames();
        } catch (SQLException e) {
            throw new PreparationException(e.getMessage(), e);
        }
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
                new PreparationException(e.getMessage(), e).setRecoverable().handle();
            }
        }
    }

    private Map<String, String[]> prepareWatchedTablesColumnNames() throws SQLException {
        Map<String, String[]> tableColumnNames = new HashMap<String, String[]>();
        for (String tableName : watchedTables) {
            tableColumnNames.put(tableName, db.selectTableColumnNames(tableName));
        }
        return ImmutableMap.copyOf(tableColumnNames);
    }
}
