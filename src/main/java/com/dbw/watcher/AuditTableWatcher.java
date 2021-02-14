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
import com.dbw.log.WarningMessages;
import com.google.inject.Singleton;

@Singleton
public class AuditTableWatcher implements Watcher {
    private final short RUN_INTERVAL = 1000;
    
    private List<String> watchedTables;
    private Database db;
    private boolean isRunning;
    private int runCounter = 0;
    private int lastId;
    private int initialAuditRecordCount;

    public void setWatchedTables(List<String> watchedTables) {
        this.watchedTables = watchedTables;
    }

    public void setDb(Database db) {
        this.db = db;
    }

    public void init(boolean configChanged) throws PreparationException {
        Logger.log(Level.INFO, LogMessages.WATCHER_INIT);
        if (configChanged) {
            Logger.log(Level.INFO, LogMessages.DB_PREPARATION);
            db.prepare(watchedTables);
        } else {
            Logger.log(Level.INFO, LogMessages.CONFIG_UNCHANGED);
        }
    }

    public void start() throws SQLException {
        setInitialAuditRecordCount();
        findLastId();
        setIsRunning(true);
        while (getIsRunning()) {
            run();
            if (getRunCounter() == 1) {
                outputInfo();
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
            if (initialAuditRecordCount == 0) {
                Logger.log(Level.WARNING, WarningMessages.NO_LAST_N_CHANGES);
                return 0;
            }
            
            int lastIdMinusN = lastId - (int)lastNChanges;
            if (lastIdMinusN <= 0) {
                Logger.log(Level.WARNING, WarningMessages.LAST_N_CHANGES_GT_AUDIT_RECORD_COUNT);
            }
            return lastIdMinusN > 0 ? lastIdMinusN : 0;
        }
        return lastId;
    }

    private void outputInfo() {
        Logger.log(Level.INFO, LogMessages.WATCHER_STARTED);
        Logger.log(Level.INFO, String.format(LogMessages.AUDIT_RECORDS_COUNT, initialAuditRecordCount));
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
    
    public void setInitialAuditRecordCount() throws SQLException {
        this.initialAuditRecordCount = db.getAuditRecordCount();
    }

}