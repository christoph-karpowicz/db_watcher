package com.dbw.watcher;

import java.util.List;

import com.dbw.cfg.Config;
import com.dbw.cfg.DatabaseConfig;
import com.dbw.db.AuditRecord;
import com.dbw.db.Database;
import com.dbw.db.DatabaseFactory;
import com.dbw.db.PostgresQueries;

public class Watcher {
    private final short RUN_INTERVAL = 1000;
    
    private Config config;
    private Database db;
    private boolean isRunning;
    private int runCounter = 0;
    private int lastId;

    public void setConfig(Config config) {
        this.config = config;
    }

    public void init() {
        setDb();
        connectToDb();
        prepareAuditObjects();
    }

    private void setDb() {
        DatabaseConfig dbConfig = config.getDatabase();
        try {
            db = DatabaseFactory.getDatabase(dbConfig);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void connectToDb() {
        db.connect();
    }

    private void prepareAuditObjects() {
        prepareAuditTable();
        prepareAuditFunction();
        prepareAuditTriggers();
    }

    private void prepareAuditTable() {
        if (!db.auditTableExists()) {
            db.createAuditTable();
        }
    }

    private void prepareAuditFunction() {
        if (!db.auditFunctionExists()) {
            db.createAuditFunction();
        }
    }

    private void prepareAuditTriggers() {
        dropUnusedAuditTriggers();
        createAuditTriggers();
    }

    private void dropUnusedAuditTriggers() {
        String[] auditTriggers = db.selectAuditTriggers();
        for (String auditTriggerName : auditTriggers) {
            if (!config.getTables().contains(auditTriggerName)) {
                db.dropAuditTrigger(auditTriggerName);
            }
        }
    }

    private void createAuditTriggers() {
        for (String tableName : config.getTables()) {
            if (!db.auditTriggerExists(tableName)) {
                db.createAuditTrigger(tableName);
            }
        }
    }
    
    public void start() {
        findLastId();
        setIsRunning(true);
        while (getIsRunning()) {
            run();
        }
    }

    private void run() {
        try {
            Thread.sleep(RUN_INTERVAL);
            List<AuditRecord> auditRecords = db.selectAuditRecords(PostgresQueries.SELECT_AUDIT_RECORDS, getLastId());
            for (AuditRecord auditRecord : auditRecords) {
                String diff = auditRecord.findDiff();
                System.out.println(diff);
            }
            findLastId();
            incrementRunCounter();
        } catch (InterruptedException e) {

        }
    }

    private void findLastId() {
        setLastId(db.selectMaxId(PostgresQueries.SELECT_AUDIT_TABLE_MAX_ID));
    }

    private int getLastId() {
        return lastId;
    }

    private void setLastId(int lastId) {
        this.lastId = lastId;
    }

    private boolean getIsRunning() {
        return this.isRunning;
    }
    
    private void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    private int getRunCounter() {
        return runCounter;
    }

    private void incrementRunCounter() {
        runCounter++;
    }

    public void end() {
        db.close();
    }
    
}