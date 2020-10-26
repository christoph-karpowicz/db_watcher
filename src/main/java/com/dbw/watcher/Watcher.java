package com.dbw.watcher;

import java.sql.SQLException;
import java.util.List;

import com.dbw.db.Database;

public interface Watcher {
    public void setWatchedTables(List<String> watchedTables);
    public void setDb(Database db);
    public void init() throws SQLException;
    public void start() throws SQLException;
}
