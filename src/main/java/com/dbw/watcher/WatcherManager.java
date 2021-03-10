package com.dbw.watcher;

import com.dbw.cfg.Config;
import com.dbw.err.PreparationException;
import com.dbw.err.UnknownDbTypeException;
import com.dbw.err.WatcherStartException;
import com.google.inject.Singleton;

import java.sql.SQLException;
import java.util.List;

@Singleton
public class WatcherManager {
    private List<AuditTableWatcher> watchers;

    public void addWatcher(Config cfg) throws UnknownDbTypeException {
        AuditTableWatcher watcher = new AuditTableWatcher(cfg);
        watcher.setDb();
    }

    public void startAll() throws WatcherStartException {
        for (AuditTableWatcher watcher : watchers) {
            try {
                watcher.init();
                watcher.start();
            } catch (PreparationException | SQLException e) {
                throw new WatcherStartException(e.getMessage(), e);
            }
        }
    }

    public void terminateAll() throws SQLException {
        for (AuditTableWatcher watcher : watchers) {
            watcher.closeDb();
        }
    }
}
