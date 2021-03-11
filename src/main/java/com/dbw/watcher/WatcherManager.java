package com.dbw.watcher;

import com.dbw.cfg.Config;
import com.dbw.db.DatabaseManager;
import com.dbw.err.PreparationException;
import com.dbw.err.UnknownDbTypeException;
import com.dbw.err.WatcherStartException;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.sql.SQLException;
import java.util.List;

@Singleton
public class WatcherManager {
    @Inject
    private DatabaseManager databaseManager;

    private List<Watcher> watchers;

    public void addWatcher(Config cfg) throws UnknownDbTypeException {
        Watcher watcher = new Watcher(cfg);
        watcher.setDb();
        watchers.add(watcher);
        databaseManager.addDatabase(cfg.getPath(), watcher.getDb());
    }

    public void startAll() throws WatcherStartException {
        for (Watcher watcher : watchers) {
            try {
                watcher.init();
                watcher.start();
            } catch (PreparationException | SQLException e) {
                throw new WatcherStartException(e.getMessage(), e);
            }
        }
    }

    public void terminateAll() throws SQLException {
        for (Watcher watcher : watchers) {
            watcher.closeDb();
        }
    }
}
