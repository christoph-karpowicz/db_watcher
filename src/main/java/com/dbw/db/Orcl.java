package com.dbw.db;

import com.dbw.cfg.Config;
import com.dbw.err.DbConnectionException;
import com.dbw.err.PreparationException;
import com.dbw.err.PurgeException;
import com.dbw.err.UnknownDbOperationException;
import com.dbw.log.ErrorMessages;
import com.dbw.log.Level;
import com.dbw.log.LogMessages;
import com.dbw.log.Logger;
import com.google.common.base.Strings;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Orcl extends Database {
    private final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    private final String COL_NAME_ALIAS = "COLUMN_NAME";
    private final String DATA_TYPE_ALIAS = "DATA_TYPE";

    public Orcl(Config config) {
        super(config);
    }

    public void connect() throws DbConnectionException {
        try {
            Connection conn;
            if (!Strings.isNullOrEmpty(dbConfig.getDriverPath())) {
                conn = getConnectionUsingTheDriverInstance();
            } else {
                conn = getConnectionUsingTheDriverManager();
            }
            setConn(conn);
            Logger.log(Level.INFO, dbConfig.getName(), LogMessages.DB_OPENED);
        } catch (SQLException e) {
            throw new DbConnectionException(e.getMessage(), e);
        } catch (ClassNotFoundException | MalformedURLException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            throw new DbConnectionException(String.format(ErrorMessages.DB_CONN_FAILED, e.getMessage()), e);
        }
    }

    private Connection getConnectionUsingTheDriverInstance() throws SQLException, ClassNotFoundException, MalformedURLException, IllegalAccessException, InstantiationException {
        URL[] urls = {};
        DriverClassLoader classLoader = new DriverClassLoader(urls);
        classLoader.addFile(dbConfig.getDriverPath());
        Class driverClass = Class.forName(DRIVER, true, classLoader);
        Driver driver = (Driver)driverClass.newInstance();
        Properties props = new Properties();
        props.put("user", dbConfig.getUser());
        props.put("password", dbConfig.getPassword());
        return driver.connect(getConnectionString(), props);
    }

    private Connection getConnectionUsingTheDriverManager() throws SQLException {
        return DriverManager.getConnection(getConnectionString(), dbConfig.getUser(), dbConfig.getPassword());
    }

    private String getConnectionString() {
        return dbConfig.getConnectionString();
    }

    public void prepare() throws PreparationException {
        OrclPrepareService orclPrepareService = new OrclPrepareService(this);
        orclPrepareService.prepare();
    }

    public boolean auditTableExists() throws SQLException {
        String[] stringArgs = {dbConfig.getUser().toUpperCase(), Common.DBW_AUDIT_TABLE_NAME};
        return objectExists(OrclQueries.FIND_AUDIT_TABLE, stringArgs);
    }

    public void createAuditTable() throws SQLException {
        executeFormattedQueryUpdate(OrclQueries.CREATE_AUDIT_TABLE, Common.DBW_AUDIT_TABLE_NAME);
        Logger.log(Level.INFO, dbConfig.getName(), LogMessages.AUDIT_TABLE_CREATED);
    }

    public boolean auditTriggerExists(String tableName) throws SQLException {
        String[] stringArgs = {Common.DBW_PREFIX + tableName + Common.AUDIT_POSTFIX};
        return objectExists(OrclQueries.FIND_AUDIT_TRIGGER, stringArgs);
    }

    public void createAuditTrigger(String tableName, String query) throws SQLException {
        executeFormattedQueryUpdate(query);
        Logger.log(Level.INFO, dbConfig.getName(), String.format(LogMessages.AUDIT_TRIGGER_CREATED, tableName));
    }

    public String deleteFirstNRows(String nRows) throws SQLException {
        return deleteFirstNRows(nRows, OrclQueries.DELETE_ALL_AUDIT_RECORDS, OrclQueries.DELETE_AUDIT_RECORDS_WITH_ID_LTE);
    }

    public void dropAuditTrigger(String tableName) throws SQLException {
        executeFormattedQueryUpdate(OrclQueries.DROP_AUDIT_TRIGGER, QueryBuilder.buildAuditTriggerName(tableName));
        Logger.log(Level.INFO, dbConfig.getName(), String.format(LogMessages.AUDIT_TRIGGER_DROPPED, tableName));
    }

    public boolean purge(List<String> watchedTables) {
        boolean success = true;
        for (String tableName : watchedTables) {
            try {
                dropAuditTrigger(tableName);
            } catch (SQLException e) {
                success = false;
                new PurgeException(e.getMessage(), e).setRecoverable().handle();
            }
        }
        try {
            dropAuditTable();
        } catch (SQLException e) {
            success = false;
            new PurgeException(e.getMessage(), e).setRecoverable().handle();
        }
        return success;
    }

    public String[] selectAuditTriggers() throws SQLException {
        String[] stringArgs = {};
        List<String> auditTriggers = selectStringArray(OrclQueries.SELECT_AUDIT_TRIGGERS, stringArgs);
        String[] auditTriggerNames = new String[auditTriggers.size()];
        for (short i = 0; i < auditTriggers.size(); i++) {
            String auditTriggerName = auditTriggers.get(i)
                .replace(Common.DBW_PREFIX, "")
                .replace(Common.AUDIT_POSTFIX, "");
            auditTriggerNames[i] = auditTriggerName;
        }
        return auditTriggerNames;
    }

    public Column[] selectTableColumns(String tableName) throws SQLException {
        List<Column> result = new ArrayList<Column>();
        PreparedStatement pstmt = getConn().prepareStatement(OrclQueries.SELECT_TABLE_COLUMNS);
        pstmt.setString(1, tableName);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            Column column = new Column();
            column.setName(rs.getString(COL_NAME_ALIAS));
            column.setDataType(rs.getString(DATA_TYPE_ALIAS));
            result.add(column);
        }
        pstmt.close();
        Column[] columns = new Column[result.size()];
        return result.toArray(columns);
    }

    public int selectMaxId() throws SQLException {
        return selectSingleIntValue(OrclQueries.SELECT_AUDIT_TABLE_MAX_ID, Common.MAX);
    }

    public List<AuditRecord> selectAuditRecords(int fromId) throws SQLException, UnknownDbOperationException {
        return selectAuditRecords(OrclQueries.SELECT_AUDIT_RECORDS, fromId);
    }
 
    public void close() throws SQLException {
        getConn().close();
        Logger.log(Level.INFO, dbConfig.getName(), LogMessages.DB_CLOSED);
    }
    
}
