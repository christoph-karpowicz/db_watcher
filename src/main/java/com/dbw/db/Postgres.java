package com.dbw.db;

import com.dbw.cfg.Config;
import com.dbw.err.PreparationException;
import com.dbw.err.RecoverableException;
import com.dbw.err.UnknownDbOperationException;
import com.dbw.err.UnrecoverableException;
import com.dbw.log.Level;
import com.dbw.log.LogMessages;
import com.dbw.log.Logger;
import com.google.common.collect.ImmutableMap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class Postgres extends Database {
    public final static String[] COLUMN_NAMES = new String[]{
        Common.COLNAME_ID, 
        Common.COLNAME_TABLE_NAME, 
        Common.COLNAME_OLD_STATE, 
        Common.COLNAME_NEW_STATE, 
        Common.COLNAME_OPERATION, 
        Common.COLNAME_QUERY,
        Common.COLNAME_TIMESTAMP
    };

    private Map<String, String[]> watchedTablesColumnNames;
    private final String DRIVER = "org.postgresql.Driver";
    
    public Postgres(Config config) {
        super(config);
    }

    public void connect() throws UnrecoverableException {
        try {
            Class.forName(DRIVER);
            Connection conn = DriverManager.getConnection(getConnectionString(), dbConfig.getUser(), dbConfig.getPassword());
            setConn(conn);
            Logger.log(Level.INFO, dbConfig.getName(), LogMessages.DB_OPENED);
        } catch (SQLException | ClassNotFoundException e) {
            throw new UnrecoverableException("DbConnection", e.getMessage(), e);
        }
    }

    private String getConnectionString() {
        if (dbConfig.getConnectionString() != null) {
            return dbConfig.getConnectionString();
        }
        
        return "jdbc:" + dbConfig.getType() +
                "://" + dbConfig.getHost() +
                ":" + dbConfig.getPort() +
                "/" + dbConfig.getName();
    }

    public void prepare() throws PreparationException {
        PostgresPrepareService postgresPrepareService = new PostgresPrepareService(this);
        postgresPrepareService.prepare();
    }

    public Map<String, String[]> getWatchedTablesColumnNames() throws SQLException {
        if (!Objects.isNull(watchedTablesColumnNames)) {
            return watchedTablesColumnNames;
        }
        Map<String, String[]> tableColumnNames = new HashMap<>();
        for (String tableName : getWatchedTables()) {
            tableColumnNames.put(tableName, selectTableColumnNames(tableName));
        }
        watchedTablesColumnNames = ImmutableMap.copyOf(tableColumnNames);
        return watchedTablesColumnNames;
    }

    public boolean auditTableExists() throws SQLException {
        String[] stringArgs = {Config.DEFAULT_SCHEMA, Common.DBW_AUDIT_TABLE_NAME.toLowerCase()};
        return objectExists(PostgresQueries.FIND_AUDIT_TABLE, stringArgs);
    }

    public void createAuditTable() throws SQLException {
        executeFormattedQueryUpdate(PostgresQueries.CREATE_AUDIT_TABLE, Common.DBW_AUDIT_TABLE_NAME.toLowerCase());
        Logger.log(Level.INFO, dbConfig.getName(), LogMessages.AUDIT_TABLE_CREATED);
    }

    public int getAuditRecordCount() throws SQLException {
        return selectSingleIntValue(PostgresQueries.COUNT_AUDIT_RECORDS, Common.ROW_COUNT);
    }

    public boolean auditFunctionExists() throws SQLException {
        String[] stringArgs = {};
        return objectExists(PostgresQueries.FIND_AUDIT_FUNCTION, stringArgs);
    }

    public void createAuditFunction() throws SQLException {
        executeFormattedQueryUpdate(PostgresQueries.CREATE_AUDIT_FUNCTION);
        Logger.log(Level.INFO, dbConfig.getName(), LogMessages.AUDIT_FUNCTION_CREATED);
    }

    public void dropAuditFunction() throws SQLException {
        executeFormattedQueryUpdate(PostgresQueries.DROP_AUDIT_FUNCTION);
        Logger.log(Level.INFO, dbConfig.getName(), LogMessages.AUDIT_FUNCTION_DROPPED);
    }

    public boolean auditTriggerExists(String tableName) throws SQLException {
        String[] stringArgs = {"dbw_" + tableName + "_audit"};
        return objectExists(PostgresQueries.FIND_AUDIT_TRIGGER, stringArgs);
    }

    public void createAuditTrigger(String tableName) throws SQLException {
        executeFormattedQueryUpdate(PostgresQueries.CREATE_AUDIT_TRIGGER, QueryBuilder.buildAuditTriggerName(tableName), tableName);
        Logger.log(Level.INFO, dbConfig.getName(), String.format(LogMessages.AUDIT_TRIGGER_CREATED, tableName));
    }

    public String deleteFirstNRows(String nRows) throws SQLException {
        return deleteFirstNRows(nRows, PostgresQueries.DELETE_ALL_AUDIT_RECORDS, PostgresQueries.DELETE_FIRST_N_AUDIT_RECORDS);
    }

    public void dropAuditTrigger(String tableName) throws SQLException {
        executeFormattedQueryUpdate(PostgresQueries.DROP_AUDIT_TRIGGER, QueryBuilder.buildAuditTriggerName(tableName), tableName);
        Logger.log(Level.INFO, dbConfig.getName(), String.format(LogMessages.AUDIT_TRIGGER_DROPPED, tableName));
    }

    public boolean purge(Set<String> watchedTables) {
        boolean success = true;
        for (String tableName : watchedTables) {
            try {
                dropAuditTrigger(tableName);
            } catch (SQLException e) {
                success = false;
                new RecoverableException("Purge", e.getMessage(), e).setRecoverable().handle();
            }
        }
        try {
            dropAuditFunction();
            dropAuditTable();
        } catch (SQLException e) {
            success = false;
            new RecoverableException("Purge", e.getMessage(), e).setRecoverable().handle();
        }
        return success;
    }

    public String[] selectAuditTriggers() throws SQLException {
        String[] stringArgs = {};
        List<String> auditTriggers = selectStringArray(PostgresQueries.SELECT_AUDIT_TRIGGERS, stringArgs);
        String[] auditTriggerNames = new String[auditTriggers.size()];
        for (short i = 0; i < auditTriggers.size(); i++) {
            String auditTriggerName = auditTriggers.get(i).split("_")[1];
            auditTriggerNames[i] = auditTriggerName;
        }
        return auditTriggerNames;
    }

    public String[] selectTableColumnNames(String tableName) throws SQLException {
        String[] stringArgs = {Config.DEFAULT_SCHEMA, tableName};
        List<String> tableColumnNames = selectStringArray(PostgresQueries.FIND_TABLE_COLUMNS, stringArgs);
        return tableColumnNames.toArray(new String[0]);
    }

    public int selectMaxId() throws SQLException {
        return selectSingleIntValue(PostgresQueries.SELECT_AUDIT_TABLE_MAX_ID, Common.MAX);
    }

    public Integer selectLatestAuditRecordId(long seconds) throws SQLException {
        return selectSingleIntValue(PostgresQueries.SELECT_LATEST_WITH_SECONDS, Common.COLNAME_ID, seconds);
    }

    public List<AuditRecord> selectAuditRecords(int fromId) throws SQLException, UnknownDbOperationException {
        return selectAuditRecords(PostgresQueries.SELECT_AUDIT_RECORDS, fromId);
    }
}
