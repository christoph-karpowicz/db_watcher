package com.dbw.actions;

import com.dbw.db.DatabaseManager;
import com.dbw.err.DbwException;
import com.google.inject.Inject;
import lombok.Setter;

public class DeleteFirstNRowsAction {
    @Inject
    private DatabaseManager databaseManager;

    @Setter
    private String numberOfRowsToDelete;

    public void execute() throws DbwException {
        databaseManager.deleteFirstNRows(numberOfRowsToDelete);
    }
}
