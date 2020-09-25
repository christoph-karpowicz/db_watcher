package com.dbw.watcher;

import java.util.List;

import com.dbw.db.AuditRecord;
import com.dbw.db.Database;
import com.dbw.diff.StateDiffService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AuditTableWatcher implements Watcher {

    @Inject
    private StateDiffService diffService;
    
    private final short RUN_INTERVAL = 1000;
    
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

    public void init() {
        db.prepare(watchedTables);
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
            // List<AuditRecord> auditRecords = db.selectAuditRecords(getLastId());
            List<AuditRecord> auditRecords = db.selectAuditRecords(0);
            for (AuditRecord auditRecord : auditRecords) {
                String diff = diffService.findDiff(db, auditRecord);
                System.out.println(diff);
            }
            findLastId();
            incrementRunCounter();
        } catch (InterruptedException e) {
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
    }

    private void findLastId() {
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