package com.dbw.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.dbw.cfg.DatabaseConfig;
import com.dbw.cli.CLI;
import com.dbw.err.PreparationException;
import com.dbw.log.ErrorMessages;
import com.dbw.log.Level;
import com.dbw.log.LogMessages;
import com.dbw.log.Logger;

public abstract class Database {
    protected DatabaseConfig config;
    private Connection conn;

    public Database(DatabaseConfig config) {
        this.config = config;
    }

    public abstract void deleteFirstNRows(String nRows) throws SQLException;

    protected void deleteFirstNRows(String nRows, String deleteAllQuery, String deleteAllLteQuery) throws SQLException {
        if (nRows.equals(CLI.ALL_SYMBOL)) {
            executeFormattedQueryUpdate(deleteAllQuery);
            return;
        }
        int rowCount = selectSingleIntValue(OrclQueries.COUNT_AUDIT_RECORDS, Common.ROW_COUNT);
        int nRowsNum = Integer.parseInt(nRows);
        if (rowCount <= nRowsNum) {
            executeFormattedQueryUpdate(deleteAllQuery);
            return;
        }
        executePreparedStatementUpdateWithSingleInt(deleteAllLteQuery, nRowsNum);
    }

    public void dropAuditTable() throws SQLException {
        executeFormattedQueryUpdate("DROP TABLE " + Common.DBW_AUDIT_TABLE_NAME);
        Logger.log(Level.INFO, LogMessages.AUDIT_TABLE_DROPPED);
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

    public static String formatQuery(String query, Object... params) {
        if (params.length > 0) {
            return String.format(query, params);
        }
        return query;
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

    public abstract List<AuditRecord> selectAuditRecords(int fromId) throws Exception;
    
    protected List<AuditRecord> selectAuditRecords(String query, int fromId) throws Exception {
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

    public DatabaseConfig getConfig() {
        return config;
    }

    public abstract void connect() throws Exception;

    public abstract void prepare(List<String> watchedTables) throws PreparationException;

    public abstract boolean clean(List<String> watchedTables);

    public abstract void close() throws SQLException;
    
}