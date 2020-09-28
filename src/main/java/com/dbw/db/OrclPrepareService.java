package com.dbw.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.dbw.state.XmlStateBuilder;

public class OrclPrepareService {
    private XmlStateBuilder xmlStateBuilder;
    private Orcl db;
    private List<String> watchedTables;
    
    public OrclPrepareService(Orcl db, List<String> watchedTables) {
        this.db = db;
        this.watchedTables = watchedTables;
        this.xmlStateBuilder = new XmlStateBuilder();
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
            Column[] tableColumns = db.selectTableColumns(tableName);
            String newStateConcat = xmlStateBuilder.build("NEW", tableColumns);
            String oldStateConcat = xmlStateBuilder.build("OLD", tableColumns);
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
                tableName
            );
            if (!db.auditTriggerExists(tableName)) {
                db.createAuditTrigger(tableName, auditTriggerQuery);
            }
        }
    }

    
}
