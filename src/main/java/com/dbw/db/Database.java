package com.dbw.db;

import com.dbw.cfg.Config;
import com.dbw.cfg.DatabaseConfig;
import com.dbw.cli.Opts;
import com.dbw.err.PreparationException;
import com.dbw.err.UnknownDbOperationException;
import com.dbw.err.UnrecoverableException;
import com.dbw.log.Level;
import com.dbw.log.LogMessages;
import com.dbw.log.Logger;
import com.dbw.log.SuccessMessages;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class Database {
    protected DatabaseConfig dbConfig;
    private Connection conn;
    private final Set<String> watchedTables;

    public Database(Config config) {
        this.dbConfig = config.getDatabase();
        this.watchedTables = config.getTables();
    }

    public Set<String> getWatchedTables() {
        return watchedTables;
    }

    public abstract String deleteFirstNRows(String nRows) throws SQLException;

    public abstract void deleteFirstNRows(int nRows) throws SQLException;

    public abstract int getAuditRecordCount() throws SQLException;

    public abstract Integer selectLatestAuditRecordId(long seconds) throws SQLException;

    public abstract List<AuditRecord> selectAuditRecords(int fromId) throws SQLException, UnknownDbOperationException;

    public abstract int selectMaxId() throws SQLException;

    public abstract void connect() throws UnrecoverableException;

    public abstract void prepare() throws PreparationException;

    public abstract boolean purge(Set<String> watchedTables);

    protected String deleteFirstNRows(String nRows, String deleteAllQuery, String deleteAllLteQuery) throws SQLException {
        int rowCount = getAuditRecordCount();
        if (rowCount == 0) {
            return SuccessMessages.CLI_AUDIT_TABLE_EMPTY;
        }
        if (nRows.equals(Opts.ALL_SYMBOL)) {
            executeFormattedQueryUpdate(deleteAllQuery);
            return String.format(SuccessMessages.CLI_ALL_ROWS_DELETED, rowCount);
        }
        int nRowsNum = Integer.parseInt(nRows);
        if (rowCount <= nRowsNum) {
            executeFormattedQueryUpdate(deleteAllQuery);
            return String.format(SuccessMessages.CLI_ALL_ROWS_DELETED, rowCount);
        }
        deleteFirstNRows(deleteAllLteQuery, nRowsNum);
        return String.format(SuccessMessages.CLI_N_ROWS_DELETED, nRowsNum);
    }

    protected void deleteFirstNRows(String deleteAllLteQuery, int nRows) throws SQLException {
        executePreparedStatementUpdateWithSingleInt(deleteAllLteQuery, nRows);
    }

    public void dropAuditTable(String tableName) throws SQLException {
        executeFormattedQueryUpdate("DROP TABLE " + tableName);
        Logger.log(Level.INFO, dbConfig.getName(), LogMessages.AUDIT_TABLE_DROPPED);
    }

    private void executePreparedStatementUpdateWithSingleInt(String query, int param) throws SQLException {
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

    protected List<AuditRecord> selectAuditRecords(String query, int fromId) throws SQLException, UnknownDbOperationException {
        List<AuditRecord> result = new ArrayList<>();
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
        List<String> result = new ArrayList<>();
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

    protected Integer selectSingleIntValue(String query, String columnName, long arg) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setLong(1, arg);
        ResultSet rs = pstmt.executeQuery();
        Integer result = getSingleIntValue(rs, columnName);
        pstmt.close();
        return result;
    }

    protected Integer selectSingleIntValue(String query, String columnName) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        Integer result = getSingleIntValue(rs, columnName);
        stmt.close();
        return result;
    }

    private Integer getSingleIntValue(ResultSet rs, String columnName) throws SQLException {
        if (rs.next()) {
            Integer value = rs.getInt(columnName);
            if (rs.wasNull()) {
                return null;
            }
            return value;
        } else {
            return null;
        }
    }

    public void close() throws SQLException {
        getConn().close();
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
}
