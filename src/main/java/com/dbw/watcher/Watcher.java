package com.dbw.watcher;

import java.util.List;

import com.dbw.db.Database;

public interface Watcher {
    public void setWatchedTables(List<String> watchedTables);
    public void setDb(Database db);
    public void init();
    public void start();
}
