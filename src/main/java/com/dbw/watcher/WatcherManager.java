package com.dbw.watcher;

import com.dbw.app.App;
import com.dbw.cache.Cache;
import com.dbw.cfg.Config;
import com.dbw.db.Common;
import com.dbw.db.DatabaseManager;
import com.dbw.err.DbwException;
import com.dbw.err.PreparationException;
import com.dbw.err.UnrecoverableException;
import com.dbw.frame.AuditFrame;
import com.dbw.log.ErrorMessages;
import com.dbw.log.Level;
import com.dbw.log.LogMessages;
import com.dbw.log.Logger;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

@Singleton
public class WatcherManager {
    @Inject
    private DatabaseManager databaseManager;
    @Inject
    private Cache cache;
    @Inject
    private WatcherCheckIn watcherCheckIn;

    private final List<Watcher> watchers = Lists.newArrayList();
    private final LinkedBlockingQueue<AuditFrame> frameQueue = new LinkedBlockingQueue<>();

    public void addWatcher(Config cfg) {
        Watcher watcher = new Watcher(this, cfg);
        watcher.setDb();
        watchers.add(watcher);
        databaseManager.addDatabase(cfg.getPath(), watcher.getDb());
    }

    public void init() throws UnrecoverableException {
        watcherCheckIn.init(watchers);
        if (App.options.getTables().isPresent()) {
            validateTablesOption();
        }
    }

    private void validateTablesOption() throws UnrecoverableException {
        Set<String> allWatcherTables = getWatchedTables();
        Set<String> optionTables = App.options.getTables().get();
        if (optionTables.stream().anyMatch(String::isEmpty)) {
            throw new UnrecoverableException("WatcherInit", ErrorMessages.CLI_TABLES_EMPTY_NAME);
        }

        Set<String> optionTablesDiff = Sets.newHashSet(optionTables);
        optionTablesDiff.removeAll(allWatcherTables);
        if (optionTablesDiff.size() > 0) {
            String tablesNotFound = String.join(Common.COMMA_DELIMITER, optionTablesDiff);
            throw new UnrecoverableException("WatcherInit", String.format(ErrorMessages.CLI_TABLES_NOT_FOUND, tablesNotFound));
        }
    }

    public void startAll() throws UnrecoverableException {
        for (Watcher watcher : watchers) {
            try {
                watcher.init();
                Thread watcherThread = new Thread(watcher);
                watcherThread.start();
            } catch (DbwException e) {
                if (e instanceof PreparationException) {
                    cache.removeConfig(watcher.getCfg().getPath());
                }
                throw new UnrecoverableException("WatcherStart", e.getMessage(), e);
            }
        }
    }

    public void terminateAll() throws SQLException {
        for (Watcher watcher : watchers) {
            watcher.closeDb();
        }
    }

    public void checkIn(Watcher watcher) {
        watcherCheckIn.checkIn(watcher);
    }

    public boolean areAllCheckedIn() {
        return watcherCheckIn.areAllCheckedIn();
    }

    public void checkOutAll() {
        watcherCheckIn.checkOutAll();
    }

    public void addFrame(AuditFrame frame) {
        frameQueue.add(frame);
    }

    public LinkedBlockingQueue<AuditFrame> getFrameQueue() {
        return frameQueue;
    }

    public boolean areAllAfterInitialRun() {
        return watchers.stream().allMatch(Watcher::isAfterInitialRun);
    }

    public void outputInitialInfo() {
        for (Watcher watcher : watchers) {
            watcher.outputInitialInfo();
        }
        if (App.options.getOneOff()) {
            Logger.log(Level.INFO, LogMessages.ONE_OFF_DONE);
        }
    }

    public int getWatchersSize() {
        return watchers.size();
    }

    public Set<String> getConfigPaths() {
        return watchers
                .stream()
                .map(watcher -> watcher.getCfg().getPath())
                .collect(Collectors.toSet());
    }

    public Set<String> getWatchedTables() {
        Set<String> watchedTables = Sets.newHashSet();
        watchers.forEach(watcher -> watchedTables.addAll(watcher.getCfg().getTables()));
        return watchedTables;
    }
}
