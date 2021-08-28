package com.dbw.watcher;

import com.dbw.actions.TruncateBasedOnLimitAction;
import com.dbw.app.App;
import com.dbw.app.ObjectCreator;
import com.dbw.cfg.Config;
import com.dbw.cli.ShowLatestOperationsOption;
import com.dbw.db.*;
import com.dbw.err.*;
import com.dbw.frame.AuditFrame;
import com.dbw.log.Level;
import com.dbw.log.LogMessages;
import com.dbw.log.Logger;
import com.dbw.log.WarningMessages;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Watcher implements Runnable {
    private final WatcherManager watcherManager;
    private final Config cfg;
    private Set<String> watchedTables;
    private Database db;
    private final String dbName;
    private int maxId;
    private int auditRecordCount;
    private int numberOfLatestOp;
    private boolean isAfterInitialRun;
    private boolean removingAuditRecords;

    public Watcher(WatcherManager watcherManager, Config cfg) {
        this.watcherManager = watcherManager;
        this.cfg = cfg;
        this.dbName = cfg.getDatabase().getName();
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

    public void setDb() {
        this.db = DatabaseFactory.getDatabase(cfg);
    }

    public void findWatchedTables() throws SQLException {
        if (cfg.getSettings().getTableNamesRegex()) {
            List<String> allTables = db.selectAllTables();
            Set<String> excludeRegex = cfg.getTables()
                    .stream()
                    .filter(regex -> regex.charAt(0) == '~')
                    .map(regex -> regex.substring(1))
                    .collect(Collectors.toSet());
            Set<String> includeRegex = cfg.getTables()
                    .stream()
                    .filter(regex -> !excludeRegex.contains(regex))
                    .collect(Collectors.toSet());
            Set<String> exclude = findTableNameMatches(allTables, excludeRegex);
            Set<String> include = findTableNameMatches(allTables, includeRegex)
                    .stream()
                    .filter(tableName -> !tableName.equalsIgnoreCase(Common.DBW_AUDIT_TABLE_NAME))
                    .collect(Collectors.toSet());
            include.removeAll(exclude);
            this.watchedTables = include;
            System.out.println(allTables);
            System.out.println(this.watchedTables);

        } else {
            this.watchedTables = cfg.getTables();
        }
    }

    private Set<String> findTableNameMatches(List<String> allTables, Set<String> regexes) {
        Set<String> matches = Sets.newHashSet();
        for (String regex : regexes) {
            if (regex.length() == 0) {
                continue;
            }
            Pattern pattern = Pattern.compile(regex);
            for (String tableName : allTables) {
                Matcher matcher = pattern.matcher(tableName);
                if (matcher.matches()) {
                    matches.add(tableName);
                }
            }
        }
        return matches;
    }

    public void assignWatchedTablesToDb() {
        db.setWatchedTables(this.watchedTables);
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

    public boolean isAfterInitialRun() {
        return isAfterInitialRun;
    }

    private void setAfterInitialRun() {
        isAfterInitialRun = true;
    }

    public boolean isRemovingAuditRecords() {
        return removingAuditRecords;
    }

    public synchronized void setRemovingAuditRecords(boolean removingAuditRecords) {
        this.removingAuditRecords = removingAuditRecords;
    }
}
