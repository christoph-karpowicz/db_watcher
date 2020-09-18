package com.dbw.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrclPrepareService {
    private Orcl db;
    private List<String> watchedTables;
    
    public OrclPrepareService(Orcl db, List<String> watchedTables) {
        this.db = db;
        this.watchedTables = watchedTables;
    }

    public void prepare() {
        prepareAuditTable();
        prepareAuditTriggers();
    }

    private void prepareAuditTable() {
        if (!db.auditTableExists()) {
            db.createAuditTable();
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
            String newStateConcat = prepareStateConcat("NEW", tableName);
            String oldStateConcat = prepareStateConcat("OLD", tableName);
            String auditTriggerQuery = db.prepareQuery(
                OrclQueries.CREATE_AUDIT_TRIGGER, 
                tableName, 
                tableName, 
                newStateConcat, 
                oldStateConcat, 
                tableName, 
                newStateConcat, 
                tableName, 
                oldStateConcat, 
                tableName);
            if (!db.auditTriggerExists(tableName)) {
                db.createAuditTrigger(tableName, auditTriggerQuery);
            }
        }
    }

    private String prepareStateConcat(String statePrefix, String tableName) {
        List<String> stateConcat = new ArrayList<String>();
        stateConcat.add("'<?xml version=\"1.0\" encoding=\"UTF-8\"?>'");
        stateConcat.add("'<table_data>'");
        Column[] tableColumns = db.selectTableColumns(tableName);
        for (Column tableColumn : tableColumns) {
            StringBuilder columnValueToStringInvocation = new StringBuilder();
            columnValueToStringInvocation
                .append("'<column_data name=\"" + tableColumn.getName() + "\">' || ")
                .append("TO_CHAR(:")
                .append(statePrefix)
                .append(".")
                .append(tableColumn.getName())
                .append(")")
                .append(" || '</column_data>'");
            stateConcat.add(columnValueToStringInvocation.toString());
        }
        stateConcat.add("'</table_data>'");
        return String.join(" || ", stateConcat);
    }
}
