package com.dbw.db;

import com.dbw.cache.Cache;
import com.dbw.err.DbConnectionException;
import com.dbw.err.InitialAuditRecordDeleteException;
import com.dbw.err.UnrecoverableException;
import com.dbw.log.*;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Provider;
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
    private Provider<Cache> cache;

    private final Map<String, Database> dbs = Maps.newHashMap();

    public void addDatabase(String configPath, Database db) {
        dbs.put(configPath, db);
    }

    public void connectDbs() throws DbConnectionException {
        for (Database db : dbs.values()) {
            db.connect();
        }
    }

    public void deleteFirstNRows(String nRows) throws InitialAuditRecordDeleteException {
        for (Database db : dbs.values()) {
            try {
                String successMessage = db.deleteFirstNRows(nRows);
                Logger.log(Level.INFO, db.getDbConfig().getName(), String.format(successMessage, nRows));
            } catch (SQLException e) {
                throw new InitialAuditRecordDeleteException(e.getMessage(), e);
            }
        }
    }

    public void purge() throws UnrecoverableException {
        for (Map.Entry<String, Database> db : dbs.entrySet()) {
            boolean isConfirmed = confirmPurge(db.getValue().getDbConfig().getName());
            if (!isConfirmed) {
                continue;
            }
            List<String> tables = cache.get().getConfigTables(db.getKey());
            String dbName = db.getValue().getDbConfig().getName();
            if (db.getValue().purge(tables)) {
                Logger.log(Level.INFO, dbName, SuccessMessages.CLI_PURGE);
            } else {
                Logger.log(Level.ERROR, dbName, ErrorMessages.CLI_PURGE);
            }
            cache.get().removeConfig(db.getKey());
        }
    }

    private boolean confirmPurge(String dbName) throws UnrecoverableException {
        try {
            System.out.println(String.format(LogMessages.CONFIRM_PURGE, dbName));
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();
            return input.equalsIgnoreCase("y");
        } catch (IOException e) {
            throw new UnrecoverableException("PurgeException" ,e.getMessage(), e);
        }
    }
}
