package com.dbw.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.dbw.cfg.DatabaseConfig;
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

    public void dropAuditTable() throws SQLException {
        executeUpdate("DROP TABLE " + Common.DBW_AUDIT_TABLE_NAME);
        Logger.log(Level.INFO, LogMessages.AUDIT_TABLE_DROPPED);
    }

    protected void executeUpdate(String query, Object... args) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(prepareQuery(query, args));
        stmt.close();
    }

    public static String prepareQuery(String query, Object... args) {
        if (args.length > 0) {
            return String.format(query, args);
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

    public abstract List<AuditRecord> selectAuditRecords(int fromId) throws SQLException;
    
    protected List<AuditRecord> selectAuditRecords(String query, int fromId) throws SQLException {
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
            auditRecord.setOperation(rs.getString(Common.COLNAME_OPERATION));
            if (columnExists(rs, Common.COLNAME_QUERY)) {
                auditRecord.setQuery(rs.getString(Common.COLNAME_QUERY));
            }
            auditRecord.setTimestamp(rs.getDate(Common.COLNAME_TIMESTAMP));
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

    protected int selectMaxId(String query) throws SQLException {
        int result = 0;
        Statement pstmt = conn.createStatement();
        ResultSet rs = pstmt.executeQuery(query);
        if (rs.next()) {
            result = rs.getInt(Common.MAX);
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

    public abstract void clean(List<String> watchedTables) throws SQLException;

    public abstract void close() throws SQLException;
    
}