package com.dbw.watcher;

import java.sql.SQLException;
import java.util.List;

import com.dbw.app.ObjectCreator;
import com.dbw.db.AuditRecord;
import com.dbw.db.Database;
import com.dbw.frame.AuditFrame;
import com.google.inject.Singleton;

@Singleton
public class AuditTableWatcher implements Watcher {
    private final short RUN_INTERVAL = 2000;
    
    private List<String> watchedTables;
    private Database db;
    private boolean isRunning;
    private int runCounter = 0;
    private int lastId;

    public void setWatchedTables(List<String> watchedTables) {
        this.watchedTables = watchedTables;
    }

    public void setDb(Database db) {
        this.db = db;
    }

    public void init() throws SQLException {
        db.prepare(watchedTables);
    }

    public void start() throws SQLException {
        findLastId();
        setIsRunning(true);
        while (getIsRunning()) {
            run();
        }
    }

    private void run() throws SQLException {
        try {
            Thread.sleep(RUN_INTERVAL);
            // List<AuditRecord> auditRecords = db.selectAuditRecords(getLastId());
            List<AuditRecord> auditRecords = db.selectAuditRecords(0);
            for (AuditRecord auditRecord : auditRecords) {
                AuditFrame frame = ObjectCreator.create(AuditFrame.class);
                frame.setAuditRecord(auditRecord);
                frame.setDbClass(db.getClass());
                frame.createDiff();
                frame.createStateColumns();
                System.out.println(frame.toString());
            }
            findLastId();
            incrementRunCounter();
        } catch (InterruptedException e) {
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            e.printStackTrace();
        }
    }

    private void findLastId() throws SQLException {
        setLastId(db.selectMaxId());
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
    
}