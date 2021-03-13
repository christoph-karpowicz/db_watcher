package com.dbw.actions;

import com.dbw.db.DatabaseManager;
import com.dbw.err.DbwException;
import com.google.inject.Inject;

public class PurgeAction implements DbAction {
    @Inject
    private DatabaseManager databaseManager;

    public void execute() throws DbwException {
        databaseManager.purge();
    }
}
