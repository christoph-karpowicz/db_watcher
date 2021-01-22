package com.dbw.watcher;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import com.dbw.app.App;
import com.dbw.app.ObjectCreator;
import com.dbw.db.AuditRecord;
import com.dbw.db.Database;
import com.dbw.err.PreparationException;
import com.dbw.err.StateDataProcessingException;
import com.dbw.err.UnknownDbOperationException;
import com.dbw.err.WatcherRunException;
import com.dbw.frame.AuditFrame;
import com.dbw.log.Level;
import com.dbw.log.LogMessages;
import com.dbw.log.Logger;
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
            if (getRunCounter() == 1) {
                Logger.log(Level.INFO, LogMessages.WATCHER_STARTED);
            }
        }
    }

    private void run() {
        try {
            Thread.sleep(RUN_INTERVAL);
            selectAndProcessAuditRecords();
            findLastId();
        } catch (InterruptedException | SQLException e) {
            new WatcherRunException(e.getMessage(), e).handle();
        }
        incrementRunCounter();
    }

    private void selectAndProcessAuditRecords() {
        try {
            List<AuditRecord> auditRecords = db.selectAuditRecords(getLastId());
            for (AuditRecord auditRecord : auditRecords) {
                try {
                    createAuditFrameAndFindDiff(auditRecord);
                } catch (StateDataProcessingException e) {
                    new WatcherRunException(e.getMessage(), e).setRecoverable().handle();
                }
            }
        } catch (SQLException | UnknownDbOperationException e) {
            new WatcherRunException(e.getMessage(), e).handle();
        }
    }

    private void createAuditFrameAndFindDiff(AuditRecord auditRecord) throws StateDataProcessingException {
        AuditFrame frame = ObjectCreator.create(AuditFrame.class);
        frame.setAuditRecord(auditRecord);
        frame.setDb(db);
        frame.createDiff();
        frame.createStateColumns();
        System.out.println(frame.toString());
    }

    private void findLastId() throws SQLException {
        setLastId(db.selectMaxId());
    }

    private int getLastId() {
        Short lastNChanges = App.options.getShowLastNChanges();
        boolean lastNChangesGtZero = Objects.nonNull(lastNChanges) && lastNChanges > 0;
        if (getRunCounter() == 0 && lastNChangesGtZero) {
            int diff = lastId - (int)lastNChanges;
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