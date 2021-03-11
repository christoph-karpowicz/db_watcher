package com.dbw.db;

import com.dbw.cache.Cache;
import com.dbw.err.InitialAuditRecordDeleteException;
import com.dbw.err.PurgeException;
import com.dbw.log.*;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Singleton
public class DatabaseManager {
    @Inject
    private Cache cache;

    private Map<String, Database> dbs = Maps.newHashMap();

    public void addDatabase(String configPath, Database db) {
        dbs.put(configPath, db);
    }

    public void deleteFirstNRows(String nRows) throws InitialAuditRecordDeleteException {
        for (Database db : dbs.values()) {
            try {
                String successMessage = db.deleteFirstNRows(nRows);
                Logger.log(Level.INFO, String.format(successMessage, nRows));
            } catch (SQLException e) {
                throw new InitialAuditRecordDeleteException(e.getMessage(), e);
            }
        }
    }

    public void purge() throws PurgeException {
        for (Map.Entry<String, Database> db : dbs.entrySet()) {
            boolean isConfirmed = confirmPurge();
            if (!isConfirmed) {
                return;
            }
            List<String> tables = cache.getConfigTables(db.getKey());
            if (db.getValue().purge(tables)) {
                Logger.log(Level.INFO, SuccessMessages.CLI_PURGE);
            } else {
                Logger.log(Level.ERROR, ErrorMessages.CLI_PURGE);
            }
            cache.removeConfig(db.getKey());
        }
    }

    private boolean confirmPurge() throws PurgeException {
        try {
            System.out.println(LogMessages.CONFIRM_PURGE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();
            return input.equals("y") || input.equals("Y");
        } catch (IOException e) {
            throw new PurgeException(e.getMessage(), e);
        }
    }
}
