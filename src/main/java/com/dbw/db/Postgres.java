package com.dbw.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import com.dbw.cfg.Config;
import com.dbw.cfg.DatabaseConfig;
import com.dbw.log.Level;
import com.dbw.log.Logger;
import com.dbw.log.LogMessages;

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
    public final String DRIVER = "org.postgresql.Driver";
    
    public Postgres(DatabaseConfig config) {
        super(config);
    }

    public void connect() throws Exception {
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

    public void prepare(List<String> watchedTables) throws SQLException {
        PostgresPrepareService postgresPrepareService = new PostgresPrepareService(this, watchedTables);
        postgresPrepareService.prepare();
    }

    public boolean auditTableExists() throws SQLException {
        String[] stringArgs = {Config.DEFAULT_SCHEMA, Common.DBW_AUDIT_TABLE_NAME};
        return objectExists(PostgresQueries.FIND_AUDIT_TABLE, stringArgs);
    }

    public void createAuditTable() throws SQLException {
        executeUpdate(PostgresQueries.CREATE_AUDIT_TABLE, Common.DBW_AUDIT_TABLE_NAME);
        Logger.log(Level.INFO, LogMessages.AUDIT_TABLE_CREATED);
    }

    public boolean auditFunctionExists() throws SQLException {
        String[] stringArgs = {};
        return objectExists(PostgresQueries.FIND_AUDIT_FUNCTION, stringArgs);
    }

    public void createAuditFunction() throws SQLException {
        executeUpdate(PostgresQueries.CREATE_AUDIT_FUNCTION);
        Logger.log(Level.INFO, LogMessages.AUDIT_FUNCTION_CREATED);
    }

    public void dropAuditFunction() throws SQLException {
        executeUpdate(PostgresQueries.DROP_AUDIT_FUNCTION);
        Logger.log(Level.INFO, LogMessages.AUDIT_FUNCTION_DROPPED);
    }

    public boolean auditTriggerExists(String tableName) throws SQLException {
        String[] stringArgs = {"dbw_" + tableName + "_audit"};
        return objectExists(PostgresQueries.FIND_AUDIT_TRIGGER, stringArgs);
    }

    public void createAuditTrigger(String tableName) throws SQLException {
        executeUpdate(PostgresQueries.CREATE_AUDIT_TRIGGER, QueryBuilder.buildAuditTriggerName(tableName), tableName);
        Logger.log(Level.INFO, String.format(LogMessages.AUDIT_TRIGGER_CREATED, tableName));
    }

    public void dropAuditTrigger(String tableName) throws SQLException {
        executeUpdate(PostgresQueries.DROP_AUDIT_TRIGGER, QueryBuilder.buildAuditTriggerName(tableName), tableName);
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

    public int selectMaxId() throws SQLException {
        return selectMaxId(PostgresQueries.SELECT_AUDIT_TABLE_MAX_ID);
    }

    public List<AuditRecord> selectAuditRecords(int fromId) throws SQLException {
        return selectAuditRecords(PostgresQueries.SELECT_AUDIT_RECORDS, fromId);
    }

    public void clean(List<String> watchedTables) throws SQLException {
        for (String tableName : watchedTables) {
            dropAuditTrigger(tableName);
        }
        dropAuditFunction();
        dropAuditTable();
    }

    public void close() throws SQLException {
        getConn().close();
        Logger.log(Level.INFO, LogMessages.DB_CLOSED);
    }
}