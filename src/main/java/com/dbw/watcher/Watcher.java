package com.dbw.watcher;

import com.dbw.actions.TruncateBasedOnLimitAction;
import com.dbw.app.App;
import com.dbw.app.ObjectCreator;
import com.dbw.cfg.Config;
import com.dbw.cli.ShowLatestOperationsOption;
import com.dbw.db.AuditRecord;
import com.dbw.db.Database;
import com.dbw.db.DatabaseFactory;
import com.dbw.db.Postgres;
import com.dbw.err.*;
import com.dbw.frame.AuditFrame;
import com.dbw.log.Level;
import com.dbw.log.LogMessages;
import com.dbw.log.Logger;
import com.dbw.log.WarningMessages;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class Watcher implements Runnable {
    private final WatcherManager watcherManager;
    @Getter
    private final Config cfg;
    @Setter
    private Set<String> watchedTables;
    @Getter @Setter
    private Database db;
    private final String dbName;
    private int maxId;
    private int auditRecordCount;
    private int numberOfLatestOp;
    @Getter
    private boolean isAfterInitialRun;
    @Getter @Setter
    private boolean removingAuditRecords;

    public Watcher(WatcherManager watcherManager, Config cfg) {
        this.watcherManager = watcherManager;
        this.cfg = cfg;
        this.dbName = cfg.getDatabase().getName();
    }

    public void closeDb() throws SQLException {
        db.close();
    }

    public void setDb() {
        setDb(DatabaseFactory.getDatabase(cfg));
    }

    public Set<String> getWatchedTables() {
        return ImmutableSet.copyOf(watchedTables);
    }

    public void init() throws PreparationException, UnrecoverableException {
        Logger.log(Level.INFO, dbName, LogMessages.WATCHER_INIT);
        if (cfg.isChanged()) {
            Logger.log(Level.INFO, dbName, LogMessages.DB_PREPARATION);
            db.prepare();
        } else {
            Logger.log(Level.INFO, dbName, LogMessages.CONFIG_UNCHANGED);
        }
    }

    @Override
    public void run() {
        try {
            findMaxId();
            do {
                Thread.sleep(App.getInterval());
                watch();
                if (!isAfterInitialRun()) {
                    setAfterInitialRun();
                }
            } while (!App.options.getOneOff());
        } catch (InterruptedException | SQLException | DbwException e) {
            new UnrecoverableException("WatcherRunException", e.getMessage(), e).handle();
        }
    }

    private void watch() throws DbwException {
        try {
            boolean auditRecordCountChanged = setAndCompareAuditRecordCount();
            if (auditRecordCountChanged) {
                selectAndProcessAuditRecords();
                findMaxId();
                evaluateOperationsLimit();
            }
            watcherManager.checkIn(this);
        } catch (SQLException e) {
            new UnrecoverableException("WatcherRunException", e.getMessage(), e).handle();
        }
    }

    private void selectAndProcessAuditRecords() {
        try {
            List<AuditRecord> auditRecords = db.selectAuditRecords(getMaxId());
            for (AuditRecord auditRecord : auditRecords) {
                try {
                    AuditFrame auditFrame = createAuditFrameAndFindDiff(auditRecord);
                    watcherManager.addFrame(auditFrame);
                } catch (RecoverableException e) {
                    e.handle();
                }
            }
            if (!isAfterInitialRun()) {
                numberOfLatestOp = auditRecords.size();
            }
        } catch (SQLException | UnknownDbOperationException e) {
            new UnrecoverableException("WatcherRunException", e.getMessage(), e).handle();
        }
    }

    private AuditFrame createAuditFrameAndFindDiff(AuditRecord auditRecord)
            throws RecoverableException, SQLException {
        AuditFrame frame = ObjectCreator.create(AuditFrame.class);
        frame.setAuditRecord(auditRecord);
        frame.setDb(db);
        frame.createDiff();
        frame.createStateColumns();
        return frame;
    }

    private void findMaxId() throws SQLException {
        setMaxId(db.selectMaxId());
    }

    private int getMaxId() throws SQLException {
        if (!isAfterInitialRun() && App.options.showLatestOperationsPresentAndGtThanZero()) {
            ShowLatestOperationsOption latestOps = App.options.getShowLatestOperations();
            if (auditRecordCount == 0) {
                Logger.log(Level.WARNING, dbName, WarningMessages.NO_LATEST_OPS);
                return 0;
            }

            if (latestOps.isTime()) {
                return db.selectLatestAuditRecordId(latestOps.getValue());
            } else {
                int lastIdMinusN = maxId - (int)latestOps.getValue();
                if (lastIdMinusN <= 0) {
                    Logger.log(Level.WARNING, dbName, WarningMessages.LATEST_OPS_NUM_GT_AUDIT_RECORD_COUNT);
                }
                return Math.max(lastIdMinusN, 0);
            }
        }
        return maxId;
    }

    public void outputInitialInfo() {
        if (!App.options.getOneOff()) {
            Logger.log(Level.INFO, dbName, LogMessages.WATCHER_STARTED);
        }
        Logger.log(Level.INFO, dbName, String.format(LogMessages.AUDIT_RECORDS_COUNT, auditRecordCount));
        if (App.options.showLatestOperationsPresentAndGtThanZero() && App.options.getShowLatestOperations().isTime()) {
            String latestOpMsg = String.format(LogMessages.NUMBER_OF_LATEST_OP, numberOfLatestOp, App.options.getShowLatestOperations().getRaw());
            Logger.log(numberOfLatestOp > 0 ? Level.INFO : Level.WARNING, dbName, latestOpMsg);
        }
        if (App.options.getShowQuery() && !(getDb() instanceof Postgres)) {
            Logger.log(Level.WARNING, dbName, WarningMessages.QUERY_FLAG_FOR_NON_POSTGRES);
        }
    }

    private void setMaxId(int maxId) {
        this.maxId = maxId;
    }

    private boolean setAndCompareAuditRecordCount() throws SQLException {
        final int previousAuditRecordCount = auditRecordCount;
        setAuditRecordCount();
        return previousAuditRecordCount != auditRecordCount;
    }

    private void evaluateOperationsLimit() throws SQLException {
        if (!getCfg().areOperationsSettingsPresent()) {
            return;
        }
        Integer opMin = getCfg().getOperationsMinimum().get();
        Integer opLim = getCfg().getOperationsLimit().get();
        if (auditRecordCount >= opLim) {
            TruncateBasedOnLimitAction truncateBasedOnLimitAction =
                    new TruncateBasedOnLimitAction(this, auditRecordCount, opMin);
            Thread truncateBasedOnLimitThread = new Thread(truncateBasedOnLimitAction);
            truncateBasedOnLimitThread.start();
        }
    }

    private void setAuditRecordCount() throws SQLException {
        this.auditRecordCount = db.getAuditRecordCount();
    }

    private void setAfterInitialRun() {
        isAfterInitialRun = true;
    }
}
