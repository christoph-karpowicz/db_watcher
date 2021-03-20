package com.dbw.watcher;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.dbw.app.App;
import com.dbw.app.ObjectCreator;
import com.dbw.cfg.Config;
import com.dbw.db.AuditRecord;
import com.dbw.db.Database;
import com.dbw.db.DatabaseFactory;
import com.dbw.err.*;
import com.dbw.frame.AuditFrame;
import com.dbw.log.Level;
import com.dbw.log.LogMessages;
import com.dbw.log.Logger;
import com.dbw.log.WarningMessages;
import com.dbw.output.TimeDiffSeparator;
import com.google.inject.Inject;

public class Watcher implements Runnable {
    private final WatcherManager watcherManager;
    private final Config cfg;
    private Database db;
    private final String dbName;
    private boolean isRunning;
    private int runCounter = 0;
    private int lastId;
    private int initialAuditRecordCount;
    private boolean isAfterInitialRun;

    public Watcher(WatcherManager watcherManager, Config cfg) {
        this.watcherManager = watcherManager;
        this.cfg = cfg;
        this.dbName = cfg.getDatabase().getName();
    }

    public void setDb() {
        this.db = DatabaseFactory.getDatabase(cfg);
    }

    public void init() throws PreparationException, DbConnectionException {
        Logger.log(Level.INFO, dbName, LogMessages.WATCHER_INIT);
        if (cfg.isChanged()) {
            Logger.log(Level.INFO, dbName, LogMessages.DB_PREPARATION);
            db.prepare();
        } else {
            Logger.log(Level.INFO, dbName, LogMessages.CONFIG_UNCHANGED);
        }
    }

    public void closeDb() throws SQLException {
        db.close();
    }

    public Database getDb() {
        return db;
    }

    @Override
    public void run() {
        try {
            setInitialAuditRecordCount();
            findLastId();
            setIsRunning(true);
            while (getIsRunning()) {
                watch();
                if (getRunCounter() == 1) {
                    setAfterInitialRun();
                }
            }
        } catch (SQLException e) {
            new UnrecoverableException("WatcherRunException", e.getMessage(), e).handle();
        }
    }

    private void watch() {
        try {
            Thread.sleep(App.getInterval());
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
                    AuditFrame auditFrame = createAuditFrameAndFindDiff(auditRecord);
                    watcherManager.addFrame(auditFrame);
                } catch (StateDataProcessingException e) {
                    new WatcherRunException(e.getMessage(), e).setRecoverable().handle();
                }
            }
        } catch (SQLException | UnknownDbOperationException e) {
            new WatcherRunException(e.getMessage(), e).handle();
        }
    }

    private AuditFrame createAuditFrameAndFindDiff(AuditRecord auditRecord) throws StateDataProcessingException, SQLException {
        AuditFrame frame = ObjectCreator.create(AuditFrame.class);
        frame.setAuditRecord(auditRecord);
        frame.setDb(db);
        frame.createDiff();
        frame.createStateColumns();
        return frame;
    }

    private void findLastId() throws SQLException {
        setLastId(db.selectMaxId());
    }

    private int getLastId() {
        Short lastNChanges = App.options.getShowLastNChanges();
        boolean lastNChangesGtZero = Objects.nonNull(lastNChanges) && lastNChanges > 0;
        if (getRunCounter() == 0 && lastNChangesGtZero) {
            if (initialAuditRecordCount == 0) {
                Logger.log(Level.WARNING, dbName, WarningMessages.NO_LAST_N_CHANGES);
                return 0;
            }
            
            int lastIdMinusN = lastId - (int)lastNChanges;
            if (lastIdMinusN <= 0) {
                Logger.log(Level.WARNING, dbName, WarningMessages.LAST_N_CHANGES_GT_AUDIT_RECORD_COUNT);
            }
            return Math.max(lastIdMinusN, 0);
        }
        return lastId;
    }

    public void outputInitialInfo() {
        Logger.log(Level.INFO, dbName, LogMessages.WATCHER_STARTED);
        Logger.log(Level.INFO, dbName, String.format(LogMessages.AUDIT_RECORDS_COUNT, initialAuditRecordCount));
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

    private void setInitialAuditRecordCount() throws SQLException {
        this.initialAuditRecordCount = db.getAuditRecordCount();
    }

    public boolean isAfterInitialRun() {
        return isAfterInitialRun;
    }

    public void setAfterInitialRun() {
        isAfterInitialRun = true;
    }
}
