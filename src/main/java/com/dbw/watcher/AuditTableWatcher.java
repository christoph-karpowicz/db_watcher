package com.dbw.watcher;

import java.sql.SQLException;
import java.util.List;

import com.dbw.app.App;
import com.dbw.app.ObjectCreator;
import com.dbw.db.AuditRecord;
import com.dbw.db.Database;
import com.dbw.err.PreparationException;
import com.dbw.err.WatcherRunException;
import com.dbw.frame.AuditFrame;
import com.google.inject.Singleton;

@Singleton
public class AuditTableWatcher implements Watcher {
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

    public void init() throws PreparationException {
        db.prepare(watchedTables);
    }

    public void start() throws SQLException {
        findLastId();
        setIsRunning(true);
        while (getIsRunning()) {
            run();
        }
    }

    private void run() {
        try {
            Thread.sleep(RUN_INTERVAL);
            selectAndProcessAuditRecords();
            findLastId();
        } catch (SQLException e) {
            new WatcherRunException(e.getMessage(), e).handle();
        } catch (Exception e) {
            new WatcherRunException(e.getMessage(), e).setRecoverable().handle();
        }
        incrementRunCounter();
    }

    private void selectAndProcessAuditRecords() throws Exception {
        List<AuditRecord> auditRecords = db.selectAuditRecords(getLastId());
        for (AuditRecord auditRecord : auditRecords) {
            createAuditFrameAndFindDiff(auditRecord);
        }
    }

    private void createAuditFrameAndFindDiff(AuditRecord auditRecord) throws Exception {
        AuditFrame frame = ObjectCreator.create(AuditFrame.class);
        frame.setAuditRecord(auditRecord);
        frame.setDbClass(db.getClass());
        frame.createDiff();
        frame.createStateColumns();
        System.out.println(frame.toString());
    }

    private void findLastId() throws SQLException {
        setLastId(db.selectMaxId());
    }

    private int getLastId() {
        if (getRunCounter() == 0 && App.options.getLastNChanges() > 0) {
            int diff = lastId - (int)App.options.getLastNChanges();
            return diff > 0 ? diff : 0; 
        }
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

    private synchronized void incrementRunCounter() {
        runCounter++;
    }
    
}