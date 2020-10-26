package com.dbw.db;

import java.sql.SQLException;
import java.util.List;

import com.dbw.state.XmlStateBuilder;

public class OrclPrepareService {
    private final String NEW_STATE_PREFIX = ":NEW.";
    private final String OLD_STATE_PREFIX = ":OLD.";
    
    private XmlStateBuilder xmlStateBuilder;
    private Orcl db;
    private List<String> watchedTables;
    
    public OrclPrepareService(Orcl db, List<String> watchedTables) {
        this.db = db;
        this.watchedTables = watchedTables;
        this.xmlStateBuilder = new XmlStateBuilder();
    }

    public void prepare() throws SQLException {
        prepareAuditTable();
        prepareAuditTriggers();
    }

    private void prepareAuditTable() throws SQLException {
        if (!db.auditTableExists()) {
            db.createAuditTable();
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
            Column[] tableColumns = db.selectTableColumns(tableName);
            String newStateConcat = xmlStateBuilder.build(NEW_STATE_PREFIX, tableColumns);
            String oldStateConcat = xmlStateBuilder.build(OLD_STATE_PREFIX, tableColumns);
            String auditTriggerQuery = db.prepareQuery(
                OrclQueries.CREATE_AUDIT_TRIGGER, 
                QueryBuilder.buildAuditTriggerName(tableName),
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
