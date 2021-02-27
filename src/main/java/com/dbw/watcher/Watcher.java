package com.dbw.watcher;

import java.sql.SQLException;
import java.util.List;

import com.dbw.db.Database;
import com.dbw.err.PreparationException;

public interface Watcher {
    public void setDb(Database db);
    public void init(boolean configChanged) throws PreparationException;
    public void start() throws SQLException;
}
