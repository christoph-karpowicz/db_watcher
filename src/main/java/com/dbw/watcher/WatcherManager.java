package com.dbw.watcher;

import com.dbw.cache.Cache;
import com.dbw.cfg.Config;
import com.dbw.db.DatabaseManager;
import com.dbw.err.DbConnectionException;
import com.dbw.err.PreparationException;
import com.dbw.err.WatcherStartException;
import com.dbw.frame.AuditFrame;
import com.google.common.collect.Lists;
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

    public void startAll() throws WatcherStartException {
        watcherCheckIn.init(watchers);
        for (Watcher watcher : watchers) {
            try {
                watcher.init();
                Thread watcherThread = new Thread(watcher);
                watcherThread.start();
            } catch (PreparationException | DbConnectionException e) {
                if (e instanceof PreparationException) {
                    cache.removeConfig(watcher.getCfg().getPath());
                }
                throw new WatcherStartException(e.getMessage(), e);
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
}
