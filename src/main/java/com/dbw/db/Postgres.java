package com.dbw.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.dbw.cfg.Config;
import com.dbw.cfg.DatabaseConfig;
import com.dbw.err.CleanupException;
import com.dbw.err.PreparationException;
import com.dbw.log.Level;
import com.dbw.log.Logger;
import com.dbw.log.LogMessages;

public class Postgres extends Database {
    private Map<String, String[]> watchedTablesColumnNames;
    public final static String[] COLUMN_NAMES = new String[]{
        Common.COLNAME_ID, 
        Common.COLNAME_TABLE_NAME, 
        Common.COLNAME_OLD_STATE, 
        Common.COLNAME_NEW_STATE, 
        Common.COLNAME_OPERATION, 
        Common.COLNAME_QUERY,
        Common.COLNAME_TIMESTAMP
    };
    public final String DRIVER = "org.postgresql.Driver";
    
    public Postgres(DatabaseConfig config) {
        super(config);
    }

    public Map<String, String[]> getWatchedTablesColumnNames() {
        return watchedTablesColumnNames;
    }

    public void connect() throws SQLException, ClassNotFoundException {
        Class.forName(DRIVER);
        Connection conn = DriverManager.getConnection(getConnectionString(), config.getUser(), config.getPassword());
        setConn(conn);
        Logger.log(Level.INFO, LogMessages.DB_OPENED);
    }

    private String getConnectionString() {
        if (config.getConnectionString() != null) {
            return config.getConnectionString();
        }
        
        return new StringBuilder()
            .append("jdbc:" + config.getType())
            .append("://" + config.getHost())
            .append(":" + config.getPort())
            .append("/" + config.getName())
            .toString();
    }

    public void prepare(List<String> watchedTables) throws PreparationException {
        PostgresPrepareService postgresPrepareService = new PostgresPrepareService(this, watchedTables);
        postgresPrepareService.prepare();
        watchedTablesColumnNames = postgresPrepareService.getWatchedTablesColumnNames();
    }

    public boolean auditTableExists() throws SQLException {
        String[] stringArgs = {Config.DEFAULT_SCHEMA, Common.DBW_AUDIT_TABLE_NAME.toLowerCase()};
        return objectExists(PostgresQueries.FIND_AUDIT_TABLE, stringArgs);
    }

    public void createAuditTable() throws SQLException {
        executeFormattedQueryUpdate(PostgresQueries.CREATE_AUDIT_TABLE, Common.DBW_AUDIT_TABLE_NAME.toLowerCase());
        Logger.log(Level.INFO, LogMessages.AUDIT_TABLE_CREATED);
    }

    public boolean auditFunctionExists() throws SQLException {
        String[] stringArgs = {};
        return objectExists(PostgresQueries.FIND_AUDIT_FUNCTION, stringArgs);
    }

    public void createAuditFunction() throws SQLException {
        executeFormattedQueryUpdate(PostgresQueries.CREATE_AUDIT_FUNCTION);
        Logger.log(Level.INFO, LogMessages.AUDIT_FUNCTION_CREATED);
    }

    public void dropAuditFunction() throws SQLException {
        executeFormattedQueryUpdate(PostgresQueries.DROP_AUDIT_FUNCTION);
        Logger.log(Level.INFO, LogMessages.AUDIT_FUNCTION_DROPPED);
    }

    public boolean auditTriggerExists(String tableName) throws SQLException {
        String[] stringArgs = {"dbw_" + tableName + "_audit"};
        return objectExists(PostgresQueries.FIND_AUDIT_TRIGGER, stringArgs);
    }

    public void createAuditTrigger(String tableName) throws SQLException {
        executeFormattedQueryUpdate(PostgresQueries.CREATE_AUDIT_TRIGGER, QueryBuilder.buildAuditTriggerName(tableName), tableName);
        Logger.log(Level.INFO, String.format(LogMessages.AUDIT_TRIGGER_CREATED, tableName));
    }

    public void deleteFirstNRows(String nRows) throws SQLException {
        deleteFirstNRows(nRows, PostgresQueries.DELETE_ALL_AUDIT_RECORDS, PostgresQueries.DELETE_AUDIT_RECORDS_WITH_ID_LTE);
    }

    public void dropAuditTrigger(String tableName) throws SQLException {
        executeFormattedQueryUpdate(PostgresQueries.DROP_AUDIT_TRIGGER, QueryBuilder.buildAuditTriggerName(tableName), tableName);
        Logger.log(Level.INFO, String.format(LogMessages.AUDIT_TRIGGER_DROPPED, tableName));
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
        return (String[])tableColumnNames.toArray(new String[tableColumnNames.size()]);
    }

    public int selectMaxId() throws SQLException {
        return selectSingleIntValue(PostgresQueries.SELECT_AUDIT_TABLE_MAX_ID, Common.MAX);
    }

    public List<AuditRecord> selectAuditRecords(int fromId) throws Exception {
        return selectAuditRecords(PostgresQueries.SELECT_AUDIT_RECORDS, fromId);
    }

    public boolean clean(List<String> watchedTables) {
        boolean success = true;
        for (String tableName : watchedTables) {
            try {
                dropAuditTrigger(tableName);
            } catch (SQLException e) {
                success = false;
                new CleanupException(e.getMessage(), e).setRecoverable().handle();
            }
        }
        try {
            dropAuditFunction();
            dropAuditTable();
        } catch (SQLException e) {
            success = false;
            new CleanupException(e.getMessage(), e).setRecoverable().handle();
        }
        return success;
    }

    public void close() throws SQLException {
        getConn().close();
        Logger.log(Level.INFO, LogMessages.DB_CLOSED);
    }
}