package com.dbw.watcher;

import com.dbw.app.App;
import com.dbw.app.ObjectCreator;
import com.dbw.cfg.Config;
import com.dbw.cli.ShowLatestOperationsOption;
import com.dbw.db.AuditRecord;
import com.dbw.db.Database;
import com.dbw.db.DatabaseFactory;
import com.dbw.err.*;
import com.dbw.frame.AuditFrame;
import com.dbw.log.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class Watcher implements Runnable {
    private final WatcherManager watcherManager;
    private final Config cfg;
    private Database db;
    private final String dbName;
    private int maxId;
    private int auditRecordCount;
    private int numberOfLatestOp;
    private boolean isAfterInitialRun;

    public Watcher(WatcherManager watcherManager, Config cfg) {
        this.watcherManager = watcherManager;
        this.cfg = cfg;
        this.dbName = cfg.getDatabase().getName();
    }

    public void setDb() {
        this.db = DatabaseFactory.getDatabase(cfg);
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

    public void closeDb() throws SQLException {
        db.close();
    }

    public Config getCfg() {
        return cfg;
    }

    public Database getDb() {
        return db;
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
        Optional<Integer> opMin = getCfg().getOperationsMinimum();
        Optional<Integer> opLim = getCfg().getOperationsLimit();
        if (getCfg().areOperationsSettingsPresent() && auditRecordCount >= opLim.get() + opMin.get()) {
            new Thread(() -> {
                try {
                    getDb().deleteFirstNRows(opLim.get());
                } catch (SQLException e) {
                    String errMsg = String.format(ErrorMessages.OP_LIMIT_REACHED_DELETE_ATTEMPT, opLim.get());
                    Logger.log(Level.ERROR, getDb().getDbConfig().getName(), errMsg);
                }
                String warnMsg = String.format(WarningMessages.OP_LIMIT_REACHED, opLim.get(), opMin.get());
                Logger.log(Level.WARNING, getDb().getDbConfig().getName(), warnMsg);
            }).start();
        }
    }

    private void setAuditRecordCount() throws SQLException {
        this.auditRecordCount = db.getAuditRecordCount();
    }

    public boolean isAfterInitialRun() {
        return isAfterInitialRun;
    }

    public void setAfterInitialRun() {
        isAfterInitialRun = true;
    }
}
