package com.dbw.db;

import com.dbw.cfg.Config;
import com.dbw.db.query.QueryHelper;
import com.dbw.db.query.SelectAuditRecordsQueryBuilder;
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
    private final String REGEX_CASE_INSENSITIVE = "(?i)";

    public Postgres(Config config) {
        super(config);
    }

    public void connect() throws UnrecoverableException {
        try {
            Class.forName(DRIVER);
            Connection conn = DriverManager.getConnection(getConnectionString(), config.getUser(), config.getPassword());
            setConn(conn);
            Logger.log(Level.INFO, config.getName(), LogMessages.DB_OPENED);
        } catch (SQLException | ClassNotFoundException e) {
            throw new UnrecoverableException("DbConnection", e.getMessage(), e);
        }
    }

    private String getConnectionString() {
        if (config.getConnectionString() != null) {
            return config.getConnectionString();
        }
        
        return "jdbc:" + config.getType() +
                "://" + config.getHost() +
                ":" + config.getPort() +
                "/" + config.getName();
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
        for (String tableName : getWatchedTables().getTableNames()) {
            tableColumnNames.put(tableName, selectTableColumnNames(tableName));
        }
        watchedTablesColumnNames = ImmutableMap.copyOf(tableColumnNames);
        return watchedTablesColumnNames;
    }

    public boolean auditTableExists() throws SQLException {
        String[] stringArgs = {config.getSchema(), Common.DBW_AUDIT_TABLE_NAME.toLowerCase()};
        return objectExists(PostgresQueries.FIND_AUDIT_TABLE, stringArgs);
    }

    public void createAuditTable() throws SQLException {
        executeFormattedQueryUpdate(PostgresQueries.CREATE_AUDIT_TABLE, getObjectNameWithSchema(Common.DBW_AUDIT_TABLE_NAME));
        Logger.log(Level.INFO, config.getName(), LogMessages.AUDIT_TABLE_CREATED);
    }

    public int getAuditRecordCount() throws SQLException {
        return selectSingleIntValue(PostgresQueries.COUNT_AUDIT_RECORDS, Common.ROW_COUNT);
    }

    public boolean auditFunctionExists() throws SQLException {
        String[] stringArgs = {config.getSchema()};
        return objectExists(PostgresQueries.FIND_AUDIT_FUNCTION, stringArgs);
    }

    public void createAuditFunction() throws SQLException {
        executeFormattedQueryUpdate(
                PostgresQueries.CREATE_AUDIT_FUNCTION,
                getObjectNameWithSchema(Common.DBW_AUDIT_FUNC_NAME),
                getObjectNameWithSchema(Common.DBW_AUDIT_TABLE_NAME),
                getObjectNameWithSchema(Common.DBW_AUDIT_TABLE_NAME),
                getObjectNameWithSchema(Common.DBW_AUDIT_TABLE_NAME)
        );
        Logger.log(Level.INFO, config.getName(), LogMessages.AUDIT_FUNCTION_CREATED);
    }

    public void dropAuditFunction() throws SQLException {
        executeFormattedQueryUpdate(PostgresQueries.DROP_AUDIT_FUNCTION, getObjectNameWithSchema(Common.DBW_AUDIT_FUNC_NAME));
        Logger.log(Level.INFO, config.getName(), LogMessages.AUDIT_FUNCTION_DROPPED);
    }

    public boolean auditTriggerExists(String triggerName) throws SQLException {
        String[] stringArgs = {triggerName};
        return objectExists(PostgresQueries.FIND_AUDIT_TRIGGER, stringArgs);
    }

    public void createAuditTrigger(WatchedTables.Entry tableNameAndHash) throws SQLException {
        String triggerName = tableNameAndHash.getEntityName();
        executeFormattedQueryUpdate(
                PostgresQueries.CREATE_AUDIT_TRIGGER,
                triggerName,
                getObjectNameWithSchema(tableNameAndHash.getTableName()),
                getObjectNameWithSchema(Common.DBW_AUDIT_FUNC_NAME)
        );
        Logger.log(Level.INFO, config.getName(), String.format(LogMessages.AUDIT_TRIGGER_CREATED, tableNameAndHash.getTableName()));
    }

    public String deleteFirstNRows(String nRows) throws SQLException {
        return deleteFirstNRows(nRows, PostgresQueries.DELETE_ALL_AUDIT_RECORDS, PostgresQueries.DELETE_FIRST_N_AUDIT_RECORDS);
    }

    public void deleteFirstNRows(int nRows) throws SQLException {
        deleteFirstNRows(PostgresQueries.DELETE_FIRST_N_AUDIT_RECORDS, nRows);
    }

    public void dropAuditTrigger(String tableName) throws SQLException {
        String triggerName = QueryHelper.buildAuditTriggerName(tableName);
        executeFormattedQueryUpdate(PostgresQueries.DROP_AUDIT_TRIGGER, triggerName, getObjectNameWithSchema(tableName));
        Logger.log(Level.INFO, config.getName(), String.format(LogMessages.AUDIT_TRIGGER_DROPPED, tableName));
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
            dropAuditTable(getObjectNameWithSchema(Common.DBW_AUDIT_TABLE_NAME));
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
            String auditTriggerName = auditTriggers.get(i)
                    .replaceAll(REGEX_CASE_INSENSITIVE + Common.DBW_PREFIX, "");
            auditTriggerNames[i] = auditTriggerName;
        }
        return auditTriggerNames;
    }

    public void findAllTables() throws SQLException {
        String[] stringArgs = {config.getName(), config.getSchema()};
        this.allTables = selectStringArray(PostgresQueries.FIND_ALL_TABLES, stringArgs);
    }

    public String[] selectTableColumnNames(String tableName) throws SQLException {
        String[] stringArgs = {config.getSchema(), tableName};
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
        SelectAuditRecordsQueryBuilder selectAuditRecordsBuilder =
                new SelectAuditRecordsQueryBuilder(PostgresQueries.SELECT_AUDIT_RECORDS);
        return selectAuditRecords(selectAuditRecordsBuilder.build(), fromId);
    }

    private String getObjectNameWithSchema(String objectName) {
        return String.join(".", config.getSchema(), objectName.toLowerCase());
    }
}
