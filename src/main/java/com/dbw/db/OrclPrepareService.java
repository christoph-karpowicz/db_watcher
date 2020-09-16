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
        System.out.println(Arrays.toString(auditTriggers));
        for (String auditTriggerName : auditTriggers) {
            if (!watchedTables.contains(auditTriggerName)) {
                db.dropAuditTrigger(auditTriggerName);
            }
        }
    }

    private void createAuditTriggers() {
        for (String tableName : watchedTables) {
            String stateConcat = prepareStateConcat("NEW", tableName);
            System.out.println(stateConcat);
        //     if (!db.auditTriggerExists(tableName)) {
        //         db.createAuditTrigger(tableName);
        //     }
        }
    }

    private String prepareStateConcat(String agePrefix, String tableName) {
        List<String> stateConcat = new ArrayList<String>();
        Column[] tableColumns = db.selectTableColumns(tableName);
        for (Column tableColumn : tableColumns) {
            StringBuilder columnValueToStringInvocation = new StringBuilder();
            columnValueToStringInvocation
                .append("TO_CHAR(:")
                .append(agePrefix)
                .append(".")
                .append(tableColumn.getName())
                .append(")");
            stateConcat.add(columnValueToStringInvocation.toString());
        }
        return String.join(" /dbw_break/ ", stateConcat);
    }
}
