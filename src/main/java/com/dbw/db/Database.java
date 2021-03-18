package com.dbw.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.dbw.cfg.Config;
import com.dbw.cfg.DatabaseConfig;
import com.dbw.cli.CLI;
import com.dbw.err.DbConnectionException;
import com.dbw.err.PreparationException;
import com.dbw.err.UnknownDbOperationException;
import com.dbw.log.ErrorMessages;
import com.dbw.log.Level;
import com.dbw.log.LogMessages;
import com.dbw.log.Logger;
import com.dbw.log.SuccessMessages;

public abstract class Database {
    protected DatabaseConfig dbConfig;
    private Connection conn;
    private final List<String> watchedTables;

    public Database(Config config) {
        this.dbConfig = config.getDatabase();
        this.watchedTables = config.getTables();
    }

    public List<String> getWatchedTables() {
        return watchedTables;
    }

    public abstract String deleteFirstNRows(String nRows) throws SQLException;

    protected String deleteFirstNRows(String nRows, String deleteAllQuery, String deleteAllLteQuery) throws SQLException {
        int rowCount = getAuditRecordCount();
        if (rowCount == 0) {
            return SuccessMessages.CLI_AUDIT_TABLE_EMPTY;
        }
        if (nRows.equals(CLI.ALL_SYMBOL)) {
            executeFormattedQueryUpdate(deleteAllQuery);
            return String.format(SuccessMessages.CLI_ALL_ROWS_DELETED, rowCount);
        }
        int nRowsNum = Integer.parseInt(nRows);
        if (rowCount <= nRowsNum) {
            executeFormattedQueryUpdate(deleteAllQuery);
            return String.format(SuccessMessages.CLI_ALL_ROWS_DELETED, rowCount);
        }
        executePreparedStatementUpdateWithSingleInt(deleteAllLteQuery, nRowsNum);
        return String.format(SuccessMessages.CLI_N_ROWS_DELETED, nRowsNum);
    }

    public void dropAuditTable() throws SQLException {
        executeFormattedQueryUpdate("DROP TABLE " + Common.DBW_AUDIT_TABLE_NAME);
        Logger.log(Level.INFO, dbConfig.getName(), LogMessages.AUDIT_TABLE_DROPPED);
    }

    protected void executePreparedStatementUpdateWithSingleInt(String query, int param) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, param);
        pstmt.executeUpdate();
        pstmt.close();
    }

    protected void executeFormattedQueryUpdate(String query, Object... params) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(formatQuery(query, params));
        stmt.close();
    }

    public String formatQuery(String query, Object... params) {
        if (params.length > 0) {
            return String.format(query, params);
        }
        return query;
    }

    public int getAuditRecordCount() throws SQLException {
        return selectSingleIntValue(OrclQueries.COUNT_AUDIT_RECORDS, Common.ROW_COUNT);
    }

    protected boolean objectExists(String query, String[] stringArgs) throws SQLException {
        boolean exists = false;
        PreparedStatement pstmt = conn.prepareStatement(query);
        for (short i = 0; i < stringArgs.length; i++) {
            pstmt.setString(i + 1, stringArgs[i]);
        }

        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            exists = rs.getBoolean(Common.EXISTS);
        }
        pstmt.close();
        return exists;
    }

    public abstract List<AuditRecord> selectAuditRecords(int fromId) throws SQLException, UnknownDbOperationException;
    
    protected List<AuditRecord> selectAuditRecords(String query, int fromId) throws SQLException, UnknownDbOperationException {
        List<AuditRecord> result = new ArrayList<AuditRecord>();
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, fromId);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            AuditRecord auditRecord = new AuditRecord();
            auditRecord.setId(rs.getInt(Common.COLNAME_ID));
            auditRecord.setTableName(rs.getString(Common.COLNAME_TABLE_NAME));
            auditRecord.setOldData(rs.getString(Common.COLNAME_OLD_STATE));
            auditRecord.setNewData(rs.getString(Common.COLNAME_NEW_STATE));
            Operation dbOperation = Operation.valueOfSymbol(rs.getString(Common.COLNAME_OPERATION));
            auditRecord.setOperation(dbOperation);
            if (columnExists(rs, Common.COLNAME_QUERY)) {
                auditRecord.setQuery(rs.getString(Common.COLNAME_QUERY));
            }
            auditRecord.setTimestamp(rs.getTimestamp(Common.COLNAME_TIMESTAMP));
            result.add(auditRecord);
        }
        pstmt.close();
        return result;
    }

    private boolean columnExists(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    protected List<String> selectStringArray(String query, String[] stringArgs) throws SQLException {
        List<String> result = new ArrayList<String>();
        PreparedStatement pstmt = conn.prepareStatement(query);
        for (short i = 0; i < stringArgs.length; i++) {
            pstmt.setString(i + 1, stringArgs[i]);
        }
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            result.add(rs.getString(Common.ITEM));
        }
        pstmt.close();
        return result;
    }

    public abstract int selectMaxId() throws SQLException;

    protected int selectSingleIntValue(String query, String columnName) throws SQLException {
        int result = 0;
        Statement pstmt = conn.createStatement();
        ResultSet rs = pstmt.executeQuery(query);
        if (rs.next()) {
            result = rs.getInt(columnName);
        } else {
            throw new SQLException(ErrorMessages.COULDNT_SELECT_MAX);
        }
        pstmt.close();
        return result;
    }

    public Connection getConn() {
        return conn;
    }

    protected void setConn(Connection conn) {
        this.conn = conn;
    }

    public DatabaseConfig getDbConfig() {
        return dbConfig;
    }

    public abstract void connect() throws DbConnectionException;

    public abstract void prepare() throws PreparationException;

    public abstract boolean purge(List<String> watchedTables);

    public abstract void close() throws SQLException;
    
}
